import java.net.*;
import java.util.ArrayList;
import java.util.HashSet;
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
	
	// Send and receive related stuff
	private static ArrayList<Long> receivedSeq = new ArrayList<Long>();
	private static RandomAccessFile recFile = null;
	private static int packetsReceived = 0;
	public static int packetsPerSend = 0;
	public static int packetsExpected = 0;
	public static int dataPacketSize = 1016;
	
	// Sending filename to set it inside this method rather than main.
	public static boolean fileTransfer(String filename)
	{
		try
		{
			recFile = new RandomAccessFile(filename, "rw");
			byte[] filedata = new byte[1024];
			
			recPacket = new DatagramPacket(filedata, 1024);
			sendbuf = new byte[socket.getSendBufferSize()];
			recbuf = new byte[socket.getReceiveBufferSize()];
			
			while (packetsReceived != packetsExpected)
			{
				dataGramSocket.receive(recPacket);
				filedata = recPacket.getData();
				
				byte[] seqnum = new byte[8];
				System.arraycopy(filedata, 0, seqnum, 0, 8);
				receivedSeq.add(byteCasting.bytesToLong(seqnum));
				recFile.seek(byteCasting.bytesToLong(seqnum)*dataPacketSize);
				recFile.write(filedata, 8, dataPacketSize);
				packetsReceived++;
				
				// Handle vir eers net plain send sonder drops
				if (receivedSeq.size() == packetsPerSend)
				{
					sendbuf = byteCasting.objectToBytes(receivedSeq);
					socket.getOutputStream().write(sendbuf);
					receivedSeq = new ArrayList<Long>();
				}
			}
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		return false;
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
		//socket = new Socket("localhost", 6066);
		//int port = Integer.parseInt(args[1]);
		//InetAddress address = InetAddress.getByName(args[0]);
		//dataGramSocket = new DatagramSocket(port, address);
		
		
		socket.getInputStream().read(recbuf);
		try 
		{
			while (true)
			{
				String[] filedata = (String[]) byteCasting.bytesToObject(recbuf);
				packetsExpected = Integer.parseInt(filedata[1]);
				packetsPerSend = Integer.parseInt(filedata[2]);
				boolean done = fileTransfer(filedata[0]);
				
				// Moet steeds probeer uitvind hoe om meer reqeust te hanteer.
				// Miskien 'n TCP signal of iets stuur, nog onseker.
				if (done == true)
				{
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
