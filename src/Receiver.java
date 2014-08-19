import java.net.*;
import java.util.ArrayList;
import java.io.*;
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
	private static int packetsSupposedToRecv = 0;
	private static int packetsReceived = 0;
	private static int packetsDropped = 0;
	private static int packetsPerSend = 0;
	private static int packetsExpected = 0;
	static int dataPacketSize = 64000;
	
	// Sending filename to set it inside this method rather than main.
	public static boolean fileTransfer(String filename)
	{
		try 
		{
			recFile = new RandomAccessFile("new_" + filename, "rw");
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
		
		while (packetsSupposedToRecv < packetsExpected)
		{
			recPacket = new DatagramPacket(filedata, filedata.length);
			
			try
			{
				if (receivedSeq.size() == packetsPerSend)
				{
					sendbuf = ByteCasting.objectToBytes(receivedSeq);
					socket.getOutputStream().write(sendbuf);
					socket.getOutputStream().flush();
					packetsReceived += receivedSeq.size();
					//System.out.println(receivedSeq.size());
					receivedSeq.clear();
				}
				dataGramSocket.receive(recPacket);
				filedata = recPacket.getData();
				
				byte[] seqNumArr = new byte[64];
				long seqnum = 0;
				System.arraycopy(filedata, 0, seqNumArr, 0, 64);
				seqnum = ByteCasting.bytesToLong(seqNumArr);
				
				receivedSeq.add(seqnum);
				
				recFile.seek(seqnum*(dataPacketSize-64));
				recFile.write(filedata, 64, dataPacketSize-64);
			}
			catch (IOException e)
			{
		        try 
		        {
		        	sendbuf = ByteCasting.objectToBytes(receivedSeq);
					socket.getOutputStream().write(sendbuf);
					socket.getOutputStream().flush();
					packetsDropped += packetsPerSend - receivedSeq.size();
					packetsReceived += receivedSeq.size();
					//System.out.println(receivedSeq.size());
					receivedSeq.clear();
				} 
		        catch (IOException e1) 
		        {
					e1.printStackTrace();
				}
			}
			packetsSupposedToRecv += 1;
		}
		try 
		{
			recFile.close();
			dataGramSocket.close();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		
		System.out.println(packetsDropped);
		/*try 
		{
			byte[] buf = ByteCasting.objectToBytes(receivedSeq);
			DatagramPacket packet = new DatagramPacket(buf, buf.length, address, 6000);
			dataGramSocket.send(packet);
			packetsReceived += receivedSeq.size();
			recFile.close();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}*/
		
		return true;
	}
	
	public static void main(String[] args) throws IOException
	{		
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
			while (true)
			{
				String[] filedata = (String[]) ByteCasting.bytesToObject(recbuf);
				packetsExpected = Integer.parseInt(filedata[1]);
				packetsPerSend = Integer.parseInt(filedata[2]);
				String fileName = filedata[0];
				boolean done = false;
				
				if (packetsExpected > 0 && packetsPerSend > 0 && fileName != "")
				{
					sendbuf = ByteCasting.objectToBytes(true);
					socket.getOutputStream().write(sendbuf);
					socket.getOutputStream().flush();
					done = fileTransfer(filedata[0]);
				}
				else
				{
					sendbuf = ByteCasting.objectToBytes(false);
					socket.getOutputStream().write(sendbuf);
					socket.getOutputStream().flush();
				}
				
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
	}
}