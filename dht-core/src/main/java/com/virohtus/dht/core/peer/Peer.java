package com.virohtus.dht.core.peer;

import com.virohtus.dht.core.DhtProtocol;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class Peer {

    private final ExecutorService executorService;
    private final AsynchronousSocketChannel socketChannel;
    private Future listenFuture;

    public Peer(ExecutorService executorService, AsynchronousSocketChannel socketChannel) {
        this.executorService = executorService;
        this.socketChannel = socketChannel;
    }

    public void listen() {
        listenFuture = executorService.submit(() -> {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    ByteBuffer byteBuffer = ByteBuffer.allocate(DhtProtocol.BUFFER_SIZE);
                    int bytesRead = socketChannel.read(byteBuffer).get();
                    if(bytesRead == 0) {
                        continue;
                    }
                    if(bytesRead < 0) {
                        // todo error
                        return;
                    }
                    byte[] data = new byte[bytesRead];
                    System.arraycopy(byteBuffer.array(), 0, data, 0, bytesRead);
                    System.out.println(new String(data));
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        });
    }
}
