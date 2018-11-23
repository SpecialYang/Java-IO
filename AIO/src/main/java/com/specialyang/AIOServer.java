package com.specialyang;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

/**
 * Created by Fan Yang in 2018/11/21 7:32 PM.
 */
public class AIOServer {

    private final int PORT = 5556;

    private ByteBuffer sendBuffer;

    public void listen() throws IOException {
        AsynchronousServerSocketChannel server = AsynchronousServerSocketChannel.open();
        server.bind(new InetSocketAddress(PORT));
        /*
            初始化一个异步操作，用于接收新的IO连接请求，不阻塞当前线程
            第一个参数是附属品，用于IO连接建立完成后，由处理器使用
            第二个就是回调接口，用于目标事件完成后调用
         */
        server.accept(null, new CompletionHandler<AsynchronousSocketChannel, Object>() {
            /**
             * @param result 事件完成后的结果
             * @param attachment 事件初始化时的附属品
             */
            @Override
            public void completed(AsynchronousSocketChannel result, Object attachment) {
                try {
                    System.out.println(((InetSocketAddress) (result.getLocalAddress())).getPort());
                    sendBuffer = ByteBuffer.wrap("AIO方式连接成功！".getBytes());
                    result.write(sendBuffer);
                    result.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void failed(Throwable exc, Object attachment) {
                throw new UnsupportedOperationException("不支持该操作！");
            }
        });
    }

    public static void main(String[] args) {
        try {
            new AIOServer().listen();
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
