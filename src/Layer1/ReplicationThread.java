package Layer1;


import Utils.Network;

import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ReplicationThread extends Thread {

    private Network network;
    private Map<Integer, Integer> infoHashMap;

    ReplicationThread(Network network, Map<Integer, Integer> data) {
        this.network = network;
        this.infoHashMap = data;
    }

    public void run() {
        while (true) {
            try {
                TimeUnit.SECONDS.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            network.broadcastLayer2(hashMapToMessage());
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
