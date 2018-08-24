package com.links86.spider.service.impl;

import com.links86.spider.domain.constant.CompanyStatusEnum;
import com.links86.spider.domain.constant.ReqUrlEnum;
import com.links86.spider.domain.dao.CompanyDO;
import com.links86.spider.domain.dao.CompanyEast;
import com.links86.spider.domain.dao.CompanyTyDO;
import com.links86.spider.interceptor.LoggingRequestsInterceptor;
import com.links86.spider.manager.IpPoolManager;
import com.links86.spider.repository.CompanyEastRepositry;
import com.links86.spider.repository.CompanyRepository;
import com.links86.spider.repository.CompanyTyRepository;
import com.links86.spider.scheduled.ScheduledTasks;
import com.links86.spider.service.CompaniesService;
import com.links86.spider.util.DynamicSpiderUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    private static final String QCC_LEGAL_PREFIX = ".html\">";
    private static final String QCC_LEGAL_SUFFIX = " </a>";
    private static final String QCC_PHONE_PREFIX = "class=\"phone a-decoration\">";
    private static final String QCC_PHONE_SUFFIX = "</a>";
    private static final String QCC_EMAIL_PREFIX = "class=\"email a-decoration\">";
    private static final String QCC_EMAIL_SUFFIX = "</a>";
    private static final String QCC_ADDRESS_PREFIX = "class=\"address>";
    private static final String QCC_ADDRESS_SUFFIX = "</div";
    private static final String QCC_REGISTRY_PREFIX = "注册号</div> <div class=\"basic-item-right\">";
    private static final String QCC_REGISTRY_SUFFIX = "</div>";
    private static final String QCC_UNIFIED_PREFIX = "统一社会信用代码</div> <div class=\"basic-item-right\">";
    private static final String QCC_UNIFIED_SUFFIX = "</div>";
    private static final String QCC_REGISTRY_TIME_PREFIX = "成立日期</div> <div class=\"basic-item-right\">";
    private static final String QCC_REGISTRY_TIME_SUFFIX = "</div>";
//    private static final String QCC_ADDRESS_PREFIX = "公司住所</div> <div class=\"basic-item-right\">";
//    private static final String QCC_ADDRESS_SUFFIX = "</div>";
    private static final String QCC_TERMS_PREFIX = "营业期限</div> <div class=\"basic-item-right\">";
    private static final String QCC_TERMS_SUFFIX = "</div>";
    private static final String QCC_INVEST_PREFIX = "注册资本</div> <div class=\"basic-item-right\">";
    private static final String QCC_INVEST_SUFFIX = "</div>";
    private static final String QCC_TYPE_PREFIX = "企业类型</div> <div class=\"basic-item-right\">";
    private static final String QCC_TYPE_SUFFIX = "</div>";

    private static final String QXB_JSON_PREFIX = "window.__INITIAL_STATE__=";
    private static final String QXB_JSON_SUFFIX = ";(function(){var s;(s=document.currentScript";


    private final CompanyEastRepositry companyEastRepositry;
    private final CompanyRepository company;
    private final CompanyTyRepository companyTyRepository;


    private String doRetrieve(String url, String param, String prefix){
        int count = 0;
        String result = null;
        while (count < 20) {
            ++count;
            result = req(url, param);
            if (StringUtils.isEmpty(result) || !result.contains(prefix)) continue;
            return result;
        }
        return result;
    }

    private void get(String param, ReqUrlEnum reqUrlEnum, CompanyDO companyDO) {
        String content = doRetrieve(reqUrlEnum.getUrl(), param, reqUrlEnum.getUrlPrefix());

        String suffix = StringUtils.substringBetween(content, reqUrlEnum.getUrlPrefix(), reqUrlEnum.getUrlSuffix());
        if (suffix == null) {
            return;
        }

        String url = reqUrlEnum.getDetailPrefix() + suffix + reqUrlEnum.getDetailSuffix();
        content = doRetrieve(url, null, TYC_COMPANY_ADDRESS_PREFIX);
        if(StringUtils.isEmpty(content)) return;

        handleDetail(content, companyDO, reqUrlEnum);

        url = StringUtils.replace(url, "info", "change");
        content = doRetrieve(url, null, QXB_JSON_PREFIX);
        handleChange(content, companyDO);
    }
    //private void get(String param, ReqUrlEnum reqUrlEnum, CompanyDO companyDO) {
    //    boolean flag1 = true;
    //    int outer = 20;
    //    while (flag1) {
    //        try {
    //
    //            String content = req(reqUrlEnum.getUrl(), param);
    //
    //            String suffix = StringUtils.substringBetween(content, reqUrlEnum.getUrlPrefix(), reqUrlEnum.getUrlSuffix());
    //            if (suffix == null) {
    //                continue;
    //            }
    //
    //            String url = reqUrlEnum.getDetailPrefix() + suffix + reqUrlEnum.getDetailSuffix();
    //
    //            // 处理列表中需要截取的数据
    //            handleLists(content, companyDO, reqUrlEnum);
    //
    //            boolean flag2 = true;
    //            int inner = 20;
    //            while (flag2) {
    //                try {
    //
    //                    Thread.sleep((long) (Math.random()*2000));
    //
    //                    content = req(url, null);
    //                    handleDetail(content, companyDO, reqUrlEnum);
    //
    //                    if (reqUrlEnum == ReqUrlEnum.QXB) {
    //                        Thread.sleep((long) (Math.random()*2000));
    //
    //                        url = StringUtils.replace(url, "info", "change");
    //                        content = req(url, null);
    //                        handleChange(content, companyDO);
    //                    }
    //
    //                    flag2 = false;
    //                } catch (Exception e) {
    //                    inner --;
    //                    if (inner == 0) {
    //                        flag2 = false;
    //                    }
    //                    log.error("get data from {} error : {}", reqUrlEnum.getReferer(), e.getMessage());
    //                    log.debug("in get's catch, before getIpAndPort");
    //                }
    //
    //            }
    //            flag1 = false;
    //        } catch (Exception e) {
    //            outer --;
    //            if (outer == 0) {
    //                flag1 = false;
    //            }
    //            log.error("get data from {} error : {}", reqUrlEnum.getReferer(), e.getMessage());
    //        }
    //    }
    //}


    private String req(String url, String param) {
        while (true) {
            try {
                int count = ScheduledTasks.orders.length;
                return DynamicSpiderUtils.request(String.format(url, param), ScheduledTasks.orders[((int)(Math.random()*10)%count)]);
            } catch (Exception e) {
                log.error(e.getMessage());
                log.debug("in req, before getIpAndPort");
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
        } else if (reqUrlEnum == ReqUrlEnum.QXB) {
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

            if (StringUtils.isBlank(score) && StringUtils.isBlank(address)) {
                throw new RuntimeException("get data from tyc error. please retry.");
            }

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
            String legal = StringUtils.substringBetween(content, QCC_LEGAL_PREFIX, QCC_LEGAL_SUFFIX);
            String tel = StringUtils.substringBetween(content, QCC_PHONE_PREFIX, QCC_PHONE_SUFFIX);
            String email = StringUtils.substringBetween(content, QCC_EMAIL_PREFIX, QCC_EMAIL_SUFFIX);
            String address = StringUtils.substringBetween(content, QCC_ADDRESS_PREFIX, QCC_ADDRESS_SUFFIX);
            String registry = StringUtils.substringBetween(content, QCC_REGISTRY_PREFIX, QCC_REGISTRY_SUFFIX);
            String unified = StringUtils.substringBetween(content, QCC_UNIFIED_PREFIX, QCC_UNIFIED_SUFFIX);
            String registryTime = StringUtils.substringBetween(content, QCC_REGISTRY_TIME_PREFIX, QCC_REGISTRY_TIME_SUFFIX);
            String terms = StringUtils.substringBetween(content, QCC_TERMS_PREFIX, QCC_TERMS_SUFFIX);
            String type = StringUtils.substringBetween(content, QCC_TYPE_PREFIX, QCC_TYPE_SUFFIX);
            String invest = StringUtils.substringBetween(content, QCC_INVEST_PREFIX, QCC_INVEST_SUFFIX);

            log.debug("经营范围 > > > " + businessScope);

            companyDO.setBusinessScope(businessScope);
            companyDO.setLegal(legal);
            companyDO.setRegisterNumber(registry);
            companyDO.setAddress(address);
            companyDO.setCapitalInvested(invest);
            companyDO.setTel(tel);
            companyDO.setEmail(email);
            companyDO.setCreditCode(unified);
            companyDO.setStartTime(registryTime);
            companyDO.setTerms(terms);
            companyDO.setType(type);

        } else if (reqUrlEnum == ReqUrlEnum.QXB) {
            content = StringUtils.substringBetween(content,QXB_JSON_PREFIX, QXB_JSON_SUFFIX);

            JSONObject jsonObject = new JSONObject(content);

            JSONObject entGSDetail = jsonObject.getJSONObject("enterprise").getJSONObject("entGSDetail");
            String legal = entGSDetail.get("operName").toString();
            String startTime = entGSDetail.get("startDate").toString();
            String capitalInvested = entGSDetail.get("regCapi").toString();

            JSONObject regInfo = entGSDetail.getJSONObject("regInfo");
            String type = regInfo.get("econKind").toString();
            String registerNumber = regInfo.get("regNo").toString();
            String orgCode = regInfo.get("orgNo").toString();
            String creditCode = regInfo.get("creditNo").toString();
            String status = regInfo.get("status").toString();
            String businessScope = regInfo.get("scope").toString();
            String address = regInfo.get("address").toString();
            String terms = regInfo.get("businessTerm").toString();
            String checkDate = regInfo.get("checkDate").toString();
            String registerAuthority = regInfo.get("belongOrg").toString();

            log.debug("法人 {}", legal);
            log.debug("注册时间 {}", startTime);
            log.debug("注册资本 {}", capitalInvested);
            log.debug("企业类型 {}", type);
            log.debug("工商注册号 {}", registerNumber);
            log.debug("组织机构代码 {}", orgCode);
            log.debug("统一信用代码 {}", creditCode);
            log.debug("企业状态 {}", status);
            log.debug("经营范围 {}", businessScope);
            log.debug("地址 {}", address);
            log.debug("经营期限 {}", terms);
            log.debug("发照时间 {}", checkDate);
            log.debug("登记机关 {}", registerAuthority);

            companyDO.setInfoJson(content);
            companyDO.setCapitalInvested(capitalInvested);
            companyDO.setStartTime(startTime);
            companyDO.setStatus(CompanyStatusEnum.getCode(status));
            companyDO.setRegisterNumber(registerNumber);
            companyDO.setType(type);
            companyDO.setOrgCode(orgCode);
            companyDO.setCreditCode(creditCode);
            companyDO.setTerms(terms);
            companyDO.setRegisterAuthority(registerAuthority);
            companyDO.setAddress(address);
            companyDO.setLegal(legal);
            companyDO.setBusinessScope(businessScope);

        }
    }

    private void handleChange(String content, CompanyDO companyDO) {
        content = StringUtils.substringBetween(content,QXB_JSON_PREFIX, QXB_JSON_SUFFIX);
        companyDO.setChangeJson(content);
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
    public CompanyDO getQxb(String id, String name) {
        CompanyDO companyDO = new CompanyDO();
        companyDO.setId(id);
        companyDO.setFlag(2);
        companyDO.setName(name);
        get(name, ReqUrlEnum.QXB, companyDO);
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
    public List<CompanyTyDO> listByTy(Integer flag, String name, Integer limit) {
        return companyTyRepository.findAllByFlagEqualsAndComNameLike(flag, name, new PageRequest(0, limit, new Sort(Sort.Direction.DESC, "id")));
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
    public void updTy(CompanyTyDO companyTyDO) {
        companyTyDO.setFlag(1);
        companyTyRepository.save(companyTyDO);
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
        while (flag2) {
            try {
                String content = req(url, null);
                handleDetail(content, companyDO, ReqUrlEnum.TYC);
                flag2 = false;
            } catch (Exception e) {
                log.error("request directy error : {}", e.getMessage());
                log.debug("in get's catch, before getIpAndPort");
            }

        }
    }


}