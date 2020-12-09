package Websockets;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public abstract class BaseServer {

    protected ServerSocket server;
    protected ObjectInputStream inputStream;
    protected ObjectOutputStream outputStream;
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

}
