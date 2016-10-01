package com.virohtus.dht.transport;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Scanner;
import java.util.concurrent.Executors;

public class TestClient {


    public static void main(String[] args) throws IOException {
        InetSocketAddress inetSocketAddress = new InetSocketAddress("localhost", 11081);
        SocketChannel socketChannel = SocketChannel.open(inetSocketAddress);

        Executors.newSingleThreadExecutor().execute(() -> {
            while(true) {
                try {
                    ByteBuffer byteBuffer = ByteBuffer.allocate(256);
                    socketChannel.read(byteBuffer);
                    System.out.println(new String(byteBuffer.array()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        Scanner keyboard = new Scanner(System.in);
        String cmd = "";
        while(!cmd.equalsIgnoreCase("quit")) {
            cmd = keyboard.nextLine();
            byte[] data = cmd.getBytes();
            ByteBuffer byteBuffer = ByteBuffer.wrap(data);
            socketChannel.write(byteBuffer);
            byteBuffer.clear();
        }

        socketChannel.close();
    }
}
