package com.videoutil.utils;

import java.io.IOException;
import java.util.List;

/**
 * ffmpeg:视频处理
 */
public class VideoUtil {

    //判断是否是liunx环境
    private static boolean osVersion;

    static {
        String os = System.getProperty("os.name").toLowerCase();
        osVersion = os.indexOf("linux") >= 0;
    }

    //执行命令
    private static void proc(String args) {
        try {
            Process proc;
            if (osVersion) {
                //Liunx环境
                proc = Runtime.getRuntime().exec(new String[]{"sh", "-c", args});
            } else {
                //Windows环境
                proc = Runtime.getRuntime().exec(args);
            }
            // 标准错误流（必须写在 waitFor 之前）
            StreamGobbler errorGobbler = new StreamGobbler(proc.getErrorStream(), "ERROR");
            errorGobbler.start();//  kick  off  stderr
            // 标准输入流（必须写在 waitFor 之前）
            StreamGobbler outGobbler = new StreamGobbler(proc.getInputStream(), "STDOUT");
            outGobbler.start();
            proc.waitFor();
            proc.destroy();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 视频去掉声音
     *
     * @param videoUrl  原视频
     * @param silentUrl 处理后的视频地址
     */
    public static void silent(String videoUrl, String silentUrl) {
        proc("ffmpeg -i " + videoUrl + " -c:v copy -an " + silentUrl);
    }

    /**
     * 视频+音频
     *
     * @param videoUrl 原视频
     * @param audio    要合并的音频
     * @param outVideo 处理后的视频地址
     */
    public static void compound(String videoUrl, String audio, String outVideo) {
        proc("ffmpeg -i " + videoUrl + " -i " + audio + " -c:v copy -c:a aac -strict experimental -map 0:v:0 -map 1:a:0 " + outVideo);
    }

    /**
     * 音频+音频 ==> 拼接
     *
     * @param audioUrls 音频地址
     * @param outAudio  处理后的音频地址
     */
    public static void audioConcat(List<String> audioUrls, String outAudio) {
        String proc = "ffmpeg -i \"concat:";
        for (String audioUrl : audioUrls) {
            proc += audioUrl + "|";
        }
        proc = proc.substring(0, proc.length() - 1);
        proc += "\" -acodec copy " + outAudio;
        proc(proc);
    }

    /**
     * 音频+音频 ==> 重叠
     *
     * @param audioUrls 音频地址
     * @param outAudio  处理后的音频地址
     */
    public static void audioOverlap(List<String> audioUrls, String outAudio) {
        //重叠合成音频
        String proc = "ffmpeg ";
        for (String audioFileName : audioUrls) {
            proc += " -i " + audioFileName;
        }
        proc += " -filter_complex amix=inputs=" + audioUrls.size() + ":duration=first:dropout_transition=2 -f mp3 " + outAudio;
        proc(proc);
    }


}
