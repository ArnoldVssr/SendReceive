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
			RandomAccessFile selectedFile = new RandomAccessFile("send.txt", "r");
			int fileSize = (int) selectedFile.length();
			System.out.println("file size: "  + fileSize);
			int numPackets = 0;
			
			if (fileSize > CHUNKSIZE)
			{
				double temp = Math.ceil(fileSize / (double) CHUNKSIZE);
				numPackets = (int) temp;
			}
			else
			{
				numPackets = 1;
			}
			
			int dataLeft = fileSize;
			int sendingSize = 0;
			System.out.println("have to send " + numPackets + " packets");
			System.out.println();
			
			for (int i = 0; i < numPackets; i++)
			{
				if (dataLeft < CHUNKSIZE)
				{
					sendingSize = dataLeft;
				}
				else
				{
					sendingSize = CHUNKSIZE;
				}
				
				System.out.println("sending a packet of: " + sendingSize + "bytes");
				dataArray = new byte[sendingSize];
			
				System.out.println("this is offset: " + i * CHUNKSIZE);
				while (selectedFile.getFilePointer() < selectedFile.length())
				{	
					selectedFile.read(dataArray, 0, sendingSize);
					
				}
				
				dataLeft = dataLeft - CHUNKSIZE;
				
			}			
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
