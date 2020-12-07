package Layer1;


import Utils.Logger;
import Utils.Message;
import Utils.Network;

import java.util.ArrayList;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import static Utils.Utils.*;

public class FirstLayerServer {

    private Network network;
    private Logger logger;
    private Map<Integer, Integer> infoHashMap;


    FirstLayerServer(Network network, Map<Integer, Integer> infoHashMap) {
        this.network = network;
        this.infoHashMap = infoHashMap;
        this.logger = new Logger("src/logs/first_layer_" + (network.getMyPort() - FIRST_LAYER_PORT) + ".txt");

        //Every 10s we send messge to second layer
        Timer t = new Timer();
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                replicateToSecondLayer();
            }
        }, 0, 10000);
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
