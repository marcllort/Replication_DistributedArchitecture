package Client;

import Utils.Network;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Random;
import java.util.Scanner;

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
            System.err.println("Error, transactions file not found");
            System.exit(1);
        }
    }

    public void sendTransactions() {
        int layer;
        String numbers;
        String[] operations;

        if (fileReader != null) {
            while (fileReader.hasNextLine()) {
                operations = fileReader.nextLine().split(",");

                layer = getLayer(operations);
                numbers = getNumbers(operations);

                sendTransaction(layer, numbers);
                sleep();
            }
            fileReader.close();
        }
    }

    private void sendTransaction(int layer, String numbers) {
        int port = getPort(layer);

        network.sendMessage(port, numbers);

        String response = network.receiveMessage();
        System.out.println("[SEND " + port + "] " + numbers +" [RECEIVED] " + response);
    }

    private String getNumbers(String[] operations) {
        String numberString, numbers = "";

        for (int i = 1; i < operations.length - 1; i++) {

            numberString = operations[i].substring(operations[i].indexOf('(') + 1);
            numberString = numberString.substring(0, numberString.indexOf(')'));

            if (i == 1) {
                numbers += numberString;
            } else {
                numbers += "-" + numberString;
            }
        }

        return numbers;
    }

    private int getLayer(String[] operations) {
        int layer;
        String layerString;

        if (operations[0].contains("<")) {
            layerString = operations[0].substring(operations[0].indexOf('<') + 1);
            layerString = layerString.substring(0, layerString.indexOf('>'));
            layer = Integer.parseInt(layerString);
        } else {
            layer = 0;
        }

        return layer;
    }

    private int getPort(int layer) {
        Random randGen = new Random();
        int port = 0;

        switch (layer) {
            case 0:
                port = CORE_LAYER_PORT + randGen.nextInt(3);
                break;
            case 1:
                port = FIRST_LAYER_PORT + randGen.nextInt(2);
                break;
            case 2:
                port = SECOND_LAYER_PORT + randGen.nextInt(2);
                break;
        }

        return port;
    }

}
