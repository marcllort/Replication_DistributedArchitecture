package Layer1;


import Utils.Logger;
import Utils.Message;
import Utils.Network;
import Websockets.WebSocketServer;

import java.net.InetSocketAddress;
import java.util.*;

import static Utils.Utils.*;

public class FirstLayerServer {

    private final Map<Integer, Integer> infoHashMap;
    private final Network network;
    private final Logger logger;
    private final WebSocketServer webSocketServer;
    private final Timer t;

    FirstLayerServer(int id, Network network) {
        this.network = network;
        this.logger = new Logger("src/main/java/logs/first_layer_" + (network.getMyPort() - FIRST_LAYER_PORT) + ".txt");

        this.infoHashMap = new HashMap<>();

        //Every 10s we send messge to second layer
        t = new Timer();


        this.webSocketServer = new WebSocketServer(new InetSocketAddress("localhost", FIRST_LAYER_SERVER_PORTS[id]));
        webSocketServer.start();
    }

    public void replicate() {
        boolean hasRead = false;
        boolean startReplicateDone = false;
        String message = "";

        while (true) {
            ArrayList<Message> operations = parseMessage(network.receiveMessage());
            for (Message operation : operations) {
                if (operation.getAction().equals(READ_ACTION)) {
                    message = manageRead(operation, message, infoHashMap);
                    hasRead = true;
                } else {
                    manageWrite(operation);
                    if (!startReplicateDone) {
                        t.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                replicateToSecondLayer();
                            }
                        }, 10000, 10000);
                        startReplicateDone = true;
                    }
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

    private void replicateToSecondLayer() {
        if (network.getMyPort() == FIRST_LAYER_PORTS[1]) {                  // NODE B2
            if (!this.infoHashMap.isEmpty()) {
                this.network.broadcastLayer2(hashMapToMessage(infoHashMap));
            }
        }
    }

}
