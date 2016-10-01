package com.virohtus.dht.transport;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class TestServer {


    private final AsynchronousServerSocketChannel server;

    public TestServer(ExecutorService executorService, SocketAddress socketAddress) throws IOException {
        AsynchronousChannelGroup group = AsynchronousChannelGroup.withCachedThreadPool(executorService, 3);
        server = AsynchronousServerSocketChannel.open(group).bind(socketAddress);
    }

    public void start() {
        try {
            while (true) {
                Future<AsynchronousSocketChannel> acceptFuture = server.accept();
                AsynchronousSocketChannel socketChannel = acceptFuture.get();
                ByteBuffer byteBuffer = ByteBuffer.allocate(256);
                socketChannel.read(byteBuffer, null, new CompletionHandler<Integer, Object>() {
                    @Override
                    public void completed(Integer result, Object attachment) {

                    }

                    @Override
                    public void failed(Throwable exc, Object attachment) {

                    }
                });
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
}
