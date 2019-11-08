package com.excelutil.entity;


import com.excelutil.airExcel.AirCell;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class TestEntity2 {

    @AirCell(names = "用户id",column = 0)
    private Integer id;

    @AirCell(names = "用户名",column = 1)
    private String name;

    @AirCell(names = "时间",column = 2,titleWidths = 6000)
    private Date time;

    @AirCell(names = {"年龄","内容"},keys = {"age","content"},column = 3,isList = true,titleWidths = {3000,6000})
    private List<Map<String, Object>> maps;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public List<Map<String, Object>> getMaps() {
        return maps;
    }

    public void setMaps(List<Map<String, Object>> maps) {
        this.maps = maps;
    }

    @Override
    public String toString() {
        return "TestEntity1{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", time=" + time +
                ", maps=" + maps +
                '}';
    }
}
