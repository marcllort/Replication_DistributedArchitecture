package CoreLayer;

import Utils.Logger;
import Utils.Message;
import Utils.Network;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static Utils.Utils.*;

public class CoreServer {

    private final Network network;
    private final Logger logger;
    private final Map<Integer, Integer> infoHashMap;
    private int numberOfAct;

    public CoreServer(Network network) {
        this.network = network;
        this.infoHashMap = new HashMap<>();
        this.numberOfAct = 0;
        this.logger = new Logger("src/logs/core_layer_" + (network.getMyPort() - CORE_LAYER_PORT) + ".txt");
    }

    public void replicate() {
        while (true) {
            ArrayList<Message> operations = parseMessage(network.receiveMessage());

            for (Message operation : operations) {
                if (operation.getAction().equals(READ_ACTION)) {
                    manageRead(operation);
                } else {
                    manageWrite(operation);
                }
                logger.writeLog(operation, network.getMyPort());
            }
        }
    }

    private void manageRead(Message receivedMessage) {
        printMessage(receivedMessage);

        // Get value from hashmap
        String message = String.valueOf(infoHashMap.getOrDefault(receivedMessage.getLine(), -1));

        // Send value to the client
        network.sendMessage(network.getClientPort(), message);
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

        // Answer with ACK to the message sender
        network.sendMessage(receivedMessage.getPort(), "ACK");
        printSeparator();

        // Update number of actualizations done
        numberOfAct++;

        replicateToFirstLayer();

    }

    private void replicateToFirstLayer() {
        if (numberOfAct == 10) {
            if (network.getMyPort() == CORE_LAYER_PORTS[1]) {
                network.sendMessage(FIRST_LAYER_PORTS[0], hashMapToMessage());
                numberOfAct = 0;
            } else if (network.getMyPort() == CORE_LAYER_PORTS[2]) {
                network.sendMessage(FIRST_LAYER_PORTS[1], hashMapToMessage());
                numberOfAct = 0;
            }
        }
    }

    private String hashMapToMessage() {
        String message = "";

        for (Integer key : infoHashMap.keySet()) {
            message = message + key + "-" + infoHashMap.get(key) + "-";
        }

        message = message.substring(0, message.length() - 1);

        return message;
    }

}
