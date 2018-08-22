package com.links86.spider.manager;

import com.links86.spider.domain.dao.CompanyTyDO;
import com.links86.spider.service.CompaniesService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * @author dyu
 * @date 2018/08/22
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CompanyDataManager {

    private static volatile ArrayBlockingQueue arrayBlockingQueue;

    public static ArrayBlockingQueue getArrayBlockingQueue() {
        if (arrayBlockingQueue == null) {
            arrayBlockingQueue =  new ArrayBlockingQueue(1000);
        }
        return arrayBlockingQueue;
    }

    @NonNull
    private CompaniesService companiesService;

    public void adds() {
        long num = getArrayBlockingQueue().size();
        if (num < 100) {
            synchronized (this) {
                List<CompanyTyDO> companyTyDOS = companiesService.listByTy(0, "%上海%", 1000);
                companyTyDOS.parallelStream().forEach(c -> {
                    getArrayBlockingQueue().add(c);
                });
            }
        }
    }

    public <T> T getOne() {
        while (true) {
            try {
                return (T) getArrayBlockingQueue().take();
            } catch (InterruptedException e) {
                log.error("get data from queue has error : {}", e.getMessage());
            }
        }

    }
}
