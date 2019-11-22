package org.stampede.socket;

import java.io.IOException;

import org.junit.Test;

public class JavaSocketTest {

    @Test
    public void TestJavaSocket() {
        AbstractSocket sock = new NanoSocket(1334);
        try {
            sock.start();
        } catch (IOException | InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            try {
                sock.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }

}
