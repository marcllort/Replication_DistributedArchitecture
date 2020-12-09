package CoreLayer;

import Utils.Network;

import static Utils.Utils.*;

public class CoreLayer {

    public static void main(String args[]) {

        if (args.length != 1) {
            System.out.println("Error in number of parameters");
            return;
        }
        int port = CORE_LAYER_PORT + Integer.parseInt(args[0]);
        Network network = new Network(port);
        System.out.println("MY PORT: " + port);

        network.setClientPort(CLIENT_PORT);
        network.setCoreLayerPorts(CORE_LAYER_PORTS);
        network.setFirstLayerPorts(FIRST_LAYER_PORTS);
        CoreServer replication;
        replication = new CoreServer(Integer.parseInt(args[0]),network);
        Thread thread = new Thread(){
            public void run(){
                replication.startRoutine();
            }
        };

        thread.start();

        replication.replicate();
    }

}
