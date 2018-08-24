package com.links86.spider.scheduled;

import com.links86.spider.domain.constant.QueueEnum;
import com.links86.spider.domain.constant.ReqUrlEnum;
import com.links86.spider.domain.dao.*;
import com.links86.spider.manager.CompanyDataManager;
import com.links86.spider.repository.TyMapper;
import com.links86.spider.service.CompaniesService;
import com.links86.spider.service.CompanySouthService;
import com.links86.spider.service.CompanySouthWestService;
import com.links86.spider.thread.SpiderEastThread;
import com.links86.spider.thread.SpiderSouthThread;
import com.links86.spider.thread.SpiderSouthWestThread;
import com.links86.spider.thread.SpiderTyThread;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author dyu
 * @date 2018/08/18
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ScheduledTasks {

    public static String[] orders = {
            "ZF20188220784hdE23h",
            "ZF20188231517gfxDNr",
            "ZF20188231833pqgIoj",
            "ZF20188232249flTtBS",
            "ZF20188232822XUU9hV",
            "ZF20188236458veV63t",
            "ZF20188237111roQRSc",
            "ZF20188238442bdYhL2",
            "ZF20188238471F8VArB",
            "ZF20188239958j3xZmu"
    };

    @NonNull
    private CompaniesService companiesService;
    @NonNull
    private CompanyDataManager companyDataManager;
    @NonNull
    private CompanySouthService companySouthService;
    @NonNull
    private CompanySouthWestService companySouthWestService;
    @NonNull
    private TyMapper tyMapper;

//    @Scheduled(fixedRate = 100000000)
    public void godSpider(){
        BlockingQueue<Runnable> bqueue = new ArrayBlockingQueue<Runnable>(20);

        ThreadPoolExecutor poolExecutor = new ThreadPoolExecutor(10, 20, 50,TimeUnit.MILLISECONDS,bqueue);
        for (int i = 0; i < 200; i++){
            poolExecutor.execute(new SpiderTyThread(companyDataManager, companiesService));
        }
        poolExecutor.shutdown();
    }

    @Scheduled(fixedRate = 500000000)
    public void writeDataFromTyc() throws InterruptedException {

        log.debug("begin get data ...");

        BlockingQueue<Runnable> bqueue = new ArrayBlockingQueue<Runnable>(20);

        ThreadPoolExecutor poolExecutor = new ThreadPoolExecutor(10, 20, 50, TimeUnit.MILLISECONDS,bqueue);
        for (int i = 0; i < 200; i++){
            poolExecutor.execute(new SpiderEastThread(companyDataManager, companiesService));
        }
        poolExecutor.shutdown();
    }

//    @Scheduled(fixedRate = 50000000)
    public void writeDataFromQcc() throws InterruptedException {
        log.debug("begin qcc ...");
        List<CompanyDO> ts = companiesService.listsByNew(2, 10);

        if (ts == null || ts.size() == 0) {
            return;
        }

        List<CompanyDO> companyDOs = new ArrayList<>();
        ExecutorService executorService = Executors.newFixedThreadPool(5);

        ts.parallelStream().forEach(t -> {
            executorService.execute(() -> {
                companyDOs.add(companiesService.getQcc(t));
            });
        });

        executorService.shutdown();
        while (!executorService.isTerminated()) {
            executorService.awaitTermination(5, TimeUnit.SECONDS);
        }

        companiesService.saveNew(companyDOs);
    }

//    @Scheduled(fixedRate = 1000000000)
    public void writeSouthDataFromQxb() throws InterruptedException {
        log.debug("begin qxb ...");
        BlockingQueue<Runnable> bqueue = new ArrayBlockingQueue<Runnable>(50);

        ThreadPoolExecutor poolExecutor = new ThreadPoolExecutor(10, 60, 50,TimeUnit.MILLISECONDS,bqueue);
        for (int i = 0; i < 300; i++){
            poolExecutor.execute(new SpiderSouthThread(companyDataManager, companiesService, companySouthService));
        }
        poolExecutor.shutdown();
    }

//    @Scheduled(fixedRate = 72000000)
    public void writeSwDataFromQxb() throws InterruptedException {
        log.debug("begin qxb ...");
        BlockingQueue<Runnable> bqueue = new ArrayBlockingQueue<Runnable>(50);

        ThreadPoolExecutor poolExecutor = new ThreadPoolExecutor(10, 60, 50,TimeUnit.MILLISECONDS,bqueue);
        for (int i = 0; i < 300; i++){
            poolExecutor.execute(new SpiderSouthWestThread(companyDataManager, companiesService, companySouthWestService));
        }
        poolExecutor.shutdown();
    }

//    @Scheduled(fixedRate = 3000)
    public void fillingSouthWestCompany() {
        companyDataManager.adds(QueueEnum.SW);
    }

//    @Scheduled(fixedRate = 3000)
    public void fillingSouthCompany() {
        companyDataManager.adds(QueueEnum.SOUTH);
    }

//    @Scheduled(fixedRate = 3000)
    public void fillingTyCompany() {
        companyDataManager.adds(QueueEnum.TY);
    }

    @Scheduled(fixedRate = 3000)
    public void fillingEastCompany() {
        companyDataManager.adds(QueueEnum.EAST);

    }

    @Scheduled(cron = "0 0 * * * ?")
    public void fillingOrderNo() {
        log.info("update orderNo ...");
        List<String> strs = tyMapper.getXDailiOrders();
        if (strs != null && strs.size() > 0) {
            for (int i = 0; i < strs.size(); i++) {
                orders[i] = strs.get(i);
            }
        }
    }

    public static void main(String[] args) {
        System.out.println(orders[(int)(Math.random()*10)%(orders.length)]);
    }
}
