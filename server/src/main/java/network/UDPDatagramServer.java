package network;

import com.google.common.primitives.Bytes;
import handlers.CommandHandler;
import main.App;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.TreeMap;

public class UDPDatagramServer extends UDPServer {
    private final int PACKET_SIZE = 1024;
    private final int DATA_SIZE = PACKET_SIZE - 2;

    private final DatagramSocket datagramSocket;

    private final Logger logger = App.logger;

    public UDPDatagramServer(InetAddress address, int port, CommandHandler commandHandler) throws SocketException {
        super(new InetSocketAddress(address, port), commandHandler);
        this.datagramSocket = new DatagramSocket(getAddr());
        this.datagramSocket.setReuseAddress(true);
    }

    @Override
    public Pair<Byte[], SocketAddress> receiveData() throws IOException {
        var received = false;
        int count = 0;
        var result = new byte[0];
        var receivedPackets = new HashMap<Integer, byte[]>();
        SocketAddress addr = null;

        while(!received) {
            var data = new byte[PACKET_SIZE];

            var dp = new DatagramPacket(data, PACKET_SIZE);
            datagramSocket.receive(dp);
            count += 1;

            addr = dp.getSocketAddress();
            logger.info("Получено \"" + new String(data) + "\" от " + dp.getAddress());
            logger.info("Последние 2 байта: " + data[data.length - 2]);

            int num_packets = data[data.length - 2];
            int num_packet = data[data.length - 1];

            receivedPackets.put(num_packet, Arrays.copyOf(data, data.length - 2));

            if (count == num_packets) {
                received = true;
                logger.info("Получение данных от " + dp.getAddress() + " окончено");
            }

        }

        TreeMap<Integer, byte[]> sortedReceivedPackets = new TreeMap<>(receivedPackets);

        for (byte[] packetData : sortedReceivedPackets.values()) {
            result = Bytes.concat(result, packetData);
        }

        return new ImmutablePair<>(ArrayUtils.toObject(result), addr);
    }

    @Override
    public void sendData(byte[] data, SocketAddress addr) throws IOException {
        int num_packets = (int) Math.ceil(data.length / (double)DATA_SIZE);
        byte[][] ret = new byte[(int)Math.ceil(data.length / (double)DATA_SIZE)][DATA_SIZE];

        int start = 0;
        for(int i = 0; i < ret.length; i++) {
            ret[i] = Arrays.copyOfRange(data, start, start + DATA_SIZE);
            start += DATA_SIZE;
        }

        logger.info("Отправляется " + ret.length + " чанков...");

        for(int i = 0; i < ret.length; i++) {
            var chunk = ret[i];
            var newChunk = Bytes.concat(chunk, new byte[]{(byte) num_packets, (byte) (i + 1)});
            var dp = new DatagramPacket(newChunk, PACKET_SIZE, addr);
            datagramSocket.send(dp);
        }

        logger.info("Отправка данных завершена");
    }

    @Override
    public void connectToClient(SocketAddress addr) throws SocketException {
        datagramSocket.connect(addr);
    }

    @Override
    public void disconnectFromClient() {
        datagramSocket.disconnect();
    }

    @Override
    public void close() {
        datagramSocket.close();
    }
}
