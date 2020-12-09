package Websockets;


import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;

import java.net.InetSocketAddress;

public class WebSocketEndpoint extends org.java_websocket.server.WebSocketServer {

    public WebSocketEndpoint(InetSocketAddress address) {
        super(address);
    }

    @Override
    public void onClose(WebSocket webSocket, int code, String reason, boolean b) {
    }

    @Override
    public void onMessage(WebSocket conn, String msg) {
    }

    @Override
    public void onError(WebSocket conn, Exception e) {
    }

    @Override
    public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {
    }

    @Override
    public void onStart() {
    }

    public void sendNewTransaction(int key, int value) {
        broadcast(key + "|" + value);
    }
}
