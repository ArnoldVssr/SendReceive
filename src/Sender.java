import java.net.*;
import java.util.ArrayList;
import java.io.*;

import javax.sound.midi.SysexMessage;

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
	
	private InetAddress address;
	private RandomAccessFile selectedFile;
	
	private byte[] dataArray;
	private int CHUNKSIZE;
	private final int PACKETSEND = 4;
	private String typeSend = "";
	
	private int serverPort = 6066;
	private int dataGramPort = 6000;
	private String host = "localhost";
	
	public void run()
	{
		boolean ready = true;
		typeSend = "false";
		try
		{
			serverSocket = new ServerSocket(serverPort);
			selectedFile = new RandomAccessFile("pn.jpg", "r");
			fileSize = selectedFile.length();
			System.out.println("file size: "  + fileSize);
			getNumPackets(typeSend);
			
			String[] data = {typeSend, "pn.jpg", "" + fileSize, "" + numPackets, "" + PACKETSEND, "" + CHUNKSIZE};
			
			ready = Signaling(serverSocket.accept(), data);
			
		}
		catch (Exception e)
		{
			System.err.println("Error in ServerSocket.");
			e.printStackTrace();
		}
		
		if (ready)
		{
			if (typeSend.equalsIgnoreCase("true"))
			{
				System.out.println("Sending file with UDP");
				SendUDP();
			}
			else
			{
				System.out.println("Sending file with TCP");
				SendTCP();
			}
		}
		else
		{
			System.err.println("Receiver not available.");
			System.err.println("Exiting...");
			System.exit(0);
		}
	}
	
	public void SendUDP()
	{
		try 
		{
			UDPsocket = new DatagramSocket(dataGramPort);
			address = InetAddress.getByName(host);
			int fileSize = (int) selectedFile.length();
			int sentPackets = 0;
			
			long dataLeft = fileSize;
			long sendingSize = 0;
			System.out.println("have to send " + numPackets + " packets");
			System.out.println();
			
			long offset = 0;
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
				
				dataArray = new byte[(int)sendingSize+Long.SIZE];
				
				byte[] seqNum = ByteCasting.longToBytes(count);
				System.arraycopy(seqNum, 0, dataArray, 0, Long.SIZE);
				
				selectedFile.seek((long) offset);
				selectedFile.read(dataArray, Long.SIZE, (int)sendingSize);
				
				DatagramPacket packet = new DatagramPacket(dataArray, dataArray.length, address, 2000); 
		        UDPsocket.send(packet);
		        sentPackets++;
		        
		        System.out.println("sending packet " + sentPackets + " of " + sendingSize + "bytes");
		        System.out.println();
				
				dataLeft = dataLeft - CHUNKSIZE;
				offset = offset + sendingSize;
				count++;
				
				if ((sentPackets % PACKETSEND) == 0)
				{
					System.out.println("sent packets, waiting for reply");					
					TCPsocket.getInputStream().read(recbuf);
					ArrayList<Long> receiverNumbers  = (ArrayList<Long>) ByteCasting.bytesToObject(recbuf);
					
					System.out.println("received Array of size: " + receiverNumbers.size());
					
					if (receiverNumbers.size() == PACKETSEND)
					{
						//continue with send
						System.out.println("Receiver got all packets.");
						System.out.println();
						sentPackets = 0;
					}
					else
					{
						//Resend
						System.err.println("Packets dropped: " + (PACKETSEND -receiverNumbers.size()));
						System.out.println();
						sentPackets = ReSendPackets(receiverNumbers, sendingSize, count);
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
	
	public void SendTCP()
	{
		//long dataSend = 0;
		//long dataLeft = fileSize;
		//long offset = 0;

		System.out.println("will send: " + numPackets);
		
		try 
		{
			sendbuf = new byte[TCPsocket.getSendBufferSize()];
			while (selectedFile.getFilePointer() < selectedFile.length())
			{
				if (selectedFile.length() - selectedFile.getFilePointer() < CHUNKSIZE)
				{
					sendbuf = new byte[(int) (selectedFile.length() - selectedFile.getFilePointer())];
				}
				selectedFile.read(sendbuf);
				TCPsocket.getOutputStream().write(sendbuf);
				TCPsocket.getOutputStream().flush();
				TCPsocket.wait(100);
			}
		} 
		catch (IOException | InterruptedException e) 
		{
			e.printStackTrace();
		}
		/*for (int i = 0; i <= numPackets; i++)
		{
			try
			{
				if (i == (numPackets))
				{
					TCPsocket.setSendBufferSize((int)(CHUNKSIZE - ((i * CHUNKSIZE) - fileSize)));
					sendbuf = new byte[TCPsocket.getSendBufferSize()];
				}
				
				//selectedFile.seek(offset);
				//System.out.println(sendbuf.length);
				//System.out.println("pointer before read: " + selectedFile.getFilePointer());
				//System.out.println("reading: " + sendbuf.length);
				//System.out.println((CHUNKSIZE - ((i * CHUNKSIZE) - fileSize)));
				selectedFile.read(sendbuf);
				
				//System.out.println("sent: " + dataSend);
			
				TCPsocket.getOutputStream().write(sendbuf);
				TCPsocket.getOutputStream().flush();
				
				//offset = offset + dataSend;
			}
			catch (Exception e)
			{
				System.err.println("Error in send TCP.");
				e.printStackTrace();
			}
		}*/
	}
	
	public static void FileBrowser()
	{
		
	}
	
	public boolean Signaling(Socket server, String[] fileData) throws Exception
	{
		boolean signal = false;
		TCPsocket = server;
		
		TCPsocket.setSendBufferSize(CHUNKSIZE);
		
		System.out.println(TCPsocket.getSendBufferSize());
		
		sendbuf = new byte[TCPsocket.getSendBufferSize()];
    	recbuf = new byte[TCPsocket.getReceiveBufferSize()];
    	
    	//System.out.println("Sending file information");
		sendbuf = ByteCasting.objectToBytes(fileData);
		TCPsocket.getOutputStream().write(sendbuf);
		TCPsocket.getOutputStream().flush();

		TCPsocket.getInputStream().read(recbuf);
		signal = (Boolean) ByteCasting.bytesToObject(recbuf);
   
    	//System.out.println("Receiver is ready");
    	return signal;
	}
	
	public void getNumPackets(String type)
	{
		if (type.equalsIgnoreCase("true"))
		{
			CHUNKSIZE = 63936;
		}
		else
		{
			CHUNKSIZE = 131000;
		}
		
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
	
	public int ReSendPackets(ArrayList<Long> receiverNumbers, long sendingSize, long count)
	{
		long start = count - PACKETSEND;
		int sentCounter = 0;
		
		for (int i = 0; i < PACKETSEND; i++)
		{
			if (!receiverNumbers.contains((start+i)))
			{
				dataArray = new byte[(int)sendingSize+Long.SIZE];
				byte[] seqNum = ByteCasting.longToBytes(start+i);
				System.arraycopy(seqNum, 0, dataArray, 0, Long.SIZE);
				
				try
				{					
					selectedFile.seek((start+i)*(CHUNKSIZE));
					selectedFile.read(dataArray, Long.SIZE, (int)sendingSize);
					
					DatagramPacket packet = new DatagramPacket(dataArray, dataArray.length, address, 2000); 
			        UDPsocket.send(packet);			        
			        sentCounter++;
				}
				catch (Exception e)
				{
					System.err.println("Error in resend.");
				}
			}
		}
		System.out.println("Resent packets");
		return sentCounter;
	}
}