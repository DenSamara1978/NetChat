package client;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Vector;

public class Controller implements Initializable {
    @FXML
    public TextArea textArea;
    @FXML
    public TextField textField;
    @FXML
    public HBox authPanel;
    @FXML
    public HBox msgPanel;
    @FXML
    public TextField loginField;
    @FXML
    public PasswordField passwordField;
    @FXML
    public ListView clientList;

    Socket socket;
    DataInputStream in;
    DataOutputStream out;

    final String IP_ADDRESS = "localhost";
    final int PORT = 8189;

    private boolean authenticated;
    private String nickname;
    private String login = "";

    Stage regStage;

    private BufferedWriter file = null;

    public void setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
        authPanel.setVisible(!authenticated);
        authPanel.setManaged(!authenticated);
        msgPanel.setVisible(authenticated);
        msgPanel.setManaged(authenticated);
        clientList.setVisible(authenticated);
        clientList.setManaged(authenticated);
        textArea.clear();
        if (!authenticated) {
            nickname = "";
            if ( file != null )
            {
                try {
                    file.close ();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                file = null;
            }
        }
        else
        {
            String filename = "history_" + login + ".txt";
            BufferedReader reader = null;
            try {
                 reader = new BufferedReader ( new FileReader ( filename ));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            String str = null;
            Vector<String> arr = new Vector<String> ();
            while (true) {
                try {
                    if ((( str = reader.readLine ()) == null)) break;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                arr.add ( str + "\n" );
            }

            int start = ( arr.size () < 100 ) ? 0 : arr.size () - 100;
            for ( int i = start; i < arr.size(); ++i )
            {
                textArea.appendText ( arr.get ( i ));
            }

            try {
                file = new BufferedWriter ( new FileWriter ( filename, true ));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        setTitle("chat 2020");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        regStage = createRegWindow();
        authenticated = false;
        Platform.runLater(() -> {
            Stage stage = (Stage) textField.getScene().getWindow();
            stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent event) {
                    System.out.println("bue");
                    if (socket != null && !socket.isClosed()) {
                        try {
                            out.writeUTF("/end");
                            socket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        });

    }

    public void connect() {
        try {
            socket = new Socket(IP_ADDRESS, PORT);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());

            new Thread(() -> {
                try {
                    //цикл аутентификации
                    while (true) {
                        String str = in.readUTF();
                        if (str.startsWith("/authok ")) {
                            setAuthenticated(true);
                            nickname = str.split(" ")[1];
                            break;
                        }
                        textArea.appendText(str + "\n");
                    }

                    setTitle("chat 2020 : " + nickname);

                    //цикл работы
                    while (true) {
                        String str = in.readUTF();
                        if (str.startsWith("/")) {
                            if (str.equals("/end")) {
                                login = "";
                                setAuthenticated(false);
                                break;
                            }
                            if (str.startsWith("/clientlist ")) {
                                String[] token = str.split(" ");
                                Platform.runLater(() -> {
                                    clientList.getItems().clear();
                                    for (int i = 1; i < token.length; i++) {
                                        clientList.getItems().add(token[i]);
                                    }
                                });
                            }
                            if (str.startsWith("/yournickis ")) {
                                nickname = str.split(" ")[1];
                                setTitle("chat 2020 : " + nickname);
                            }

                        } else {
                            textArea.appendText(str + "\n");
                            file.append ( str + "\n" );
                            file.flush ();
                        }
                    }
                } catch (SocketException e) {
                    System.out.println("Сервер отключился");
                    setAuthenticated(false);
                } catch (IOException e) {
//                    e.printStackTrace();
                    System.out.println("Соединение с сервером разорвано");
                } finally {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMsg() {
        if (textField.getText().trim().length() > 0) {
            try {
                out.writeUTF(textField.getText());
                textField.clear();
                textField.requestFocus();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public void tryToAuth(ActionEvent actionEvent) {
        if (socket == null || socket.isClosed()) {
            connect();
        }

        try {
            login = loginField.getText();
            out.writeUTF("/auth " + login + " " + passwordField.getText());
//            loginField.clear();
            passwordField.clear();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void setTitle(String title) {
        Platform.runLater(() -> {
            ((Stage) textField.getScene().getWindow()).setTitle(title);
        });
    }

    public void clickClientList(MouseEvent mouseEvent) {
//        System.out.println(clientList.getSelectionModel().getSelectedItem());
        String receiver = clientList.getSelectionModel().getSelectedItem().toString();
        textField.setText("/w " + receiver + " ");

    }

    public void registration(ActionEvent actionEvent) {
        regStage.show();
    }

    private Stage createRegWindow() {
        Stage stage = null;
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/reg.fxml"));
            Parent root1 = fxmlLoader.load();
            stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);

            RegController regController = fxmlLoader.getController();
            regController.controller = this;

            stage.setTitle("registration");
            stage.setScene(new Scene(root1, 300, 200));

        } catch (IOException e) {
            e.printStackTrace();
        }
        return stage;
    }

    public void tryRegistr(String login, String password, String nickname){
        String msg = String.format("/reg %s %s %s",login, password, nickname );

        if (socket == null || socket.isClosed()) {
            connect();
        }

        try {
            out.writeUTF(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
