package CoreLayer;

import Utils.Network;

import static Utils.Utils.*;

public class CoreLayer {

    public static void main(String args[]) {

        int port = CORE_LAYER_PORT + Integer.parseInt(args[0]);
        Network network = new Network(port);
        System.out.println("MY PORT: " + port);

        network.setClientPort(CLIENT_PORT);

        network.setCoreLayerPorts(CORE_LAYER_PORTS);

        network.setFirstLayerPorts(FIRST_LAYER_PORTS);


        CoreServer replication = new CoreServer(network);
        replication.startReplication();
    }

}
