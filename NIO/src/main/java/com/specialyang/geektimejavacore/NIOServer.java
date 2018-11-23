package com.specialyang.geektimejavacore;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
/**
 * Created by Fan Yang in 2018/11/23 10:01 AM.
 */
public class NIOServer extends Thread {

    private Selector selector;

    private final int PORT = 5555;

    private ServerSocketChannel server;

    private ByteBuffer sendBuffer;

    public NIOServer() throws IOException {
        //开启大管家
        selector = Selector.open();
        server = ServerSocketChannel.open();
        server.bind(new InetSocketAddress(PORT));
        server.configureBlocking(false);
        //向大管家注册，并告诉它我感兴趣 新的连接请求
        server.register(selector, SelectionKey.OP_ACCEPT);
        System.out.println("服务器准备就绪，监听端口是：" +
                ((InetSocketAddress) (server.getLocalAddress())).getPort());
        sendBuffer = ByteBuffer.allocate(1024);
    }

    @Override
    public void run() {
        while (true) {
            try {
                //阻塞等待已准备好的管道
                selector.select();
                //选择IO准备的管道的key
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectionKeys.iterator();
                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    //处理该管道
                    process(key);
                    iterator.remove();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void process(SelectionKey key) throws IOException {
        if (key.isAcceptable()) {
            ServerSocketChannel server = (ServerSocketChannel) key.channel();
            SocketChannel client = server.accept();
            //证明使用的是单线程，且用的控制连接的端口，并没有新分配端口
            System.out.println(((InetSocketAddress) (client.getLocalAddress())).getPort());
            sendBuffer = ByteBuffer.wrap("连接成功！".getBytes());
            client.write(sendBuffer);
            client.close();
        }
    }


    public static void main(String[] args) {
        try {
            new NIOServer().run();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
