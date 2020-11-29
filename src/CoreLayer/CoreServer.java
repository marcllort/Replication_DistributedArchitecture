package CoreLayer;

import Utils.Logger;
import Utils.Message;
import Utils.Network;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static Utils.Utils.*;

public class CoreServer {

    private Network network;

    private String message;
    private Map<Integer, Integer> data = new HashMap<>();
    private int numberOfAct = 0;
    private Logger logger;

    public CoreServer(Network network) {
        this.network = network;
        this.logger = new Logger("src/logs/core_layer_" + (network.getMyPort() - CORE_LAYER_PORT) + ".txt");
    }

    public void startReplication() {

        while (true) {
            this.message = this.network.receiveMessage();
            System.out.println("RECIBIDO " + this.message);
            ArrayList<Message> operations = this.parseMessage(this.message);

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

    private void manageRead(Message receivedMessage){
        System.out.println("He recibido un READ");
        System.out.println("El puerto  " + receivedMessage.getPort());
        System.out.println("La key a leer " + receivedMessage.getLine());

        //check value to read
        this.message = String.valueOf(this.data.getOrDefault(receivedMessage.getLine(), Integer.valueOf(-1)));

        //send the value to the client
        this.network.sendMessage(this.network.getClientPort(), message);

    }

    private void manageWrite(Message receivedMessage){
        String ack;
        System.out.println("He recibido un WRITE");
        System.out.println("El puerto " + receivedMessage.getPort());
        System.out.println("El valor a escribir key:" + receivedMessage.getLine() + " valor:" + receivedMessage.getValue());


        this.data.put(receivedMessage.getLine(), receivedMessage.getValue());

        //In case the one who sends is the client
        if (receivedMessage.getPort() == this.network.getClientPort()) {

            //replicamos el mensaje sustituyendo el puerto
            message = receivedMessage.getLine() + ";" + receivedMessage.getValue();
            this.network.broadcastCoreLayer(this.message);

            //We wait for the ack before accepting another transaction (EAGER)
            for (int i = 0; i < this.network.getCoreLayerPorts().length; i++) {
                ack = this.network.receiveMessage();
                System.out.println("Me han contestado " + ack);
            }
            message = "ACK";
            this.network.sendMessage(receivedMessage.getPort(), message);

        } else {

            //answering
            message = "ACK";

            this.network.sendMessage(receivedMessage.getPort(), message);
        }


        System.out.println("---------------------------------------");

        numberOfAct++;
        this.sendMessageToServer();

        if (numberOfAct == 10 && (this.network.getMyPort() == 6663 || this.network.getMyPort() == 6662)) {
            numberOfAct = 0;
            this.sendMessageToLayer1();
        }


    }
    private ArrayList<Message> parseMessage(String message) {

        ArrayList<Message> operations = new ArrayList<>();

        String[] parts = message.split("&");
        String[] transactions = parts[1].split("-");

        String port = (parts[0]);

        for (String transaction : transactions) {
            if (transaction.contains(";")) {
                // Write operation
                String[] writeOperations = transaction.split(";");
                Message m = new Message(port, WRITE_ACTION, writeOperations[0], writeOperations[1]);
                operations.add(m);
            } else {
                // Read operation
                Message m = new Message(port, READ_ACTION, transaction);
                operations.add(m);
            }
        }

        return operations;

    }

    private void sendMessageToLayer1() {
        String message = "";

        for (Integer key :
                this.data.keySet()) {
            message = message + key + "&" + this.data.get(key) + "&";
        }

        this.network.sendMessage(this.network.getFirstLayerPorts()[0], message);
    }

    private void sendMessageToServer() {
        String message = "";

        for (Integer key :
                this.data.keySet()) {
            message = message + key + "&" + this.data.get(key) + "&";
        }

        this.network.sendMessage(6659, message);
    }

}
