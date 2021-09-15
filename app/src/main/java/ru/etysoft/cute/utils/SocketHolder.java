package ru.etysoft.cute.utils;

import java.net.URISyntaxException;
import java.util.ArrayList;

import ru.etysoft.cuteframework.sockets.paradigm.Chat.ChatSocket;

public class SocketHolder {

    private static ChatSocket chatSocket;
    private static ArrayList<Runnable> onSocketCreatedRunnables = new ArrayList<>();

    public static void initialize(final String token)
    {

                try {
                   chatSocket = createNewSocket(token);
                } catch (Exception e) {
                    e.printStackTrace();
                }

    }

    public static void addOnSocketCreated(Runnable runnable)
    {
        onSocketCreatedRunnables.add(runnable);
    }

    public static void clear()
    {
        onSocketCreatedRunnables.clear();
    }

    private static ChatSocket createNewSocket(final String token) throws URISyntaxException {
        ChatSocket chatSocket = new ChatSocket(token);
        chatSocket.getWebSocket().addOnClose(new Runnable() {
            @Override
            public void run() {
                System.out.println("Trying to create new socket...");
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            SocketHolder.chatSocket = createNewSocket(token);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                thread.start();

            }
        });
        try
        {
            for(Runnable runnable : onSocketCreatedRunnables)
            {
                runnable.run();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
      return chatSocket;
    }

    public static ChatSocket getChatSocket() {
        return chatSocket;
    }
}
