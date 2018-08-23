package com.links86.spider.manager;

import com.links86.spider.domain.constant.QueueEnum;
import com.links86.spider.domain.constant.ReqUrlEnum;
import com.links86.spider.domain.dao.CompanySouthDO;
import com.links86.spider.domain.dao.CompanyTyDO;
import com.links86.spider.service.CompaniesService;
import com.links86.spider.service.CompanySouthService;
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

    private static ArrayBlockingQueue<CompanyTyDO> tyQueue;
    private static ArrayBlockingQueue<CompanySouthDO> southQueue;

    public static ArrayBlockingQueue getTyQueue() {
        if (tyQueue == null) {
            tyQueue =  new ArrayBlockingQueue(1000);
        }
        return tyQueue;
    }

    public static ArrayBlockingQueue getSouthQueue() {
        if (southQueue == null) {
            southQueue = new ArrayBlockingQueue(1000);
        }
        return southQueue;
    }

    @NonNull
    private CompaniesService companiesService;
    @NonNull
    private CompanySouthService companySouthService;

    public void adds() {
        long num = getTyQueue().size();
        if (num < 100) {
            synchronized (this) {
                List<CompanyTyDO> companyTyDOS = companiesService.listByTy(0, "%上海%", 1000);
                companyTyDOS.parallelStream().forEach(c -> {
                    getTyQueue().add(c);
                });
            }
        }
    }

    public void addSouth() {
        long num = getSouthQueue().size();
        if (num < 100) {
            synchronized (this) {
                List<CompanySouthDO> companyTyDOS = companySouthService.listsByFlag(3, 1000);
                companyTyDOS.parallelStream().forEach(c -> {
                    getSouthQueue().add(c);
                });
            }
        }
    }

    public <T> T getOne(QueueEnum queueEnum) {
        while (true) {
            try {
                if (queueEnum == QueueEnum.EAST) {
                    return (T) getTyQueue().take();
                } else if (queueEnum == QueueEnum.SOUTH) {
                    return (T) getSouthQueue().take();
                } else {
                    return null;
                }
            } catch (InterruptedException e) {
                log.error("get data from queue has error : {}", e.getMessage());
            }
        }
    }
}
