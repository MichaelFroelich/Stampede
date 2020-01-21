package org.stampede.socket;

import java.net.*;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import java.io.*;

public class JavaSocket extends AbstractSocket {

	public JavaSocket() throws IOException, InterruptedException {
		try {
			if (SSL) {
				setupSSL();
			}
		} catch (GeneralSecurityException e) {

		}
		if (serverSocket == null) {
			serverSocket = new ServerSocket(PORT);
		}
	}

	private ServerSocket serverSocket;
	private Socket clientSocket;
	private PrintWriter out;
	private BufferedReader in;
	ConcurrentLinkedQueue<ServerSocket> servers;

	@Override
	public boolean serve() throws IOException {
		clientSocket = serverSocket.accept();
		Runnable job = new Runnable() {
			@Override
			public void run() {
				try {
					
					String path = getPath(clientSocket.getInputStream()); // TODO: do something with the path
					control(path).toString();
					
					clientSocket.getOutputStream().write(OK);
					
					clientSocket.close();
				} catch (IOException e) {
					logger.error("Java Socket error: " + e.getMessage());
				}
			}
		};
		executor.execute(job);
		return true;
	}

	@Override
	protected void close() throws IOException {
		if (in != null)
			in.close();
		if (out != null)
			out.close();
		if (clientSocket != null)
			clientSocket.close();
		if (serverSocket != null)
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

		ServerSocket s = ssf.createServerSocket(PORT);

		SSLSocket c = (SSLSocket) s.accept();
		c.startHandshake();

		out = new PrintWriter(c.getOutputStream(), true);
		in = new BufferedReader(new InputStreamReader(c.getInputStream()));

		out.print(OK);
		c.close();
	}
}
