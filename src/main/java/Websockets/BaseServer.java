package Websockets;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Servidor base del que hereten els altres servidors.
 * Aporta els mètodes per a obrir, transmetre i registrar comunicacions entre servidors.
 *
 * @author Ajordat
 * @version 1.0
 **/
@SuppressWarnings({"unused", "WeakerAccess", "UnusedReturnValue"})
public abstract class BaseServer {

	protected ServerSocket server;
	protected ObjectInputStream inputStream;
	protected ObjectOutputStream outputStream;
	private static final String LOOPBACK_IP = "127.0.0.1";
	protected int port;


	protected BaseServer() {
		this.inputStream = null;
		this.outputStream = null;
	}

	protected boolean open() {
		try {
			this.server = new ServerSocket(port);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	protected void close() {
		try {
			server.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	protected void loadStreams(Socket socket) throws IOException {
		this.inputStream = new ObjectInputStream(socket.getInputStream());
		this.outputStream = new ObjectOutputStream(socket.getOutputStream());
	}

	protected void closeStreams(Socket socket) throws IOException {
		this.inputStream.close();
		this.outputStream.close();
		socket.close();
	}

	protected Frame request(int address, Frame.Type type) throws IOException, ClassNotFoundException {
		return request(address, new Frame(type));
	}

	protected Frame request(int address, Frame.Type type, Object data) throws IOException, ClassNotFoundException {
		return request(address, new Frame(type, data));
	}

	protected void send(int address, Frame.Type type) throws IOException {
		send(address, new Frame(type));
	}

	protected void send(int address, Frame.Type type, Object data) throws IOException {
		send(address, new Frame(type, data));
	}


	private Frame request(int address, Frame frame) throws IOException, ClassNotFoundException {
		Socket socket = new Socket(LOOPBACK_IP, address);
		ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
		ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());

		outputStream.writeObject(frame);
		Frame answer = (Frame) inputStream.readObject();
		inputStream.close();
		outputStream.close();
		socket.close();
		return answer;
	}

	private void send(int address, Frame frame) throws IOException {
		Socket socket = new Socket(LOOPBACK_IP, address);
		ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());

		outputStream.writeObject(frame);
		outputStream.close();
		socket.close();
	}

	protected void reply(Frame.Type type) throws IOException {
		this.outputStream.writeObject(new Frame(type));
	}

	protected void reply(Frame.Type type, Object data) throws IOException {
		this.outputStream.writeObject(new Frame(type, data));
	}

	protected void broadcast(int[] addresses, Frame.Type type, Object data) throws IOException, ClassNotFoundException {
		for (int address : addresses)
			request(address, type, data);
	}

	public void setStreams(ObjectInputStream input, ObjectOutputStream output) {
		this.inputStream = input;
		this.outputStream = output;
	}
}
