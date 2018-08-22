package com.links86.spider.manager;

import com.links86.spider.domain.constant.ReqUrlEnum;
import com.links86.spider.domain.dao.CompanyTyDO;
import com.links86.spider.service.CompaniesService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * @author dyu
 * @date 2018/08/22
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CompanyDataManager {

    private static final String C_SPIDER_DATA_KEY = "spider:company:data:key";

    @NonNull
    @Qualifier("formatRedisTemplate")
    private RedisTemplate redisTemplate;

    @NonNull
    private CompaniesService companiesService;

    public void adds() {
        long num = redisTemplate.opsForList().size(C_SPIDER_DATA_KEY);
        if (num < 100) {
            synchronized (C_SPIDER_DATA_KEY) {
                redisTemplate.opsForList().leftPushAll(C_SPIDER_DATA_KEY, companiesService.listByTy(0, "%上海%", 1000));
            }
        }
    }

    public <T> T getOne() {
        return (T) redisTemplate.opsForList().rightPop(C_SPIDER_DATA_KEY);
    }
}
