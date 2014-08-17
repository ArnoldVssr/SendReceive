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
	/*private static Socket socket = null;
	private static byte[] sendbuf = null;
	private static byte[] recbuf = null;*/
	
	// UDP related stuff
	private static DatagramSocket dataGramSocket = null;
	private static DatagramPacket recPacket = null;
	public static InetAddress address = null;
	
	// Send and receive related stuff
	private static ArrayList<Long> receivedSeq = new ArrayList<Long>();
	private static RandomAccessFile recFile = null;
	private static int packetsReceived = 0;
	public static int packetsPerSend = 0;
	public static int packetsExpected = 0;
	public static int dataPacketSize = 64000;
	
	// Sending filename to set it inside this method rather than main.
	public static boolean fileTransfer(String filename)
	{
		try 
		{
			recFile = new RandomAccessFile(filename + "_test", "rw");
		} 
		catch (FileNotFoundException e) 
		{
			e.printStackTrace();
		}
		
		byte[] filedata = new byte[dataPacketSize];
		
		//sendbuf = new byte[socket.getSendBufferSize()];
		//recbuf = new byte[socket.getReceiveBufferSize()];
		
		try
		{
			dataGramSocket.setSoTimeout(200);
		}
		catch (SocketException e)
		{
			System.out.println("Socket exception, line 60");
		}

		while (packetsReceived != packetsExpected)
		{
			recPacket = new DatagramPacket(filedata, filedata.length);
			try
			{
				dataGramSocket.receive(recPacket);
			}
			catch (IOException e)
			{
		        try 
		        {
		        	byte[] buf = byteCasting.objectToBytes(receivedSeq);
					DatagramPacket packet = new DatagramPacket(buf, buf.length, address, 6066);
					dataGramSocket.send(packet);
					packetsReceived += receivedSeq.size();
					System.out.println(packetsReceived);
					receivedSeq.clear();
				} 
		        catch (IOException e1) 
		        {
					e1.printStackTrace();
				}
			}
			
			filedata = recPacket.getData();
			byte[] seqNumArr = new byte[64];
			long seqnum = 0;
			System.arraycopy(filedata, 0, seqNumArr, 0, 64);
			seqnum = byteCasting.bytesToLong(seqNumArr);
			receivedSeq.add(seqnum);
			try
			{
				recFile.seek(seqnum*(dataPacketSize-64));
				recFile.write(filedata, 64, dataPacketSize-64);
			}
			catch (Exception e)
			{
				System.out.println("Error with handeling signal numbers");
			}
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
		//socket = new Socket(address,port);
		dataGramSocket = new DatagramSocket(2000);
		//socket.getInputStream().read(recbuf);
		byte[] buf = new byte[256];
		DatagramPacket packet = new DatagramPacket(buf, buf.length, address, 6066);
        dataGramSocket.send(packet);
		
		try 
		{
			while (true)
			{
				//String[] filedata = (String[]) byteCasting.bytesToObject(recbuf);
				packetsExpected = 46;//Integer.parseInt(filedata[1]);
				packetsPerSend = 46;//Integer.parseInt(filedata[2]);
				boolean done = fileTransfer("ta.png");
				
				// Moet steeds probeer uitvind hoe om meer reqeust te hanteer.
				// Miskien 'n TCP signal of iets stuur, nog onseker.
				if (done == true)
				{
					break;
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
		
		// Test code related stuff, to have idea of how it works. (personal use)
		/*********************************************************************/
		
		/*RandomAccessFile test = null;
		RandomAccessFile newFile = new RandomAccessFile("test.png", "rw");
		//RandomAccessFile newFile = new RandomAccessFile("ntw.mkv", "rw");
		
		
		byte[] recbuff = new byte[1024];
		
		try {
			test = new RandomAccessFile("ta.png", "r");
			//test = new RandomAccessFile("otw.mkv", "r");
			
			long longnum = 0;
			byte[] longbytearr = Seqnumber.longToBytes(longnum);
			
			while (test.getFilePointer() < test.length())
			{
				System.arraycopy(longbytearr, 0, recbuff, 0, 8);
				//System.out.println(test.getFilePointer());
				test.read(recbuff,8,dataPacketSize);
				newFile.write(recbuff,8,dataPacketSize);
				if (longnum == 0)
				{
					byte[] seqnum = new byte[8];
					System.arraycopy(recbuff, 0, seqnum, 0, 8);
					receivedSeq.add(Seqnumber.bytesToLong(seqnum));
				}
				longnum++;
				longbytearr = Seqnumber.longToBytes(longnum);
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		System.out.println(test.length());
		test.close();
		newFile.close();
		
		*/
		
		/*********************************************************************/
	}

}
