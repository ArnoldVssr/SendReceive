import java.net.*;
import java.util.ArrayList;
//import java.util.HashMap;
import java.io.*;

/*
 * Author: Arnold Visser
 * 
 * Sender program that browses files and sends it to the receiver
 * using UDP Datagram Packets or TCP
 */
public class Sender extends Thread
{
	private DatagramSocket socket;
	//private byte[] sendArray;
	//private byte[] receiveArray;
	private byte[] dataArray;
	private final int CHUNKSIZE = 64000;
	//private HashMap<Integer, int[]> SentData = new HashMap<Integer, int[]>();
	//private boolean keepAlive = true;
	
	public void run()
	{
		int port = 6066;
		String host = "localhost";
		try 
		{
			socket = new DatagramSocket(port);
			InetAddress address = InetAddress.getByName(host);
			RandomAccessFile selectedFile = new RandomAccessFile("otw.mkv", "r");
			
			int fileSize = (int) selectedFile.length();
			System.out.println("file size: "  + fileSize);
			int numPackets = 0;
			
			byte[] buf = new byte[256];

            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            socket.receive(packet);
			
			if (fileSize > CHUNKSIZE)
			{
				numPackets = fileSize/(CHUNKSIZE - 64) + 1;
			}
			else
			{
				numPackets = 1;
			}
			
			int dataLeft = fileSize;
			//int sendingSize = 0;
			System.out.println("have to send " + numPackets + " packets");
			System.out.println();
			long counter = 0;
			while (counter <= numPackets) 
			{
				dataArray = new byte[CHUNKSIZE];
				byte[] seqArr = ByteCasting.longToBytes(counter);
				//System.out.println(counter);
				System.arraycopy(seqArr, 0,	dataArray, 0, 64);
				selectedFile.read(dataArray, 64, CHUNKSIZE - 64);
				packet = new DatagramPacket(dataArray, dataArray.length, address, 2000);
				socket.send(packet);
				counter++;
				if (counter%4 == 0)
				{
					packet = new DatagramPacket(buf, buf.length);
		            socket.receive(packet);
		            ArrayList<Long> seq = (ArrayList<Long>) ByteCasting.bytesToObject(packet.getData());
				}
			}
			selectedFile.close();
			socket.disconnect();
			socket.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public static void SendUDP()
	{
		
	}
	
	public static void SendTCP()
	{
		
	}
	
	public static void SignalReceiver()
	{
		
	}
	
	public static void FileBrowser()
	{
		
	}
}