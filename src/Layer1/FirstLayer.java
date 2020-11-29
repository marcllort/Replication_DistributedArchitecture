package Layer1;

import CoreLayer.CoreServer;
import Utils.Network;

import java.util.HashMap;
import java.util.Map;

import static Utils.Utils.*;
import static Utils.Utils.FIRST_LAYER_PORTS;

public class FirstLayer {
    private static Map<String,String> data = new HashMap<>();

    public static void main(String args[]) {

        if ( args.length != 1){
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

        Layer1 replication = new Layer1(network, data);
        replication.startReplication();

        TimerThread timer = new TimerThread(network, data);
        timer.start();
    }

}
