package com.excelutil.airExcel;

import org.apache.poi.ss.usermodel.*;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface TitleStyle {
    /**
     * 表头信息
     */
    String[] title() default {};
    /**
     * 设置标题行高
     */
    short titleHeight() default -1;
    /**
     * 单元格水平对齐
     */
    HorizontalAlignment alignment() default HorizontalAlignment.CENTER;
    /**
     * 垂直对齐方式
     */
    VerticalAlignment verticalAlignment() default VerticalAlignment.CENTER;
    /**
     * 设置前景色    填充样式需要为1才显示
     */
    IndexedColors fillForegroundColor() default IndexedColors.WHITE;
    /**
     * 填充样式         1 为填充前景色  其它不知道
     */
    FillPatternType fillPattern() default FillPatternType.NO_FILL;
    /**
     * 设置边框样式
     */
    BorderStyle border() default BorderStyle.NONE;
    /**
     * 设置边框颜色
     */
    IndexedColors borderColor() default IndexedColors.AUTOMATIC;
    /**
     * 自动换行
     */
    boolean wrapText() default true;
}
