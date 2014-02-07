package com.mofirouz.lightpackremote.jlightpack;

import com.mofirouz.lightpackremote.jlightpack.api.LightPackCommand;
import com.mofirouz.lightpackremote.jlightpack.api.LightPackResponse;
import com.mofirouz.lightpackremote.jlightpack.api.LightPackResponse.LightPackApiResponse;
import com.mofirouz.lightpackremote.jlightpack.api.LockError;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Responsible for low level communication to LightPack.
 * All calls are non-blocking and happen on a new thread.
 *
 * There are a fix number of 20 calls that can be made at any onetime. More than that results in invocation queues.
 *
 * NB: Even though internally, I've written this to be sync-IO (because commands and responses are very small),
 * the operation outside of this class are all Async.
 */
public class LightPackConnection {
    private final static Charset CHARSET_UTF8 = Charset.forName("UTF-8");
    private final Socket socket;
    private final LightPackResponseCaller lightPackResponseCaller;
    private volatile boolean lock = false;

    private OutputStream out;
    private InputStream in;

    private ExecutorService executorService = Executors.newFixedThreadPool(20);

    public LightPackConnection(Socket socket, LightPackResponseCaller parser) {
        this.socket = socket;
        this.lightPackResponseCaller = parser;
        try {
            out = socket.getOutputStream();
            in = socket.getInputStream();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void requestInfo(LightPackCommand command) {
        lock();
        Map<LightPackCommand, LightPackResponse> result = mapResponse(command, sendCommand(command.getCommand()));
        unlock();

        lightPackResponseCaller.callback(result);
    }

    public void sendCommand(LightPackCommand command, LightPackApiResponse value) {
        sendCommand(command, value.name().toLowerCase());
    }

    public void sendCommand(LightPackCommand command, String value) {
        lock();
        Map<LightPackCommand, LightPackResponse> result = mapResponse(command, sendCommand(command.getCommand() + ":" + value));
        unlock();

        lightPackResponseCaller.callback(result);
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

    private Map<LightPackCommand, LightPackResponse> mapResponse(LightPackCommand command, String rawResponse) {
        Map<LightPackCommand, LightPackResponse> result = new HashMap<LightPackCommand, LightPackResponse>();
        String[] response = rawResponse.split(":");

        if (response.length == 1) {
            result.put(command, LightPackResponse.parseResponse(response[0]));
        } else {
            for (int i = 0; i < response.length; i++) {
                result.put(command, LightPackResponse.parseResponse(response[i+1]));
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

        sendCommand(LightPackCommand.LOCK.getCommand());
    }

    private void unlock() throws LockError {
        sendCommand(LightPackCommand.UNLOCK.getCommand());

        lock = false;
    }
}
