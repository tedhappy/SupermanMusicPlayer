package why.supermanmusic.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;

import why.supermanmusic.bean.LyricBean;


public class LyricParser {

    private static BufferedReader bfr;

    public static ArrayList<LyricBean> parseLyric(File file) {
        //创建歌词集合
        ArrayList<LyricBean> lyricBeens = new ArrayList<LyricBean>();
        //判断当前file是否存在
        if (file == null || !file.exists()) {
            //如果不存在 创建一条错误的歌词
            lyricBeens.add(new LyricBean(0, "未找到对应的歌词文件"));
            return lyricBeens;
        }
        //如果存在 解析歌词
        try {
            //使用reader 1：可以读一行  2：指定编码方式为gbk
            bfr = new BufferedReader(new InputStreamReader(new FileInputStream(file), "gbk"));
            //读取一行
            String line = bfr.readLine();//[01:21.30][03:00.44]谁能年少不痴狂独自闯荡
            while (line != null) {
                //解析当前行返回当前行的歌词集合
                ArrayList<LyricBean> lyrics = parseLine(line);
                lyricBeens.addAll(lyrics);
                //读取下一行
                line = bfr.readLine();
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bfr != null) {
                try {
                    bfr.close();
                    bfr = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        //排序集合
        Collections.sort(lyricBeens);
        //返回集合
        return lyricBeens;
    }
    //解析一行歌词  [01:21.30 [03:00.44 [03:00.44 [03:00.44 谁能年少不痴狂独自闯荡
    private static ArrayList<LyricBean> parseLine(String line) {
        //创建歌词集合
        ArrayList<LyricBean> lyrics = new ArrayList<LyricBean>();
        //解析歌词添加到集合中
        String[] arr = line.split("]");
        //获取歌词
        String content = arr[arr.length-1];
        for (int i = 0; i < arr.length-1; i++) {
            int time = parseTime(arr[i]);
            //创建歌词条目
            LyricBean lyricbean = new LyricBean(time,content);
            lyrics.add(lyricbean);
        }
        //返回集合
        return lyrics;
    }
    //解析时间 毫秒值  [01 21.30
    private static int parseTime(String s) {
        String[] arr = s.split(":");
        //获取分钟字符串
        String minS = arr[0];//[01
        //获取秒数字符串
        String secS = arr[1];//21.30

        int min = Integer.parseInt(minS.substring(1));//01
        float sec = Float.parseFloat(secS);//21.30
        return (int) (min*60*1000+sec*1000);
    }
}
