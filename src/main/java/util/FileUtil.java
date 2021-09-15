package util;

import java.io.*;
import java.nio.charset.StandardCharsets;
//用来读写文件
public class FileUtil {
    public static String readFile(String path) {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(path), StandardCharsets.UTF_8));
            String str = "";
            for (String buf = ""; (buf = br.readLine()) != null;) {
                str += buf + "\n";
            }
            return str;
        } catch (IOException e) {
            System.out.println("输入文件异常");
            e.printStackTrace();
        }
        return null;
    }
    public static void writeFile(String path, String data, int count) {
        try {
            PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path), StandardCharsets.UTF_8)));
            out.write("Total: " + count + "\n");
            out.write(data);
            out.close();
        } catch (FileNotFoundException e) {
            System.out.println("输出文件异常");
            e.printStackTrace();
        }
    }
}
