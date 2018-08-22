package com.links86.spider.thread;

import com.links86.spider.service.CompaniesService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author dyu
 * @date 2018/08/22
 */
@Slf4j
@AllArgsConstructor
public class QxbSpiderThread implements Runnable {
    private final CompaniesService companiesService;

    @Override
    public void run() {
        while (true) {

        }
    }
}
