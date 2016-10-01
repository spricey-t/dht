package com.virohtus.dht.core.transport;

import java.nio.channels.CompletionHandler;

public class ReceiveHandler implements CompletionHandler<Integer, ReceiveContext> {

    @Override
    public void completed(Integer result, ReceiveContext attachment) {
        if(result == 0) {
            return;
        }
        if(result < 0) {
            // todo error
        }
        byte[] data = new byte[result];
        System.arraycopy(attachment.getByteBuffer().array(), 0, data, 0, result);
        System.out.println(new String(data));
    }

    @Override
    public void failed(Throwable exc, ReceiveContext attachment) {
        exc.printStackTrace();
    }
}
