package Layer1;

import Utils.Network;

import static Utils.Utils.*;

public class FirstLayer {

    public static void main(String args[]) {

        if (args.length != 1) {
            System.out.println("Error in number of parameters");
            return;
        }

        int port = FIRST_LAYER_PORT + Integer.parseInt(args[0]);
        Network network = new Network(port);
        System.out.println("MY PORT: " + port);

        network.setClientPort(CLIENT_PORT);
        network.setCoreLayerPorts(CORE_LAYER_PORTS);
        network.setFirstLayerPorts(FIRST_LAYER_PORTS);
        network.setSecondLayerPorts(SECOND_LAYER_PORTS);

        FirstLayerServer replication = new FirstLayerServer(Integer.parseInt(args[0]), network);

        replication.replicate();
    }

}
