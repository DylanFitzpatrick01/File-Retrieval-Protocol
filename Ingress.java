import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

public class Ingress extends Node {
	static final int INGRESS_SRC_PORT = 50000;
	static final int CLIENT_DST_PORT = 14430;
	static final String CLIENT_DST_NODE = "client";
	InetSocketAddress dstAddressWorker;
	InetSocketAddress dstAddressClient;
	static int workerNumber;
	static final int WORKER_PORT = 20000;
	static String[] workerNodes = {"worker1", "worker2", "worker3", "worker4"};
	/*
	 *
	 */
	Ingress(String dstHostClient, int dstPortClient, int srcPort) {
		try {
			dstAddressClient = new InetSocketAddress(dstHostClient, dstPortClient);
			socket = new DatagramSocket(srcPort);
			listener.go();
		}
		catch(java.lang.Exception e) {e.printStackTrace();}
	}

	/**
	 * Assume that incoming packets contain a String and print the string.
	 */
	public void onReceipt(DatagramPacket packet) {
		try {
			PacketContent content= PacketContent.fromDatagramPacket(packet);

			if (content.getType()==PacketContent.FILEREQ) {
				Thread.sleep(500);
				System.out.println("---\n\nReceived request for file \"" + content + "\"\n");
				if(content.toString().endsWith(".txt") | content.toString().endsWith(".docx") | content.toString().endsWith(".md")) {
					dstAddressWorker = new InetSocketAddress(workerNodes[0], WORKER_PORT);
					workerNumber = 1;
				}
				else if(content.toString().endsWith(".png") | content.toString().endsWith(".jpg") | content.toString().endsWith(".jpeg")) {
					dstAddressWorker = new InetSocketAddress(workerNodes[1], WORKER_PORT);
					workerNumber = 2;
				}
				else if(content.toString().endsWith(".gif") | content.toString().endsWith(".mp3") | content.toString().endsWith(".mp4")) {
					dstAddressWorker = new InetSocketAddress(workerNodes[2], WORKER_PORT);
					workerNumber = 3;
				}
				else {
					dstAddressWorker = new InetSocketAddress(workerNodes[3], WORKER_PORT);
					workerNumber = 4;
				}
				packet.setSocketAddress(dstAddressWorker);
				socket.send(packet);
				Thread.sleep(500);
				System.out.println("---\n\nSent request for \"" + content + "\" to worker " + workerNumber + "\n");
			}
			if(content.getType()==PacketContent.FILEINFO){
				System.out.println("---\n\nReceived packet from worker " + workerNumber + "\n");
				packet.setSocketAddress(dstAddressClient);
				socket.send(packet);
			}
		}
		catch(Exception e) {e.printStackTrace();}
	}


	public synchronized void start() throws Exception {
		System.out.println("\n---\n\nWaiting...\n");
		this.wait();
	}

	/*
	 *
	 */
	public static void main(String[] args) {
		try {
			(new Ingress(CLIENT_DST_NODE, CLIENT_DST_PORT, INGRESS_SRC_PORT)).start();
			System.out.println("---\n\nProgram completed\n");
		} catch(java.lang.Exception e) {e.printStackTrace();}
	}
}
