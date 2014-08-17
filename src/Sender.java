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
			RandomAccessFile selectedFile = new RandomAccessFile("drow.jpg", "r");
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
			
			int offset = 0;
			
			while (selectedFile.getFilePointer() < selectedFile.length())
			{	
				if (dataLeft < CHUNKSIZE)
				{
					sendingSize = dataLeft;
					if (sendingSize < 0)
					{
						sendingSize = -1* sendingSize;
					}
				}
				else
				{
					sendingSize = CHUNKSIZE;
				}
				
				System.out.println("send size: " + sendingSize);
				System.out.println("offset: " + offset);
				
				dataArray = new byte[sendingSize];
				
				selectedFile.seek((long) offset);
				selectedFile.read(dataArray, 0, sendingSize);
				//selectedFile.read(dataArray, offset, sendingSize);
				
				DatagramPacket packet = new DatagramPacket(dataArray, dataArray.length, address, 2000); 
		        socket.send(packet);
		        
		        System.out.println("sending a packet of: " + sendingSize + "bytes");
		        System.out.println();
				
				dataLeft = dataLeft - CHUNKSIZE;
				offset = offset + sendingSize;
				
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
