package main;

import commands.*;
import handlers.CommandHandler;
import managers.AuthManager;
import managers.CollectionManager;
import managers.CommandManager;
import managers.db.PersistenceManager;
import network.UDPDatagramServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utility.Commands;

import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.sql.Connection;

import static managers.db.ConnectionManager.getConnection;
import static managers.db.DatabaseManager.createDatabaseIfNotExists;

/**
 * Серверная часть приложения.
 *
 * @author steepikk
 */
public class App {
    public static final int PORT = 1821;

    public static Logger logger = LogManager.getLogger("ServerLogger");

    public static void main(String[] args) {
        createDatabaseIfNotExists();

        Connection connection = getConnection();
        PersistenceManager persistenceManager = new PersistenceManager(connection);
        AuthManager authManager = new AuthManager(connection, logger);

        CollectionManager collectionManager = new CollectionManager(persistenceManager);

        var commandManager = new CommandManager() {{
            register(Commands.REGISTER, new RegisterCommand(authManager));
            register(Commands.AUTHENTICATE, new AuthenticateCommand(authManager));
            register(Commands.HELP, new HelpCommand(this));
            register(Commands.INFO, new InfoCommand(collectionManager));
            register(Commands.SHOW, new ShowCommand(collectionManager));
            register(Commands.ADD, new AddCommand(collectionManager));
            register(Commands.UPDATE, new UpdateCommand(collectionManager));
            register(Commands.REMOVE_BY_ID, new RemoveCommand(collectionManager));
            register(Commands.CLEAR, new ClearCommand(collectionManager));
            register(Commands.COUNT_LESS_THAN_AGE, new CountLessThanAgeCommand(collectionManager));
            register(Commands.ADD_IF_MAX, new AddIfMaxCommand(collectionManager));
            register(Commands.FILTER_LESS_THAN_CHARACTER, new FilterLessThanCharacterCommand(collectionManager));
            register(Commands.HISTORY, new HistoryCommand(collectionManager, this));
            register(Commands.REMOVE_GREATER, new RemoveGreaterCommand(collectionManager));
            register(Commands.PRINT_ASCENDING, new PrintAscendingCommand(collectionManager));
        }};

        try {
            var server = new UDPDatagramServer(InetAddress.getLocalHost(), PORT, new CommandHandler(commandManager, authManager));
            server.run();
        } catch (SocketException e) {
            logger.fatal("Случилась ошибка сокета", e);
        } catch (UnknownHostException e) {
            logger.fatal("Неизвестный хост", e);
        }

    }
}
