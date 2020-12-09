package CoreLayer;

import Utils.Logger;
import Utils.Message;
import Utils.Network;
import Websockets.BaseNode;
import Websockets.WebSocketEndpoint;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static Utils.Utils.*;

public class CoreServer extends BaseNode {

    private final Network network;
    private final Map<Integer, Integer> infoHashMap;
    private final Logger logger;
    private int numberOfAct;
    private final WebSocketEndpoint webSocketEndpoint;


    public CoreServer(int id, Network network) {
        super(CORE_LAYER_PORTS[id], CORE_LAYER_SERVER_PORTS[id]);

        this.network = network;
        this.logger = new Logger("src/main/java/logs/core_layer_" + (network.getMyPort() - CORE_LAYER_PORT) + ".txt");

        this.infoHashMap = new HashMap<>();
        this.numberOfAct = 0;

        this.webSocketEndpoint = new WebSocketEndpoint(new InetSocketAddress("localhost", wsPort));
        webSocketEndpoint.start();
    }

    public void replicate() {
        boolean hasRead = false;
        int hasWrite = 0;
        String message = "";
        while (true) {
            ArrayList<Message> operations = parseMessage(network.receiveMessage());
            for (Message operation : operations) {
                if (operation.getAction().equals(READ_ACTION)) {
                    message = manageRead(operation, message);
                    hasRead = true;
                } else {
                    manageWrite(operation);
                    hasWrite = operation.getPort();
                }
                logger.writeLog(operation, network.getMyPort());
            }
            if (hasRead) {
                network.sendMessage(network.getClientPort(), message);
                hasRead = false;
            } else if (hasWrite != 0) {
                network.sendMessage(hasWrite, "ACK");
                hasWrite = 0;
                message = "";
            }
        }
    }

    private String manageRead(Message receivedMessage, String returnMessage) {
        printMessage(receivedMessage);

        // Get value from hashmap
        String message = String.valueOf(infoHashMap.getOrDefault(receivedMessage.getLine(), -1));

        // Send value to the client
        if (message.equals("-1")) {
            message = "NULL";
        }

        returnMessage += message + "/";

        return returnMessage;
    }

    private void manageWrite(Message receivedMessage) {
        String message;
        printMessage(receivedMessage);

        // Add new value to the hashMap
        infoHashMap.put(receivedMessage.getLine(), receivedMessage.getValue());

        if (receivedMessage.getPort() == network.getClientPort()) {
            message = receivedMessage.getLine() + ";" + receivedMessage.getValue();
            network.broadcastCoreLayer(message);

            //We wait for the ACK from other nodes before accepting another transaction (EAGER)
            for (int i = 0; i < network.getCoreLayerPorts().length; i++) {
                System.out.println("ANSWER: " + network.receiveMessage());
            }
        }

        printSeparator();

        // Update number of actualizations done
        numberOfAct++;

        // Update websocket
        webSocketEndpoint.sendNewTransaction(receivedMessage.getLine(), receivedMessage.getValue());

        replicateToFirstLayer();
    }

    private void replicateToFirstLayer() {
        if (numberOfAct == 10) {
            if (network.getMyPort() == CORE_LAYER_PORTS[1]) {
                network.sendMessage(FIRST_LAYER_PORTS[0], hashMapToMessage(infoHashMap));
                numberOfAct = 0;
            } else if (network.getMyPort() == CORE_LAYER_PORTS[2]) {
                network.sendMessage(FIRST_LAYER_PORTS[1], hashMapToMessage(infoHashMap));
                numberOfAct = 0;
            }
        }
    }
}
