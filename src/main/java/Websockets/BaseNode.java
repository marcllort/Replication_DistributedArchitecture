package Websockets;


import Utils.Logger;

import java.io.IOException;
import java.net.Socket;

import static Utils.Utils.CORE_LAYER_PORT;

/**
 * @author Ajordat
 * @version 1.0
 **/
public abstract class BaseNode extends BaseServer {
	protected NodeRole node;
	protected Logger logger;


	public BaseNode(NodeRole node) {
		this.node = node;
		this.port = node.getPort();
		this.logger = new Logger("src/main/java/logs/core_layer_" + (node.getPort() - CORE_LAYER_PORT) + ".txt");

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
				this.action((Frame) this.inputStream.readObject());
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

	protected abstract void action(Frame frame) throws IOException, ClassNotFoundException;
}
