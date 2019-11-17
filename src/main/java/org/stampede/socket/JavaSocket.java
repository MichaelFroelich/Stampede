package org.stampede.socket;

import java.net.*;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.util.concurrent.ConcurrentLinkedQueue;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import java.io.*;

public class JavaSocket extends AbstractSocket implements AutoCloseable {

    public JavaSocket(int port) {
        super(port);
        // TODO Auto-generated constructor stub
    }

    static final String HTTP = "HTTP/1.1 ";

    static final String S200 = "200 Ok\r\n\r\n";

    static final String G503 = "503 Service Unavailable\r\n\r\n";

    private ServerSocket serverSocket;
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    ConcurrentLinkedQueue<ServerSocket> servers;
    // boolean state = false;

    @Override
    public void start() throws IOException {
        try {
            if (SSL) {
                setupSSL();
            } else {
                serverSocket = new ServerSocket(port);
            }
        } catch (GeneralSecurityException e) {
            // todo SSL task
            serverSocket = new ServerSocket(port);
        }
    }

    @Override
    public boolean serve() throws IOException {
        clientSocket = serverSocket.accept();
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        out.print(HTTP);
        out.print(S200);
        clientSocket.close();
        return true;
    }

    @Override
    public void close() throws IOException {
        in.close();
        out.close();
        clientSocket.close();
        serverSocket.close();
    }

    private KeyStore getKeyStore() throws GeneralSecurityException, IOException {
        File file;

        KeyStore keystore = KeyStore.getInstance(KEYSTORE_TYPE);

        file = new File(KEYSTORE_FILE);
        if (file.exists()) {
            keystore.load(new FileInputStream(file), KEYSTORE_PASSWORD.toCharArray());
        } else {
            keystore.load(null, null);
            keystore.store(new FileOutputStream(file), KEYSTORE_PASSWORD.toCharArray());
        }

        return keystore;
    }

    private void setupSSL() throws IOException, GeneralSecurityException {

        String KEYSTORE_PASSWORD = System.getProperty("javax.net.ssl.keyStorePassword", "changeit");
        KeyStore ks = getKeyStore();
        ks.getCertificate("changeit");
        KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
        kmf.init(ks, KEYSTORE_PASSWORD.toCharArray());

        TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
        tmf.init(ks);

        SSLContext sc = SSLContext.getInstance("TLSv1");

        TrustManager[] trustManagers = tmf.getTrustManagers();
        sc.init(kmf.getKeyManagers(), trustManagers, null);

        SSLServerSocketFactory ssf = sc.getServerSocketFactory();

        ServerSocket s = ssf.createServerSocket(port);

        SSLSocket c = (SSLSocket) s.accept();
        c.startHandshake();

        out = new PrintWriter(c.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(c.getInputStream()));

        out.print(HTTP);
        out.print(S200);
        c.close();
    }
}
