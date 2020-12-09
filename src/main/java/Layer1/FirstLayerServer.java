package Layer1;


import Utils.Logger;
import Utils.Message;
import Utils.Network;
import Websockets.BaseNode;
import Websockets.WebSocketEndpoint;

import java.net.InetSocketAddress;
import java.util.*;

import static Utils.Utils.*;

public class FirstLayerServer extends BaseNode {

    private final Map<Integer, Integer> infoHashMap;
    private final Network network;
    private final Logger logger;
    private final WebSocketEndpoint webSocketEndpoint;

    FirstLayerServer(int id, Network network) {
        super(FIRST_LAYER_PORTS[id], FIRST_LAYER_SERVER_PORTS[id]);

        this.network = network;
        this.logger = new Logger("src/main/java/logs/first_layer_" + (network.getMyPort() - FIRST_LAYER_PORT) + ".txt");

        this.infoHashMap = new HashMap<>();

        //Every 10s we send messge to second layer
        Timer t = new Timer();
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                replicateToSecondLayer();
            }
        }, 0, 10000);

        this.webSocketEndpoint = new WebSocketEndpoint(new InetSocketAddress("localhost", wsPort));
        webSocketEndpoint.start();
    }

    public void replicate() {
        boolean hasRead = false;
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
        webSocketEndpoint.sendNewTransaction(receivedMessage.getLine(), receivedMessage.getValue());

        printSeparator();
    }

    private void replicateToSecondLayer() {
        if (network.getMyPort() == FIRST_LAYER_PORTS[1]) {                  // NODE B2
            if (!this.infoHashMap.isEmpty()) {
                this.network.broadcastLayer2(hashMapToMessage(infoHashMap));
            }
        }
    }

}
