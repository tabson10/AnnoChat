package server;

import server.ClientInfo;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Properties;

public class Server {
    private  DatagramSocket socket;
    private  boolean running;
    private  int ClientID;
    private  int port;
    private  String password;
    private  Connection conn;
    private  ArrayList<ClientInfo> clients = new ArrayList<ClientInfo>();

    public void start(int port, String passwd) {
        try {
            this.port = port;
            socket = new DatagramSocket(port);
            password = passwd;
            running = true;
            listen();
            System.out.println("Server started on port, " + port);

            //polaczenie z baza danych
            String url = "jdbc:postgresql://localhost/AnnoChat";
            Properties props = new Properties();
            props.setProperty("user","server");
            props.setProperty("password","server");
            props.setProperty("ssl","false");
            try {
                conn = DriverManager.getConnection(url, props);
            } catch (SQLException e) {
                System.out.println(e.getLocalizedMessage());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void broadcast(String message) {
        for(ClientInfo info: clients) {
            send(message, info.getAddress(), info.getPort());
        }
    }

    private void send(String message, InetAddress address, int port) {
        try {
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
                            broadcast(message);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }; listenThread.start();
    }


    private boolean isCommand(String message, DatagramPacket packet) throws SQLException {
        if(message.startsWith("\\com:")) {
            String name = message.substring(message.indexOf(":")+1);
            if(ClientID==0) {
                clients.add(new ClientInfo(name, ClientID++, packet.getAddress(), packet.getPort(), true));
                broadcast("Użytkownik, " + name + " założył pokój");
            }
            else {
                clients.add(new ClientInfo(name, ClientID++, packet.getAddress(), packet.getPort(), false));
                broadcast("Użytkownik, " + name + " się połączył");
            }


            return true;
        }
        if(message.startsWith("\\pw:")) {
            password = message.substring(message.indexOf(":") + 1);
            Statement stmt = conn.createStatement();
            String sql ="INSERT INTO chatroom VALUES ('"+this.port+"', '"+password+"')";
            try {
                stmt.executeUpdate(sql);
            } catch (SQLException e) {
                System.out.println(e.getLocalizedMessage());
            }

            return  true;
        }
        if(message.startsWith("\\delete")) {
            Statement stmt = conn.createStatement();
            String sql ="DELETE FROM chatroom WHERE port="+this.port;
            try {
                stmt.executeUpdate(sql);
            } catch (SQLException e) {
                System.out.println(e.getLocalizedMessage());
            }

            return  true;
        }

        return false;
    }



    public void stop() {
        running = false;
    }

}
