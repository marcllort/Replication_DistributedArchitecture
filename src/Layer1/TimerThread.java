package Layer1;


import Utils.Network;

import java.util.Map;
import java.util.concurrent.TimeUnit;

public class TimerThread extends Thread {

    private Network network;
    private Map<String,String> data;

    TimerThread(Network network,Map<String,String> data){
        this.network = network;
        this.data = data;
    }

    public void run(){
        while (true) {
            try {
                TimeUnit.SECONDS.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            sendMessageToLayer2();
        }
    }

    private void sendMessageToLayer2(){
        String message = "";

        for (String key :
                this.data.keySet()) {
            message = message + key + "&" + this.data.get(key) + "&";
        }
        if(!this.data.isEmpty()) {
            this.network.broadcastLayer2(message);
        }
        //this.network.sendMessage(this.network.getLayer2Ports()[0],message);
    }
}
