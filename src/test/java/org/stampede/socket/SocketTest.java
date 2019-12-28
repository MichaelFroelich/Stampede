package org.stampede.socket;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Test class for sockets Add: Thread.sleep(Long.MAX_VALUE); after the start
 * method of a second to manually test this
 * 
 * @author Michael
 *
 */
public class SocketTest {

	static int runItteration = 10;

	static int port = 1024;

	@BeforeClass
	public static void init() throws Exception {
		System.setProperty("port", String.valueOf(port));
	}

	@Test
	public void TestGrizzlySocket() throws Exception {
		AbstractSocket sock = new GrizzlySocket();
		sock.start();
		long start = System.currentTimeMillis();
		for (int i = 0; i < runItteration; i++)
			assertEquals(200, getResponse());
		System.out.println("GRIZZY=============" + (System.currentTimeMillis() - start));
		sock.close();
		Thread.sleep(10);
	}

	@Test
	public void TestJavaSocket() throws Exception {
		AbstractSocket sock = new JavaSocket();
		sock.start();
		long start = System.currentTimeMillis();
		for (int i = 0; i < runItteration; i++)
			assertEquals(200, getResponse());
		System.out.println("JAVA=============" + (System.currentTimeMillis() - start));
		sock.close();
		Thread.sleep(10);
	}

	@Test
	public void TestJeroSocket() throws Exception {
		AbstractSocket sock = new JeroSocket();
		sock.start();
		long start = System.currentTimeMillis();
		for (int i = 0; i < runItteration; i++)
			assertEquals(200, getResponse());
		System.out.println("JERO=============" + (System.currentTimeMillis() - start));
		sock.close();
		Thread.sleep(10);
	}

	@Test
	public void TestNanoSocket() throws Exception {
		AbstractSocket sock = new NanoSocket();
		sock.start();
		long start = System.currentTimeMillis();
		for (int i = 0; i < runItteration; i++)
			assertEquals(200, getResponse());
		System.out.println("NANO=============" + (System.currentTimeMillis() - start));
		sock.close();
		Thread.sleep(10);
	}

	@Test
	public void TestNettySocket() throws Exception {
		AbstractSocket sock = new NettySocket();
		sock.start();
		long start = System.currentTimeMillis();
		for (int i = 0; i < runItteration; i++)
			assertEquals(200, getResponse());
		System.out.println("NETTY=============" + (System.currentTimeMillis() - start));
		sock.close();
		Thread.sleep(10);
	}

	int getResponse() throws Exception {
		URL url = new URL("http",AbstractSocket.getLocalHostLANAddress(),port,"/hellothisisalongstring");
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setRequestMethod("GET");
		con.connect();
		return con.getResponseCode();
	}
	
	String chinese = "GET /你好 HTTP/1.1";
	
	/**
	 * Tests getting UTF characters from a stream
	 * Ignore this test because gradle can't pass it
	 * @throws IOException
	 */
	@Test
	public void testUTF8Stuff() throws IOException {
		// take the copy of the stream and re-write it to an InputStream
		PipedInputStream in = new PipedInputStream();
		final PipedOutputStream out = new PipedOutputStream(in);
		new Thread(new Runnable() {
		    public void run () {
		        try {
		        	for(byte b : chinese.getBytes())
		        		out.write(b);
		        }
		        catch (IOException e) {
		        }
		        finally {
		            // close the PipedOutputStream here because we're done writing data
		            // once this thread has completed its run
		            if (out != null) {
		                try {
							out.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
		            }
		        }   
		    }
		}).start();
		
		String path = AbstractSocket.getPath(in);
		byte[] actualbytes = path.getBytes();
		byte[] expectedbytes = "你好".getBytes();
		assertTrue(Arrays.equals(actualbytes, expectedbytes));
	}
}
