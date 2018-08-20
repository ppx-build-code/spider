package com.links86.spider.service.impl;

import com.links86.spider.domain.constant.ReqUrlEnum;
import com.links86.spider.domain.dao.CompanyDO;
import com.links86.spider.interceptor.LoggingRequestsInterceptor;
import com.links86.spider.manager.IpPoolManager;
import com.links86.spider.service.CompaniesService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.*;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;


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


    private void get(String param, ReqUrlEnum reqUrlEnum, CompanyDO companyDO) {
        boolean flag1 = true;
        while (flag1) {
            try {
                Object[] ip = IpPoolManager.getIpAndPort(reqUrlEnum.getKey());

                ResponseEntity<String> result = req(reqUrlEnum.getUrl(), reqUrlEnum, param, ip);

                if (result.getStatusCode() == HttpStatus.OK) {
                    String content = result.getBody();

                    String suffix = StringUtils.substringBetween(content, reqUrlEnum.getUrlPrefix(), reqUrlEnum.getUrlSuffix());
                    if (suffix == null) {
                        IpPoolManager.delOne(reqUrlEnum.getKey());
                        continue;
                    }

                    String url = reqUrlEnum.getDetailPrefix() + suffix;

                    // 处理列表中需要截取的数据
                    handleLists(content, companyDO, reqUrlEnum);

                    boolean flag2 = true;
                    while (flag2) {
                        try {

                            Thread.sleep((long) (Math.random()*5000));

                            result = req(url, reqUrlEnum, null, ip);

                            if (result.getStatusCode() == HttpStatus.OK) {
                                content = result.getBody();
                                handleDetail(content, companyDO, reqUrlEnum);

                                flag2 = false;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            ip = IpPoolManager.getIpAndPort(reqUrlEnum.getKey());
                            IpPoolManager.delOne(reqUrlEnum.getKey());
                        }

                    }
                    flag1 = false;
                } else {
                    IpPoolManager.delOne(reqUrlEnum.getKey());
                }
            } catch (Exception e) {
                IpPoolManager.delOne(reqUrlEnum.getKey());
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
                requestFactory.setConnectTimeout(10000);
                requestFactory.setReadTimeout(10000);
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
                IpPoolManager.delOne(reqUrlEnum.getKey());
                tempIp = IpPoolManager.getIpAndPort(reqUrlEnum.getKey());
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
                IpPoolManager.delOne(ReqUrlEnum.QCC.getKey());
                tempId = IpPoolManager.getIpAndPort(ReqUrlEnum.QCC.getKey());
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

            System.out.println("id > > > " + id);
            System.out.println("详情 > > > " + url);
            System.out.println("注册资金 > > > " + money);
            System.out.println("注册时间 > > > " + time);
            System.out.println("企业状态 > > > " + status);

            companyDO.setId(id);
            companyDO.setRegistryMoney(money);
            companyDO.setRegistryTime(time);
            companyDO.setStatus(status);
        } else if (reqUrlEnum == ReqUrlEnum.QCC) {
            // todo
        }

    }

    private void handleDetail(String content, CompanyDO companyDO, ReqUrlEnum reqUrlEnum) {
        if (reqUrlEnum == ReqUrlEnum.TYC) {

            String category = StringUtils.substringBetween(content, TYC_COMPANY_CATEGORY_PREFIX, TYC_COMPANY_BASE_SUFFIX);
            String type = StringUtils.substringBetween(content, TYC_COMPANY_TYPE_PREFIX, TYC_COMPANY_BASE_SUFFIX);
            String registry = StringUtils.substringBetween(content, TYC_REGISTRY_CODE_PREFIX, TYC_COMPANY_BASE_SUFFIX);
            String organization = StringUtils.substringBetween(content, TYC_ORGANIZATION_CODE_PREFIX, TYC_COMPANY_BASE_SUFFIX);
            String unified = StringUtils.substringBetween(content, TYC_UNIFIED_CREDIT_CODE_PREFIX, TYC_COMPANY_BASE_SUFFIX);
            String tax = StringUtils.substringBetween(content, TYC_TAXPAYER_IDENTIFICATION_CODE_PREFIX, TYC_COMPANY_BASE_SUFFIX);
            String operating = StringUtils.substringBetween(content, TYC_OPERATING_PERIOD_CODE_PREFIX, TYC_COMPANY_BASE_SUFFIX);
            String approval = StringUtils.substringBetween(content, TYC_APPROVAL_DATE_PREFIX, TYC_COMPANY_BASE_SUFFIX);
            String registration = StringUtils.substringBetween(content, TYC_REGISTRATION_AUTHORITY_PREFIX, TYC_COMPANY_BASE_SUFFIX);
            String address = StringUtils.substringBetween(content, TYC_COMPANY_ADDRESS_PREFIX, TYC_COMPANY_BASE_SUFFIX);
            String legal = StringUtils.substringBetween(content, TYC_LEGAL_PERSON_PREFIX, TYC_LEGAL_PERSON_SUFFIX);

            System.out.println("行业 > > > " + category);
            System.out.println("企业类型 > > > " + type);
            System.out.println("工商注册号 > > > " + registry);
            System.out.println("组织机构代码 > > > " + organization);
            System.out.println("统一信用代码 > > > " + unified);
            System.out.println("纳税人识别码 > > > " + tax);
            System.out.println("经营期限 > > > " + operating);
            System.out.println("核准日期 > > > " + approval);
            System.out.println("登记机关 > > > " + registration);
            System.out.println("地址 > > > " + address);
            System.out.println("法人 > > > " + legal);

            companyDO.setCategory(category);
            companyDO.setType(type);
            companyDO.setRegistryNo(registry);
            companyDO.setOrganizationCode(organization);
            companyDO.setUnifiedCredit(unified);
            companyDO.setTaxNo(tax);
            companyDO.setOperatingPeriod(operating);
            companyDO.setRegistration(registration);
            companyDO.setAddress(address);
            companyDO.setLegal(legal);
        } else if (reqUrlEnum == ReqUrlEnum.QCC) {
            String businessScope = StringUtils.substringBetween(content, QCC_BUSINESS_SCOPE_PREFIX, QCC_BUSINESS_SCOPE_SUFFIX);
            System.out.println("经营范围 > > > " + businessScope);
            companyDO.setScope(businessScope);
        }
    }

    public static void main(String[] args) throws IOException {
        new CompaniesServiceImpl().get("阿里巴巴", ReqUrlEnum.TYC, new CompanyDO());
        new CompaniesServiceImpl().get("阿里巴巴", ReqUrlEnum.QCC, new CompanyDO());
    }

    @Override
    public CompanyDO get(String name) {
        CompanyDO companyDO = new CompanyDO();
        get(name, ReqUrlEnum.TYC, companyDO);
        get(name, ReqUrlEnum.QCC, companyDO);
        return companyDO;
    }
}