package com.excelutil;

import com.excelutil.airExcel.AirExcelUtil;
import com.excelutil.entity.TestEntity1;
import com.excelutil.entity.TestEntity2;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.*;

public class TestDemo {



    public TestDemo() throws FileNotFoundException {
    }


    @Test
    public void testEntity1ImportExcel() throws FileNotFoundException {
        FileInputStream input = new FileInputStream("F:\\test\\AirUtils\\ExcelUtil\\src\\main\\resources\\test1.xlsx");
        AirExcelUtil<TestEntity1> util = new AirExcelUtil<>(TestEntity1.class);
        List<TestEntity1> list = util.importExcel("test", "test1.xlsx", input);
        System.out.println(list);
    }

    @Test
    public void testEntity1ExportExcel() throws IllegalAccessException, FileNotFoundException {
        FileOutputStream output = new FileOutputStream("F:\\test\\AirUtils\\ExcelUtil\\src\\main\\resources\\test1.xlsx");
        AirExcelUtil<TestEntity1> util = new AirExcelUtil<>(TestEntity1.class);
        boolean b = util.exportExcel(getEntity1(10, 5), "test", "test1.xlsx", output);
        System.out.println(b);
    }

    @Test
    public void testEntity2ImportExcel() throws FileNotFoundException {
        FileInputStream input = new FileInputStream("F:\\test\\AirUtils\\ExcelUtil\\src\\main\\resources\\test2.xlsx");
        AirExcelUtil<TestEntity1> util = new AirExcelUtil<>(TestEntity1.class);
        List<TestEntity1> list = util.importExcel("test", "test1.xlsx", input);
        System.out.println(list);
    }

    @Test
    public void testEntity2ExportExcel() throws FileNotFoundException, IllegalAccessException {
        FileOutputStream output = new FileOutputStream("F:\\test\\AirUtils\\ExcelUtil\\src\\main\\resources\\test2.xlsx");
        AirExcelUtil<TestEntity2> util = new AirExcelUtil<>(TestEntity2.class);
        boolean b = util.exportExcel(getEntity2(10, 5), "test", "test1.xlsx", output);
        System.out.println(b);
    }


    public ArrayList<TestEntity1> getEntity1(int i, int j){
        ArrayList<TestEntity1> users = new ArrayList<>();
        for (int i1 = 0; i1 < i; i1++) {
            TestEntity1 user = new TestEntity1();
            user.setId(i1);
            user.setName("name"+i1);
            user.setTime(new Date());
            user.setMaps(getMap(j--));
            users.add(user);
        }
        return users;
    }
    public ArrayList<TestEntity2> getEntity2(int i, int j){
        ArrayList<TestEntity2> users = new ArrayList<>();
        for (int i1 = 0; i1 < i; i1++) {
            TestEntity2 user = new TestEntity2();
            user.setId(i1);
            user.setName("name"+i1);
            user.setTime(new Date());
            user.setMaps(getMap(j--));
            users.add(user);
        }
        return users;
    }

    public List<Map<String,Object>> getMap(int i){
        ArrayList<Map<String, Object>> maps = new ArrayList<>();
        for (int i1 = 0; i1 < i; i1++) {
            HashMap<String, Object> map = new HashMap<>();
            map.put("age",i1);
            map.put("content",new Date());
            maps.add(map);
        }
        return maps;
    }

}
