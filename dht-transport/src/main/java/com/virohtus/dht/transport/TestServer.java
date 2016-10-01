package com.virohtus.dht.transport;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class TestServer {

    private Selector selector;
    private ServerSocketChannel serverSocketChannel;
    private SelectionKey selectionKey;

    public TestServer(InetSocketAddress inetSocketAddress) throws IOException {
        selector = Selector.open();
        serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.bind(inetSocketAddress);
        serverSocketChannel.configureBlocking(false);
        int ops = serverSocketChannel.validOps();
        selectionKey = serverSocketChannel.register(selector, ops, null);
    }

    public void serve() throws IOException {
        System.out.println("server started: " + serverSocketChannel.getLocalAddress());
        while(true) {
            selector.select();

            Iterator<SelectionKey> selectionKeyIterator = selector.selectedKeys().iterator();
            while(selectionKeyIterator.hasNext()) {
                SelectionKey key = selectionKeyIterator.next();
                if(key.isAcceptable()) {
                    handleAcceptEvent();
                } else if(key.isReadable()) {
                    handleReadEvent(key);
                }
                selectionKeyIterator.remove();
            }
        }
    }

    private void handleAcceptEvent() {
        try {
            SocketChannel socketChannel = serverSocketChannel.accept();
            socketChannel.configureBlocking(false);
            socketChannel.register(selector, SelectionKey.OP_READ);
            System.out.println("client connected: " + socketChannel.getLocalAddress());
        } catch (IOException e) {
        }
    }

    private void handleReadEvent(SelectionKey key) {
        SocketChannel socketChannel = (SocketChannel) key.channel();
        ByteBuffer byteBuffer = ByteBuffer.allocate(256);
        try {
            int bytesRead = socketChannel.read(byteBuffer);
            if(bytesRead < 0) {
                System.out.println("received eof");
                return;
            }
            byte[] received = byteBuffer.array();
            byte[] data = new byte[bytesRead];
            for(int i = 0; i < bytesRead; i++) {
                data[i] = received[i];
            }
            socketChannel.write(ByteBuffer.wrap(data));
            System.out.println(new String(data));
        } catch (IOException e) {
        }
    }

    public static void main(String[] args) throws IOException {
        TestServer server = new TestServer(new InetSocketAddress("localhost", 11081));
        server.serve();
    }
}
