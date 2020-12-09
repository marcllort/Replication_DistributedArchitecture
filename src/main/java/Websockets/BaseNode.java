package Websockets;

import java.io.IOException;
import java.net.Socket;

public abstract class BaseNode extends BaseServer {

	protected int nodePort;
    protected int wsPort;

    public BaseNode(int nodePort, int wsPort) {
        this.nodePort = nodePort;
        this.wsPort = wsPort;
    }

    public void startRoutine() {

        if (!this.open()) {
            System.exit(1);
        }

        try {
            while (!this.server.isClosed()) {
                Socket client = this.server.accept();
                this.loadStreams(client);
                this.inputStream.readObject();
                this.closeStreams(client);
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            System.exit(1);
        }

        this.close();
    }
}
