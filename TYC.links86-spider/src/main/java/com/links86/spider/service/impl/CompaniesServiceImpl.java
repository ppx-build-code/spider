package com.links86.spider.service.impl;

import com.links86.spider.domain.constant.CompanyStatusEnum;
import com.links86.spider.domain.constant.ReqUrlEnum;
import com.links86.spider.domain.dao.CompanyDO;
import com.links86.spider.domain.dao.CompanyEast;
import com.links86.spider.interceptor.LoggingRequestsInterceptor;
import com.links86.spider.manager.IpPoolManager;
import com.links86.spider.repository.CompanyEastRepositry;
import com.links86.spider.repository.CompanyRepository;
import com.links86.spider.service.CompaniesService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.io.IOException;
import java.net.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class CompaniesServiceImpl implements CompaniesService {

    private static final String BROWER_SIGN = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/62.0.3202.62 Safari/537.36";
    private static final String TYC_COMPANY_REGISTRY_MONEY_PREFIX_INDEX = "注册资本：<span>";
    private static final String TYC_COMPANY_REGISTRY_TIME_PREFIX_INDEX = "注册时间：<span>";
    private static final String TYC_COMPANY_STATUS = "状态：<span>";
    private static final String TYC_COMPANY_BASE_SUFFIX = "</span>";

    private static final String TYC_LEGAL_PERSON_PREFIX = "title=\"";
    private static final String TYC_LEGAL_PERSON_SUFFIX = "\"";

    private static final String TYC_SCORE_PREFIX = "评分</span>";
    private static final String TYC_SCORE_SUFFIX = "</span>";
    private static final String TYC_EMAIL_PREFIX = "<span class=\"icon-email417 \"></span><span class=\"title-right417\">";
    private static final String TYC_EMAIL_SUFFIX = "</span>";
    private static final String TYC_WEBSITE_PREFIX = "<span class=\"icon-cp417\"></span><span class=\"title-right417\">";
    private static final String TYC_WEBSITE_SUFFIX = "</span>";
    private static final String TYC_COMPANY_CATEGORY_PREFIX = "行业：</span><span>";
    private static final String TYC_COMPANY_TYPE_PREFIX = "企业类型：</span><span>";
    private static final String TYC_REGISTRY_CODE_PREFIX = "工商注册号：</span><span>";
    private static final String TYC_ORGANIZATION_CODE_PREFIX = "组织结构代码：</span><span>";
    private static final String TYC_UNIFIED_CREDIT_CODE_PREFIX = "统一信用代码：</span><span>";
    private static final String TYC_TAXPAYER_IDENTIFICATION_CODE_PREFIX = "纳税人识别号：</span><span>";
    private static final String TYC_OPERATING_PERIOD_CODE_PREFIX = "经营期限：</span><span>";
    private static final String TYC_APPROVAL_DATE_PREFIX = "核准日期：</span><span>";
    private static final String TYC_REGISTRATION_AUTHORITY_PREFIX = "登记机关：</span><span>";
    private static final String TYC_COMPANY_ADDRESS_PREFIX = "注册地址：</span><span>";

    private static final String QCC_BUSINESS_SCOPE_PREFIX = "经营范围</div> <div class=\"basic-item-right\">";
    private static final String QCC_BUSINESS_SCOPE_SUFFIX = "<";

    @Resource
    private IpPoolManager ipPoolManager;
    @Resource
    private CompanyEastRepositry companyEastRepositry;
    @Resource
    private CompanyRepository company;


    private void get(String param, ReqUrlEnum reqUrlEnum, CompanyDO companyDO) {
        boolean flag1 = true;
        while (flag1) {
            try {
                log.debug("in get, before getIpAndPort");
                Object[] ip = ipPoolManager.getIpAndPort(reqUrlEnum.getKey());

                ResponseEntity<String> result = req(reqUrlEnum.getUrl(), reqUrlEnum, param, ip);

                if (result.getStatusCode() == HttpStatus.OK) {
                    String content = result.getBody();

                    String suffix = StringUtils.substringBetween(content, reqUrlEnum.getUrlPrefix(), reqUrlEnum.getUrlSuffix());
                    if (suffix == null) {
                        ipPoolManager.delOne(reqUrlEnum.getKey());
                        continue;
                    }

                    String url = reqUrlEnum.getDetailPrefix() + suffix;

                    // 处理列表中需要截取的数据
                    handleLists(content, companyDO, reqUrlEnum);

                    boolean flag2 = true;
                    while (flag2) {
                        try {

                            Thread.sleep((long) (Math.random()*2000));

                            result = req(url, reqUrlEnum, null, ip);

                            if (result.getStatusCode() == HttpStatus.OK) {
                                content = result.getBody();
                                handleDetail(content, companyDO, reqUrlEnum);

                                flag2 = false;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            log.debug("in get's catch, before getIpAndPort");
                            ip = ipPoolManager.getIpAndPort(reqUrlEnum.getKey());
                            ipPoolManager.delOne(reqUrlEnum.getKey());
                        }

                    }
                    flag1 = false;
                } else {
                    ipPoolManager.delOne(reqUrlEnum.getKey());
                }
            } catch (Exception e) {
                e.printStackTrace();
                ipPoolManager.delOne(reqUrlEnum.getKey());
            }
        }
    }


    private ResponseEntity<String> req(String url, ReqUrlEnum reqUrlEnum, String param, Object [] ip) {
        Object [] tempIp = ip;

        while (true) {
            try {
                SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();


                Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(tempIp[0].toString(), (int) tempIp[1]));

                requestFactory.setProxy(proxy);
                requestFactory.setConnectTimeout(5000);
                requestFactory.setReadTimeout(5000);
                RestTemplate restTemplate = new RestTemplate(requestFactory);
                restTemplate.setInterceptors(Stream.of(new LoggingRequestsInterceptor()).collect(Collectors.toList()));

                HttpHeaders httpHeaders = new HttpHeaders();
                httpHeaders.add("Accept", MediaType.ALL_VALUE);
                httpHeaders.add("User-Agent", BROWER_SIGN);
                httpHeaders.add("Referer", reqUrlEnum.getReferer());

                HttpEntity<String> entity = new HttpEntity<>(null, httpHeaders);

                ResponseEntity<String> result = null;
                if (param == null) {
                    result = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
                } else {
                    result = restTemplate.exchange(url, HttpMethod.GET, entity, String.class, param);
                }

                if (result.getStatusCode() == HttpStatus.OK) {
                    return result;
                }
            } catch (Exception e) {
                e.printStackTrace();
                ipPoolManager.delOne(reqUrlEnum.getKey());
                log.debug("in req, before getIpAndPort");
                tempIp = ipPoolManager.getIpAndPort(reqUrlEnum.getKey());
            }
        }


    }

    private String [] getCookieByQcc(Object [] ip) {
        while (true) {
            Object [] tempId = ip;
            try {
                SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();

                Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(tempId[0].toString(), (int) tempId[1]));

                requestFactory.setProxy(proxy);
                requestFactory.setConnectTimeout(10000);
                requestFactory.setReadTimeout(10000);
                RestTemplate restTemplate = new RestTemplate(requestFactory);

                HttpHeaders httpHeaders = new HttpHeaders();
                httpHeaders.add("User-Agent", BROWER_SIGN);
                HttpEntity<String> entity = new HttpEntity<>(null, httpHeaders);

                ResponseEntity<String> result = restTemplate.exchange("https://www.qichacha.com", HttpMethod.GET, entity, String.class);
                HttpHeaders headers = result.getHeaders();
                return new String[] {headers.getFirst(httpHeaders.SET_COOKIE), headers.getFirst(httpHeaders.SET_COOKIE2)};
            } catch (Exception e) {
                ipPoolManager.delOne(ReqUrlEnum.QCC.getKey());
                tempId = ipPoolManager.getIpAndPort(ReqUrlEnum.QCC.getKey());
            }
        }



    }

    private void handleLists(String content, CompanyDO companyDO, ReqUrlEnum reqUrlEnum) {

        if (reqUrlEnum == ReqUrlEnum.TYC) {
            String url = content.substring(content.indexOf(reqUrlEnum.getUrlPrefix()), content.indexOf(reqUrlEnum.getUrlSuffix()));

            String id = url.substring(url.lastIndexOf("/") + 1);
            String money = StringUtils.substringBetween(content, TYC_COMPANY_REGISTRY_MONEY_PREFIX_INDEX, TYC_COMPANY_BASE_SUFFIX);
            String time = StringUtils.substringBetween(content, TYC_COMPANY_REGISTRY_TIME_PREFIX_INDEX, TYC_COMPANY_BASE_SUFFIX);
            String status = StringUtils.substringBetween(content, TYC_COMPANY_STATUS, TYC_COMPANY_BASE_SUFFIX);

            log.debug("id > > > " + id);
            log.debug("详情 > > > " + url);
            log.debug("注册资金 > > > " + money);
            log.debug("注册时间 > > > " + time);
            log.debug("企业状态 > > > " + status);

            companyDO.setTycId(id);
            companyDO.setCapitalInvested(money);
            companyDO.setStartTime(time);
            companyDO.setStatus(CompanyStatusEnum.getCode(status));
        } else if (reqUrlEnum == ReqUrlEnum.QCC) {
            // todo
        }

    }

    private void handleDetail(String content, CompanyDO companyDO, ReqUrlEnum reqUrlEnum) {
        if (reqUrlEnum == ReqUrlEnum.TYC) {

            String score = StringUtils.substringBetween(content, TYC_SCORE_PREFIX, TYC_SCORE_SUFFIX);
            String email = StringUtils.substringBetween(content, TYC_EMAIL_PREFIX, TYC_EMAIL_SUFFIX);
            String website = StringUtils.substringBetween(content, TYC_WEBSITE_PREFIX, TYC_WEBSITE_SUFFIX);
            String category = StringUtils.substringBetween(content, TYC_COMPANY_CATEGORY_PREFIX, TYC_COMPANY_BASE_SUFFIX);
            String type = StringUtils.substringBetween(content, TYC_COMPANY_TYPE_PREFIX, TYC_COMPANY_BASE_SUFFIX);
            String registry = StringUtils.substringBetween(content, TYC_REGISTRY_CODE_PREFIX, TYC_COMPANY_BASE_SUFFIX);
            String organization = StringUtils.substringBetween(content, TYC_ORGANIZATION_CODE_PREFIX, TYC_COMPANY_BASE_SUFFIX);
            String unified = StringUtils.substringBetween(content, TYC_UNIFIED_CREDIT_CODE_PREFIX, TYC_COMPANY_BASE_SUFFIX);
            String tax = StringUtils.substringBetween(content, TYC_TAXPAYER_IDENTIFICATION_CODE_PREFIX, TYC_COMPANY_BASE_SUFFIX);
            String operating = StringUtils.substringBetween(content, TYC_OPERATING_PERIOD_CODE_PREFIX, TYC_COMPANY_BASE_SUFFIX);
            String registration = StringUtils.substringBetween(content, TYC_REGISTRATION_AUTHORITY_PREFIX, TYC_COMPANY_BASE_SUFFIX);
            String address = StringUtils.substringBetween(content, TYC_COMPANY_ADDRESS_PREFIX, TYC_COMPANY_BASE_SUFFIX);
            String legal = StringUtils.substringBetween(content, TYC_LEGAL_PERSON_PREFIX, TYC_LEGAL_PERSON_SUFFIX);

            if (StringUtils.isNotBlank(website)) {
                if (website.indexOf("</a>") != 0) {
                    website = StringUtils.substringBetween(website, ">", "</a>");
                } else {
                    website = null;
                }
            }

            log.debug("分数:{}", score);
            log.debug("邮箱:{}", email);
            log.debug("网址:{}", website);

            log.debug("行业:{}", category);
            log.debug("企业类型:{}", type);
            log.debug("工商注册号:{}", registry);
            log.debug("组织机构代码:{}", organization);
            log.debug("统一信用代码:{}", unified);
            log.debug("纳税人识别码:{}", tax);
            log.debug("经营期限:{}", operating);
            log.debug("登记机关:{}", registration);
            log.debug("地址:{}", address);
            log.debug("法人:{}", legal);

            companyDO.setScore(score);
            companyDO.setRegisterNumber(registry);
            companyDO.setEmail(email);
            companyDO.setWebsite(website);
            companyDO.setIndustry(category);
            companyDO.setType(type);
            companyDO.setOrgCode(organization);
            companyDO.setCreditCode(unified);
            companyDO.setTaxCode(tax);
            companyDO.setTerms(operating);
            companyDO.setRegisterAuthority(registration);
            companyDO.setAddress(address);
            companyDO.setLegal(legal);
        } else if (reqUrlEnum == ReqUrlEnum.QCC) {
            String businessScope = StringUtils.substringBetween(content, QCC_BUSINESS_SCOPE_PREFIX, QCC_BUSINESS_SCOPE_SUFFIX);
            log.debug("经营范围 > > > " + businessScope);
            companyDO.setBusinessScope(businessScope);
        }
    }

    public static void main(String[] args) throws IOException {
        new CompaniesServiceImpl().get("阿里巴巴", ReqUrlEnum.TYC, new CompanyDO());
        new CompaniesServiceImpl().get("阿里巴巴", ReqUrlEnum.QCC, new CompanyDO());
    }

    @Override
    public CompanyDO getTyc(String id, String name) {
        CompanyDO companyDO = new CompanyDO();
        companyDO.setId(id);
        companyDO.setFlag(2);
        companyDO.setName(name);
        get(name, ReqUrlEnum.TYC, companyDO);
        return companyDO;
    }

    @Override
    public CompanyDO getQcc(CompanyDO companyDO) {
        get(companyDO.getName(), ReqUrlEnum.QCC, companyDO);
        companyDO.setFlag(1);
        return companyDO;
    }

    @Override
    public List<CompanyEast> listsByEast(Integer flag, Integer limit) {
        return companyEastRepositry.findAllByFlagEquals(new PageRequest(0, limit, new Sort(Sort.Direction.DESC, "id")), flag);
    }

    @Override
    public List<CompanyDO> listsByNew(Integer flag, Integer limit) {
        return company.findAllByFlagEquals(new PageRequest(0, limit, new Sort(Sort.Direction.DESC, "id")), flag);
    }

    @Override
    public void save(List<CompanyEast> companyEasts) {
        companyEastRepositry.save(companyEasts);
    }

    @Override
    public void saveNew(List<CompanyDO> companyDOs) {
        company.save(companyDOs);
    }

    @Override
    public CompanyDO getTycDirectly(String id, String name, String url){
        CompanyDO companyDO = new CompanyDO();
        companyDO.setId(id);
        companyDO.setFlag(2);
        companyDO.setName(name);
        getDirecty(companyDO, url);
        return companyDO;
    }

    private void getDirecty(CompanyDO companyDO, String url) {
        boolean flag2 = true;
        Object[] ip;
        while (flag2) {
            try {
                ip = ipPoolManager.getIpAndPort(ReqUrlEnum.TYC.getKey());
                ResponseEntity<String> result = req(url, ReqUrlEnum.TYC, null, ip);

                if (result.getStatusCode() == HttpStatus.OK) {
                    String content = result.getBody();
                    handleDetail(content, companyDO, ReqUrlEnum.TYC);

                    flag2 = false;
                }
            } catch (Exception e) {
                e.printStackTrace();
                log.debug("in get's catch, before getIpAndPort");
                ipPoolManager.delOne(ReqUrlEnum.TYC.getKey());
            }

        }
    }
}