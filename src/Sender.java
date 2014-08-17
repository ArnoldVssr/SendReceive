import java.net.*;
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
	private byte[] sendArray;
	//private byte[] receiveArray;
	//private byte[] dataArray;
	private final int CHUNKSIZE = 64000;
	//private HashMap<Integer, int[]> SentData = new HashMap<Integer, int[]>();
	//private boolean keepAlive = true;
	
	public void run()
	{
		int port = 6066;
		String host = "localhost";
		try 
		{
			RandomAccessFile selectedFile = null;
			selectedFile = new RandomAccessFile("drow.jpg", "r");
			
			if ((int) selectedFile.length() > CHUNKSIZE)
			{
				System.err.println("FILE TOO BIG");
				System.err.println(((int) selectedFile.length() / CHUNKSIZE)); //determine packets
			}
			sendArray = new byte[(int) selectedFile.length()];
			
			while (selectedFile.getFilePointer() < selectedFile.length())
			{	
				selectedFile.read(sendArray);
			}
			System.out.println(selectedFile.length());
			
			socket = new DatagramSocket(port);
			
			InetAddress address = InetAddress.getByName(host);
	        DatagramPacket packet = new DatagramPacket(sendArray, sendArray.length, address, 2000); 
	        socket.send(packet);			
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
