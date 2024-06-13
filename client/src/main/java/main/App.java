package main;

import gui.managers.AuthManager;
import gui.managers.EditManager;
import gui.managers.MainManager;
import gui.utility.Localizator;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import network.UDPClient;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Locale;
import java.util.ResourceBundle;

public class App extends Application {

    private static final int PORT = 1821;
    public static UDPClient client;

    private Stage mainStage;
    private Localizator localizator;

    public static void main(String[] args) {
        try {
            client = new UDPClient(InetAddress.getLocalHost(), PORT);
            launch(args);
        } catch (IOException e) {
            System.err.println("Невозможно подключиться к серверу!");
        }
    }

    @Override
    public void start(Stage stage) {
        localizator = new Localizator(ResourceBundle.getBundle("locales/gui", new Locale("en", "IE")));
        mainStage = stage;
        authStage();
    }

    public void startMain() {
        var mainLoader = new FXMLLoader(getClass().getResource("/main2.fxml"));
        var mainRoot = loadFxml(mainLoader);

        var editLoader = new FXMLLoader(getClass().getResource("/edit.fxml"));
        var editRoot = loadFxml(editLoader);

        var editScene = new Scene(editRoot);
        var editStage = new Stage();
        editStage.setScene(editScene);
        editStage.setResizable(false);
        editStage.setTitle("Dragons");
        EditManager editManager = editLoader.getController();

        editManager.setStage(editStage);
        editManager.setLocalizator(localizator);

        MainManager mainManager = mainLoader.getController();
        mainManager.setEditController(editManager);
        mainManager.setContext(client, localizator, mainStage);
        mainManager.setAuthCallback(this::authStage);

        mainStage.setScene(new Scene(mainRoot));
        mainManager.setRefreshing(true);
        mainManager.refresh();
        mainStage.show();
    }

    private void authStage() {
        var authLoader = new FXMLLoader(getClass().getResource("/auth.fxml"));
        Parent authRoot = loadFxml(authLoader);
        AuthManager authManager = authLoader.getController();
        authManager.setCallback(this::startMain);
        authManager.setClient(client);
        authManager.setLocalizator(localizator);

        mainStage.setScene(new Scene(authRoot));
        mainStage.setTitle("Dragons");
        mainStage.setResizable(true);
        mainStage.show();
    }

    private Parent loadFxml(FXMLLoader loader) {
        Parent parent = null;
        try {
            parent = loader.load();
        } catch (IOException e) {
            System.exit(1);
        }
        return parent;
    }

    public Stage getMainStage() {
        return mainStage;
    }
}
