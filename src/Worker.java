import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

public class Worker extends Node{
    static final int INGRESS_PORT = 50000;
    static final String INGRESS_DST_NODE = "ingress";
    InetSocketAddress dstAddressIngress;
    static final int WORKER_PORT = 20000;

    Worker(String dstHost, int dstPort, int srcPort){
        try{
            dstAddressIngress = new InetSocketAddress(dstHost, dstPort);
            socket = new DatagramSocket(srcPort);
            listener.go();
        } catch (java.lang.Exception e) {
            e.printStackTrace();
        }
    }

    public void onReceipt(DatagramPacket packet) throws IOException {
        PacketContent content= PacketContent.fromDatagramPacket(packet);
        if (content.getType()==PacketContent.FILEREQ) {
            try {
                Thread.sleep(500);
                System.out.println("---\n\nChecking for file \"" + content + "\"\n");
                String fileName = content.toString();
                File file = new File(fileName);
                byte[] buffer = new byte[(int) file.length()];

                FileInputStream fin = new FileInputStream(file);
                int size = fin.read(buffer);

                FileInfoContent fileContent;

                if (size == -1) {
                    fin.close();
                    throw new Exception("\n---\n\nProblem with File Access:" + fileName);
                }

                fileContent = new FileInfoContent(fileName, size, buffer);
                Thread.sleep(500);
                System.out.println("---\n\nSending file " + fileContent.getFileName() + " to ingress\n");
                Thread.sleep(500);
                DatagramPacket response;
                // code for splitting into packets in case of large file
                int count = 0;
                int numberOfPackets = 0;
                int splitSize = Node.PACKETSIZE - fileName.length() - (size / 255);
                byte[] splitBuffer = new byte[splitSize];
                if (buffer.length > splitSize) {
                    for (int i = 0; i < size/splitSize; i++) {
                        // limit to 10 packets
                        if(i == 10){
                            System.err.println("---\n\nFile too big. \n");
                            break;
                        }
                        for (int j = 0; j < splitSize; j++) {
                            splitBuffer[j] = buffer[count];
                            if (count > buffer.length) {
                                // break out of loop
                                j = splitSize+1;
                            }
                            count++;
                        }
                        numberOfPackets++;
                        fileContent = new FileInfoContent(fileName, size, splitBuffer);
                        response = fileContent.toDatagramPacket();
                        response.setSocketAddress(dstAddressIngress);
                        socket.send(response);
                        System.out.println("\n\nSent " +  numberOfPackets + " packet(s) to ingress \n");
                        splitBuffer = new byte[splitSize];
                    }
                } else {
                    response = fileContent.toDatagramPacket();
                    response.setSocketAddress(dstAddressIngress);
                    Thread.sleep(500);
                    socket.send(response);
                    System.out.println("---\n\nPacket sent \n");
                }
            } catch (FileNotFoundException e) {
                System.out.println("---\n\nFile \"" + content + "\" not found\n\n");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized void start() throws Exception{
        System.out.println("\n---\n\nWaiting...\n");
        this.wait();
    }

    public static void main(String[] args) {
        try{
            (new Worker(INGRESS_DST_NODE, INGRESS_PORT, WORKER_PORT)).start();
        } catch (java.lang.Exception e) {
            e.printStackTrace();
        }
    }
}
