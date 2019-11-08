package com.imgcompound.utils;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;

/**
 * 海报生成工具
 */
public class ImgCompoundUtil {

    //海报背景
    private BufferedImage poster;
    //画笔
    private Graphics g;

    /**
     * 创建对象
     * @param url       海报背景链接
     * @throws IOException
     */
    public ImgCompoundUtil(URL url){
        Image image = Toolkit.getDefaultToolkit().getImage(url);
        poster = toBufferedImage(image);
        g = poster.getGraphics();
        g.setColor(Color.black);
    }

    /**
     * 创建对象
     * @param is        海报背景输入流
     * @throws IOException
     */
    public ImgCompoundUtil(InputStream is) throws IOException {
        poster = ImageIO.read(is);
        g = poster.getGraphics();
        g.setColor(Color.black);
    }

    /**
     * 合成图片
     * @param is        图片输入流
     * @param x         横坐标
     * @param y         纵坐标
     * @param w         图片缩放-宽度     传 0 不缩放
     * @param h         图片缩放-高度     传 0 不缩放
     * @throws IOException
     */
    public void drawImage(InputStream is, int x, int y, int w, int h) throws IOException {
        BufferedImage read = ImageIO.read(is);
        drawImage(read,x,y,w,h);
    }

    /**
     * 合成图片
     * @param is        图片URL
     * @param x         横坐标
     * @param y         纵坐标
     * @param w         图片缩放-宽度     传 0 不缩放
     * @param h         图片缩放-高度     传 0 不缩放
     * @throws IOException
     */
    public void drawImage(URL url, int x, int y, int w, int h,boolean isYuan) throws IOException {
        Image read = Toolkit.getDefaultToolkit().getImage(url);
        BufferedImage bf = toBufferedImage(read);
        if (isYuan) {
            BufferedImage bi = roundImage(bf, w, w);
            drawImage(bi,x,y,w,h);
        }else {
            drawImage(bf,x,y,w,h);
        }
    }

    //将图片合成到背景图中
    private void drawImage(BufferedImage Image, int x, int y, int w, int h){
        if (w != 0 && y != 0) {
            BufferedImage wy = new BufferedImage(w, h, Image.getType());
            wy.getGraphics().drawImage(Image,0,0,w,h,null);
            g.drawImage(wy,x,y,null);
        }else {
            g.drawImage(Image,x,y,null);
        }
    }

    //将图片改成圆形
    private BufferedImage roundImage(BufferedImage image, int targetSize, int cornerRadius) {
        BufferedImage outputImage = new BufferedImage(targetSize, targetSize, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = outputImage.createGraphics();
        g2.setComposite(AlphaComposite.Src);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(Color.WHITE);
        g2.fill(new RoundRectangle2D.Float(0, 0, targetSize, targetSize, cornerRadius, cornerRadius));
        g2.setComposite(AlphaComposite.SrcAtop);
        g2.drawImage(image, 0, 0, targetSize,targetSize,null);
        return outputImage;
    }

    /**
     * 设置字体
     * @param font
     */
    public void setFont(Font font){
        g.setFont(font);
    }

    /**
     * 设置字体颜色
     * @param color
     */
    public void setColor(Color color){
        g.setColor(color);
    }

    /**
     * 插入文字
     * @param context   内容
     * @param x         横坐标
     * @param y         纵坐标
     */
    public void drawString(String context,int x,int y){
        g.drawString(context,x,y);
    }

    /**
     * 插入文字
     * @param font      指定文字字体
     * @param context   内容
     * @param x         横坐标
     * @param y         纵坐标
     */
    public void drawString(Font font,String context,int x,int y){
        g.setFont(font);
        g.drawString(context,x,y);
    }

    /**
     * 获取合成后的海报
     * @param formatName    图片类型 jpg png
     * @param out           指定输出流
     * @throws IOException
     */
    public void getPoster(String formatName, OutputStream out) throws IOException {
        g.dispose();
        ImageIO.write(poster,formatName,out);
    }

    /**
     * 获取合成后的海报
     * @param formatName    图片类型 jpg png
     * @return
     * @throws IOException
     */
    public InputStream getPoster(String formatName) throws IOException {
        g.dispose();
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(poster,formatName,os);
        ByteArrayInputStream input = new ByteArrayInputStream(os.toByteArray());
        return input;
    }


    /**
     * 解决ImageIo直接读取图片可能出现的红色蒙版
     *
     * @param image
     * @return
     */
    public static BufferedImage toBufferedImage(Image image) {
        if (image instanceof BufferedImage) {
            return (BufferedImage) image;
        }
        // This code ensures that all the pixels in the image are loaded
        image = new ImageIcon(image).getImage();
        BufferedImage bimage = null;
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        try {
            int transparency = Transparency.OPAQUE;
            GraphicsDevice gs = ge.getDefaultScreenDevice();
            GraphicsConfiguration gc = gs.getDefaultConfiguration();
            bimage = gc.createCompatibleImage(image.getWidth(null), image.getHeight(null), transparency);
        } catch (HeadlessException e) {
            // The system does not have a screen
        }
        if (bimage == null) {
            // Create a buffered image using the default color model
            int type = BufferedImage.TYPE_INT_RGB;
            bimage = new BufferedImage(image.getWidth(null), image.getHeight(null), type);
        }
        // Copy image to buffered image
        Graphics g = bimage.createGraphics();
        // Paint the image onto the buffered image
        g.drawImage(image, 0, 0, null);
        g.dispose();
        return bimage;
    }

}
