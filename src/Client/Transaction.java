package Client;

import Utils.Network;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import static Utils.Utils.*;

public class Transaction {

    private Scanner fileReader;
    private Network network;

    public Transaction(String fileName, int ownPort) {
        try {
            fileReader = new Scanner(new File(fileName));
            network = new Network(ownPort);
            network.setCoreLayerPorts(CORE_LAYER_PORTS);
            network.setFirstLayerPorts(FIRST_LAYER_PORTS);
            network.setSecondLayerPorts(SECOND_LAYER_PORTS);
        } catch (FileNotFoundException e) {
            System.out.println("Error, transactions file not found");
            System.exit(1);
        }
    }

    public void sendTransactions() {

        String[] operations;
        String transaction, numbers, orders;
        int layer;

        if (fileReader != null) {
            while (fileReader.hasNextLine()) {

                transaction = fileReader.nextLine();

                operations = transaction.split(",");

                layer = getLayer(operations);
                numbers = getNumbers(operations);
                sendTransaction(layer, numbers);
                try {
                    TimeUnit.SECONDS.sleep(5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }

            fileReader.close();
        }
    }


    private String getNumbers(String[] operations) {
        String aux;
        String numbers;
        numbers = "";
        for (int i = 1; i < operations.length - 1; i++) {
            aux = operations[i].substring(operations[i].indexOf('(') + 1);
            aux = aux.substring(0, aux.indexOf(')'));
            if (i == 1) numbers += aux;
            else numbers += "-" + aux;
        }
        return numbers;
    }

    private int getLayer(String[] operations) {
        String aux;
        int layer;
        if (operations[0].contains("<")) {
            aux = operations[0].substring(operations[0].indexOf('<') + 1);
            aux = aux.substring(0, aux.indexOf('>'));
            layer = Integer.parseInt(aux);
        } else layer = 0;
        return layer;
    }

    private void sendTransaction(int layer, String numbers) {

        int port = getPort(layer);

        network.sendMessage(port, numbers);
        System.out.println("SEND MESSAGE TO " + port + ": " + numbers);

        System.out.println("WAITING FOR RESPONSE");
        String response = network.receiveMessage();
        System.out.println("FROM PORT: " + port + " --> " + response);

    }

    private int getPort(int layer) {
        Random rand = new Random();
        int random, port = 0;
        switch (layer) {

            case 0:
                random = (rand.nextInt() % 3);
                if (random < 0) random = random * (-1);
                port = CORE_LAYER_PORT + random;
                break;
            case 1:
                random = (rand.nextInt() % 2);
                if (random < 0) random = random * (-1);
                random++;
                port = FIRST_LAYER_PORT + random;
                break;
            case 2:
                random = (rand.nextInt() % 2);
                if (random < 0) random = random * (-1);
                port = SECOND_LAYER_PORT + random;
                break;
        }
        return port;
    }

}
