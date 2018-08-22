package com.links86.spider.scheduled;

import com.links86.spider.domain.constant.ReqUrlEnum;
import com.links86.spider.domain.dao.CompanyDO;
import com.links86.spider.domain.dao.CompanyEast;
import com.links86.spider.domain.dao.CompanyTyDO;
import com.links86.spider.manager.CompanyDataManager;
import com.links86.spider.repository.TyMapper;
import com.links86.spider.service.CompaniesService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * @author dyu
 * @date 2018/08/18
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ScheduledTasks {

    @NonNull
    private CompaniesService companiesService;
    @NonNull
    private CompanyDataManager companyDataManager;

    class TinySpider implements Runnable {
        @Override
        public void run() {
            while(true){

                CompanyTyDO tyDO = companyDataManager.getOne();
                if (tyDO == null) {
                    try {
                        Thread.sleep((long) (Math.random() * 2000));
                        continue;
                    } catch (InterruptedException e) {
                        log.error(e.getMessage());
                    }
                }
                CompanyDO companyDO = companiesService.getTycDirectly(tyDO.getId().toString(), tyDO.getComName(), tyDO.getTyUrl().replace("www", "m"));
                companiesService.updTy(tyDO);
                companiesService.saveNew(Stream.of(companyDO).collect(Collectors.toList()));

            }
        }
    }

    @Scheduled(fixedRate = 1000000)
    public void godSpider(){
        BlockingQueue<Runnable> bqueue = new ArrayBlockingQueue<Runnable>(20);

        ThreadPoolExecutor poolExecutor = new ThreadPoolExecutor(10, 20, 50,TimeUnit.MILLISECONDS,bqueue);
        for (int i = 0; i < 200; i++){
            poolExecutor.execute(new TinySpider());
        }
        poolExecutor.shutdown();
    }

    @Scheduled(fixedRate = 60000)
    public void writeDataFromTyc() throws InterruptedException {

        log.debug("begin get data ...");

        // 从数据库中获取需完善信息的企业列表
        List<CompanyEast> ts = companiesService.listsByEast(3, 100);

        // 爬取数据填充列表
        if (ts == null || ts.size() == 0) {
            return;
        }

        List<CompanyDO> companyDOs = new ArrayList<>();

        ExecutorService executorService = Executors.newFixedThreadPool(50);

        ts.parallelStream().forEach(t -> {
            executorService.execute(() -> {
                companyDOs.add(companiesService.getTyc(t.getId(), t.getName()));
            });
        });

        executorService.shutdown();
        while (!executorService.isTerminated()) {
            executorService.awaitTermination(5, TimeUnit.SECONDS);
        }

        log.debug(companyDOs.toString());

        // 入库
        companiesService.save(ts.parallelStream().map(t -> t.upd(ReqUrlEnum.TYC)).collect(Collectors.toList()));
        companiesService.saveNew(companyDOs);
    }

    @Scheduled(fixedRate = 50000)
    public void writeDataFromQcc() throws InterruptedException {
        log.debug("begin qcc ...");
        List<CompanyDO> ts = companiesService.listsByNew(2, 50);

        if (ts == null || ts.size() == 0) {
            return;
        }

        List<CompanyDO> companyDOs = new ArrayList<>();
        ExecutorService executorService = Executors.newFixedThreadPool(20);

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

    //@Scheduled(fixedRate = 3000)
    public void fillingCompany() {
        companyDataManager.adds();
    }
}
