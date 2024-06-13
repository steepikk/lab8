package managers;

import commands.Command;

import java.util.HashMap;
import java.util.Map;

/**
 * Управляет командами.
 *
 * @author steepikk
 */
public class CommandManager {
    private final Map<String, Command> commands = new HashMap<>();
    private final int COMMAND_HISTORY_SIZE = 8;
    private final String[] commandHistory = new String[COMMAND_HISTORY_SIZE];

    /**
     * Добавляет команду.
     *
     * @param commandName Название команды.
     * @param command     Команда.
     */
    public void register(String commandName, Command command) {
        commands.put(commandName, command);
    }

    /**
     * @return Словарь команд.
     */
    public Map<String, Command> getCommands() {
        return commands;
    }

    /**
     * Добавляет команду в историю команд.
     *
     * @param commandToStore Команда для добавления.
     */
    public void addToHistory(String commandToStore) {
        System.arraycopy(commandHistory, 0, commandHistory, 1, COMMAND_HISTORY_SIZE - 1);
        commandHistory[0] = commandToStore;
    }

    public String[] getCommandHistory() {
        return commandHistory;
    }
}
