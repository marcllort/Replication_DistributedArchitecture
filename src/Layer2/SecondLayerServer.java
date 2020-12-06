package Layer2;


import Utils.Logger;
import Utils.Message;
import Utils.Network;

import java.util.ArrayList;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import static Utils.Utils.*;
import static Utils.Utils.FIRST_LAYER_PORTS;

public class SecondLayerServer {

    private Network network;
    private Logger logger;
    private Map<Integer, Integer> infoHashMap;


    SecondLayerServer(Network network, Map<Integer, Integer> infoHashMap) {
        this.network = network;
        this.infoHashMap = infoHashMap;
        this.logger = new Logger("src/logs/second_layer_" + (network.getMyPort() - SECOND_LAYER_PORT) + ".txt");
    }

    public void replicate() {
        while (true) {
            ArrayList<Message> operations = parseMessage(network.receiveMessage());
            for (Message m : operations){
                logger.writeLog(m, network.getMyPort());
            }
        }
    }


}
