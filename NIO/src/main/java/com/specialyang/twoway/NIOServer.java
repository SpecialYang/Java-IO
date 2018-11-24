package com.specialyang.twoway;

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
 * Created by Fan Yang in 2018/11/22 8:52 AM.
 */
public class NIOServer {

    private ServerSocketChannel serverSocketChannel;

    //服务器发送缓冲区
    private ByteBuffer sendBuffer = ByteBuffer.allocate(1024);
    //服务器接收缓冲区
    private ByteBuffer receiveBuffer = ByteBuffer.allocate(1024);

    private Selector selector;
    public static final int PORT = 8888;

    private String msg;

    public NIOServer() throws IOException {
        serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.bind(new InetSocketAddress(PORT));
        serverSocketChannel.configureBlocking(false);
        selector = Selector.open();
        //将该管道注册到selector上，并指定自己感兴趣的事情
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        System.out.println("NIO服务器已经初始化完毕！监听端口为：" + PORT);
    }

    //同步非阻塞，所以需要有一个轮询监听机制
    public void listen() throws IOException {
        while (true) {
            int eventSize = selector.select();
            if (eventSize == 0) {
                continue;
            }
            //这里返回的set集合里面每一个eventKey对应一个详细的事件内容
            Set<SelectionKey> eventKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = eventKeys.iterator();
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                handleKey(key);
                iterator.remove();
            }

        }
    }


    private void handleKey(SelectionKey key) throws IOException {
        if (key.isAcceptable()) {
            SocketChannel client = serverSocketChannel.accept();
            client.configureBlocking(false);
            //这里改变注册器上面的事件状态
            client.register(selector, SelectionKey.OP_READ);
        } else if (key.isReadable()) {
            //获取与此事件有关的管道
            SocketChannel client = (SocketChannel) key.channel();
            receiveBuffer.clear();
            int len = client.read(receiveBuffer);
            if (len > 0) {
                //拿之前锁以下
                receiveBuffer.flip();
                msg = new String(receiveBuffer.array(), 0, len);
                System.out.println("客户端发来：" + msg);
                client.register(selector, SelectionKey.OP_WRITE);
            }
        } else if (key.isWritable()) {
            SocketChannel client = (SocketChannel) key.channel();
            sendBuffer.clear();
            sendBuffer.put(("收到" + msg).getBytes());
            sendBuffer.flip();
            client.write(sendBuffer);
            client.register(selector, SelectionKey.OP_READ);
        }
    }

    public static void main(String[] args) {
        try {
            new NIOServer().listen();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
