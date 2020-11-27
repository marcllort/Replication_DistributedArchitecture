package Client;

import static Utils.Utils.CLIENT_PORT;

public class Client {

    private static Transaction transaction;

    private Client(String fileName) {

        transaction = new Transaction(fileName, CLIENT_PORT);

    }

    public static void main(String[] args) {

        Client client = new Client(args[0]);

        transaction.sendTransactions();
    }
}
