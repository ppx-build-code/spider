package com.links86.spider.service.impl;

import com.links86.spider.domain.dao.CompanySouthDO;
import com.links86.spider.domain.dao.CompanySouthWestDO;
import com.links86.spider.repository.CompanySwRepository;
import com.links86.spider.service.CompanySouthService;
import com.links86.spider.service.CompanySouthWestService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author dyu
 * @date 2018/08/23
 */
@Service
@RequiredArgsConstructor
public class CompanySouthWestServiceImpl implements CompanySouthWestService {
    private final CompanySwRepository companySwRepository;

    @Override
    public void upd(CompanySouthWestDO companySouthDO, int flag) {
        companySouthDO.setFlag(flag);
        companySwRepository.save(companySouthDO);
    }

    @Override
    public List<CompanySouthWestDO> listsByFlag(int flag, int limit) {
        return companySwRepository.findAllByFlagEquals(flag, new PageRequest(0, limit, new Sort(Sort.Direction.DESC, "id")));
    }
}
