package socket.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

import socket.common.utils.CommonUtils;
import socket.server.task.PrintOutClientTask;
import socket.server.task.SendMessageToClientTask;

/**
 * @Description: socket服务端
 * @author yuanfy
 * @date 2017年5月25日 上午8:32:03 
 * @version 6.2
 */
public class Server extends ServerSocket{

    private static Logger log = Logger.getLogger(Server.class);    
    
    //定义缓存线程池
    private static Executor threadPool = Executors.newCachedThreadPool();
    
    public Server(int port) throws IOException {
        super(port);
    }
    
    /**
     * @Description: 服务端启动接收和发送
     * @author yuanfy
     * @date 2017年5月25日 上午8:46:11 
     * @version 6.2
     */
    public void run() {
        try {
            //1、创建向客户端打印信息线程
            new Thread(new PrintOutClientTask()).start();
            //2、主线程 监听客户端请求，并启动线程处理请求
            while (true) {
                Socket socket = this.accept();//监听客户端请求
                threadPool.execute(new SendMessageToClientTask(socket));//启动线程处理请求
            }
        }
        catch (IOException e) {
            log.error("接收客户端请求出错：", e);
        }
        finally {
            try {
                close();
            }
            catch (IOException e) {
                log.error("关闭ServerSocket出错：", e);
            }
        }
    }

    @SuppressWarnings("resource")
	public static void main(String[] args) {
    	try {
            Server server = new Server(CommonUtils.getPort());//创建ServerSocket
            server.run();
        }
        catch (IOException e) {
            if (e.getMessage().contains("Address already in use")) {
                log.error("服务端已经启动，若需要再次启动请修改端口！");
            }
        }
	}
}
