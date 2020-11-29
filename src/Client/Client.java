package Client;

import static Utils.Utils.CLIENT_PORT;

public class Client {
    public static void main(String[] args) {

        if (args.length != 1) {
            System.out.println("Error in number of parameters");
            return;
        }

        Transaction transaction = new Transaction(args[0], CLIENT_PORT);
        transaction.sendTransactions();
    }
}
