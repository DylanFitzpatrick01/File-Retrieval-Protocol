import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.util.Scanner;

/**
 *
 * Client class
 *
 * An instance accepts user input
 *
 */
public class Client extends Node {
	static final int CLIENT_SRC_PORT = 14430;
	static final int INGRESS_DST_PORT = 50000;
	static final String INGRESS_DST_NODE = "ingress";
	static String fileName;
	InetSocketAddress dstAddress;

	/**
	 * Constructor
	 *
	 * Attempts to create socket at given port and create an InetSocketAddress for the destinations
	 */
	Client(String dstHost, int dstPort, int srcPort) {
		try {
			dstAddress= new InetSocketAddress(dstHost, dstPort);
			socket= new DatagramSocket(srcPort);
			listener.go();
		}
		catch(java.lang.Exception e) {e.printStackTrace();}
	}


	/**
	 * Assume that incoming packets contain a String
	 * and print the string.
	 */
	public synchronized void onReceipt(DatagramPacket packet) throws InterruptedException {
		PacketContent content= PacketContent.fromDatagramPacket(packet);

		if(content.getType()==PacketContent.FILEINFO){
			Thread.sleep(500);
			System.out.println("---\n\nReceived file " + fileName + " from ingress " );
		}
		this.notify();
	}


	/**
	 * Sender Method
	 *
	 */
	public synchronized void start() throws Exception {
		boolean exit = false;

		Scanner sc = new Scanner(System.in);
		while(!exit) {
			try {
			System.out.print("\n---\n\nFile name ('exit' to cancel): ");
			fileName = sc.next();
			if (fileName.equalsIgnoreCase("exit")) {
				exit = true;
			}
			else {
				Thread.sleep(500);
				System.out.println("\n---\n\nSending request for " + fileName + " to ingress... \n"); // Send packet with file name and length
				DatagramPacket request;
				request = new GetPacketContent(fileName).toDatagramPacket();
				request.setSocketAddress(dstAddress);
				Thread.sleep(500);
				socket.send(request);
				Thread.sleep(500);
				System.out.println("---\n\nRequest sent\n");
				this.wait();
			}
			} catch (Exception e){
				e.printStackTrace();
			}
		}
	}


	/**
	 * Test method
	 *
	 * Sends a packet to a given address
	 */

	public static void main(String[] args) {
		try {
			(new Client(INGRESS_DST_NODE, INGRESS_DST_PORT, CLIENT_SRC_PORT)).start();
			System.out.println("\n-\nProgram completed\n");
		} catch(java.lang.Exception e) {e.printStackTrace();}
	}
}
