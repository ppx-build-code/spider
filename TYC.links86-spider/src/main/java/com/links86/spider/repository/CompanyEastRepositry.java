package com.links86.spider.repository;

import com.links86.spider.domain.dao.CompanyEast;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CompanyEastRepositry extends CrudRepository<CompanyEast, String> {

    List<CompanyEast> findAllByFlagEquals(Pageable pageable, Integer flag);
}
