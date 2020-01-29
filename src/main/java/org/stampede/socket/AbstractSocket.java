package org.stampede.socket;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stampede.Util;

public abstract class AbstractSocket {

	protected final Logger logger = LoggerFactory.getLogger(this.getClass());

	protected static final String HTTP = "HTTP/1.1 ";
	protected static final String S200 = "200 Ok\r\n\r\n";
	protected static final String G503 = "503 Service Unavailable\r\n\r\n";
	protected static final byte[] OK = (HTTP + S200).getBytes();
	protected static final byte[] NOTOK = (HTTP + G503).getBytes();
	private static final int SPACEBYTE = (int) " ".getBytes()[0];
	private static final int SLASHBYTE = (int) "/".getBytes()[0];
	private static final int PERCENTBYTE = (int) "%".getBytes()[0];
	private volatile boolean stopped;
	private Controller controller = null;
	private Object lock = new Object();
	protected ExecutorService executor;

	// Do not make these static as we'd like them initiated after config is loaded
	protected final boolean SSL = Util.safeGetBooleanSystemProperty("ssl");
	protected final int PORT = Integer.parseInt(Util.safeGetSystemProperty("port", "1024"));
	protected final String HOST = Util.safeGetSystemProperty("ip", getLocalHostLANAddress());
	protected final String KEYSTORE_FILE = Util.safeGetSystemProperty("javax.net.ssl.keyStore", ".keystore");
	protected final String KEYSTORE_TYPE = Util.safeGetSystemProperty("javax.net.ssl.keyStoreType", "JKS");
	protected final String KEYSTORE_PASSWORD = Util.safeGetSystemProperty("javax.net.ssl.keyStorePassword", "changeit");

	public AbstractSocket() throws IOException, InterruptedException {
	}

	public final void start() throws IOException, InterruptedException {
		logger.info("Starting a " + this.getClass().getSimpleName() + " listening on " + HOST + ":" + PORT);
		executor = Executors.newCachedThreadPool();
		controller = new Controller();
		Runnable job = new Runnable() {
			@Override
			public void run() {
				synchronized (lock) {
					while (!stopped && !Thread.currentThread().isInterrupted()) {
						try {
							if (!serve()) { // If the current implementation is non blocking
								lock.wait();
							}
						} catch (Exception ex) {
							logger.error("Listening thread failed for " + this.getClass().getSimpleName());
							Thread.currentThread().interrupt();
						}
					}
				}
			}
		};
		executor.execute(job);
	}

	public final void stop() throws IOException {
		logger.info("Closing " + this.getClass().getSimpleName() + " listening on " + HOST + ":" + PORT);
		close();
		synchronized (lock) {
			stopped = true;
			lock.notify();
		}
		executor.shutdownNow();
		logger.info("Closed " + this.getClass().getSimpleName());
	}

	protected abstract void close() throws IOException;

	/**
	 * Requests that the socket implementation begins serving
	 * 
	 * @return whether the implementation is blocking
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public boolean serve() throws IOException, InterruptedException {
		return false;
	}

	public static final String getPath(byte[] input) {
		return getPath(new String(input, StandardCharsets.UTF_8));
	}

	public static final String getPath(String input) {
		int pathindex = input.indexOf("/");
		if (pathindex != -1) {
			int endindex = input.indexOf(" ", pathindex);
			if (endindex != -1) {
				return input.substring(pathindex + 1, endindex);
			}
		}
		return "";
	}

	static String getPath(InputStream inputStream) throws IOException {
		byte[] bytes = new byte[100];
		inputStream.read(bytes, 0, bytes.length);

		int pathindex = -1;
		for (int i = 0; i < 100; i++) {
			if (bytes[i] == SLASHBYTE) {
				pathindex = i;
				break;
			}
		}

		int endindex = -1;
		for (int i = pathindex + 1; i < 100; i++) {
			if (bytes[i] == SPACEBYTE) {
				endindex = i - 1;
				break;
			}
		}

		byte[] outputbytes = new byte[endindex - pathindex];
		int j = 0;
		for (int i = pathindex + 1; i <= endindex; i++) {
			if (bytes[i] == PERCENTBYTE) {
				char[] chararray = new char[] { (char) bytes[++i], (char) bytes[++i] };
				outputbytes[j++] = ((byte) Integer.parseInt(new String(chararray), 16));
			} else {
				outputbytes[j++] = bytes[i];
			}
		}
		return new String(outputbytes, StandardCharsets.UTF_8);
	}

	static String getLocalHostLANAddress() {
		try {
			return getLocalHostLAN().getHostAddress();
		} catch (UnknownHostException e) {
			return "localhost";
		}
	}

	/**
	 * Returns an <code>InetAddress</code> object encapsulating what is most likely
	 * the machine's LAN IP address.
	 * <p/>
	 * This method is intended for use as a replacement of JDK method
	 * <code>InetAddress.getLocalHost</code>, because that method is ambiguous on
	 * Linux systems. Linux systems enumerate the loopback network interface the
	 * same way as regular LAN network interfaces, but the JDK
	 * <code>InetAddress.getLocalHost</code> method does not specify the algorithm
	 * used to select the address returned under such circumstances, and will often
	 * return the loopback address, which is not valid for network communication.
	 * Details <a href=
	 * "http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4665037">here</a>.
	 * <p/>
	 * This method will scan all IP addresses on all network interfaces on the host
	 * machine to determine the IP address most likely to be the machine's LAN
	 * address. If the machine has multiple IP addresses, this method will prefer a
	 * site-local IP address (e.g. 192.168.x.x or 10.10.x.x, usually IPv4) if the
	 * machine has one (and will return the first site-local address if the machine
	 * has more than one), but if the machine does not hold a site-local address,
	 * this method will return simply the first non-loopback address found (IPv4 or
	 * IPv6).
	 * <p/>
	 * If this method cannot find a non-loopback address using this selection
	 * algorithm, it will fall back to calling and returning the result of JDK
	 * method <code>InetAddress.getLocalHost</code>.
	 * <p/>
	 *
	 * https://stackoverflow.com/questions/9481865/getting-the-ip-address-of-the-current-machine-using-java/38342964
	 *
	 * @throws UnknownHostException If the LAN address of the machine cannot be
	 *                              found.
	 */
	private static InetAddress getLocalHostLAN() throws UnknownHostException {
		try {
			InetAddress candidateAddress = null;
			// Iterate all NICs (network interface cards)...
			for (Enumeration<NetworkInterface> ifaces = NetworkInterface.getNetworkInterfaces(); ifaces
					.hasMoreElements();) {
				NetworkInterface iface = (NetworkInterface) ifaces.nextElement();
				// Iterate all IP addresses assigned to each card...
				for (Enumeration<InetAddress> inetAddrs = iface.getInetAddresses(); inetAddrs.hasMoreElements();) {
					InetAddress inetAddr = (InetAddress) inetAddrs.nextElement();
					if (!inetAddr.isLoopbackAddress()) {

						if (inetAddr.isSiteLocalAddress()) {
							// Found non-loopback site-local address. Return it immediately...
							return inetAddr;
						} else if (candidateAddress == null) {
							// Found non-loopback address, but not necessarily site-local.
							// Store it as a candidate to be returned if site-local address is not
							// subsequently found...
							candidateAddress = inetAddr;
							// Note that we don't repeatedly assign non-loopback non-site-local addresses as
							// candidates,
							// only the first. For subsequent iterations, candidate will be non-null.
						}
					}
				}
			}
			if (candidateAddress != null) {
				// We did not find a site-local address, but we found some other non-loopback
				// address.
				// Server might have a non-site-local address assigned to its NIC (or it might
				// be running
				// IPv6 which deprecates the "site-local" concept).
				// Return this non-loopback candidate address...
				return candidateAddress;
			}
			// At this point, we did not find a non-loopback address.
			// Fall back to returning whatever InetAddress.getLocalHost() returns...
			InetAddress jdkSuppliedAddress = InetAddress.getLocalHost();
			if (jdkSuppliedAddress == null) {
				throw new UnknownHostException("The JDK InetAddress.getLocalHost() method unexpectedly returned null.");
			}
			return jdkSuppliedAddress;
		} catch (Exception e) {
			UnknownHostException unknownHostException = new UnknownHostException(
					"Failed to determine LAN address: " + e);
			unknownHostException.initCause(e);
			throw unknownHostException;
		}
	}
	
	protected byte[] control(String path) {
		//controller.checkRole("role");
		switch(path.charAt(0)) {
			case '0':
				break;
		}
		return null;
	}
}
