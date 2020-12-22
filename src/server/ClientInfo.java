package server;

import java.net.InetAddress;

public class ClientInfo {
    private InetAddress address;
    private int port;
    private String name;
    private int id;
    private boolean creator;

    public ClientInfo(String name, int id, InetAddress address, int port, boolean creator) {
        this.name = name;
        this.id = id;
        this.address = address;
        this.port = port;
        this.creator = creator;
    }

    public String getName() {
        return name;
    }

    public int getID(){
        return id;
    }

    public InetAddress getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }
}
