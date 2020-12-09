package Websockets;


import Utils.Logger;
import Utils.Utils;

import java.io.IOException;
import java.net.Socket;

import static Utils.Utils.CORE_LAYER_PORT;

/**
 * @author Ajordat
 * @version 1.0
 **/
public abstract class BaseNode extends BaseServer {
	protected int nodePort;
	protected int wsPort;

	protected Logger logger;


	public BaseNode(int nodePort, int wsPort) {
		this.nodePort = nodePort;
		this.wsPort = wsPort;
	}

	public void startRoutine() {

		if (!this.open()) {
			//logger.error("Couldn't open server on port " + node.getPort());
			System.exit(1);
		}

		try {
			while (!this.server.isClosed()) {
				//logger.debug("Waiting new connections...");
				Socket client = this.server.accept();
				//logger.debug("New connection received on port " + this.port);

				this.loadStreams(client);
				this.inputStream.readObject();
				this.closeStreams(client);

				//logger.debug("New connection finished.");
			}
		} catch (IOException | ClassNotFoundException e) {
			//logger.error("Server failure on port " + node.getPort());
			if (server != null && !server.isClosed()) {
				try {
					server.close();
				} catch (IOException ignored) {
					//logger.error("Caught exception on server close.");
				} finally {
					//logger.error("Server closed.");
				}
			} else
				//logger.error("Server is down.");
			System.exit(1);
		}

		this.close();
	}
}
