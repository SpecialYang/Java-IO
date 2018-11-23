package com.specialyang.geektimejavacore;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;

/**
 * Created by Fan Yang in 2018/11/22 9:31 PM.
 */
public class BIOClient {

    private final int PORT = 8901;
    //客户端Socket
    private Socket client;

    public BIOClient() throws IOException {
        client = new Socket("localhost", PORT);
    }

    public void request() throws IOException {
        BufferedReader bufferedReader = null;
        Scanner input = new Scanner(System.in);
        while (input.hasNext()) {
            String requestContent = input.nextLine();
            OutputStream outputStream = client.getOutputStream();
            outputStream.write(requestContent.getBytes());
            outputStream.flush();
            bufferedReader = new BufferedReader(new InputStreamReader(client.getInputStream()));
            bufferedReader.lines().forEach(x -> System.out.println(x));
        }
    }

    public static void main(String[] args) {
        try {
            new BIOClient().request();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
