package code.others;

import org.junit.Test;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class OpenFileWithApp {
    @Test
    public void useProcessBuilder() throws IOException{
        List<String> commands = new ArrayList<>();
        commands.add("E:\\Tencent\\WeChat\\WeChat.exe");
        commands.add("C:/Users/lydia/Desktop/readme.txt");
        new ProcessBuilder(commands).start();
    }

    @Test
    public void useAWTDesktop() throws IOException{
        Desktop.getDesktop().open(new File("C:/Users/lydia/Desktop/readme.txt"));
    }

    @Test
    public void useRuntimeExec() throws IOException{
//        WPS文字--------Runtime.getRuntime().exec("cmd /c start wps")
//        WPS表格--------Runtime.getRuntime().exec("cmd /c start et")
//        WPS演示--------Runtime.getRuntime().exec("cmd /c start wpp")
//        Office Word---Runtime.getRuntime().exec("cmd /c start winword")
//        Office Excel--Runtime.getRuntime().exec("cmd /c start excel")
        /*
         * 若打开的目录或文件名中不包含空格,就用下面的方式
         */
        Runtime.getRuntime().exec("cmd /c start E:\\Tencent\\WeChat\\WeChat.exe");
        Runtime.getRuntime().exec("cmd /c start C:/Users/lydia/Desktop/readme.txt");

        /*
         * 借助本地安装程序打开
         * 若打开的目录或文件名中包含空格,它就无能为力了..不过本地程序的安装目录允许含空格
         */
        String etCommand = "D:/Program Files/WPS/8.1.0.3526/office6/et.exe";
        String filePath = "D:/mylocal/测试用例.xls";
        Runtime.getRuntime().exec(etCommand + " " + filePath);
    }

    @Test
    public void test() {
        String path = "D:\\public.bat";
        Runtime run = Runtime.getRuntime();
        try {
            // run.exec("cmd /k shutdown -s -t 3600");
            Process process = run.exec("cmd.exe /k start " + path);
            InputStream in = process.getInputStream();
            while (in.read() != -1) {
                System.out.println(in.read());
            }
            in.close();
            process.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }
     }
}
