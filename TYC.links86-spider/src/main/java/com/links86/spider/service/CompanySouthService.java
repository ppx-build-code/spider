package com.links86.spider.service;

import com.links86.spider.domain.dao.CompanyDO;
import com.links86.spider.domain.dao.CompanySouthDO;
import com.links86.spider.domain.dao.CompanySouthTempDO;
import com.links86.spider.domain.dao.CompanySouthWestDO;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CompanySouthService {
    List<CompanySouthDO> listsByFlag(Integer flag, Integer limit);

    void upd(CompanySouthDO companySouthDO, Integer flag);

    void addST(CompanySouthTempDO companySouthTempDO);
}
