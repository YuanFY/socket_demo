package socket.server.task;

import socket.common.utils.CommonUtils;
/**
 * @Description: 向客户端打印公共信息
 * @author yuanfy
 * @date 2017年5月25日 上午10:57:00 
 * @version 6.2
 */
public class PrintOutClientTask implements Runnable{

    @Override
    public void run() {
        while (true) {
            if (CommonUtils.isPrint) {
                String message = CommonUtils.removeMessage();
                if (CommonUtils.queue.size() == 0) {
                    CommonUtils.isPrint = false;
                }
                //向每个客户端都打印公共消息
                for (SendMessageToClientTask task : CommonUtils.taskList) {
                    task.sendMessage(message);
                }
            }
        }
    }
}
