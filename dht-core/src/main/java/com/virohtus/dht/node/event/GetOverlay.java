package com.virohtus.dht.node.event;

import com.virohtus.dht.event.Event;
import com.virohtus.dht.event.EventProtocol;
import com.virohtus.dht.node.overlay.OverlayNode;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GetOverlay extends Event {

    private List<OverlayNode> overlayNodes;

    public GetOverlay(OverlayNode startNode) {
        super(startNode.getNodeId());
        overlayNodes = new ArrayList<>();
        overlayNodes.add(startNode);
    }

    public GetOverlay(byte[] data) throws IOException {
        super(data);
    }

    @Override
    public int getType() {
        return EventProtocol.GET_OVERLAY;
    }

    @Override
    protected void serialize(DataOutputStream dataOutputStream) throws IOException {
        super.serialize(dataOutputStream);
        dataOutputStream.writeInt(overlayNodes.size());
        for(OverlayNode node : overlayNodes) {
            dhtUtilities.writeSizedData(node.serialize(), dataOutputStream);
        }
    }

    @Override
    protected void deserialize(DataInputStream dataInputStream) throws IOException {
        super.deserialize(dataInputStream);
        overlayNodes = new ArrayList<>();
        int nodeCount = dataInputStream.readInt();
        for(int i = 0; i < nodeCount; i++) {
            OverlayNode overlayNode = new OverlayNode(dhtUtilities.readSizedData(dataInputStream));
            overlayNodes.add(overlayNode);
        }
    }

    public List<OverlayNode> getOverlayNodes() {
        return new ArrayList<>(overlayNodes);
    }

    public void addOverlayNode(OverlayNode overlayNode) {
        overlayNodes.add(overlayNode);
    }
}
