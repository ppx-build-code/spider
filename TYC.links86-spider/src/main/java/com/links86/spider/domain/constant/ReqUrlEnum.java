package com.links86.spider.domain.constant;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum ReqUrlEnum {
    TYC(1, "https://m.tianyancha.com/search?key=%s",
            "tyc_spider_key", "http://m.tianyancha.com/",
            "https://m.tianyancha.com/company",
            "\" style=\"word-break:break-all;",
            "https://m.tianyancha.com/company"),
    QCC(2, "http://m.qichacha.com/search?key=%s",
            "qcc_spider_key",
            "http://m.qichacha.com/",
            "<a href=\"/firm_",
            "\" class=\"a-decoration\"",
            "http://m.qichacha.com/firm_"),
    ;
    private int num;
    private String url;
    private String key;
    private String referer;
    private String urlPrefix;
    private String urlSuffix;
    private String detailPrefix;

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }


    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getUrlPrefix() {
        return urlPrefix;
    }

    public void setUrlPrefix(String urlPrefix) {
        this.urlPrefix = urlPrefix;
    }

    public String getUrlSuffix() {
        return urlSuffix;
    }

    public void setUrlSuffix(String urlSuffix) {
        this.urlSuffix = urlSuffix;
    }

    public String getReferer() {
        return referer;
    }

    public void setReferer(String referer) {
        this.referer = referer;
    }

    public String getDetailPrefix() {
        return detailPrefix;
    }

    public void setDetailPrefix(String detailPrefix) {
        this.detailPrefix = detailPrefix;
    }
}
