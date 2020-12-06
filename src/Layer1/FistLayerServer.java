package Layer1;


import Utils.Logger;
import Utils.Message;
import Utils.Network;

import java.util.ArrayList;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import static Utils.Utils.*;
import static Utils.Utils.FIRST_LAYER_PORTS;

public class FistLayerServer {

    private Network network;
    private Logger logger;
    private Map<Integer, Integer> infoHashMap;


    FistLayerServer(Network network, Map<Integer, Integer> infoHashMap) {
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
        while (true) {
            ArrayList<Message> operations = parseMessage(network.receiveMessage());
            for (Message m : operations){
                logger.writeLog(m, network.getMyPort());
            }
        }
    }

    private void replicateToSecondLayer() {
        String message = "";
            if (network.getMyPort() == FIRST_LAYER_PORTS[1]) {
                //network.sendMessage(SECOND_LAYER_PORTS[0], hashMapToMessage());
                for (Integer key :
                        this.infoHashMap.keySet()) {
                    message = message + key + "&" + this.infoHashMap.get(key) + "&";
                }
                if(!this.infoHashMap.isEmpty()) {
                    this.network.broadcastLayer2(message);
                }
            }
    }

}
