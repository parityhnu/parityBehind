package com.binqing.parity.py;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class ToUsePy {

    public static void catchGoods(String index, String good, String sort) {
        try {

            long currentTime = System.currentTimeMillis();
            File directory = new File("");//设定为当前文件夹
            String dirPath = directory.getAbsolutePath();//获取绝对路径
            String pyPath = "\\src\\main\\java\\com\\binqing\\parity\\py\\getGoods";
            String exe = "python";
            String command = dirPath + pyPath;
            String[] cmdArr = new String[] {exe, command, good, index, sort};
            Process process = Runtime.getRuntime().exec(cmdArr);
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    process.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                System.out.println(line);
            }
            in.close();
            process.waitFor();
            long currentTime2 = System.currentTimeMillis();
            System.out.println(currentTime2-currentTime);

        }catch (IOException e){
            System.out.println(e);
        }catch (InterruptedException e2){
            System.out.println(e2);
        }
    }

    public static void main(String args[]){

    }
}
