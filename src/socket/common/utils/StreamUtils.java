package socket.common.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import org.apache.log4j.Logger;

/**
 * @Description: 流相关工具类
 * @author yuanfy
 * @date 2017年5月25日 下午2:17:38 
 * @version 6.2
 */
public class StreamUtils {

    
    private static Logger log = Logger.getLogger(StreamUtils.class);
    
    public static void close(Socket socket) {
        if (socket != null) {
            try {
                socket.close();
            }
            catch (IOException e) {
                log.error("关闭socket失败：" + e);
            }
        }
    }
    
    public static void close(PrintWriter printWriter) {
        if (printWriter != null) {
            printWriter.close();
        }
    }
    
    public static void close(BufferedReader bufferedReader) {
        if (bufferedReader != null) {
            try {
                bufferedReader.close();
            }
            catch (IOException e) {
                log.error("关闭bufferedReader失败：" + e);
            }
        }
    }
}
