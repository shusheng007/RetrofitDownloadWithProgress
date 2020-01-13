package top.ss007.library;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by shusheng007
 *
 * @author Ben.Wang
 * @modifier
 * @createDate 2019/5/31 13:54
 * @description
 */
public final class FileUtil {

    private FileUtil() {
    }

    public static File writeFile(String filePath, InputStream input) {
        if (input == null)
            return null;
        File file = new File(filePath);
        try (FileOutputStream fos = new FileOutputStream(file);
             InputStream ins = input) {
            byte[] b = new byte[1024];
            int len;
            while ((len = ins.read(b)) != -1) {
                fos.write(b, 0, len);
            }
            fos.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }
}
