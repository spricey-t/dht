package com.virohtus.dht.utils;

import com.virohtus.dht.event.EventProtocol;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;

public class DhtUtilities {

    public String generateId() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    public String ipAddrToString(byte[] ipAddr) {
        String[] octets = new String[ipAddr.length];
        for(int i = 0; i < ipAddr.length; i++) {
            octets[i] = String.format("%d", ipAddr[i] & 0xff);
        }
        return String.join(".", octets);
    }

    public byte[] stringToIpAddr(String ipString) {
        String[] octets = ipString.split(".");
        byte[] ipAddr = new byte[octets.length];
        for(int i = 0; i < octets.length; i++) {
            ipAddr[i] = Byte.parseByte(octets[i]);
        }
        return ipAddr;
    }

    public void writeString(String string, DataOutputStream dataOutputStream) throws IOException {
        byte[] data = string.getBytes(EventProtocol.STRING_ENCODING);
        dataOutputStream.writeInt(data.length);
        dataOutputStream.write(data);
    }

    public String readString(DataInputStream dataInputStream) throws IOException {
        int dataLength = dataInputStream.readInt();
        byte[] data = new byte[dataLength];
        dataInputStream.readFully(data);
        return new String(data, EventProtocol.STRING_ENCODING);
    }

    public byte[] readSizedData(DataInputStream dataInputStream) throws IOException {
        int dataLength = dataInputStream.readInt();
        byte[] data = new byte[dataLength];
        dataInputStream.readFully(data);
        return data;
    }

    public void writeSizedData(byte[] data, DataOutputStream dataOutputStream) throws IOException {
        dataOutputStream.writeInt(data.length);
        dataOutputStream.write(data);
    }
}
