package com.specialyang.sam;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
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
    private int port;
    //消息会话列表
    private Map<SelectionKey, String> sessionMsgs = new HashMap<>();


    public NIOServer(int port) throws IOException {
        this.port = port;
        serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.socket().bind(new InetSocketAddress(port));
        serverSocketChannel.configureBlocking(false);
        selector = Selector.open();
        //将该管道注册到selector上，并指定自己感兴趣的事情
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        System.out.println("NIO服务器已经初始化完毕！监听端口为：" + port);
    }

    //非阻塞的，服务端接收线程没有在阻塞等待，所以需要有一个监听机制
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
            int len = client.read(receiveBuffer);

            if (len > 0) {
                //拿之前锁以下
                receiveBuffer.flip();
                String msg = new String(receiveBuffer.array(), 0, len);
                System.out.println("收到：" + msg);
                //建立一个根据key
                sessionMsgs.put(key, msg);
                client.register(selector, SelectionKey.OP_WRITE);
            }
        } else if (key.isWritable()) {
            if (sessionMsgs.containsKey(key)) {
                SocketChannel client = (SocketChannel) key.channel();
                sendBuffer.clear();
                sendBuffer.put((sessionMsgs.get(key)
                        + ",你好，你的请求已经处理完毕").getBytes());
                //业务处理完备
                sendBuffer.flip();
                client.write(sendBuffer);
            }
        }
    }

    public static void main(String[] args) {
        try {
            new NIOServer(8089).listen();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
