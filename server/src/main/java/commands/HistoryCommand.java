package commands;

import managers.CollectionManager;
import managers.CommandManager;
import network.requests.HistoryRequest;
import network.requests.Request;
import network.responses.HistoryResponse;
import network.responses.Response;

import java.util.ArrayList;
import java.util.List;

/**
 * Команда 'history'. Выводит последние 8 команд.
 *
 * @author steepikk
 */
public class HistoryCommand extends Command {
    private final CollectionManager collectionManager;
    private final CommandManager commandManager;

    public HistoryCommand(CollectionManager collectionManager, CommandManager commandManager) {
        super("history", "выводит последние 8 команд");
        this.collectionManager = collectionManager;
        this.commandManager = commandManager;
    }

    /**
     * Выполняет команду
     *
     * @return Успешность выполнения команды.
     */
    @Override
    public Response apply(Request request) {
        var req = (HistoryRequest) request;
        try {
            List<String> commands = new ArrayList<>();
            for (String command : commandManager.getCommandHistory()) {
                commands.add(command);
            }
            return new HistoryResponse(commands, null);
        } catch (Exception e) {
            return new HistoryResponse(null, e.toString());
        }
    }
}
