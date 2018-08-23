package com.links86.spider.domain.constant;

import lombok.AllArgsConstructor;

import java.util.stream.Stream;

@AllArgsConstructor
public enum CompanyStatusEnum {
    IN(1, "在业"),
    ING(2, "存续"),
    REVOKE(3, "吊销"),
    LOGOUT(4, "注销"),
    OUT(5, "迁出"),
    ;
    private Integer code;
    private String name;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static Integer getCode(String name) {
        return Stream.of(CompanyStatusEnum.values())
                .filter(i -> name.startsWith(i.name))
                .findFirst()
                .map(CompanyStatusEnum::getCode)
                .orElse(null);
    }

    public static void main(String[] args) {
        System.out.println(getCode("存续"));
    }
}
