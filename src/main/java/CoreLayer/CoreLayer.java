package CoreLayer;

import Utils.Network;
import Websockets.NodeRole;

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
        switch (Integer.parseInt(args[0])){
            case 0:
                 replication = new CoreServer(NodeRole.A1,network);
                break;
            case 1:
                 replication = new CoreServer(NodeRole.A2,network);
                break;
            default:
                 replication = new CoreServer(NodeRole.A3,network);
                break;
        }
        replication.startRoutine();
        replication.replicate();
    }

}
