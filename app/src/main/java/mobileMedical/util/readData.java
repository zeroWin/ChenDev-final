package mobileMedical.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 * Created by lenovo on 2016/1/10.
 */
public class readData {
    public static double[] readFileByChars(String fileName) {
        StringBuilder tempB = new StringBuilder();
        String temp = "";
        double [] res ;
        double r;
        File file = new File(fileName);
        Reader reader = null;
        try {
            System.out.println("以字符为单位读取文件内容，一次读一个字节：");
            // 一次读一个字符
            reader = new InputStreamReader(new FileInputStream(file));
            int tempchar;
            while ((tempchar = reader.read()) != -1) {
                // 对于windows下，\r\n这两个字符在一起时，表示一个换行。
                // 但如果这两个字符分开显示时，会换两次行。
                // 因此，屏蔽掉\r，或者屏蔽\n。否则，将会多出很多空行。
                if (((char) tempchar) != '\r') {
                }
                tempB.append((char)tempchar);
            }
            temp = tempB.toString();
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
        String[] resTemp = temp.split("\n");
        res = new double[resTemp.length];
        for(int i=0;i<resTemp.length;i++){
            r = Double.valueOf(resTemp[i].toString());
            res[i] = r;
        }
        return res;
    }
}
