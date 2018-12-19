package com.geekbrains.server;

import java.io.Console;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class ClientHandler {
    private Server server;
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private String clientId;

    public ClientHandler(Server server, Socket socket) {
        try {
            this.clientId = "client" + socket.getPort();
            this.server = server;
            this.socket = socket;
            this.in = new DataInputStream(socket.getInputStream());

            this.out = new DataOutputStream(socket.getOutputStream());

            startEchoThread();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public void startEchoThread() {
        new Thread(() -> {
            try {
                while (true) {
                    String msg = in.readUTF();
                    if (msg.equals("/end")) {
                        break;
                    } else if (msg.trim().isEmpty()) {
                        System.out.println("Log - " + clientId + " sent the empty message");
                        continue;
                    }
                    String outMsg = clientId + ": " + msg;
                    this.server.broadcastMsg(outMsg);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                disconnect();
            }
        }).start();
    }

    public void sendMsg(String msg) {
        try {
            out.writeUTF(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void disconnect() {
        server.unsubscribe(this);
        try {
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
