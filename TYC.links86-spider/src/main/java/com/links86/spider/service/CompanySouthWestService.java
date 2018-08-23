package com.links86.spider.service;

import com.links86.spider.domain.dao.CompanySouthWestDO;

import java.util.List;

public interface CompanySouthWestService {

    List<CompanySouthWestDO> listsByFlag(int flag, int limit);

    void upd(CompanySouthWestDO companySouthWestDO, int flag);
}
