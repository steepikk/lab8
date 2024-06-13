package commands;


import managers.CommandManager;
import network.requests.Request;
import network.responses.HelpResponse;
import network.responses.Response;

/**
 * Команда 'help'. Выводит справку по доступным командам
 *
 * @author steepikk
 */
public class HelpCommand extends Command {
    private final CommandManager commandManager;

    public HelpCommand(CommandManager commandManager) {
        super("help", "вывести справку по доступным командам");
        this.commandManager = commandManager;
    }

    /**
     * Выполняет команду
     *
     * @return Успешность выполнения команды.
     */
    @Override
    public Response apply(Request request) {
        var helpMessage = new StringBuilder();

        commandManager.getCommands().values().forEach(command -> {
            helpMessage.append(" %-40s%-1s%n".formatted(command.getName(), command.getDescription()));
        });

        return new HelpResponse(helpMessage.toString(), null);
    }
}
