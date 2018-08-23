package com.links86.spider.service.impl;

import com.links86.spider.domain.dao.CompanySouthDO;
import com.links86.spider.repository.CompanySouthRepository;
import com.links86.spider.service.CompanySouthService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author dyu
 * @date 2018/08/22
 */
@Service
@RequiredArgsConstructor
public class CompanySouthServiceImpl implements CompanySouthService {

    @NonNull
    private CompanySouthRepository companySouthRepository;

    @Override
    public List<CompanySouthDO> listsByFlag(Integer flag, Integer limit) {
        return companySouthRepository.findAllByFlagEquals(flag, new PageRequest(0, limit, new Sort(Sort.Direction.DESC, "id")));
    }

    @Override
    public void upd(CompanySouthDO companySouthDO, Integer flag) {
        companySouthDO.setFlag(flag);
        companySouthRepository.save(companySouthDO);
    }
}
