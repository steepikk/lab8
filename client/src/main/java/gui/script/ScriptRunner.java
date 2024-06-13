package gui.script;

import exceptions.*;
import gui.managers.DialogManager;
import gui.managers.MainManager;
import gui.utility.Localizator;
import javafx.scene.control.Alert;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class ScriptRunner {
    public enum ExitCode {
        OK,
        ERROR
    }

    private final List<String> scriptStack = new ArrayList<>();
    private Localizator localizator;
    private MainManager mainManager;

    public ScriptRunner(MainManager mainManager, Localizator localizator) {
        this.localizator = localizator;
        this.mainManager = mainManager;
    }

    public ExitCode run(String path) {
        String userCommand;
        ExitCode commandStatus;
        scriptStack.add(path);
        try (Scanner scriptScanner = new Scanner(new File(path))) {
            if (!scriptScanner.hasNext()) throw new NoSuchElementException();

            do {
                userCommand = scriptScanner.nextLine().trim();
                while (scriptScanner.hasNextLine() && userCommand.isEmpty()) {
                    userCommand = scriptScanner.nextLine().trim();
                }

                if (userCommand.startsWith("execute_script")) {
                    for (String scriptPath : scriptStack) {
                        if (new File(path).getAbsolutePath().equals(scriptPath)) {
                            throw new ScriptRecursionException();
                        }
                    }
                }
                commandStatus = launchCommand((userCommand + " ").split(" ", 2));
            } while (commandStatus == ExitCode.OK && scriptScanner.hasNextLine());


            if (commandStatus == ExitCode.ERROR && !(userCommand.startsWith("execute_script") && !userCommand.split(" ", 2)[1].trim().isEmpty())) {
                DialogManager.alert("CheckScriptErr", localizator);
            }

            return commandStatus;

        } catch (FileNotFoundException exception) {
            DialogManager.alert("FileNotFoundException", localizator);
        } catch (NoSuchElementException exception) {
            DialogManager.alert("EmptyFileErr", localizator);
        } catch (ScriptRecursionException exception) {
            DialogManager.alert("ScriptRecursionException", localizator);
        } catch (IllegalStateException exception) {
            DialogManager.alert("UnexpectedErr", localizator);
            System.exit(0);
        } finally {
            scriptStack.remove(scriptStack.size() - 1);
        }

        return ExitCode.ERROR;
    }

    private ExitCode launchCommand(String[] userCommand) {
        userCommand[0] = userCommand[0].trim();
        userCommand[1] = userCommand[1].trim();

        if (userCommand[0].equals("")) return ExitCode.OK;

        var noSuchCommand = false;
        switch (userCommand[0]) {
            case "help" -> mainManager.help();
            case "info" -> mainManager.info();
            case "add" -> mainManager.add();
            case "update" -> mainManager.update();
            case "remove_by_id" -> mainManager.removeById();
            case "clear" -> mainManager.clear();
            case "execute_script" -> mainManager.executeScript();
            //case "head" -> mainManager.head();
            case "add_if_max" -> mainManager.addIfMax();
            //case "add_if_min" -> mainManager.addIfMin();
            //case "sum_of_price" -> mainManager.sumOfPrice();
            //case "filter_by_price" -> mainManager.filterByPrice();
            //case "filter_contains_part_number" -> mainManager.filterContainsPartNumber();
            case "exit" -> mainManager.exit();
            default -> {
                noSuchCommand = true;
                var formatted = MessageFormat.format(localizator.getKeyString("CommandNotFound"), userCommand[0]);
                DialogManager.createAlert(localizator.getKeyString("Error"), formatted, Alert.AlertType.ERROR, true);
            }
        };

        if (noSuchCommand) return ExitCode.ERROR;
        return ExitCode.OK;
    }
}
