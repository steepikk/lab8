package gui.managers;

import auth.SessionHandler;
import data.*;
import exceptions.*;
import gui.script.ScriptRunner;
import gui.utility.*;
import javafx.animation.FillTransition;
import javafx.animation.PathTransition;
import javafx.animation.SequentialTransition;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.paint.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import network.UDPClient;
import network.requests.*;
import network.responses.*;

import java.io.File;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

public class MainManager {
    private Localizator localizator;
    private UDPClient client;

    private Runnable authCallback;
    private volatile boolean isRefreshing = false;

    private List<Dragon> collection;

    private final HashMap<String, Locale> localeMap = new HashMap<>() {{
        put("Русский", new Locale("ru", "RU"));
        put("English(IE)", new Locale("en", "IE"));
        put("Latviešu", new Locale("lv", "LV"));
        put("Slovenčina", new Locale("sk", "SK"));
    }};

    private HashMap<String, Color> colorMap;
    private HashMap<Integer, Label> infoMap;
    private Random random;

    private EditManager editManager;
    private Stage stage;

    @FXML
    private ComboBox<String> languageComboBox;
    @FXML
    private Label userLabel;

    @FXML
    private Button helpButton;
    @FXML
    private Button infoButton;
    @FXML
    private Button addButton;
    @FXML
    private Button updateButton;
    @FXML
    private Button removeByIdButton;
    @FXML
    private Button removeGreaterButton;
    @FXML
    private Button clearButton;
    @FXML
    private Button executeScriptButton;
    @FXML
    private Button addIfMaxButton;
    @FXML
    private Button printAscendingButton;
    @FXML
    private Button historyButton;
    @FXML
    private Button filterLessThanCharacterButton;
    @FXML
    private Button countLessThanAgeButton;
    @FXML
    private Button exitButton;
    @FXML
    private Button logoutButton;

    @FXML
    private Tab tableTab;
    @FXML
    private TableView<Dragon> tableTable;

    @FXML
    private TableColumn<Dragon, Integer> ownerColumn;
    @FXML
    private TableColumn<Dragon, Integer> idColumn;
    @FXML
    private TableColumn<Dragon, String> nameColumn;
    @FXML
    private TableColumn<Dragon, Double> xColumn;
    @FXML
    private TableColumn<Dragon, Integer> yColumn;
    @FXML
    private TableColumn<Dragon, String> dateColumn;
    @FXML
    private TableColumn<Dragon, Long> ageColumn;
    @FXML
    private TableColumn<Dragon, String> descriptionColumn;
    @FXML
    private TableColumn<Dragon, String> colorColumn;
    @FXML
    private TableColumn<Dragon, String> dragonCharacterColumn;

    @FXML
    private TableColumn<Dragon, String> killerNameColumn;
    @FXML
    private TableColumn<Dragon, Long> killerWeightColumn;
    @FXML
    private TableColumn<Dragon, String> killerEyeColorColumn;
    @FXML
    private TableColumn<Dragon, String> killerHairColorColumn;

    @FXML
    private Tab visualTab;
    @FXML
    private AnchorPane visualPane;

    @FXML
    public void initialize() {
        colorMap = new HashMap<>();
        infoMap = new HashMap<>();
        random = new Random();

        languageComboBox.setItems(FXCollections.observableArrayList(localeMap.keySet()));
        languageComboBox.setStyle("-fx-font: 13px \"Sergoe UI\";");
        languageComboBox.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> {
            localizator.setBundle(ResourceBundle.getBundle("locales/gui", localeMap.get(newValue)));
            changeLanguage();
        });

        for (TableColumn<?, ?> column : tableTable.getColumns()) {
            column.prefWidthProperty().bind(Bindings.divide(tableTable.widthProperty(), tableTable.getColumns().size()));
        }

        ownerColumn.setCellValueFactory(dragon -> new SimpleIntegerProperty(dragon.getValue().getCreatorId()).asObject());
        idColumn.setCellValueFactory(dragon -> new SimpleIntegerProperty(dragon.getValue().getId()).asObject());
        nameColumn.setCellValueFactory(dragon -> new SimpleStringProperty(dragon.getValue().getName()));
        xColumn.setCellValueFactory(dragon -> new SimpleDoubleProperty(dragon.getValue().getCoordinates().getX()).asObject());
        yColumn.setCellValueFactory(dragon -> new SimpleIntegerProperty(dragon.getValue().getCoordinates().getY()).asObject());
        dateColumn.setCellValueFactory(dragon -> new SimpleStringProperty(localizator.getDate(dragon.getValue().getCreationDate())));
        ageColumn.setCellValueFactory(dragon -> new SimpleLongProperty(dragon.getValue().getAge()).asObject());
        descriptionColumn.setCellValueFactory(dragon -> new SimpleStringProperty(dragon.getValue().getDescription()));
        colorColumn.setCellValueFactory(
                dragon -> new SimpleStringProperty(
                        dragon.getValue().getColor() != null ? dragon.getValue().getColor().toString() : null
                )
        );
        dragonCharacterColumn.setCellValueFactory(
                dragon -> new SimpleStringProperty(
                        dragon.getValue().getCharacter() != null ? dragon.getValue().getCharacter().toString() : null
                )
        );

        killerNameColumn.setCellValueFactory(dragon -> {
            if (dragon.getValue().getKiller() != null) {
                return new SimpleStringProperty(dragon.getValue().getKiller().getName());
            }
            return null;
        });

        killerWeightColumn.setCellValueFactory(dragon -> {
            if (dragon.getValue().getKiller() != null) {
                return new SimpleLongProperty(dragon.getValue().getKiller().getWeight()).asObject();
            }
            return null;
        });

        killerEyeColorColumn.setCellValueFactory(dragon -> {
            if (dragon.getValue().getKiller() != null) {
                return new SimpleStringProperty(dragon.getValue().getKiller().getEyeColor().toString());
            }
            return null;
        });

        killerHairColorColumn.setCellValueFactory(dragon -> {
            if (dragon.getValue().getKiller() != null) {
                return new SimpleStringProperty(dragon.getValue().getKiller().getHairColor().toString());
            }
            return null;
        });

        tableTable.setRowFactory(tableView -> {
            var row = new TableRow<Dragon>();
            row.setOnMouseClicked(mouseEvent -> {
                if (mouseEvent.getClickCount() == 2 && !row.isEmpty()) {
                    doubleClickUpdate(row.getItem());
                }
            });
            return row;
        });

        visualTab.setOnSelectionChanged(event -> visualise(false));
    }

    @FXML
    public void exit() {
        System.exit(0);
    }

    @FXML
    public void logout() {
        SessionHandler.setCurrentUser(null);
        SessionHandler.setCurrentLanguage("Русский");
        setRefreshing(false);
        authCallback.run();
    }

    @FXML
    public void help() {
        try {
            var response = (HelpResponse) client.sendAndReceiveCommand(new HelpRequest(SessionHandler.getCurrentUser()));
            if (response.getError() != null && !response.getError().isEmpty()) {
                throw new APIException(response.getError());
            }

            DialogManager.createAlert(localizator.getKeyString("Help"), localizator.getKeyString("HelpResult"), Alert.AlertType.INFORMATION, true);
        } catch (APIException | ErrorResponseException | IOException e) {
            DialogManager.alert("UnavailableError", localizator);
        }
    }

    @FXML
    public void info() {
        try {
            var response = (InfoResponse) client.sendAndReceiveCommand(new InfoRequest(SessionHandler.getCurrentUser()));
            if (response.getError() != null && !response.getError().isEmpty()) {
                throw new APIException(response.getError());
            }

            var formatted = MessageFormat.format(
                    localizator.getKeyString("InfoResult"),
                    response.type,
                    response.size,
                    localizator.getDate(response.lastSaveTime),
                    localizator.getDate(response.lastInitTime)
            );
            DialogManager.createAlert(localizator.getKeyString("Info"), formatted, Alert.AlertType.INFORMATION, true);
        } catch (APIException | ErrorResponseException | IOException e) {
            DialogManager.alert("UnavailableError", localizator);
        }
    }

    @FXML
    public void add() {
        editManager.clear();
        editManager.show();
        var dragon = editManager.getDragon();
        if (dragon != null) {
            dragon = dragon.copy(dragon.getId(), SessionHandler.getCurrentUser().getId());

            try {
                var response = (AddResponse) client.sendAndReceiveCommand(new AddRequest(dragon, SessionHandler.getCurrentUser()));
                if (response.getError() != null && !response.getError().isEmpty()) {
                    throw new APIException(response.getError());
                }

                loadCollection();
                DialogManager.createAlert(localizator.getKeyString("Add"), localizator.getKeyString("AddResult"), Alert.AlertType.INFORMATION, false);
            } catch (APIException | ErrorResponseException e) {
                DialogManager.alert("AddErr", localizator);
            } catch (IOException e) {
                DialogManager.alert("UnavailableError", localizator);
            }
        }
    }

    @FXML
    public void update() {
        Optional<String> input = DialogManager.createDialog(localizator.getKeyString("Update"), "ID:");
        if (input.isPresent() && !input.get().equals("")) {
            try {
                var id = Integer.parseInt(input.orElse(""));
                var dragon = collection.stream()
                        .filter(d -> d.getId() == id)
                        .findAny()
                        .orElse(null);
                if (dragon == null) throw new NotFoundException();
                if (dragon.getCreatorId() != SessionHandler.getCurrentUser().getId()) throw new BadOwnerException("BadOwnerError");
                doubleClickUpdate(dragon, false);
            } catch (NumberFormatException e) {
                DialogManager.alert("NumberFormatException", localizator);
            } catch (BadOwnerException e) {
                DialogManager.alert("BadOwnerError", localizator);
            } catch (NotFoundException e) {
                DialogManager.alert("NotFoundException", localizator);
            }
        }
    }

    @FXML
    public void removeById() {
        Optional<String> input = DialogManager.createDialog(localizator.getKeyString("RemoveByID"), "ID: ");
        if (input.isPresent() && !input.get().equals("")) {
            try {
                var id = Integer.parseInt(input.orElse(""));
                var dragon = collection.stream()
                        .filter(d -> d.getId() == id)
                        .findAny()
                        .orElse(null);
                if (dragon == null) throw new NotFoundException();
                if (dragon.getCreatorId() != SessionHandler.getCurrentUser().getId()) throw new BadOwnerException("BadOwnerError");

                var response = (RemoveResponse) client.sendAndReceiveCommand(new RemoveRequest(id, SessionHandler.getCurrentUser()));
                if (response.getError() != null && !response.getError().isEmpty()) {
                    throw new APIException(response.getError());
                }

                loadCollection();
                DialogManager.createAlert(
                        localizator.getKeyString("RemoveByID"), localizator.getKeyString("RemoveByIDSuc"), Alert.AlertType.INFORMATION, false
                );
            } catch (APIException | ErrorResponseException e) {
                DialogManager.alert("RemoveByIDErr", localizator);
            } catch (IOException e) {
                DialogManager.alert("UnavailableError", localizator);
            } catch (NumberFormatException e) {
                DialogManager.alert("NumberFormatException", localizator);
            } catch (BadOwnerException e) {
                DialogManager.alert("BadOwnerError", localizator);
            } catch (NotFoundException e) {
                DialogManager.alert("NotFoundException", localizator);
            }
        }
    }

    @FXML
    public void removeGreater() {
        editManager.clear();
        editManager.show();
        var dragon = editManager.getDragon();
        if (dragon != null) {
            dragon = dragon.copy(dragon.getId(), SessionHandler.getCurrentUser().getId());

            try {
                if (dragon.getCreatorId() != SessionHandler.getCurrentUser().getId()) throw new BadOwnerException("BadOwnerError");

                var response = (RemoveGreaterResponse) client.sendAndReceiveCommand(new RemoveGreaterRequest(dragon, SessionHandler.getCurrentUser()));
                if (response.getError() != null && !response.getError().isEmpty()) {
                    throw new APIException(response.getError());
                }

                loadCollection();
                DialogManager.createAlert(
                        localizator.getKeyString("RemoveGreater"), localizator.getKeyString("RemoveGreaterSuc"), Alert.AlertType.INFORMATION, false
                );
            } catch (APIException | ErrorResponseException e) {
                DialogManager.alert("RemoveGreaterErr", localizator);
            } catch (IOException e) {
                DialogManager.alert("UnavailableError", localizator);
            } catch (NumberFormatException e) {
                DialogManager.alert("NumberFormatException", localizator);
            } catch (BadOwnerException e) {
                DialogManager.alert("BadOwnerError", localizator);
            }
        }
    }

    @FXML
    public void clear() {
        try {
            var response = (ClearResponse) client.sendAndReceiveCommand(new ClearRequest(SessionHandler.getCurrentUser()));
            if (response.getError() != null && !response.getError().isEmpty()) {
                throw new APIException(response.getError());
            }

            loadCollection();
            DialogManager.createAlert(
                    localizator.getKeyString("Clear"), localizator.getKeyString("ClearSuc"), Alert.AlertType.INFORMATION, false
            );
        } catch (APIException | ErrorResponseException e) {
            DialogManager.alert("ClearErr", localizator);
        } catch (IOException e) {
            DialogManager.alert("UnavailableError", localizator);
        }
    }

    @FXML
    public void executeScript() {
        var chooser = new FileChooser();
        chooser.setInitialDirectory(new File("."));
        var file = chooser.showOpenDialog(stage);
        if (file != null) {
            var result = (new ScriptRunner(this, localizator)).run(file.getAbsolutePath());
            if (result == ScriptRunner.ExitCode.ERROR) {
                DialogManager.alert("ScriptExecutionErr", localizator);
            } else {
                DialogManager.info("ScriptExecutionSuc", localizator);
            }
        }
    }

    @FXML
    public void histroy() {
        try {
            var response = (HistoryResponse) client.sendAndReceiveCommand(new HistoryRequest(SessionHandler.getCurrentUser()));
            if (response.getError() != null && !response.getError().isEmpty()) {
                throw new APIException(response.getError());
            }

            String formattedHistoryMessage = response.historyMessage.stream()
                    .filter(Objects::nonNull)
                    .collect(Collectors.joining("\n"));

            var formatted = MessageFormat.format(
                    localizator.getKeyString("HistoryResult"),
                    String.valueOf(response.historyMessage)
            );
            System.out.println(formattedHistoryMessage);
            DialogManager.createAlert(
                    localizator.getKeyString("History"),
                    formatted + formattedHistoryMessage,
                    Alert.AlertType.INFORMATION,
                    true
            );
        } catch (APIException | ErrorResponseException e) {
            DialogManager.alert("HistoryError", localizator);
        } catch (IOException e) {
            DialogManager.alert("UnavailableError", localizator);
        }
    }

    @FXML
    public void addIfMax() {
        editManager.clear();
        editManager.show();
        var dragon = editManager.getDragon();
        if (dragon != null) {
            dragon = dragon.copy(dragon.getId(), SessionHandler.getCurrentUser().getId());

            try {
                var response = (AddIfMaxResponse) client.sendAndReceiveCommand(new AddIfMaxRequest(dragon, SessionHandler.getCurrentUser()));
                if (response.getError() != null && !response.getError().isEmpty()) {
                    throw new APIException(response.getError());
                }

                loadCollection();
                if (response.isAdded) {
                    DialogManager.createAlert(localizator.getKeyString("Add"), localizator.getKeyString("AddResult"), Alert.AlertType.INFORMATION, false);
                } else {
                    DialogManager.createAlert(localizator.getKeyString("Add"), localizator.getKeyString("AddNotMax"), Alert.AlertType.INFORMATION, false);
                }
            } catch (APIException | ErrorResponseException e) {
                DialogManager.alert("AddErr", localizator);
            } catch (IOException e) {
                DialogManager.alert("UnavailableError", localizator);
            }
        }
    }

    @FXML
    public void printAscending() {
        try {
            var response = (PrintAscendingResponse) client.sendAndReceiveCommand(new PrintAscendingRequest(SessionHandler.getCurrentUser()));
            if (response.getError() != null && !response.getError().isEmpty()) {
                throw new APIException(response.getError());
            }

            var result = new StringBuilder();
            response.filteredDragons.forEach(dragon -> {
                result.append(new DragonShow(localizator).describe(dragon)).append("\n\n");
            });

            DialogManager.createAlert(
                    localizator.getKeyString("PrintAscending"),
                    MessageFormat.format(
                            localizator.getKeyString("PrintAscendingResult"),
                            String.valueOf(response.filteredDragons.size())
                    ) + result,
                    Alert.AlertType.INFORMATION,
                    true
            );
        } catch (APIException | ErrorResponseException e) {
            DialogManager.alert("PrintAscendingError", localizator);
        } catch (IOException e) {
            DialogManager.alert("UnavailableError", localizator);
        }
    }

    @FXML
    public void filterLessThanCharacter() {
        List<String> choices = Arrays.asList("GOOD", "FICKLE", "CHAOTIC_EVIL");

        ChoiceDialog<String> dialogCharacter = new ChoiceDialog<>(choices.get(0), choices);
        dialogCharacter.setTitle(localizator.getKeyString("FilterLessThanCharacter"));
        dialogCharacter.setHeaderText(null);
        dialogCharacter.setContentText(localizator.getKeyString("FilterLessThanCharacter") + ": ");

        var character = dialogCharacter.showAndWait();
        if (character.isPresent() && !character.get().trim().equals("")) {
            try {
                var response = (FilterLessThanCharacterResponse) client.sendAndReceiveCommand(new FilterLessThanCharacterRequest(character.get().trim(), SessionHandler.getCurrentUser()));
                if (response.getError() != null && !response.getError().isEmpty()) {
                    throw new APIException(response.getError());
                }

                var result = new StringBuilder();
                response.filteredDragons.forEach(dragon -> {
                    result.append(new DragonShow(localizator).describe(dragon)).append("\n\n");
                });

                DialogManager.createAlert(
                        localizator.getKeyString("FilterLessThanCharacter"),
                        MessageFormat.format(
                                localizator.getKeyString("FilterLessThanCharacterResult"),
                                String.valueOf(response.filteredDragons.size())
                        ) + result,
                        Alert.AlertType.INFORMATION,
                        true
                );
            } catch (APIException | ErrorResponseException e) {
                DialogManager.alert("FilterLessThanCharacterError", localizator);
            } catch (IOException e) {
                DialogManager.alert("UnavailableError", localizator);
            }
        }
    }

    @FXML
    public void countLessThanAge() {
        var dialogAge = new TextInputDialog();
        dialogAge.setTitle(localizator.getKeyString("CountLessThanAge"));
        dialogAge.setHeaderText(null);
        dialogAge.setContentText(localizator.getKeyString("CountLessThanAge") + ": ");

        var age = dialogAge.showAndWait();
        if (age.isPresent() && !age.get().trim().equals("")) {
            try {
                var response = (CountLessThanAgeResponse) client.sendAndReceiveCommand(new CountLessThanAgeRequest(age.get().trim(), SessionHandler.getCurrentUser()));
                if (response.getError() != null && !response.getError().isEmpty()) {
                    throw new APIException(response.getError());
                }

                DialogManager.createAlert(
                        localizator.getKeyString("CountLessThanAge"),
                        MessageFormat.format(localizator.getKeyString("CountLessThanAgeResult"), String.valueOf(response.countDragons)) + String.valueOf(response.countDragons),
                        Alert.AlertType.INFORMATION,
                        true
                );
            } catch (APIException | ErrorResponseException e) {
                DialogManager.alert("CountLessThanAgeError", localizator);
            } catch (IOException e) {
                DialogManager.alert("UnavailableError", localizator);
            }
        }
    }


    public void refresh() {
        Thread refresher = new Thread(() -> {
            while (isRefreshing()) {
                Platform.runLater(this::loadCollection);
                try {
                    Thread.sleep(10_000);
                } catch (InterruptedException ignored) {
                    Thread.currentThread().interrupt();
                    System.out.println("Thread was interrupted, Failed to complete operation");
                }
            }
        });
        refresher.start();
    }

    public void visualise(boolean refresh) {
        visualPane.getChildren().clear();
        infoMap.clear();

        for (var dragon: tableTable.getItems()) {
            var creatorId = dragon.getCreatorId();

            if (!colorMap.containsKey(creatorId)) {
                var r = random.nextDouble();
                var g = random.nextDouble();
                var b = random.nextDouble();
                if (Math.abs(r - g) + Math.abs(r - b) + Math.abs(b - g) < 0.6) {
                    r += (1 - r) / 1.4;
                    g += (1 - g) / 1.4;
                    b += (1 - b) / 1.4;
                }
                colorMap.put(String.valueOf(creatorId), Color.color(r, g, b));
            }

            var size = Math.min(125, Math.max(75, dragon.getAge() * 2) / 2);

            var circle = new Circle(size, colorMap.get(String.valueOf(creatorId)));
            double x = Math.abs(dragon.getCoordinates().getX());
            while (x >= 720) {
                x = x / 10;
            }
            double y = Math.abs(dragon.getCoordinates().getY());
            while (y >= 370) {
                y = y / 3;
            }
            if (y < 100) y += 125;

            var id = new Text('#' + String.valueOf(dragon.getId()));
            var info = new Label(new DragonShow(localizator).describe(dragon));

            info.setVisible(false);
            circle.setOnMouseClicked(mouseEvent -> {
                if (mouseEvent.getClickCount() == 2) {
                    doubleClickUpdate(dragon);
                }
            });

            circle.setOnMouseEntered(mouseEvent -> {
                id.setVisible(false);
                info.setVisible(true);
                circle.setFill(colorMap.get(String.valueOf(creatorId)).brighter());
            });

            circle.setOnMouseExited(mouseEvent -> {
                id.setVisible(true);
                info.setVisible(false);
                circle.setFill(colorMap.get(String.valueOf(creatorId)));
            });

            id.setFont(Font.font("Segoe UI", size / 1.4));
            info.setStyle("-fx-background-color: white; -fx-border-color: #c0c0c0; -fx-border-width: 2");
            info.setFont(Font.font("Segoe UI", 15));

            visualPane.getChildren().add(circle);
            visualPane.getChildren().add(id);

            infoMap.put(dragon.getId(), info);
            if (!refresh) {
                var path = new Path();
                path.getElements().add(new MoveTo(-500, -150));
                path.getElements().add(new HLineTo(x));
                path.getElements().add(new VLineTo(y));
                id.translateXProperty().bind(circle.translateXProperty().subtract(id.getLayoutBounds().getWidth() / 2));
                id.translateYProperty().bind(circle.translateYProperty().add(id.getLayoutBounds().getHeight() / 4));
                info.translateXProperty().bind(circle.translateXProperty().add(circle.getRadius()));
                info.translateYProperty().bind(circle.translateYProperty().subtract(120));
                var transition = new PathTransition();
                transition.setDuration(Duration.millis(750));
                transition.setNode(circle);
                transition.setPath(path);
                transition.setOrientation(PathTransition.OrientationType.NONE);
                transition.play();
            } else {
                circle.setCenterX(x);
                circle.setCenterY(y);
                info.translateXProperty().bind(circle.centerXProperty().add(circle.getRadius()));
                info.translateYProperty().bind(circle.centerYProperty().subtract(120));
                id.translateXProperty().bind(circle.centerXProperty().subtract(id.getLayoutBounds().getWidth() / 2));
                id.translateYProperty().bind(circle.centerYProperty().add(id.getLayoutBounds().getHeight() / 4));
                if (colorMap.get(String.valueOf(creatorId)) != null) {
                    var darker = new FillTransition(Duration.millis(750), circle);
                    darker.setFromValue(colorMap.get(String.valueOf(creatorId)));
                    darker.setToValue(colorMap.get(String.valueOf(creatorId)).darker().darker());
                    var brighter = new FillTransition(Duration.millis(750), circle);
                    brighter.setFromValue(colorMap.get(String.valueOf(creatorId)).darker().darker());
                    brighter.setToValue(colorMap.get(String.valueOf(creatorId)));
                    var transition = new SequentialTransition(darker, brighter);
                    transition.play();
                }
            }
        }

        for (var id : infoMap.keySet()) {
            visualPane.getChildren().add(infoMap.get(id));
        }
    }

    private void loadCollection() {
        try {
            var response = (ShowResponse) client.sendAndReceiveCommand(new ShowRequest(SessionHandler.getCurrentUser()));
            if (response.getError() != null && !response.getError().isEmpty()) {
                throw new APIException(response.getError());
            }

            setCollection(response.dragons);
            visualise(true);
        } catch (SocketTimeoutException e) {
            DialogManager.alert("RefreshLost", localizator);
        } catch (APIException | ErrorResponseException e) {
            DialogManager.alert("RefreshFailed", localizator);
        } catch (IOException e) {
            DialogManager.alert("UnavailableError", localizator);
        }
    }

    private void doubleClickUpdate(Dragon dragon) {
        doubleClickUpdate(dragon, true);
    }

    private void doubleClickUpdate(Dragon dragon, boolean ignoreAnotherUser) {
        if (ignoreAnotherUser && dragon.getCreatorId() != SessionHandler.getCurrentUser().getId()) return;

        editManager.fill(dragon);
        editManager.show();

        var updatedDragon = editManager.getDragon();
        if (updatedDragon != null) {
            updatedDragon = updatedDragon.copy(dragon.getId(), SessionHandler.getCurrentUser().getId());

            /*if (dragon.getKiller() != null && updatedDragon.getKiller != null) {
                updatedDragon.getKiller().setId(dragon.getKiller().getId());
            }*/

            try {
                if (!updatedDragon.validate()) throw new InvalidFormException();

                var response = (UpdateResponse) client.sendAndReceiveCommand(new UpdateRequest(dragon.getId(), updatedDragon, SessionHandler.getCurrentUser()));
                if (response.getError() != null && !response.getError().isEmpty()) {
                    if (response.getError().contains("BAD_OWNER")) {
                        throw new BadOwnerException("BAD_OWNER");
                    }
                    throw new APIException(response.getError());
                }

                loadCollection();
                DialogManager.createAlert(
                        localizator.getKeyString("Update"), localizator.getKeyString("UpdateSuc"), Alert.AlertType.INFORMATION, false
                );
            } catch (APIException | ErrorResponseException e) {
                DialogManager.createAlert(localizator.getKeyString("Error"), localizator.getKeyString("UpdateErr"), Alert.AlertType.ERROR, false);
            } catch (IOException e) {
                DialogManager.alert("UnavailableError", localizator);
            } catch (InvalidFormException e) {
                DialogManager.createAlert(
                        localizator.getKeyString("Update"), localizator.getKeyString("InvalidDragon"), Alert.AlertType.INFORMATION, false
                );
            } catch (BadOwnerException e) {
                DialogManager.alert("BadOwnerError", localizator);
            }
        }
    }

    public void changeLanguage() {
        userLabel.setText(localizator.getKeyString("UserLabel") + " " + SessionHandler.getCurrentUser().getName());

        exitButton.setText(localizator.getKeyString("Exit"));
        logoutButton.setText(localizator.getKeyString("LogOut"));
        helpButton.setText(localizator.getKeyString("Help"));
        infoButton.setText(localizator.getKeyString("Info"));
        addButton.setText(localizator.getKeyString("Add"));
        updateButton.setText(localizator.getKeyString("Update"));
        removeByIdButton.setText(localizator.getKeyString("RemoveByID"));
        clearButton.setText(localizator.getKeyString("Clear"));
        executeScriptButton.setText(localizator.getKeyString("ExecuteScript"));
        historyButton.setText(localizator.getKeyString("History"));
        addIfMaxButton.setText(localizator.getKeyString("AddIfMax"));
        filterLessThanCharacterButton.setText(localizator.getKeyString("FilterLessThanCharacter"));
        removeGreaterButton.setText(localizator.getKeyString("RemoveGreater"));
        countLessThanAgeButton.setText(localizator.getKeyString("CountLessThanAge"));
        printAscendingButton.setText(localizator.getKeyString("PrintAscending"));

        tableTab.setText(localizator.getKeyString("TableTab"));
        visualTab.setText(localizator.getKeyString("VisualTab"));

        ownerColumn.setText(localizator.getKeyString("Owner"));
        nameColumn.setText(localizator.getKeyString("Name"));
        dateColumn.setText(localizator.getKeyString("CreationDate"));
        ageColumn.setText(localizator.getKeyString("Age"));
        descriptionColumn.setText(localizator.getKeyString("Description"));
        colorColumn.setText(localizator.getKeyString("Color"));
        dragonCharacterColumn.setText(localizator.getKeyString("DragonCharacter"));

        killerNameColumn.setText(localizator.getKeyString("KillerName"));
        killerWeightColumn.setText(localizator.getKeyString("KillerWeight"));
        killerEyeColorColumn.setText(localizator.getKeyString("KillerEyeColor"));
        killerHairColorColumn.setText(localizator.getKeyString("KillerHairColor"));

        editManager.changeLanguage();

        loadCollection();
    }

    public void setCollection(List<Dragon> collection) {
        this.collection = collection;
        tableTable.setItems(FXCollections.observableArrayList(collection));
    }

    public void setAuthCallback(Runnable authCallback) {
        this.authCallback = authCallback;
    }

    public void setContext(UDPClient client, Localizator localizator, Stage stage) {
        this.client = client;
        this.localizator = localizator;
        this.stage = stage;

        languageComboBox.setValue(SessionHandler.getCurrentLanguage());
        localizator.setBundle(ResourceBundle.getBundle("locales/gui", localeMap.get(SessionHandler.getCurrentLanguage())));
        changeLanguage();

        userLabel.setText(localizator.getKeyString("UserLabel") + " " + SessionHandler.getCurrentUser().getName() + " (ID:" + SessionHandler.getCurrentUser().getId() + ")");
    }

    public boolean isRefreshing() {
        return isRefreshing;
    }

    public void setRefreshing(boolean refreshing) {
        isRefreshing = refreshing;
    }

    public void setEditController(EditManager editManager) {
        this.editManager = editManager;
        editManager.changeLanguage();
    }
}
