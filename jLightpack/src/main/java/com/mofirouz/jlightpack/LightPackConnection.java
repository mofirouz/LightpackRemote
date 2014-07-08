package com.mofirouz.jlightpack;

import com.mofirouz.jlightpack.api.LightPackCommand;
import com.mofirouz.jlightpack.api.LightPackResponse;
import com.mofirouz.jlightpack.api.LightPackResponse.LightPackApiResponse;
import com.mofirouz.jlightpack.api.LockException;

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

    private ExecutorService executorService = Executors.newSingleThreadExecutor();

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
        try {
            lock();
            Map<LightPackCommand, LightPackResponse> result = mapResponse(command, sendCommand(command.getCommand()));
//            unlock();

            lightPackResponseCaller.callback(result);
        } catch (Exception e) {
            lightPackResponseCaller.onError(command, e);
        }
    }

    public void sendCommand(LightPackCommand command, LightPackApiResponse value) {
        sendCommand(command, value.name().toLowerCase());
    }

    public void sendCommand(LightPackCommand command, String value) {
        try {
            lock();
            Map<LightPackCommand, LightPackResponse> result = mapResponse(command, sendCommand(command.getCommand() + ":" + value));
            //unlock();

            lightPackResponseCaller.callback(result);
        } catch (Exception e) {
            lightPackResponseCaller.onError(command, e);
        }
    }

    public String readRawResponse() {
        String data = "";
        byte[] bytesReceived = new byte[256];
        try {
            if (!socket.isClosed()) {
                int bytes = in.read(bytesReceived,0,bytesReceived.length);
                data = new String(bytesReceived, CHARSET_UTF8).trim();
                data = data.replaceAll("\n", "");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return data;
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

    private String sendCommand(final String command) throws Exception {
        try {
            return executorService.submit(new Callable<String>() {
                @Override
                public String call() throws Exception {
                    byte[] bytesSent = (command+"\n").getBytes();
                    out.write(bytesSent);
                    out.flush();
                    return readRawResponse();
                }
            }).get();
        } catch (Exception e) {
            throw new Exception(e.getCause());
        }
    }

    protected Socket getSocket() {
        return socket;
    }

    private void lock() throws LockException {
        if (lock)
            return;

        try {
            sendCommand(LightPackCommand.LOCK.getCommand());
        } catch (Exception e) {
            throw new LockException(e);
        }

    }

    protected void unlock() throws LockException {
        try {
            sendCommand(LightPackCommand.UNLOCK.getCommand());
        } catch (Exception e) {
            throw new LockException(e);
        }

        lock = false;
    }
}
