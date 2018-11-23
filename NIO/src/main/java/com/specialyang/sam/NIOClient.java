package com.specialyang.sam;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;

import static java.lang.System.exit;

/**
 * Created by Fan Yang in 2018/11/22 9:32 AM.
 *
 * IO方式只是一端的收发数据的过程，所以可以任意搭配
 * 所以客户端处也可以使用BIO，这里用NIO仅仅为了学习
 */
public class NIOClient {

    //客户端发送缓冲区
    private ByteBuffer sendBuffer = ByteBuffer.allocate(1024);
    //客户端接收缓冲区
    private ByteBuffer receiveBuffer = ByteBuffer.allocate(1024);

    private InetSocketAddress serverAddress = new InetSocketAddress("127.0.0.1", 8089);

    private SocketChannel client;

    private Selector selector;

    public NIOClient() throws IOException {
        client = SocketChannel.open();
        client.configureBlocking(false);
        client.connect(serverAddress);
        selector = Selector.open();
        //告诉选择器我这个通道是要连接服务器
        client.register(selector, SelectionKey.OP_CONNECT);
    }

    public void clientServer() throws IOException {
        if (client.isConnectionPending()) {
            client.finishConnect();
            System.out.println("已经连接到客户端，请在控制台注册你的信息！");
            //写之前，告诉管家，我要干什么
            client.register(selector, SelectionKey.OP_WRITE);
        }
        Scanner input = new Scanner(System.in);
        while (input.hasNext()) {
            String msg = input.nextLine();
            if (msg.trim().equals("Exit")) {
                exit(0);
            } else if (!(msg.trim().equals(""))) {
                handleInput(msg);
            }
        }
    }

    private void handleInput(String msg) throws IOException {
            int len = selector.select();
            if (len == 0) {
                return;
            }
            //拿到该管道的所有已发生感兴趣事件的key
            Set<SelectionKey> keys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = keys.iterator();
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                if (key.isWritable()) {
                    sendBuffer.clear();
                    sendBuffer.put(msg.getBytes());
                    sendBuffer.flip();
                    client.write(sendBuffer);
                    //写完之后它要读，读的话注册事件
                    client.register(selector, SelectionKey.OP_READ);
                } else if (key.isReadable()) {
                    receiveBuffer.clear();
                    int size = client.read(receiveBuffer);
                    if (size > 0) {
                        System.out.println("服务器返回的信息为：" + new String(receiveBuffer.array(), 0, size));
                    }
                    client.register(selector, SelectionKey.OP_WRITE);
                }
            }

    }

    public static void main(String[] args) throws IOException {
        new NIOClient().clientServer();

    }
}
