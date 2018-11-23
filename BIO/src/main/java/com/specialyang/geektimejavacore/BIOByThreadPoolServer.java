package com.specialyang.geektimejavacore;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by Fan Yang in 2018/11/22 9:12 PM.
 */
public class BIOByThreadPoolServer extends Thread{

    //服务端socket
    private ServerSocket serverSocket;
    //线程池
    private Executor executor;


    public BIOByThreadPoolServer() {
        executor = Executors.newFixedThreadPool(10);
    }
    private final int PORT = 8099;

    public int getPort() {
        //获取该套接字监听的端口
        return serverSocket.getLocalPort();
    }

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("服务器已启动，监听端口：" + getPort());
            while (true) {
                //阻塞等待客户端连接，接收到客户端请求后，新创建一个socket用于与客户端交互
                Socket socket = serverSocket.accept();
                System.out.println("与客户端通信的分配的端口号为：" + socket.getPort());
                //提交给任务执行框架执行
                executor.execute(new RequestHandler(socket));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (serverSocket != null) {
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {
        BIOByThreadPoolServer server = new BIOByThreadPoolServer();
        server.start();
    }
}
