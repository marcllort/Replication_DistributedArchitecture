package Utils;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static Utils.Utils.MAX_LEN;


public class Network {

    private int myPort;
    private DatagramSocket socket;
    private InetAddress receiverHost;

    private int clientPort;
    private int[] coreLayerPorts;
    private int[] firstLayerPorts;
    private int[] secondLayerPorts;

    public Network(int ownPort) {
        try {
            this.myPort = ownPort;
            this.socket = new DatagramSocket(ownPort);
            this.receiverHost = InetAddress.getLocalHost();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean contains(final int[] arr, final int key) {
        Arrays.sort(arr);
        return Arrays.binarySearch(arr, key) >= 0;
    }

    public String receiveMessage() {
        byte[] receiverBuffer = new byte[MAX_LEN];
        DatagramPacket packetReceiver = new DatagramPacket(receiverBuffer, MAX_LEN);

        try {
            this.socket.receive(packetReceiver);
        } catch (SocketTimeoutException e) {
            return "timeout";
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new String(packetReceiver.getData(), 0, packetReceiver.getLength());
    }

    public void sendMessage(int port, String message) {
        String messageBuffer = this.myPort + "&" + message;
        byte[] senderBuffer = messageBuffer.getBytes();

        DatagramPacket datagramPacket = new DatagramPacket(senderBuffer, senderBuffer.length);
        datagramPacket.setAddress(receiverHost);

        datagramPacket.setPort(port);

        try {
            this.socket.send(datagramPacket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void broadcastCoreLayer(String message) {
        for (int port : this.coreLayerPorts) {
            sendMessage(port, message);
        }
    }

    public void broadcastLayer2(String message) {
        for (int port : this.secondLayerPorts) {
            sendMessage(port, message);
        }
    }

    public int getMyPort() {
        return myPort;
    }

    public int getClientPort() {
        return clientPort;
    }

    public void setClientPort(int clientPort) {
        this.clientPort = clientPort;
    }

    public int[] getCoreLayerPorts() {
        return coreLayerPorts;
    }

    public void setCoreLayerPorts(int[] coreLayerPorts) {
        this.coreLayerPorts = coreLayerPorts;
        //Timeout of 10s
        if (contains(coreLayerPorts, myPort)) {
            try {
                this.socket.setSoTimeout(100000);
            } catch (SocketException e) {
                e.printStackTrace();
            }
        }
    }

    public void setFirstLayerPorts(int[] firstLayerPorts) {
        this.firstLayerPorts = firstLayerPorts;
    }

    public void setSecondLayerPorts(int[] secondLayerPorts) {
        this.secondLayerPorts = secondLayerPorts;
    }

}

