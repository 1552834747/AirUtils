package com.excelutil.airExcel;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface AirCell {
    /**
     * 导出到Excel中的名字
     */
    String[] names();
    /**
     * map的key list类型必填
     */
    String[] keys() default {};
    /**
     * 列数 list类型列数必须最大
     */
    int column();
    /**
     * 设置提示标题
     */
    String promptTitle() default "";
    /**
     * 设置提示内容
     */
    String promptContent() default "";
    /**
     * 提示内容范围
     */
    int promptRow() default 200;
    /**
     * 设置只能输入特定值
     */
    String[] textList() default {};
    /**
     * 输入特定值范围
     */
    int textListRow() default 200;
    /**
     * 是否是list
     */
    boolean isList() default false;
    /**
     * 设置列宽 时间类型推荐6000
     */
    int[] titleWidths() default {3000};
    /**
     * 时间格式：非时间格式忽略
     */
    String dateFormat() default "yyyy/MM/dd HH:mm:ss";
}
