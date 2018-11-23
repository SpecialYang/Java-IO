package com.specialyang.buffer;

import java.nio.IntBuffer;

/**
 * Created by Fan Yang in 2018/11/20 下午9:39.
 */
public class TestIntBuffer {

    public static void main(String[] args) {
        IntBuffer intBuffer = IntBuffer.allocate(8);
        for (int i = 0; i < 8; i++) {
            intBuffer.put((i + 1) * 2);
        }
        intBuffer.flip();
        while(intBuffer.hasRemaining()) {
            System.out.print(intBuffer.get() + " ");
        }
        System.out.println();
    }
}
