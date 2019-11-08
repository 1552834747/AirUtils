package com.excelutil.airExcel;

import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface DataStyle {
    /**
     * 单元格水平对齐
     */
    HorizontalAlignment alignment() default HorizontalAlignment.CENTER;
    /**
     * 垂直对齐方式
     */
    VerticalAlignment verticalAlignment() default VerticalAlignment.CENTER;
    /**
     * 是否自动换行
     */
    boolean wrapText() default false;

}
