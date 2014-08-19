import java.net.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Timer;
import java.util.concurrent.TimeUnit;
import java.awt.List;
import java.io.*;
import java.nio.channels.*;
/*
 * Author: Timotius Johannes Petrus Gabriel Butler
 * 
 * Receiver program that receives data from the sender through UDP
 * Datagram Packets or TCP packets. GENIET HOM TIM
 */

public class Receiver 
{
	// TCP related stuff
	private static Socket socket = null;
	private static byte[] sendbuf = null;
	private static byte[] recbuf = null;
	
	// UDP related stuff
	private static DatagramSocket dataGramSocket = null;
	private static DatagramPacket recPacket = null;
	public static InetAddress address = null;
	
	// Send and receive related stuff
	private static ArrayList<Long> receivedSeq = new ArrayList<Long>();
	private static RandomAccessFile recFile = null;
	private static int packetsReceived = 0;
	private static int packetsDropped = 0;
	private static int packetsPerSend = 0;
	private static int packetsExpected = 0;
	//static int dataPacketSize = 64000;
	static int dataPacketSize = 16000;
	private static int fileSize = 0;
	private static int CHUNKSIZE;
	
	// Sending filename to set it inside this method rather than main.
	public static boolean UDPFileTransfer(String filename)
	{
		System.out.println(packetsExpected);
		try 
		{
			recFile = new RandomAccessFile("test_" + filename, "rw");
		} 
		catch (FileNotFoundException e) 
		{
			e.printStackTrace();
		}
		
		byte[] filedata = new byte[dataPacketSize];
		
		try
		{
			dataGramSocket.setSoTimeout(100);
		}
		catch (SocketException e)
		{
			System.out.println("Socket exception, line 60");
		}
		long seqnum = 0;
		while (packetsReceived < packetsExpected)
		{
			recPacket = new DatagramPacket(filedata, filedata.length);
			//System.out.println(packetsReceived);
			try
			{
				if (receivedSeq.size() == packetsPerSend)
				{
					sendbuf = ByteCasting.objectToBytes(receivedSeq);
					socket.getOutputStream().write(sendbuf);
					socket.getOutputStream().flush();
					receivedSeq.clear();
				}
				dataGramSocket.receive(recPacket);
				packetsReceived += 1;
				filedata = recPacket.getData();
				byte[] seqNumArr = new byte[Long.SIZE];
				//long seqnum = 0;
				System.arraycopy(filedata, 0, seqNumArr, 0, Long.SIZE);
				seqnum = ByteCasting.bytesToLong(seqNumArr);
				
				receivedSeq.add(seqnum);
				
				recFile.seek(seqnum*(dataPacketSize-Long.SIZE));
				recFile.write(filedata, Long.SIZE, dataPacketSize-Long.SIZE);
			}
			catch (IOException e)
			{
		        try 
		        {
		        	System.out.println("dropped");
		        	sendbuf = ByteCasting.objectToBytes(receivedSeq);
					socket.getOutputStream().write(sendbuf);
					socket.getOutputStream().flush();
					packetsDropped += packetsPerSend - receivedSeq.size();
					receivedSeq.clear();
				} 
		        catch (Exception e1) 
		        {
					e1.printStackTrace();
				}
			}
			//System.out.println(packetsReceived);
		}
		dataGramSocket.disconnect();
		dataGramSocket.close();
		System.out.println(packetsDropped);

		
		
		
		return true;
	}
	
	public static boolean TCPFileTransfer(String filename)
	{
		try
		{
			recFile = new RandomAccessFile("test_" + filename, "rw");
		}
		catch (Exception e)
		{
			System.err.println("Do you even open");
		}
		System.out.println(packetsExpected);
		try 
		{
			recbuf = new byte[CHUNKSIZE];
			while (recFile.getFilePointer() < recFile.length())
			{
				if (recFile.length() - recFile.getFilePointer() < CHUNKSIZE)
				{
					recbuf = new byte[(int) (recFile.length() - recFile.getFilePointer())];
				}
				socket.getInputStream().read(recbuf);
				recFile.write(recbuf);
				socket.notifyAll();
			}
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		return true;
		
	}
	
	
	public static void main(String[] args) throws IOException
	{		
		/* Stuur die filename, expected packets(total), aantaal packets per send
		 * Laat dit String[] object wees, waar dit in die bogenoemde order is.
		 * Maak gebruik van die Seqnumber om dit te cast na byte[].
		 * */
		
		if (args.length != 2) {
            System.out.println("Usage: java QuoteClient <hostname> <port>");
            return;
		}
		
		// Set socket na regte port volgens server.
		// UDP en TCP behoort op sele port te werk
		int port = Integer.parseInt(args[1]);
		address = InetAddress.getByName(args[0]);
		socket = new Socket(address,port);
		dataGramSocket = new DatagramSocket(2000);
		
		recbuf = new byte[socket.getReceiveBufferSize()];
		sendbuf = new byte[socket.getSendBufferSize()];
		socket.getInputStream().read(recbuf);
		
		try 
		{
			boolean done = false;
			String typeSend = "";
			
			while (!done)
			{
				String[] filedata = (String[]) ByteCasting.bytesToObject(recbuf);
				typeSend = filedata[0];
				fileSize = Integer.parseInt(filedata[2]);
				packetsExpected = Integer.parseInt(filedata[3]);
				packetsPerSend = Integer.parseInt(filedata[4]);
				CHUNKSIZE = Integer.parseInt(filedata[5]);
				
				sendbuf = ByteCasting.objectToBytes(true);
				socket.getOutputStream().write(sendbuf);
				socket.getOutputStream().flush();
				
				if (typeSend.equalsIgnoreCase("true"))
				{
					done = UDPFileTransfer(filedata[1]);
				}
				else
				{
					socket.setReceiveBufferSize(CHUNKSIZE);
					recbuf = new byte[socket.getReceiveBufferSize()];
					done = TCPFileTransfer(filedata[1]);
				}
				
				
				// Moet steeds probeer uitvind hoe om meer reqeust te hanteer.
				// Miskien 'n TCP signal of iets stuur, nog onseker.
				if (done == true)
				{
					//Prompt user for moar
				}
				else 
				{
					System.out.println("Oh shit break this thing now!!");
					break;
				}
			}
		} 
		catch (Exception e)
		{
			e.printStackTrace();
		} 
	}

}