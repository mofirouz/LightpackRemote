package com.mofirouz.lightpackremote.jlightpack;

import android.util.Log;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Responsible for connecting up to a LightPack device
 */
public class LightPackConnector {
    public static LightPack connect(String host, int port, LightPackResponseListener listener) {
        try {
            Socket socket = new Socket();
            socket.setTcpNoDelay(true);
            socket.setReceiveBufferSize(socket.getReceiveBufferSize() * 2);
            socket.connect(new InetSocketAddress(host, port), 5000);

            LightPackConnection comm = new LightPackConnection(socket, new LightPackResponseCaller(listener));
            String version = comm.readRawResponse();

            Log.i(LightPackConnector.class.getSimpleName(), "*** LightPack connected! - Version: " + version);

            return new LightPack(comm, version);
        } catch (IOException e) {
            listener.onConnectFailure();
            return null;
        }
    }

    public static void disconnect(LightPack lightPack) throws IOException {
        lightPack.getComm().getSocket().close();
    }
}
