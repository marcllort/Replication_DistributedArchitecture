package Layer2;


import Utils.Logger;
import Utils.Message;
import Utils.Network;
import Websockets.BaseNode;
import Websockets.WebSocketEndpoint;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Map;

import static Utils.Utils.*;

public class SecondLayerServer extends BaseNode {

    private Network network;
    private Logger logger;
    private Map<Integer, Integer> infoHashMap;
    private WebSocketEndpoint webSocketEndpoint;

    SecondLayerServer( int id,Network network, Map<Integer, Integer> infoHashMap) {
        super(SECOND_LAYER_PORTS[id], SECOND_LAYER_SERVER_PORTS[id]);
        this.network = network;
        this.infoHashMap = infoHashMap;
        this.logger = new Logger("src/main/java/logs/second_layer_" + (network.getMyPort() - SECOND_LAYER_PORT) + ".txt");
        this.webSocketEndpoint = new WebSocketEndpoint(
                new InetSocketAddress("localhost", wsPort),
                logger
        );

        webSocketEndpoint.start();
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

        webSocketEndpoint.updateNodeStatus(receivedMessage.getLine(), receivedMessage.getValue());

        printSeparator();
    }

}
