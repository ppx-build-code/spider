package com.links86.spider.repository;

import com.links86.spider.domain.dao.CompanyTyDO;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * @author dyu
 * @date 2018/08/22
 */
public interface CompanyTyRepository extends CrudRepository<CompanyTyDO, Integer> {

    List<CompanyTyDO> findAllByFlagEqualsAndComNameLike(Integer flag, String name, Pageable pageable);
}
