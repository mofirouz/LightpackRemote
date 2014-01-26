package com.mofirouz.lightpackremote.lightpacklibrary;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Responsible for connecting up to a Lightpack device
 */
public class LightpackConnector {
    public static Lightpack connect(String host, int port) throws IOException {
        Socket socket = new Socket();
        socket.setTcpNoDelay(true);
        socket.setReceiveBufferSize(socket.getReceiveBufferSize() * 2);
        socket.connect(new InetSocketAddress(host, port), 5000);

        LightpackComm comm = new LightpackComm(socket);
        String version = comm.readResponse();

        System.out.println("*** Lightpack connected! - Version: " + version);

        return new Lightpack(comm, version);
    }

    public static void disconnect(Lightpack lightpack) throws IOException {
        lightpack.getComm().getSocket().close();
    }
}
