package udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Client {
	public void send(String host, int port) {

		try {
			InetAddress ia = InetAddress.getByName(host);
			DatagramSocket socket = new DatagramSocket(9999);
			socket.connect(ia, port);
			byte[] buffer = new byte[1024];

			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < 1; i++) {
				sb.append("123456789");
			}

			buffer = (sb.toString()).getBytes();
			DatagramPacket dp = new DatagramPacket(buffer, buffer.length);
			System.out.println(dp.getLength());
			DatagramPacket dp1 = new DatagramPacket(new byte[22312], 22312);
			socket.send(dp);

			socket.receive(dp1);
			byte[] bb = dp1.getData();
			for (int i = 0; i < dp1.getLength(); i++) {
				System.out.println((char) bb[i]);
			}
			while (true) {
				Thread.sleep(5000);
				socket.send(dp);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws IOException {
		new Client().send("127.0.0.1", 8088);
	}

}
