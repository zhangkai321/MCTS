package code.others;

import java.io.IOException;
import org.junit.Test;

public class OpenApp {
    @Test
    public void test1() throws IOException {
        Runtime.getRuntime().exec("E:\\Notepad++\\notepad++.exe"); // 打开酷狗

        Runtime.getRuntime().exec("C:/Windows/System32/cmd.exe /k start E:\\Notepad++\\notepad++.exe"); // 通过cmd窗口执行命令
        Runtime.getRuntime().exec("C:/Windows/System32/cmd.exe /k start C:\\Users\\lydia\\Desktop\\界面.html"); // 通过cmd命令打开一个网页
        Runtime.getRuntime().exec("C:/Windows/System32/cmd.exe /k mkdir C:\\Users\\lydia\\Desktop\\java键的1"); // 通过cmd创建目录用两个反斜杠
        Runtime.getRuntime().exec("C:/Windows/System32/cmd.exe /k mkdir C:\\Users\\lydia\\Desktop\\java键的2"); // 通过cmd创建目录用两个反斜杠
        Runtime.getRuntime().exec("C:/Windows/System32/cmd.exe /c calc ");// 通过cmd打开计算器
        Runtime.getRuntime().exec("C:/Windows/System32/cmd.exe /c osk");// 通过屏幕软键盘
    }

    @Test
    public void  test2(){
        Runtime rt = Runtime.getRuntime();
        String fileLac;
        try {
            fileLac = "E:\\Notepad++\\notepad++.exe";// 要调用的程序路径
            rt.exec(fileLac);
        }
        catch (Exception e) {
            System.out.println("open failure");
        }
    }
}
