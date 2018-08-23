package com.links86.spider.repository;

import com.links86.spider.domain.dao.CompanySouthWestDO;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CompanySwRepository extends CrudRepository<CompanySouthWestDO, String> {
    List<CompanySouthWestDO> findAllByFlagEquals(Integer flag, Pageable pageable);
}
