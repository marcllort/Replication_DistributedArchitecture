package Layer1;


import Utils.Logger;
import Utils.Network;

import java.util.Map;

import static Utils.Utils.*;

public class Layer1 {

    private Network network;
    private String message;
    private  Map<String,String> data;
    private Logger log;

    Layer1(Network network, Map<String,String> data){
        this.network = network;
        this.data = data;
        this.log = new Logger("src/logs/first_layer_" + (network.getMyPort() - FIRST_LAYER_PORT) + ".txt");

    }

    public void startReplication(){
        while(true) {
            this.message = this.network.receiveMessage();
            this.parseMessage();
        }
    }


    private void parseMessage(){
        String[] parts = message.split("&");
        System.out.println("MESSAGE " + this.message);
        if(parts[0].equals(CORE_LAYER_PORTS[0]) || parts[0].equals(CORE_LAYER_PORTS[1])){
            this.isACoreMessage(parts);
            this.printData();
            this.sendMessageToServer();
        }else if(parts[0].equals(CLIENT_PORT)){
            //miramos si tenemos valor
            this.message = data.getOrDefault(parts[2],"null");
            //this.log.writeLog();

            //respondemos al cliente con la respuesta
            this.network.sendMessage(this.network.getClientPort(),message);
        }
    }

    private void isACoreMessage(String parts[]){
        for (int i = 1; i < parts.length;i++){
            data.put(parts[i],parts[i+1]);
            //this.log.writeLog();
            i++;
        }
    }

    private void printData(){
        for (String key :
                data.keySet()) {
            System.out.println("KEY: "+ key + " Valor: " + data.get(key));
        }
    }

    private void sendMessageToServer(){
        String message = "";

        for (String key :
                this.data.keySet()) {
            message = message + key + "&" + this.data.get(key) + "&";
        }

        this.network.sendMessage(CORE_LAYER_PORT,message);
    }
}
