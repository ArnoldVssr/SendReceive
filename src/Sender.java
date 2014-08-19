import java.net.*;
import java.util.ArrayList;
import java.io.*;

/*
 * Author: Arnold Visser
 * 
 * Sender program that browses files and sends it to the receiver
 * using UDP Datagram Packets or TCP
 */
public class Sender extends Thread
{
	private DatagramSocket UDPsocket;
	private ServerSocket serverSocket;
	private Socket TCPsocket;
	
	private byte[] sendbuf;
	private byte[] recbuf;
	
	private long fileSize;
	private int numPackets;
	
	private byte[] dataArray;
	private final int CHUNKSIZE = 63936;
	private final int PACKETSEND = 4;
	
	private int serverPort = 6066;
	private int dataGramPort = 6000;
	private String host = "localhost";
	
	public void run()
	{
		boolean ready = true;
		try
		{
			serverSocket = new ServerSocket(serverPort);
			RandomAccessFile selectedFile = new RandomAccessFile("ntw.mkv", "r");
			fileSize = selectedFile.length();
			getNumPackets();
			
			String[] data = {"ntw.mkv", "" + numPackets, "" + PACKETSEND};
			
			ready = Signaling(serverSocket.accept(), data);
			
		}
		catch (Exception e)
		{
			System.err.println("Error in ServerSocket.");
			e.printStackTrace();
		}
		
		if (ready)
		{
			SendUDP();
		}
		else
		{
			System.err.println("Receiver not available.");
			System.err.println("Exiting...");
			System.exit(0);
		}
	}
	
	@SuppressWarnings("unchecked")
	public void SendUDP()
	{
		try 
		{
			UDPsocket = new DatagramSocket(dataGramPort);
			InetAddress address = InetAddress.getByName(host);
			RandomAccessFile selectedFile = new RandomAccessFile("ntw.mkv", "r");
			int fileSize = (int) selectedFile.length();
			System.out.println("file size: "  + fileSize);
			int sentPackets = 0;
			
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
				
				//System.out.println("send size: " + sendingSize);
				//System.out.println("offset: " + offset);
				//System.out.println("count: " + count);
				
				dataArray = new byte[sendingSize+64];
				
				byte[] seqNum = ByteCasting.longToBytes(count);
				System.arraycopy(seqNum, 0, dataArray, 0, 64);
				
				selectedFile.seek((long) offset);
				selectedFile.read(dataArray, 64, sendingSize);
				
				DatagramPacket packet = new DatagramPacket(dataArray, dataArray.length, address, 2000); 
		        UDPsocket.send(packet);
		        sentPackets++;
		        
		        //System.out.println("sending a packet of: " + sendingSize + "bytes");
		        //System.out.println();
				
				dataLeft = dataLeft - CHUNKSIZE;
				offset = offset + sendingSize;
				count++;
				
				if ((sentPackets % PACKETSEND) == 0)
				{
					//System.out.println("sent 4 packets, waiting for reply");
					//packet = new DatagramPacket(dataArray, dataArray.length);
					//UDPsocket.receive(packet);
					//ArrayList<Long> receiverNumbers = (ArrayList<Long>) ByteCasting.bytesToObject(packet.getData());
					TCPsocket.getInputStream().read(recbuf);
					Object temparr = ByteCasting.bytesToObject(recbuf);
					ArrayList<Long> receiverNumbers = (ArrayList<Long>) temparr;
					
					//System.out.println("received Array of size: " + receiverNumbers.size());
					
					if (receiverNumbers.size() == PACKETSEND)
					{
						//continue with send
						//System.out.println("Receiver got all 4 packets.");
						sentPackets = 0;
					}
					else
					{
						//Resend
						//System.out.println("Packets dropped: " + (PACKETSEND -receiverNumbers.size()));
					}
				}
				
				
			}
			selectedFile.close();
		}
		catch (Exception e)
		{
			System.err.println("Error in sendUDP.");
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
	
	public boolean Signaling(Socket server, String[] fileData) throws Exception
	{
		boolean signal = false;
		TCPsocket = server;
		
		sendbuf = new byte[TCPsocket.getSendBufferSize()];
    	recbuf = new byte[TCPsocket.getReceiveBufferSize()];
    	
    	System.out.println("Sending file information");
		sendbuf = ByteCasting.objectToBytes(fileData);
		TCPsocket.getOutputStream().write(sendbuf);
		TCPsocket.getOutputStream().flush();

		TCPsocket.getInputStream().read(recbuf);
		signal = (Boolean) ByteCasting.bytesToObject(recbuf);
   
    	System.out.println("Receiver is ready");
    	return signal;
	}
	
	public void getNumPackets()
	{
		if (fileSize > CHUNKSIZE)
		{
			double temp = Math.ceil(fileSize / (double) CHUNKSIZE);
			numPackets = (int) temp;
		}
		else
		{
			numPackets = 1;
		}
	}
	
	public static int ReSendPackets(ArrayList<Long> receiverNumbers)
	{
		return 0;
	}
}