package com.mofirouz.lightpackremote.lightpacklibrary;

import com.googlecode.androidannotations.annotations.Background;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Responsible for low level communication to Lightpack
 */
public class LightpackComm {
    private final static Charset CHARSET_UTF8 = Charset.forName("UTF-8");
    private final Socket socket;
    private volatile boolean lock = false;

    private OutputStream out;
    private InputStream in;

    private ExecutorService executorService = Executors.newCachedThreadPool();

    public LightpackComm(Socket socket) {
        this.socket = socket;
        try {
            out = socket.getOutputStream();
            in = socket.getInputStream();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Map<String, String> getInfo(LightpackCommand command) {
        lock();
        Map<String, String> result = mapResponse(command, sendCommand(command.getCommand()));
        unlock();

        return result;
    }

    public Map<String, String> sendCommand(LightpackCommand command, String value) {
        lock();
        Map<String, String> result = mapResponse(command, sendCommand(command.getCommand() + ":" + value));
        unlock();

        return result;
    }

    public String readRawResponse() {
        byte[] bytesReceived = new byte[256];
        try {

            int bytes = in.read(bytesReceived,0,bytesReceived.length);
            String data = new String(bytesReceived, CHARSET_UTF8).trim();

            data = data.replaceAll("\n", "");

            System.out.println(" +++ " + data);
            return data;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "";
    }

    private Map<String, String> mapResponse(LightpackCommand command, String rawResponse) {
        Map result = new HashMap<String, String>();
        String[] response = rawResponse.split(":");

        if (response.length == 1) {
            result.put(command.getResponse(), response[0]);
        } else {
            for (int i = 0; i < response.length; i++) {
                result.put(response[i], response[i+1]);
                i++;
            }
        }

        return result;
    }

    private String sendCommand(final String command) {
        try {
            return executorService.submit(new Callable<String>() {
                @Override
                public String call() throws Exception {
                    byte[] bytesSent = (command+"\n").getBytes();
                    out.write(bytesSent);
                    out.flush();
                    System.out.println(" --- " + new String(bytesSent));
                    return readRawResponse();
                }
            }).get();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    protected Socket getSocket() {
        return socket;
    }

    private void lock() throws LockError {
        if (lock)
            return;

        sendCommand(LightpackCommand.LOCK.getCommand());
    }

    private void unlock() throws LockError {
        sendCommand(LightpackCommand.UNLOCK.getCommand());

        lock = false;
    }
}
