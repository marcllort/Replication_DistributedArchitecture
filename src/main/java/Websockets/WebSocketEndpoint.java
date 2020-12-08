package Websockets;


import Utils.Logger;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;

import java.net.InetSocketAddress;

public class WebSocketEndpoint extends org.java_websocket.server.WebSocketServer {
	private Logger logger;

	public WebSocketEndpoint(InetSocketAddress address, Logger logger) {
		super(address);
		this.logger = logger;
	}

	@Override
	public void onClose(WebSocket webSocket, int code, String reason, boolean b) {
		//logger.print("Websocket closed " + webSocket.getRemoteSocketAddress() + " with exit code " + code + " additional info: " + reason);
	}

	@Override
	public void onMessage(WebSocket conn, String msg) {
		//logger.print("Websocket received message from " + conn.getRemoteSocketAddress() + ": " + msg);
	}

	@Override
	public void onError(WebSocket conn, Exception e) {
		//logger.error("Websocket error occurred on connection " + conn.getRemoteSocketAddress() + ":" + e);
	}

	@Override
	public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {
		webSocket.send("Welcome to the websocket server!"); //This method sends a message to the new client
		//logger.print("new connection to " + webSocket.getRemoteSocketAddress());
	}

	@Override
	public void onStart() {
		//logger.print("Websocket server started successfully");
	}

	public void updateNodeStatus(int variable, int value) {
		broadcast(variable + "|" + value);
	}
}
