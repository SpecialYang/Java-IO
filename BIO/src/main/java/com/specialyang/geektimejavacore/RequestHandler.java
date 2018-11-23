package com.specialyang.geektimejavacore;

import java.io.PrintWriter;
import java.net.Socket;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by Fan Yang in 2018/11/22 9:18 PM.
 *
 * 处理请求类
 */
public class RequestHandler extends Thread{

    private Socket socket;

    public RequestHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        //try-with-resources 方式
        try {
            /*
                打印带有格式的文本的输出流
                若不指定自动刷新，则遇到换行符之类的不会自动flush
             */
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            while (true) {
                out.println(new Date() + " Hello World!");
                System.out.println(new Date() + " 发送！");
                TimeUnit.SECONDS.sleep(5);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
