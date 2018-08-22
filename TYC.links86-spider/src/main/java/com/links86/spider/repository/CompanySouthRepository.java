package com.links86.spider.repository;

import com.links86.spider.domain.dao.CompanySouthDO;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CompanySouthRepository extends CrudRepository<CompanySouthDO, String> {
    List<CompanySouthDO> findAllByFlagEquals(Integer flag, Pageable pageable);
}
