package socket.server.task;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import org.apache.log4j.Logger;

import socket.common.utils.CommonUtils;
import socket.common.utils.StreamUtils;

/**
 * @Description: 与客户端交互的线程
 * @author yuanfy
 * @date 2017年5月25日 下午2:37:18 
 * @version 6.2
 */
public class SendMessageToClientTask implements Runnable{

    private static Logger log = Logger.getLogger(SendMessageToClientTask.class);  
    
    private Socket socket;//定义socket
    
    private PrintWriter out;//打印流
    
    private BufferedReader bufferedReader ;//字符输入流
    
    private String name;//定义全局用户名
    
    /**
     * 定义有参构造函数 通过socket获取打印流和输入流
     * @param socket
     * @throws IOException 
     */
    public SendMessageToClientTask(Socket socket) throws IOException {
        this.socket = socket;
        out = new PrintWriter(socket.getOutputStream(), true);//设置为true自动刷新缓冲流
        bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out.println("成功连接,欢迎进入Holle Kitty的聊天室，请输入你的名字：");
    }

    /**
     * 1、接收客户端的消息后，根据消息内容发送具体的消息过去。
     * 2、缓存用户和对应的用户线程
     * 3、将公共消息存至队列，通过队列发送给每个客户端。
     * 4、客户端退出后要清理缓存中对应的用户和线程
     */
    @Override
    public void run() {
        try {
            int count = 0;
            //1、接收客户端的消息后，根据消息内容发送具体的消息过去。
            String content = bufferedReader.readLine();
            while (!"bye".equals(content)) {
                if ((count++) == 0) {//2、缓存用户和对应的用户线程
                    name = content;
                    CommonUtils.taskList.add(this);
                    CommonUtils.addUser(content);
                    
                    String clientContent = name + ",你好！可以开始聊天了...";
                    out.println(clientContent);
                    log.info(clientContent);
                    
                    String publicMessage = "Client(" + name + ")进入聊天室...";
                    CommonUtils.putMessage(publicMessage);
                } else if ("showuser".equals(content)) {//打印用户列表
                    out.println(CommonUtils.getUserListInfo());
                } else {//3、将公共消息存至队列，通过队列发送给每个客户端。
                    String publicMessage = "Client(" + name + ") say : " + content;
                    log.info(publicMessage);
                    CommonUtils.putMessage(publicMessage);
                }
                content = bufferedReader.readLine();
            }
            //退出客户端
            out.println("byeClient");
        }
        catch (IOException e) {
            if (e.getMessage().contains("Connection reset")) {
                log.error("Client(" + name + ")退出了聊天室!");
            } else {
                log.error("获取客户端信息出错：" + e);
            }
        }
        finally {
            //4、客户端退出后要清理缓存中对应的用户和线程
            CommonUtils.taskList.remove(this);
            CommonUtils.removeUser(name);
            CommonUtils.putMessage("Client(" + name + ")退出了聊天室!");
            StreamUtils.close(socket);
            StreamUtils.close(out);
            StreamUtils.close(bufferedReader);
        }
    }
    
    /**
     * @Description: 发送公共信息
     * @param message
     * @author yuanfy
     * @date 2017年5月25日 上午9:54:23 
     * @version 6.2
     */
    public void sendMessage(String message) {
        out.println(message);
    }
}
