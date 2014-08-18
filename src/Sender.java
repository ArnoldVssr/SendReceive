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
	private final int CHUNKSIZE = 63936;
	private final int PACKETSEND = 4;
	
	private int port = 6066;
	private String host = "localhost";
	
	//private HashMap<Integer, int[]> SentData = new HashMap<Integer, int[]>();
	//private boolean keepAlive = true;
	
	public void run()
	{
		SendUDP();
	}
	
	public void SendUDP()
	{
		try 
		{
			socket = new DatagramSocket(port);
			InetAddress address = InetAddress.getByName(host);
			RandomAccessFile selectedFile = new RandomAccessFile("drow.jpg", "r");
			int fileSize = (int) selectedFile.length();
			System.out.println("file size: "  + fileSize);
			int numPackets = 0;
			int sentPackets = 0;
			
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
			long count = 0;
			
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
				System.out.println("count: " + count);
				
				dataArray = new byte[sendingSize+64];
				
				byte[] seqNum = ByteCasting.longToBytes(count);
				System.arraycopy(seqNum, 0, dataArray, 0, 64);
				
				selectedFile.seek((long) offset);
				selectedFile.read(dataArray, 64, sendingSize);
				//selectedFile.read(dataArray, offset, sendingSize);
				
				DatagramPacket packet = new DatagramPacket(dataArray, dataArray.length, address, 2000); 
		        socket.send(packet);
		        sentPackets++;
		        
		        System.out.println("sending a packet of: " + sendingSize + "bytes");
		        System.out.println();
				
				dataLeft = dataLeft - CHUNKSIZE;
				offset = offset + sendingSize;
				count++;
				
				if ((sentPackets % PACKETSEND) == 0)
				{
					System.out.println("sent 4 packets, waiting for reply");
					packet = new DatagramPacket(dataArray, dataArray.length);
					socket.receive(packet);
					ArrayList<Long> receiverNumbers = (ArrayList<Long>) ByteCasting.bytesToObject(packet.getData());
					
					if (receiverNumbers.size() == 4)
					{
						//continue with send
						System.out.println("Receiver got all 4 packets.");
					}
					else
					{
						//Resend
						System.out.println("Packets dropped: " + (4 -receiverNumbers.size()));
					}
				}
				
				
			}
			selectedFile.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
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
	
	public static int ReSendPackets(ArrayList<Long> receiverNumbers)
	{
		return 0;
	}
}