package com.specialyang.twoway;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;
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

    private SocketChannel client;

    private Selector selector;

    public NIOClient(int port) throws IOException {
        client = SocketChannel.open();
        client.configureBlocking(false);
        client.connect(new InetSocketAddress("127.0.0.1", port));
        selector = Selector.open();
        //告诉选择器我这个通道是要连接服务器
        client.register(selector, SelectionKey.OP_CONNECT);
    }

    public void clientServer() throws IOException {
        while (true) {
            selector.select();
            Set<SelectionKey> keys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = keys.iterator();
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                process(key);
                iterator.remove();
            }
        }
    }

    private void process(SelectionKey key) throws IOException {
        if (key.isConnectable()) {
            System.out.println("连接服务器成功！");
            SocketChannel client = (SocketChannel) key.channel();
            if (client.isConnectionPending()) {
                client.finishConnect();
            }
            client.register(selector, SelectionKey.OP_WRITE);
        } else if (key.isReadable()) {
            SocketChannel client = (SocketChannel) key.channel();
            receiveBuffer.clear();
            int len;
            if ((len = client.read(receiveBuffer)) > 0) {
                String msg = new String(receiveBuffer.array(), 0, len);
                System.out.println("服务器发来：" + msg);
                client.register(selector, SelectionKey.OP_WRITE);
            }
        } else if (key.isWritable()) {
            SocketChannel client = (SocketChannel) key.channel();
            Scanner input = new Scanner(System.in);
            input.hasNext();
            String sendText = input.nextLine();
            sendBuffer.clear();
            sendBuffer.put(sendText.getBytes());
            sendBuffer.flip();
            client.write(sendBuffer);
            client.register(selector, SelectionKey.OP_READ);
        }

    }

    public static void main(String[] args) throws IOException {
        new NIOClient(NIOServer.PORT).clientServer();

    }
}
