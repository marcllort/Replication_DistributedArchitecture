package Utils;

public class Message {

    private int port;
    private String action;
    private int line;
    private int value;

    public Message(int port, String action, int line, int value) {
        this.port = port;
        this.action = action;
        this.line = line;
        this.value = value;
    }

    public Message(String port, String action, String line, String value) {
        this.port = Integer.parseInt(port);
        this.action = action;
        this.line = Integer.parseInt(line);
        this.value = Integer.parseInt(value);
    }

    public Message(String port, String action, String line) {
        this.port = Integer.parseInt(port);
        this.action = action;
        this.line = Integer.parseInt(line);
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line = line;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
