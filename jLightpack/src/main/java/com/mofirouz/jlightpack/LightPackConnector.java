package com.mofirouz.jlightpack;

import com.mofirouz.jlightpack.api.LightPackCommand;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Responsible for connecting up to a LightPack device
 */
public class LightPackConnector {
    public static void connect(String host, int port, LightPackResponseListener listener) {
        connect(host, port, "", listener);
    }

    public static void connect(String host, int port, String apiKey, LightPackResponseListener listener) {
        try {
            Socket socket = new Socket();
            socket.setTcpNoDelay(true);
            socket.setReceiveBufferSize(socket.getReceiveBufferSize() * 2);
            socket.connect(new InetSocketAddress(host, port), 1000);

            LightPackConnection comm = new LightPackConnection(socket, new LightPackResponseCaller(listener));
            String version = comm.readRawResponse();

            if (!apiKey.isEmpty())
                comm.sendCommand(LightPackCommand.API_KEY, apiKey);

            listener.onConnect(new LightPack(comm, version));

        } catch (IOException e) {
            listener.onConnectFailure();
        }
    }

    public static void disconnect(LightPack lightPack) throws IOException {
        lightPack.getComm().unlock();
        lightPack.getComm().getSocket().close();
    }
}
