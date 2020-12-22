package client;

public class Creator extends Client {
    private String password;
    public Creator(String name, String address, int port, String passowrd) {
        super(name, address, port);
        this.password = passowrd;
    }
}
