package Utils;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

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
            receiverHost = InetAddress.getLocalHost();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String receiveMessage() {
        byte[] reciverBuffer = new byte[MAX_LEN];
        DatagramPacket packetReciver = new DatagramPacket(reciverBuffer, MAX_LEN);

        try {
            this.socket.receive(packetReciver);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new String(packetReciver.getData(), 0, packetReciver.getLength());
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

    public void broadcastLayer1(String message){
        for (int port :
                this.firstLayerPorts) {
            sendMessage(port, message);
        }
    }

    public void broadcastLayer2(String message){
        for (int port :
                this.secondLayerPorts) {
            sendMessage(port, message);
        }
    }

    public int getMyPort() {
        return myPort;
    }

    public void setMyPort(int myPort) {
        this.myPort = myPort;
    }

    public DatagramSocket getSocket() {
        return socket;
    }

    public void setSocket(DatagramSocket socket) {
        this.socket = socket;
    }

    public InetAddress getReceiverHost() {
        return receiverHost;
    }

    public void setReceiverHost(InetAddress receiverHost) {
        this.receiverHost = receiverHost;
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
    }

    public int[] getFirstLayerPorts() {
        return firstLayerPorts;
    }

    public void setFirstLayerPorts(int[] firstLayerPorts) {
        this.firstLayerPorts = firstLayerPorts;
    }

    public int[] getSecondLayerPorts() {
        return secondLayerPorts;
    }

    public void setSecondLayerPorts(int[] secondLayerPorts) {
        this.secondLayerPorts = secondLayerPorts;
    }
}

