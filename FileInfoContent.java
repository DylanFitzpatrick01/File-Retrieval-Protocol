import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Class for packet content that represents file information
 *
 */
public class FileInfoContent extends PacketContent {

	String filename;
	int size;
	byte[] buffer;

	/**
	 * Constructor that takes in information about a file.
	 * @param filename Initial filename.
	 * @param size Size of filename.
	 * @param buffer byte array containing file content
	 */
	FileInfoContent(String filename, int size, byte[] buffer) {
		type = FILEINFO;
		this.filename = filename;
		this.size = size;
		this.buffer = buffer;
	}

	/**
	 * Constructs an object out of a datagram packet.
	 * @param oin reads in file information
	 */
	protected FileInfoContent(ObjectInputStream oin) {
		try {
			type = FILEINFO;
			filename = oin.readUTF();
			size = oin.readInt();
			buffer = new byte[size];
			if (buffer!=null){
				oin.read(buffer);
			}
		}
		catch(Exception e) {e.printStackTrace();}
	}

	/**
	 * Writes the content into an ObjectOutputStream
	 * @param oout writes file information
	 */
	protected void toObjectOutputStream(ObjectOutputStream oout) {
		try {
			oout.writeUTF(filename);
			oout.writeInt(size);
			if(buffer!=null){
				oout.write(buffer);
			}
		}
		catch(Exception e) {e.printStackTrace();}
	}


	/**
	 * Returns the content of the packet as String.
	 *
	 * @return Returns the content of the packet as String.
	 */
	public String toString() {
		return "Filename: " + filename + " - Size: " + size;
	}

	/**
	 * Returns the file name contained in the packet.
	 *
	 * @return Returns the file name contained in the packet.
	 */
	public String getFileName() {
		return filename;
	}

	/**
	 * Returns the file size contained in the packet.
	 *
	 * @return Returns the file size contained in the packet.
	 */
	public int getFileSize() {
		return size;
	}

	/**
	 * Returns the buffer array
	 * @return Returns the buffer array
	 */
	public byte[] bufferContent(){ return buffer; }
}
