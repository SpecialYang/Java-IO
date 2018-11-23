package com.specialyang.demo;

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
 * Created by Fan Yang in 2018/11/21 上午9:19.
 */
public class NIOServer {

    private int port = 8080;
    private InetSocketAddress address;
    private Selector selector;

    private ServerSocketChannel server;

    public NIOServer(int port) {
        try {
            this.port = port;
            address = new InetSocketAddress(this.port);

            //要想富，先修路，开启高速公路
            server = ServerSocketChannel.open();
            server.bind(address);
            //默认阻塞，要手动设置为阻塞
            server.configureBlocking(false);

            selector = Selector.open();
            //Option的简称
            server.register(selector, SelectionKey.OP_ACCEPT);

            System.out.println("服务器准备就绪，监听端口是：" + this.port);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void listen() {
        try {
            while (true) {
                //有多少个人在排队
                int wait = this.selector.select();
                if (wait == 0) {
                    continue;
                }
                Set<SelectionKey> keys = this.selector.selectedKeys();
                Iterator<SelectionKey> iterator = keys.iterator();
                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    //处理拿到的key
                    process(key);
                    iterator.remove();
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void process(SelectionKey key) throws IOException {
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        if (key.isAcceptable()) {
            ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
            SocketChannel client = serverSocketChannel.accept();
            client.configureBlocking(false);
            client.register(selector, SelectionKey.OP_READ);

        } else if (key.isReadable()) {
            SocketChannel client = (SocketChannel) key.channel();
            int len = client.read(byteBuffer);
            if (len > 0) {
                byteBuffer.flip();
                String content = new String(byteBuffer.array(), 0, len);
                System.out.println(content);
                client.register(selector, SelectionKey.OP_WRITE);
            }
            byteBuffer.clear();
        } else if (key.isWritable()) {
            SocketChannel client = (SocketChannel) key.channel();
            byteBuffer = ByteBuffer.wrap("Hello World".getBytes());
            client.write(byteBuffer);
        }
    }

    public static void main(String[] args) {
        new NIOServer(8080).listen();
    }

}
