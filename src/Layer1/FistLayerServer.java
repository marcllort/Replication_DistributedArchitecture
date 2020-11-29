package Layer1;


import Utils.Logger;
import Utils.Message;
import Utils.Network;

import java.util.ArrayList;
import java.util.Map;

import static Utils.Utils.FIRST_LAYER_PORT;
import static Utils.Utils.parseMessage;

public class FistLayerServer {

    private Network network;
    private Logger logger;
    private Map<Integer, Integer> infoHashMap;


    FistLayerServer(Network network, Map<Integer, Integer> infoHashMap) {
        this.network = network;
        this.infoHashMap = infoHashMap;
        this.logger = new Logger("src/logs/first_layer_" + (network.getMyPort() - FIRST_LAYER_PORT) + ".txt");
    }

    public void replicate() {
        while (true) {
            ArrayList<Message> operations = parseMessage(network.receiveMessage());
        }
    }

}
