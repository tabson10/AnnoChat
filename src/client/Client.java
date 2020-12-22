package client;


import client.MessageWindow;

import javax.xml.crypto.Data;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Client {
    private DatagramSocket socket;
    private InetAddress address;
    private String name;
    private int port;

    private boolean running;

    public Client(String name, String address, int port) {
        try {
            this.address = InetAddress.getByName(address);
            this.port = port;
            this.name = name;

            socket = new DatagramSocket();
            running = true;
            listen();
            send("\\com:" + name);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void send(String message) {
        try {
            if(!message.startsWith("\\")) {
                message = name+": "+message;
            }
            message += "\\e";
            byte[] data = message.getBytes();
            DatagramPacket packet = new DatagramPacket(data, data.length, address, port);
            socket.send(packet);
            System.out.println("Sent message to, " + address.getHostAddress() + ":" + port);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void listen() {
        Thread listenThread = new Thread("Listener") {
            public void run() {
                try {
                    while (running) {

                        byte[] data = new byte[1024];
                        DatagramPacket packet = new DatagramPacket(data, data.length);
                        socket.receive(packet);

                        String message = new String(data);
                        //koniec wiadomosci
                        message = message.substring(0, message.indexOf("\\e"));

                        //Zarzadzanie wiadomoscia
                        if(!isCommand(message, packet)) {
                            MessageWindow.printConsole(message);
                        }


                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }; listenThread.start();
    }

    private boolean isCommand(String message, DatagramPacket packet) {
        if(message.startsWith("\\com:")) {

        }

        return false;
    }
}
