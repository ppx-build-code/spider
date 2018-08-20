package com.links86.spider.repository;

import com.links86.spider.domain.dao.CompanyDO;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import java.util.List;


public interface CompanyRepository extends CrudRepository<CompanyDO, String> {

    List<CompanyDO> findAllByFlagEquals(Pageable pageable, Integer flag);
}
