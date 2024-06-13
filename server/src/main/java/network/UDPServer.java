package network;

import handlers.CommandHandler;
import main.App;
import network.requests.Request;
import network.responses.NoSuchCommandResponse;
import network.responses.Response;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.SerializationException;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;

/**
 * UDP обработчик запросов
 *
 * @author steepikk
 */
abstract class UDPServer {
    private final InetSocketAddress addr;
    private final CommandHandler commandHandler;
    private final ExecutorService readService;
    private final ExecutorService processingService;
    private final ForkJoinPool sendPool;

    private final Logger logger = App.logger;

    private boolean running = true;

    public UDPServer(InetSocketAddress addr, CommandHandler commandHandler) {
        this.addr = addr;
        this.commandHandler = commandHandler;
        this.readService = Executors.newCachedThreadPool();
        this.processingService = Executors.newCachedThreadPool();
        this.sendPool = ForkJoinPool.commonPool();
    }

    public InetSocketAddress getAddr() {
        return addr;
    }

    /**
     * Получает данные с клиента.
     * Возвращает пару из данных и адреса клиента
     */
    public abstract Pair<Byte[], SocketAddress> receiveData() throws IOException;

    /**
     * Отправляет данные клиенту
     */
    public abstract void sendData(byte[] data, SocketAddress addr) throws IOException;

    public abstract void connectToClient(SocketAddress addr) throws SocketException;

    public abstract void disconnectFromClient();

    public abstract void close();

    /*public void run() {
        logger.info("Сервер запущен по адресу " + addr);

        while (running) {
            Pair<Byte[], SocketAddress> dataPair;
            try {
                dataPair = receiveData();
            } catch (Exception e) {
                logger.error("Ошибка получения данных : " + e.toString(), e);
                disconnectFromClient();
                continue;
            }

            var dataFromClient = dataPair.getKey();
            var clientAddr = dataPair.getValue();

            try {
                connectToClient(clientAddr);
                logger.info("Соединено с " + clientAddr);
            } catch (Exception e) {
                logger.error("Ошибка соединения с клиентом : " + e.toString(), e);
            }

            Request request;
            try {
                request = SerializationUtils.deserialize(ArrayUtils.toPrimitive(dataFromClient));
                logger.info("Обработка " + request + " из " + clientAddr);
            } catch (SerializationException e) {
                logger.error("Невозможно десериализовать объект запроса.", e);
                disconnectFromClient();
                continue;
            }

            Response response = null;
            try {
                response = commandHandler.handle(request);
            } catch (Exception e) {
                logger.error("Ошибка выполнения команды : " + e.toString(), e);
            }
            if (response == null) response = new NoSuchCommandResponse(request.getName());

            var data = SerializationUtils.serialize(response);
            logger.info("Ответ: " + response);

            try {
                sendData(data, clientAddr);
                logger.info("Отправлен ответ клиенту " + clientAddr);
            } catch (Exception e) {
                logger.error("Ошибка ввода-вывода : " + e.toString(), e);
            }

            disconnectFromClient();
            logger.info("Отключение от клиента " + clientAddr);
        }

        close();
    }*/

    public void run() {
        logger.info("Сервер запущен по адресу " + addr);

        while (running) {
            try {
                Pair<Byte[], SocketAddress> dataPair = receiveData();

                readService.submit(() -> {
                    var dataFromClient = dataPair.getKey();
                    var clientAddr = dataPair.getValue();

                    try {
                        //connectToClient(clientAddr);
                        logger.info("Соединено с " + clientAddr);
                    } catch (Exception e) {
                        logger.error("Ошибка соединения с клиентом : " + e.toString(), e);
                    }

                    try {
                        Request request = SerializationUtils.deserialize(ArrayUtils.toPrimitive(dataFromClient));
                        logger.info("Обработка " + request + " из " + clientAddr);

                        processingService.submit(() -> {
                            Response response = null;
                            try {
                                response = commandHandler.handle(request);
                            } catch (Exception e) {
                                logger.error("Ошибка выполнения команды : " + e.toString(), e);
                            }
                            if (response == null) response = new NoSuchCommandResponse(request.getName());

                            var data = SerializationUtils.serialize(response);
                            logger.info("Ответ: " + response);

                            sendPool.submit(() -> {
                                try {
                                    sendData(data, clientAddr);
                                    logger.info("Отправлен ответ клиенту " + clientAddr);
                                } catch (Exception e) {
                                    logger.error("Ошибка ввода-вывода : " + e.toString(), e);
                                }
                            });
                        });

                    } catch (SerializationException e) {
                        logger.error("Невозможно десериализовать объект запроса.", e);
                        disconnectFromClient();
                    }

                    disconnectFromClient();
                    logger.info("Отключение от клиента " + clientAddr);
                });

            } catch (Exception e) {
                logger.error("Ошибка получения данных : " + e.toString(), e);
                disconnectFromClient();
            }
        }

        readService.shutdown();
        processingService.shutdown();
        sendPool.shutdown();
        close();
    }
}
