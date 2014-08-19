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
<<<<<<< HEAD
	private static int dataPacketSize = 0;
	private static int fileSize = 0;
=======
	static int dataPacketSize = 64000;
>>>>>>> e035ace6c74da6ddd3adea2b2669ebd4a9d1e590
	
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
<<<<<<< HEAD
		long seqnum = 0;
=======
		
>>>>>>> e035ace6c74da6ddd3adea2b2669ebd4a9d1e590
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
<<<<<<< HEAD
				//long seqnum = 0;
=======
				long seqnum = 0;
>>>>>>> e035ace6c74da6ddd3adea2b2669ebd4a9d1e590
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
<<<<<<< HEAD
		        	System.out.println("dropped");
=======
>>>>>>> e035ace6c74da6ddd3adea2b2669ebd4a9d1e590
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
<<<<<<< HEAD
			//System.out.println(packetsReceived);
=======
			packetsReceived += 1;
			System.out.println(packetsReceived);
>>>>>>> e035ace6c74da6ddd3adea2b2669ebd4a9d1e590
		}
		dataGramSocket.disconnect();
		dataGramSocket.close();
		System.out.println(packetsDropped);

		
		
		
<<<<<<< HEAD
		return true;
	}
	
	public static boolean TCPFileTransfer(String filename)
	{
		System.out.println("Packets expected: " + packetsExpected);	
		try 
		{
			recFile = new RandomAccessFile("test_" + filename, "rw");
			recbuf = new byte[dataPacketSize];
			while (recFile.getFilePointer() < fileSize)
			{
				socket.getInputStream().read(recbuf);
				recFile.write(recbuf);
				
				if (recFile.getFilePointer() + dataPacketSize > fileSize)
				{
					recbuf = new byte[(int) (fileSize - recFile.getFilePointer())];
				}
			}
			socket.close();
			recFile.close();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
=======
>>>>>>> e035ace6c74da6ddd3adea2b2669ebd4a9d1e590
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
<<<<<<< HEAD
			String typeSend = "";
			
			while (!done)
			{
				String[] filedata = (String[]) ByteCasting.bytesToObject(recbuf);
				typeSend = filedata[0];
				fileSize = Integer.parseInt(filedata[2]);
				packetsExpected = Integer.parseInt(filedata[3]);
				packetsPerSend = Integer.parseInt(filedata[4]);
				dataPacketSize = Integer.parseInt(filedata[5]);
=======
			while (!done)
			{
				String[] filedata = (String[]) ByteCasting.bytesToObject(recbuf);
				packetsExpected = Integer.parseInt(filedata[1]);
				packetsPerSend = Integer.parseInt(filedata[2]);
>>>>>>> e035ace6c74da6ddd3adea2b2669ebd4a9d1e590
				
				sendbuf = ByteCasting.objectToBytes(true);
				socket.getOutputStream().write(sendbuf);
				socket.getOutputStream().flush();
				
<<<<<<< HEAD
				if (typeSend.equalsIgnoreCase("true"))
				{
					done = UDPFileTransfer(filedata[1]);
				}
				else
				{
					//socket.setReceiveBufferSize(CHUNKSIZE);
					//recbuf = new byte[socket.getReceiveBufferSize()];
					done = TCPFileTransfer(filedata[1]);
				}
				
=======
				done = fileTransfer(filedata[0]);
>>>>>>> e035ace6c74da6ddd3adea2b2669ebd4a9d1e590
				
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
