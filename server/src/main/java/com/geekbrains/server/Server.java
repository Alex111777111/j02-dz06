package com.geekbrains.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.Vector;

public class Server {
    private Vector<ClientHandler> clients;
    private Scanner scanner;

    public Server() {
        clients = new Vector<>();
        this.scanner = new Scanner(System.in);
    }


    public void start() {
        int port = 8189 + 1;
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            startConsoleThread();
            System.out.println("Сервер запущен на порту " + port);

            while (true) {
                Socket socket = serverSocket.accept();
                subscribe(new ClientHandler(this, socket));
                System.out.println("Подключился новый клиент");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Сервер завершил свою работу");
    }

    private void startConsoleThread() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    String msg = scanner.nextLine();
                    if (msg.trim().equals(":exit")) {
                        break;
                    } else if (msg.trim().isEmpty()){
                        continue;
                    }
                    String outMsg = "server: " + msg;
                    broadcastMsg(outMsg);
                }
            }
        }).start();
    }

    public void broadcastMsg(String msg) {
        System.out.println("Log - " + msg);
        for (ClientHandler o : clients) {
            o.sendMsg(msg);
        }
    }

    public void subscribe(ClientHandler clientHandler) {
        clients.add(clientHandler);
    }

    public void unsubscribe(ClientHandler clientHandler) {
        clients.remove(clientHandler);
    }
}
