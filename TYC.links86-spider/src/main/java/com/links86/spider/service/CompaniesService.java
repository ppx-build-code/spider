package com.links86.spider.service;

import com.links86.spider.domain.constant.ReqUrlEnum;
import com.links86.spider.domain.dao.CompanyDO;
import reactor.core.publisher.Flux;

public interface CompaniesService {
    Flux<String> get(String param, ReqUrlEnum reqUrlEnum, CompanyDO companyDO);
}
