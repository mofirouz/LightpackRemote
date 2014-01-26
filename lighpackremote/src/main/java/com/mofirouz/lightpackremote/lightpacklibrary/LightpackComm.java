package com.mofirouz.lightpackremote.lightpacklibrary;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.nio.charset.Charset;

/**
 * Responsible for low level communication to Lightpack
 */
public class LightpackComm {
    private final static Charset CHARSET_UTF8 = Charset.forName("UTF-8");
    private final Socket socket;

    private ObjectOutputStream out;
    private DataInputStream in;

    public LightpackComm(Socket socket) {
        this.socket = socket;
        try {
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new DataInputStream(socket.getInputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String readResponse() {
        byte[] bytesReceived = new byte[256];
        try {

            int bytes = in.read(bytesReceived,0,bytesReceived.length);
            String data = new String(bytesReceived, CHARSET_UTF8);

            data = data.replaceAll("\n", "");

            System.out.println("*** recieved response: " + data);
            return data;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "";
    }

    public void sendCommand(LightpackCommand command) {
        sendCommand(command.getCommand());
    }

    public void sendCommand(String command) {
        try {
            byte[] bytesSent = command.getBytes(CHARSET_UTF8);
            out.write(bytesSent);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Socket getSocket() {
        return socket;
    }
}
