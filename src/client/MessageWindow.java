package client;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import server.Server;

import java.sql.*;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Semaphore;


public class MessageWindow extends Application {
    private Client client;
    private static TextArea chatarea;
    private static TextArea messagearea;
    private String url = "jdbc:postgresql://localhost/AnnoChat";
    private Connection connn;
    private ResultSet rs;
    private Semaphore connections;
    int chatAddress;
    //private static Timer timer;

    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle("AnnoChat");
        stage.setHeight(600);
        stage.setWidth(800);
        stage.setMaxHeight(600);
        stage.setMaxWidth(800);
        stage.setMinHeight(500);
        stage.setMinWidth(500);
        stage.setResizable(false);
        GridPane root = new GridPane();


        //Ustawienie logo
        Image logo = new Image("logoall.png");
        ImageView logov = new ImageView(logo);
        root.add(logov, 0, 0, 4, 1);


        //Stworz pokoj
        //usuniecie wiadomosci
        TextField sec = new TextField();
        sec.setMaxWidth(30);
        Label secs = new Label("Usunięcie wiadomości po(sec):");
        root.add(secs, 0, 1, 1, 1);
        root.add(sec, 1, 1, 1, 1);
        //ilosc osob
        Label size = new Label("Ilość osób:");
        ChoiceBox sizet = new ChoiceBox();
        sizet.getItems().add("2");
        sizet.getItems().add("3");
        sizet.getItems().add("4");
        sizet.setValue("2");
        root.add(size, 0, 2, 1, 1);
        root.add(sizet, 1, 2, 1, 1);

        //stworz pokoj
        Button create = new Button("Stwórz Pokój");
        create.setPrefSize(360, 30);
        root.add(create, 0, 4, 2, 1);


        //Dolacz do pokoju
        //adres
        Label alert = new Label("Błędne hasło!!");
        PasswordField addressT = new PasswordField();
        addressT.setPrefWidth(200);
        Label address = new Label("Adres:");
        root.add(address, 2, 1, 1, 1);
        root.add(addressT, 3, 1, 1, 1);
        //haslo
        PasswordField passwdT = new PasswordField();
        addressT.setPrefWidth(200);
        Label passwd = new Label("Hasło:");
        root.add(passwd, 2, 2, 1, 1);
        root.add(passwdT, 3, 2, 1, 1);
        //dolacz
        Button conn = new Button("Dołącz do pokoju");
        conn.setPrefSize(360, 30);
        root.add(conn, 2, 4, 2, 1);
        //alert


        //ustawienia
        root.setVgap(25);
        root.setHgap(10);
        root.setPadding(new Insets(10));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        scene.getStylesheets().add(MessageWindow.class.getResource("style.css").toExternalForm());
        stage.show();
        Generate g = new Generate();

        //okno chatu
        GridPane chat = new GridPane();

        chatarea = new TextArea();
        chatarea.setEditable(false);
        chatarea.setPrefWidth(600);
        chatarea.setPrefHeight(500);
        chat.add(chatarea, 0, 0, 1, 10);

        messagearea = new TextArea();
        messagearea.setPrefHeight(90);
        messagearea.setPrefWidth(600);
        chat.add(messagearea, 0, 11, 1, 1);

        Button sendButton = new Button("Wyślij");
        sendButton.setPrefSize(200, 90);
        chat.add(sendButton, 1, 11, 1, 1);

        //wysylanie wiadomosci
        sendButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                if (!messagearea.getText().equals("")) {
                    client.send(messagearea.getText());
                    messagearea.setText("");
                }
            }
        });

        messagearea.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                if (keyEvent.getCode().equals(KeyCode.ENTER)) {
                    StringBuffer sb = new StringBuffer(messagearea.getText());
                    sb.deleteCharAt(sb.length() - 1);
                    if (!String.valueOf(sb).equals("")) {
                        client.send(String.valueOf(sb));
                        messagearea.setText("");
                    }
                }
            }
        });

        Label addressW = new Label("Adres:");
        chat.add(addressW, 1, 0, 1, 1);
        TextField addressShow = new TextField();
        addressShow.setEditable(false);

        //chat.add(addressShow,1,1,1,1);

        PasswordField addressShowP = new PasswordField();
        addressShowP.setEditable(false);
        chat.add(addressShowP, 1, 1, 1, 1);

        Label passwordW = new Label("Haslo:");
        chat.add(passwordW, 1, 2, 1, 1);
        TextField passwordShow = new TextField();
        passwordShow.setEditable(false);
        //chat.add(passwordShow,1,3,1,1);

        PasswordField passwordShowP = new PasswordField();
        passwordShowP.setEditable(false);
        chat.add(passwordShowP, 1, 3, 1, 1);

        Button show = new Button("Pokaż");
        chat.add(show, 1, 4, 1, 1);
        show.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                if (show.getText().equals("Ukryj")) {
                    chat.getChildren().remove(addressShow);
                    chat.add(addressShowP, 1, 1, 1, 1);
                    chat.getChildren().remove(passwordShow);
                    chat.add(passwordShowP, 1, 3, 1, 1);
                    show.setText("Pokaż");
                } else {
                    chat.getChildren().remove(addressShowP);
                    chat.add(addressShow, 1, 1, 1, 1);
                    chat.getChildren().remove(passwordShowP);
                    chat.add(passwordShow, 1, 3, 1, 1);
                    show.setText("Ukryj");
                }


            }
        });

        Scene chatW = new Scene(chat);
        chatW.getStylesheets().add(MessageWindow.class.getResource("style.css").toExternalForm());
        chat.setVgap(10);
        chat.setHgap(10);
        chat.setPadding(new Insets(5, 5, 5, 5));

        create.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {

                stage.setResizable(false);
                chatAddress = Integer.parseInt(g.generateAddress(4));
                addressShow.setText(Integer.toString(chatAddress));
                addressShowP.setText(addressShow.getText());

                passwordShow.setText(g.generatePassword(12));

                passwordShowP.setText(passwordShow.getText());

                Server s = new Server();
                s.start(chatAddress, passwordShow.getText());
                stage.setScene(chatW);
                stage.show();


                client = new Creator(g.generateName(8), "localhost", chatAddress, passwordShow.getText());
                client.send("\\pw:" + passwordShow.getText());
                stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                    @Override
                    public void handle(WindowEvent windowEvent) {
                        client.send("\\delete");
                        s.stop();
                        System.exit(0);
                    }
                });
            }
        });

        conn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                if (passwdT.getText().equals(""))
                    passwdT.setText("Brak");

                root.getChildren().remove(alert);
                client = new Client(g.generateName(8), "localhost", Integer.parseInt(addressT.getText()));

                //polaczenie z baza danych
                String url = "jdbc:postgresql://localhost/AnnoChat";
                Properties props = new Properties();
                props.setProperty("user", "user");
                props.setProperty("password", "user");
                props.setProperty("ssl", "false");
                connections = new Semaphore(1);

                //uzycie semafora
                try {
                    connections.acquire();
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                }
                try {
                    connn = DriverManager.getConnection(url, props);
                } catch (SQLException e) {
                    System.out.println(e.getLocalizedMessage());
                }
                connections.release();

                //zapytanie o poprawnosc hasla
                Statement stmt = null;
                try {
                    stmt = connn.createStatement();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
                String sql = "SELECT passwd FROM chatroom WHERE port=" + addressT.getText();
                try {
                    rs = stmt.executeQuery(sql);
                } catch (SQLException e) {
                    System.out.println(e.getLocalizedMessage());
                }

                try {
                    rs.next();
                    if (rs.getString("passwd").equals(passwdT.getText())) {
                        root.getChildren().remove(alert);
                        stage.setResizable(false);
                        stage.setScene(chatW);
                        stage.show();
                        addressShow.setText(addressT.getText());
                        addressShowP.setText(addressShow.getText());
                        passwordShow.setText(passwdT.getText());
                        passwordShowP.setText(passwordW.getText());
                    } else {
                        root.add(alert, 2, 5, 2, 1);
                    }

                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }

                stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                    @Override
                    public void handle(WindowEvent windowEvent) {

                    }
                });
            }
        });
    }

    @Override
    public void stop() throws Exception {

    }

    public static void printConsole(String message) {
        chatarea.setText(chatarea.getText() + message + "\n");
    }
}

