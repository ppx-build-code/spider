package com.links86.spider.service;

import com.links86.spider.domain.dao.CompanySouthWestDO;
import com.links86.spider.domain.dao.CompanySouthWestTempDO;

import java.util.List;

public interface CompanySouthWestService {

    List<CompanySouthWestDO> listsByFlag(int flag, int limit);

    void upd(CompanySouthWestDO companySouthWestDO, int flag);

    void addSWT(CompanySouthWestTempDO companySouthWestTempDO);
}
