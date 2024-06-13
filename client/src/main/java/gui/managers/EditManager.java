package gui.managers;

import auth.SessionHandler;
import data.*;
import gui.utility.*;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.*;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

public class EditManager {
    private Stage stage;
    private Dragon dragon;
    private Localizator localizator;

    @FXML
    private Label titleLabel;
    @FXML
    private Label nameLabel;
    @FXML
    private Label ageLabel;
    @FXML
    private Label descriptionLabel;
    @FXML
    private Label colorLabel;
    @FXML
    private Label dragonCharacterLabel;
    @FXML
    private Label hasKillerLabel;
    @FXML
    private Label kNameLabel;
    @FXML
    private Label kWeightLabel;
    @FXML
    private Label kEyeColorLabel;
    @FXML
    private Label kHairColorLabel;

    @FXML
    private TextField nameField;
    @FXML
    private TextField xField;
    @FXML
    private TextField yField;
    @FXML
    private TextField ageField;
    @FXML
    private TextField descriptionField;
    @FXML
    private TextField kNameField;
    @FXML
    private TextField kWeightField;

    @FXML
    private ChoiceBox<String> colorBox;
    @FXML
    private ChoiceBox<String> dragonCharacterBox;
    @FXML
    private ChoiceBox<String> hasKillerBox;
    @FXML
    private ChoiceBox<String> kEyeColorBox;
    @FXML
    private ChoiceBox<String> kHairColorBox;

    @FXML
    private Button cancelButton;

    @FXML
    void initialize() {
        cancelButton.setOnAction(event -> stage.close());
        var colorTypes = FXCollections.observableArrayList(
                Arrays.stream(Color.values()).map(Enum::toString).collect(Collectors.toList())
        );
        colorBox.setItems(colorTypes);
        colorBox.setStyle("-fx-font: 12px \"Sergoe UI\";");
        kEyeColorBox.setItems(colorTypes);
        kEyeColorBox.setStyle("-fx-font: 12px \"Sergoe UI\";");
        kHairColorBox.setItems(colorTypes);
        kHairColorBox.setStyle("-fx-font: 12px \"Sergoe UI\";");

        var dragonCharacter = FXCollections.observableArrayList(
                Arrays.stream(DragonCharacter.values()).map(Enum::toString).collect(Collectors.toList())
        );
        dragonCharacterBox.setItems(dragonCharacter);
        dragonCharacterBox.setStyle("-fx-font: 12px \"Sergoe UI\";");

        var hasKiller = FXCollections.observableArrayList("TRUE", "FALSE");
        hasKillerBox.setItems(hasKiller);
        hasKillerBox.setValue("FALSE");
        hasKillerBox.setStyle("-fx-font: 12px \"Sergoe UI\";");


        Arrays.asList(kNameField, kWeightField, kEyeColorBox, kHairColorBox).forEach(field -> {
            field.disableProperty().bind(
                    hasKillerBox.getSelectionModel().selectedItemProperty().isEqualTo("FALSE")
            );
        });

        /*xField.textProperty().addListener((observableValue, oldValue, newValue) -> {
            if (!newValue.matches("[-\\d]{0,11}")) {
                xField.setText(oldValue);
            } else {
                if (newValue.matches(".+-.*")) {
                    Platform.runLater(() -> xField.clear());
                } else if (
                        newValue.length() == 10 && Long.parseLong(newValue) > Integer.MAX_VALUE
                                || newValue.length() == 11 && Long.parseLong(newValue) < Integer.MIN_VALUE
                ) {
                    xField.setText(oldValue);
                }
            }
        });

        yField.textProperty().addListener((observableValue, oldValue, newValue) -> {
            if (!newValue.matches("[-\\d]{0,20}")) {
                yField.setText(oldValue);
            } else {
                if (newValue.matches(".+-.*")) {
                    Platform.runLater(() -> yField.clear());
                } else if (!newValue.isEmpty() && (
                        newValue.length() == 19 && new BigInteger(newValue).compareTo(BigInteger.valueOf(Long.MAX_VALUE)) > 0
                                || newValue.length() == 20 && new BigInteger(newValue).compareTo(BigInteger.valueOf(Long.MIN_VALUE)) < 0
                )) {
                    yField.setText(oldValue);
                }
            }
        });*/
        xField.textProperty().addListener((observableValue, oldValue, newValue) -> {
            if (!newValue.matches("[-\\d.]{0,16}")) {
                xField.setText(oldValue);
            } else {
                try {
                    double xValue = Double.parseDouble(newValue);
                    if (xValue > 244) {
                        xField.setText(oldValue);
                    }
                } catch (NumberFormatException e) {
                    xField.setText(oldValue);
                }
            }
        });

        yField.textProperty().addListener((observableValue, oldValue, newValue) -> {
            if (!newValue.matches("[-\\d]{0,11}")) {
                yField.setText(oldValue);
            } else {
                try {
                    int yValue = Integer.parseInt(newValue);
                    if (yValue > 826) {
                        yField.setText(oldValue);
                    }
                } catch (NumberFormatException e) {
                    yField.setText(oldValue);
                }
            }
        });

        Arrays.asList(ageField, kWeightField).forEach(field -> {
            field.textProperty().addListener((observableValue, oldValue, newValue) -> {
                if (!field.isDisabled()) {
                    if (!newValue.matches("\\d{0,19}")) {
                        field.setText(oldValue);
                    } else {
                        if (!newValue.isEmpty() && (
                                new BigInteger(newValue).compareTo(BigInteger.valueOf(Long.MAX_VALUE)) > 0
                                        || new BigInteger(newValue).compareTo(new BigInteger(String.valueOf(0))) <= 0
                        )) {
                            field.setText(oldValue);
                        }
                    }

                }
            });
        });
    }

    @FXML
    public void ok() {
        nameField.setText(nameField.getText().trim());
        descriptionField.setText(descriptionField.getText().trim());
        kNameField.setText(kNameField.getText().trim());

        var errors = new ArrayList<String>();

        Person person = null;
        if (hasKillerBox.getValue().equals("TRUE")) {
            if (kNameField.getText().isEmpty()) errors.add(
                    "- " + localizator.getKeyString("KillerName") + " " + localizator.getKeyString("CannotBeEmpty")
            );
            if (kWeightField.getText().isEmpty()) errors.add(
                    "- " + localizator.getKeyString("KillerWeight") + " " + localizator.getKeyString("CannotBeEmpty")
            );

            Color hairColor = null;
            if (kHairColorBox.getValue() != null) {
                hairColor = Color.valueOf(kHairColorBox.getValue());
            } else {
                errors.add("- " + localizator.getKeyString("KillerHairColor") + " " + localizator.getKeyString("CannotBeEmpty"));
            }

            Color eyeColor = null;
            if (kEyeColorBox.getValue() != null) {
                eyeColor = Color.valueOf(kEyeColorBox.getValue());
            } else {
                errors.add("- " + localizator.getKeyString("KillerEyeColor") + " " + localizator.getKeyString("CannotBeEmpty"));
            }

            person = new Person(kNameField.getText(), Long.parseLong(kWeightField.getText()), eyeColor, hairColor);
        }

        if (nameField.getText().isEmpty()) errors.add(
                "- " + localizator.getKeyString("Name") + " " + localizator.getKeyString("CannotBeEmpty")
        );

        var description = descriptionField.getText();
        if (descriptionField.getText().isEmpty()) description = null;

        Color color = null;
        if (colorBox.getValue() != null) color = color.valueOf(colorBox.getValue());

        DragonCharacter dragonCharacter = null;
        if (dragonCharacterBox.getValue() != null) dragonCharacter = dragonCharacter.valueOf(dragonCharacterBox.getValue());

        if (!errors.isEmpty()) {
            DialogManager.createAlert(localizator.getKeyString("Error"), String.join("\n", errors), Alert.AlertType.ERROR, false);
        } else {
            var newDragon = new Dragon(
                    -1,
                    nameField.getText(),
                    new Coordinates(Double.parseDouble(xField.getText()), Integer.parseInt(yField.getText())),
                    LocalDate.now(),
                    Long.parseLong(ageField.getText()),
                    description,
                    color,
                    dragonCharacter,
                    person,
                    SessionHandler.getCurrentUser().getId()
            );
            if (!newDragon.validate()) {
                DialogManager.alert("InvalidDragon", localizator);
            } else {
                dragon = newDragon;
                stage.close();
            }
        }
    }

    public Dragon getDragon() {
        var tmpDragon = dragon;
        dragon = null;
        return tmpDragon;
    }

    public void clear() {
        nameField.clear();
        xField.clear();
        yField.clear();
        ageField.clear();
        descriptionField.clear();
        colorBox.valueProperty().setValue(null);
        dragonCharacterBox.valueProperty().setValue(null);
        hasKillerBox.valueProperty().setValue("FALSE");

        kNameField.clear();
        kWeightField.clear();
        kEyeColorBox.valueProperty().setValue(null);
        kHairColorBox.valueProperty().setValue(null);
    }

    public void fill(Dragon dragon) {
        nameField.setText(dragon.getName());
        xField.setText(Double.toString(dragon.getCoordinates().getX()));
        yField.setText(Integer.toString(dragon.getCoordinates().getY()));
        ageField.setText(Long.toString(dragon.getAge()));
        descriptionField.setText(dragon.getDescription());
        colorBox.setValue(dragon.getColor() == null ? null : dragon.getColor().toString());
        dragonCharacterBox.setValue(dragon.getCharacter() == null ? null : dragon.getCharacter().toString());
        hasKillerBox.setValue(dragon.getKiller() == null ? "FALSE" : "TRUE");

        if (dragon.getKiller() != null) {
            var killer = dragon.getKiller();
            kNameField.setText(killer.getName());
            kWeightField.setText(Long.toString(killer.getWeight()));
            kEyeColorBox.setValue(killer.getEyeColor().toString());
            kHairColorBox.setValue(killer.getHairColor().toString());
        } else {
            kNameField.clear();
            kWeightField.clear();
            kEyeColorBox.valueProperty().setValue(null);
            kHairColorBox.valueProperty().setValue(null);
        }
    }

    public void changeLanguage() {
        titleLabel.setText(localizator.getKeyString("EditTitle"));
        nameLabel.setText(localizator.getKeyString("Name"));
        ageLabel.setText(localizator.getKeyString("Age"));
        descriptionLabel.setText(localizator.getKeyString("Description"));
        colorLabel.setText(localizator.getKeyString("Color"));
        dragonCharacterLabel.setText(localizator.getKeyString("DragonCharacter"));
        hasKillerLabel.setText(localizator.getKeyString("HasKiller"));
        kNameLabel.setText(localizator.getKeyString("KillerName"));
        kWeightLabel.setText(localizator.getKeyString("KillerWeight"));
        kEyeColorLabel.setText(localizator.getKeyString("KillerEyeColor"));
        kHairColorLabel.setText(localizator.getKeyString("KillerHairColor"));

        cancelButton.setText(localizator.getKeyString("CancelButton"));
    }

    public void show() {
        if (!stage.isShowing()) stage.showAndWait();
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setLocalizator(Localizator localizator) {
        this.localizator = localizator;
    }
}
