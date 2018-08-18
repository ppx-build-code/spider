package com.links86.spider.scheduled;

import com.links86.spider.domain.dao.CompanyDO;
import com.links86.spider.service.CompaniesService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * @author dyu
 * @date 2018/08/18
 */
@Component
@RequiredArgsConstructor
public class ScheduledTasks {

    @NonNull
    private CompaniesService companiesService;

    @Scheduled(fixedRate = 1000000)
    public void writeData() {

        // 从数据库中获取需完善信息的企业列表
        List<String> ts = new ArrayList<>();
        ts.add("腾讯");
        ts.add("百度");
        ts.add("集商网络科技上海");
        ts.add("北控软件");
        ts.add("阿里巴巴");
        ts.add("美团");
        ts.add("滴滴");

        // 爬取数据填充列表
        if (ts.size() == 0) {
            return;
        }
        //ExecutorService executorService = Executors.newFixedThreadPool(10);
        //
        //executorService.execute(new Thread(new Runnable() {
        //    @Override
        //    public void run() {
        //        companiesService.get()
        //    }
        //}));

        List<CompanyDO> companyDOs = ts.stream().map(t -> companiesService.get(t)).collect(Collectors.toList());
        System.out.println(companyDOs);


        // 入库
    }
}
