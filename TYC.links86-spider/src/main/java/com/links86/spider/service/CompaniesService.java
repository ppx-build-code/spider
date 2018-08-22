package com.links86.spider.service;

import com.links86.spider.domain.dao.CompanyDO;
import com.links86.spider.domain.dao.CompanyEast;
import com.links86.spider.domain.dao.CompanyTyDO;

import java.util.List;

public interface CompaniesService {
    CompanyDO getTyc(String id, String name);

    CompanyDO getTycDirectly(String id, String name, String url);

    CompanyDO getQcc(CompanyDO companyDO);

    List<CompanyEast> listsByEast(Integer flag, Integer limit);

    List<CompanyDO> listsByNew(Integer flag, Integer limit);

    List<CompanyTyDO> listByTy(Integer flag, String name, Integer limit);

    void save(List<CompanyEast> companyEasts);

    void saveNew(List<CompanyDO> companyDOs);

}
