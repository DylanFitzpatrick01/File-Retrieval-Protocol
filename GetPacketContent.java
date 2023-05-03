import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Class for packet content that represents acknowledgements
 *
 */
public class GetPacketContent extends PacketContent {

	String info;

	/**
	 * Constructor that takes in information about a file.
	 * @param info information about the file
	 */
	GetPacketContent(String info) {
		type = FILEREQ;
		this.info = info;
	}

	/**
	 * Constructs an object out of a datagram packet.
	 * @param oin reads in the file info
	 */
	protected GetPacketContent(ObjectInputStream oin) {
		try {
			type = FILEREQ;
			info = oin.readUTF();
		}
		catch(Exception e) {e.printStackTrace();}
	}

	/**
	 * Writes the content into an ObjectOutputStream
	 * @param oout writes the file info
	 */
	protected void toObjectOutputStream(ObjectOutputStream oout) {
		try {
			oout.writeUTF(info);
		}
		catch(Exception e) {e.printStackTrace();}
	}



	/**
	 * Returns the content of the packet as String.
	 *
	 * @return Returns the content of the packet as String.
	 */
	public String toString() {
		return info;
	}

	/**
	 * Returns the info contained in the packet.
	 *
	 * @return Returns the info contained in the packet.
	 */
	public String getPacketInfo() {
		return info;
	}
}
