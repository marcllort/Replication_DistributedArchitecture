package CoreLayer;

import Utils.Message;
import Utils.Network;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CoreServer {

    private Network network;

    private String message;
    private Map<Integer, Integer> data = new HashMap<>();
    private int numberOfAct = 0;

    public CoreServer(Network network) {
        this.network = network;
    }

    public void startReplication() {
        String response;

        while (true) {
            this.message = this.network.receiveMessage();
            System.out.println("RECIBIDO " + this.message);
            ArrayList<Message> operations = this.parseMessage(this.message);

            for (Message operation : operations) {
                if (operation.getAction().equals("read")) {

                    System.out.println("He recibido un READ");
                    System.out.println("El puerto  " + operation.getPort());
                    System.out.println("La key a leer " + operation.getLine());

                    //miramos si tenemos valor
                    this.message = String.valueOf(this.data.getOrDefault(operation.getLine(), Integer.valueOf(-1)));

                    //respondemos al cliente con la respuesta
                    this.network.sendMessage(this.network.getClientPort(), message);

                } else {

                    System.out.println("He recibido un WRITE");
                    System.out.println("El puerto " + operation.getPort());
                    System.out.println("El valor a escribir key:" + operation.getLine() + " valor:" + operation.getValue());


                    this.data.put(operation.getLine(), operation.getValue());

                    //en caso de que quien lo envie sea el cliente
                    if (operation.getPort() == this.network.getClientPort()) {

                        //replicamos el mensaje sustituyendo el puerto
                        message = operation.getLine() + ";" + operation.getValue();
                        this.network.broadcastCoreLayer(this.message);

                        //Esperamos el ok de los otros nodos del core layer por ser eager
                        for (int i = 0; i < this.network.getCoreLayerPorts().length; i++) {
                            response = this.network.receiveMessage();
                            System.out.println("Me han contestado " + response);
                        }

                        //respondemos al cliente con ACK
                        message = "ACK";

                        this.network.sendMessage(operation.getPort(), message);

                    } else {

                        //respondemos
                        message = "ACK";

                        this.network.sendMessage(operation.getPort(), message);
                    }


                    System.out.println("---------------------------------------");

                    numberOfAct++;
                    this.sendMessageToServer();

                    if (numberOfAct == 10 && (this.network.getMyPort() == 6663 || this.network.getMyPort() == 6662)) {
                        numberOfAct = 0;
                        this.sendMessageToLayer1();
                    }


                }
            }

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
                Message m = new Message(port, "write", writeOperations[0], writeOperations[1]);
                operations.add(m);
            } else {
                // Read operation
                Message m = new Message(port, "read", transaction);
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
