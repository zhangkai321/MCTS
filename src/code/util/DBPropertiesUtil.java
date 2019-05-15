package code.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class DBPropertiesUtil {
    private static Properties prop;
    static {
        try{
            File file=new File("");
            String abspath=file.getAbsolutePath() + "/src/";
            prop = new Properties();
            prop.load(new FileInputStream(abspath + "database.properties"));
        }
        catch (FileNotFoundException e) {
            System.out.println("加载配置文件失败");
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static String getDriverProperties(String key){
        return prop.getProperty(key + "driverClass");
    }
    public static String getUrlProperties(String key) {
        return prop.getProperty(key + "url");
    }
    public static String getUsernameProperties(String key) {
        return prop.getProperty(key + "username");
    }
    public static String getPasswordProperties(String key){
        return prop.getProperty(key + "password");
    }
}
