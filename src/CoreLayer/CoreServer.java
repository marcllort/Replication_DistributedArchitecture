package CoreLayer;

import Utils.Network;

import java.util.HashMap;
import java.util.Map;

public class CoreServer {

    private Network network;
    private String portSender;
    private String action;
    private String message;
    private Map<String, String> data = new HashMap<>();
    private String key, valor;
    private int numberOfAct = 0;

    public CoreServer(Network network) {
        this.network = network;
    }

    public void startReplication(){
        String response;

        while(true) {
            //recibimos un mensaje en la core layer
            this.message = this.network.receiveMessage();
            System.out.println("RECIBIDO " + this.message);
            //parseamos el mensaje
            this.parseMessage(this.message);
            //si la peticion es de lectura, simplemente retornamos el valor
            if(this.action.equals("read")){

                System.out.println("He recibido un READ");
                System.out.println("El puerto  " + this.portSender);
                System.out.println("La key a leer " + this.key);

                //miramos si tenemos valor
                this.message = this.data.getOrDefault(this.key,"null");

                //respondemos al cliente con la respuesta
                this.network.sendMessage(this.network.getClientPort(),message);

            }else{

                System.out.println("He recibido un WRITE");
                System.out.println("El puerto " + this.portSender);
                System.out.println("El valor a escribir key:" + this.key + " valor:" + this.valor);


                this.data.put(this.key,this.valor);

                //en caso de que quien lo envie sea el cliente
                if(Integer.valueOf(this.portSender) == this.network.getClientPort()){

                    //replicamos el mensaje sustituyendo el puerto
                    message = this.action + "&" + this.key + "&" + this.valor;

                    //En caso de lectura como el core layer es update everywhere hacemos
                    //un broadcast de la peticion
                    this.network.broadcastCoreLayer(this.message);

                    //Esperamos el ok de los otros nodos del core layer por ser eager
                    for (int i =0 ; i < this.network.getCoreLayerPorts().length; i++){
                        response = this.network.receiveMessage();
                        System.out.println("Me han contestado " + response);
                    }

                    //respondemos al cliente con ACK
                    message = "ACK";

                    this.network.sendMessage(Integer.valueOf(this.portSender),message);

                }else{

                    //respondemos
                    message = "ACK";

                    this.network.sendMessage(Integer.valueOf(this.portSender),message);
                }


                System.out.println("---------------------------------------");

                numberOfAct++;
                this.sendMessageToServer();

                if(numberOfAct == 10 && (this.network.getMyPort() == 6663 || this.network.getMyPort() == 6662)){
                    numberOfAct = 0;
                    this.sendMessageToLayer1();
                }


            }

        }
    }

    private void parseMessage(String message){
        String[] parts = message.split("&");
        this.portSender = (parts[0]);
        this.action = parts[1];

        if(this.action.equals("read")){
            this.key = (parts[2]);

        }else{
            this.key = (parts[2]);
            this.valor = (parts[3]);

        }

    }

    private void sendMessageToLayer1(){
        String message = "";

        for (String key :
                this.data.keySet()) {
            message = message + key + "&" + this.data.get(key) + "&";
        }

        this.network.sendMessage(this.network.getFirstLayerPorts()[0],message);
    }

    private void sendMessageToServer(){
        String message = "";

        for (String key :
                this.data.keySet()) {
            message = message + key + "&" + this.data.get(key) + "&";
        }

        this.network.sendMessage(6659,message);
    }

}
