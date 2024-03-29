package Layer2;


import Utils.Logger;
import Utils.Message;
import Utils.Network;
import Websockets.WebSocketServer;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static Utils.Utils.*;

public class SecondLayerServer {

    private final Network network;
    private final Logger logger;
    private final Map<Integer, Integer> infoHashMap;
    private final WebSocketServer webSocketServer;

    SecondLayerServer(int id, Network network) {
        this.network = network;
        this.logger = new Logger("src/main/java/logs/second_layer_" + (network.getMyPort() - SECOND_LAYER_PORT) + ".txt");

        this.infoHashMap = new HashMap<>();

        this.webSocketServer = new WebSocketServer(new InetSocketAddress("localhost", SECOND_LAYER_SERVER_PORTS[id]));
        webSocketServer.start();
    }

    public void replicate() {
        Boolean hasRead = false;
        String message = "";
        while (true) {
            ArrayList<Message> operations = parseMessage(network.receiveMessage());
            for (Message operation : operations) {
                if (operation.getAction().equals(READ_ACTION)) {
                    message = manageRead(operation, message, infoHashMap);
                    hasRead = true;
                } else {
                    manageWrite(operation);
                }
                logger.writeLog(operation, network.getMyPort());
            }
            if (hasRead) {
                network.sendMessage(network.getClientPort(), message);
                hasRead = false;
                message = "";
            }
        }
    }

    private void manageWrite(Message receivedMessage) {
        printMessage(receivedMessage);

        // Add new value to the hashMap
        infoHashMap.put(receivedMessage.getLine(), receivedMessage.getValue());

        // Update websocket
        webSocketServer.sendNewTransaction(receivedMessage.getLine(), receivedMessage.getValue());

        printSeparator();
    }

}
