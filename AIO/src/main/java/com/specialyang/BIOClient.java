package com.specialyang;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * Created by Fan Yang in 2018/11/22 9:31 PM.
 */
public class BIOClient {

    private final int PORT = 5556;
    //客户端Socket
    private Socket client;

    public BIOClient() throws IOException {
        client = new Socket("localhost", PORT);
    }

    public void request() throws IOException {
        BufferedReader bufferedReader =
                new BufferedReader(new InputStreamReader(client.getInputStream()));
        bufferedReader.lines().forEach(x -> System.out.println(x));
    }

    public static void main(String[] args) {
        try {
            new BIOClient().request();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
