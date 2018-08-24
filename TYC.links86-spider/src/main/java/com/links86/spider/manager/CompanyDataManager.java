package com.links86.spider.manager;

import com.links86.spider.domain.constant.QueueEnum;
import com.links86.spider.domain.constant.ReqUrlEnum;
import com.links86.spider.domain.dao.CompanyEast;
import com.links86.spider.domain.dao.CompanySouthDO;
import com.links86.spider.domain.dao.CompanySouthWestDO;
import com.links86.spider.domain.dao.CompanyTyDO;
import com.links86.spider.service.CompaniesService;
import com.links86.spider.service.CompanySouthService;
import com.links86.spider.service.CompanySouthWestService;
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
    private static ArrayBlockingQueue<CompanySouthWestDO> swQueue;
    private static ArrayBlockingQueue<CompanyEast> eastQueue;

    private static ArrayBlockingQueue<CompanyTyDO> getTyQueue() {
        if (tyQueue == null) {
            tyQueue =  new ArrayBlockingQueue<>(1000);
        }
        return tyQueue;
    }

    private static ArrayBlockingQueue<CompanySouthDO> getSouthQueue() {
        if (southQueue == null) {
            southQueue = new ArrayBlockingQueue<>(1000);
        }
        return southQueue;
    }

    private static ArrayBlockingQueue<CompanySouthWestDO> getSwQueue() {
        if (swQueue == null) {
            swQueue = new ArrayBlockingQueue<>(1000);
        }
        return swQueue;
    }

    public static ArrayBlockingQueue<CompanyEast> getEastQueue() {
        if (eastQueue == null) {
            eastQueue = new ArrayBlockingQueue<>(1000);
        }
        return eastQueue;
    }

    @NonNull
    private CompaniesService companiesService;
    @NonNull
    private CompanySouthService companySouthService;
    @NonNull
    private CompanySouthWestService companySouthWestService;

    public void adds(QueueEnum queueEnum) {
        long num = getQ(queueEnum).size();
        if (num < 100) {
            synchronized (this) {
                fillingData(queueEnum);
            }
        }
    }

    private ArrayBlockingQueue getQ(QueueEnum queueEnum) {
        if (queueEnum == QueueEnum.SOUTH) {
            return getSouthQueue();
        } else if (queueEnum == QueueEnum.TY) {
            return getTyQueue();
        } else if (queueEnum == QueueEnum.SW) {
            return getSwQueue();
        } else if (queueEnum == QueueEnum.EAST) {
            return getEastQueue();
        } else {
            return null;
        }
    }

    private void fillingData(QueueEnum queueEnum) {
        if (queueEnum == QueueEnum.SOUTH) {
            List<CompanySouthDO> companyTyDOS = companySouthService.listsByFlag(3, 1000);
            companyTyDOS.parallelStream().forEach(c -> {
                getSouthQueue().add(c);
            });
        } else if (queueEnum == QueueEnum.TY) {
            List<CompanyTyDO> companyTyDOS = companiesService.listByTy(0, "%上海%", 1000);
            companyTyDOS.parallelStream().forEach(c -> {
                getTyQueue().add(c);
            });
        } else if (queueEnum == QueueEnum.SW) {
            List<CompanySouthWestDO> companySouthWestDOS = companySouthWestService.listsByFlag(3, 1000);
            companySouthWestDOS.parallelStream().forEach(c -> {
                getSwQueue().add(c);
            });
        } else if (queueEnum == QueueEnum.EAST) {
            List<CompanyEast> companyEasts = companiesService.listsByEast(3, 1000);
            companyEasts.parallelStream().forEach(c -> {
                getEastQueue().add(c);
            });
        }
    }



    public <T> T getOne(QueueEnum queueEnum) {
        while (true) {
            try {
                if (queueEnum == QueueEnum.TY) {
                    return (T) getTyQueue().take();
                } else if (queueEnum == QueueEnum.SOUTH) {
                    return (T) getSouthQueue().take();
                } else if (queueEnum == QueueEnum.SW){
                    return (T) getSwQueue().take();
                } else if (queueEnum == QueueEnum.EAST) {
                    return (T) getEastQueue().take();
                }
            } catch (InterruptedException e) {
                log.error("get data from queue has error : {}", e.getMessage());
            }
        }
    }
}
