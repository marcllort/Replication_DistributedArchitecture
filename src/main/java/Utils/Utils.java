package Utils;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class Utils {

    public static final String READ_ACTION = "read";
    public static final String WRITE_ACTION = "write";

    public static final int CLIENT_PORT = 6000;
    public static final int CORE_LAYER_PORT = 6010;
    public static final int FIRST_LAYER_PORT = 6110;
    public static final int SECOND_LAYER_PORT = 6210;
    public static final int[] CORE_LAYER_PORTS = {6010, 6011, 6012};
    public static final int[] FIRST_LAYER_PORTS = {6110, 6111};
    public static final int[] SECOND_LAYER_PORTS = {6210, 6211};
    public static final int[] CORE_LAYER_SERVER_PORTS = {4010, 4011, 4012};
    public static final int[] FIRST_LAYER_SERVER_PORTS = {4013, 4014};
    public static final int[] SECOND_LAYER_SERVER_PORTS = {4015, 4016};

    public static final int TIMEOUT = 5;
    public static int MAX_LEN = 100;


    public static void printSeparator() {
        System.out.println("------------------------------------------");
    }

    public static void printMessage(Message message) {
        if (message.getAction().equals(READ_ACTION)) {
            System.out.println("READ Action received");
            System.out.println("PORT: " + message.getPort() + " - LINE: " + message.getLine());
        } else {
            System.out.println("WRITE Action received");
            System.out.println("PORT: " + message.getPort() + " - LINE: " + message.getLine() + "- VALUE: " + message.getValue());
        }
    }

    public static ArrayList<Message> parseMessage(String message) {

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

    public static String hashMapToMessage(Map<Integer, Integer> infoHashMap) {
        String message = "";

        for (Integer key : infoHashMap.keySet()) {
            message = message + key + ";" + infoHashMap.get(key) + "-";
        }
        message = message.substring(0, message.length() - 1);

        return message;
    }

    public static String manageRead(Message receivedMessage, String returnMessage, Map<Integer, Integer> infoHashMap) {
        printMessage(receivedMessage);

        // Get value from hashmap
        String message = String.valueOf(infoHashMap.getOrDefault(receivedMessage.getLine(), -1));

        // Send value to the client
        if (message.equals("-1")) {
            message = "NULL";
        }

        returnMessage += message + "/";
        printSeparator();

        return returnMessage;
    }

    public static void sleep() {
        try {
            TimeUnit.SECONDS.sleep(TIMEOUT);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
