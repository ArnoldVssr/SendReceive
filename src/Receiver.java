import java.net.*;
import java.util.HashSet;
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
	//private static DatagramSocket socket;
	private static final int CHUNKSIZE = 64000;
	
	public static void main(String[] args)
	{
		try
		{	
			
			DatagramSocket socket = new DatagramSocket(2000);
	        
			RandomAccessFile newFile = new RandomAccessFile("poes.jpg", "rw");
			int counter = 0;
			try 
			{
				while (counter < 9)
				{
					if (counter == 0)
					{
						byte[] sendArray = new byte[CHUNKSIZE];
				        // get response
				        DatagramPacket packet = new DatagramPacket(sendArray, sendArray.length);
				        socket.receive(packet);
				        newFile.seek((long) 0);
						newFile.write(sendArray, 0, CHUNKSIZE);
					}
					else if (counter == 1)
					{
						byte[] sendArray = new byte[CHUNKSIZE];
				        // get response
				        DatagramPacket packet = new DatagramPacket(sendArray, sendArray.length);
				        socket.receive(packet);
				        newFile.seek((long) 64000);
						newFile.write(sendArray, 0, CHUNKSIZE);
					}
					else if (counter == 2)
					{
						byte[] sendArray = new byte[CHUNKSIZE];
				        // get response
				        DatagramPacket packet = new DatagramPacket(sendArray, sendArray.length);
				        socket.receive(packet);
				        newFile.seek((long) 128000);
						newFile.write(sendArray, 0, CHUNKSIZE);
					}
					else if (counter == 3)
					{
						byte[] sendArray = new byte[CHUNKSIZE];
				        // get response
				        DatagramPacket packet = new DatagramPacket(sendArray, sendArray.length);
				        socket.receive(packet);
				        newFile.seek((long) 192000);
						newFile.write(sendArray, 0, CHUNKSIZE);
					}
					else if (counter == 4)
					{
						byte[] sendArray = new byte[CHUNKSIZE];
				        // get response
				        DatagramPacket packet = new DatagramPacket(sendArray, sendArray.length);
				        socket.receive(packet);
				        newFile.seek((long) 256000);
						newFile.write(sendArray, 0, CHUNKSIZE);
					}
					else if (counter == 5)
					{
						byte[] sendArray = new byte[CHUNKSIZE];
				        // get response
				        DatagramPacket packet = new DatagramPacket(sendArray, sendArray.length);
				        socket.receive(packet);
				        newFile.seek((long) 320000);
						newFile.write(sendArray, 0, CHUNKSIZE);
					}
					else if (counter == 6)
					{
						byte[] sendArray = new byte[CHUNKSIZE];
				        // get response
				        DatagramPacket packet = new DatagramPacket(sendArray, sendArray.length);
				        socket.receive(packet);
				        newFile.seek((long) 384000);
						newFile.write(sendArray, 0, CHUNKSIZE);
					}
					else if (counter == 7)
					{
						byte[] sendArray = new byte[CHUNKSIZE];
				        // get response
				        DatagramPacket packet = new DatagramPacket(sendArray, sendArray.length);
				        socket.receive(packet);
				        newFile.seek((long) 448000);
						newFile.write(sendArray, 0, CHUNKSIZE);
					}
					else if (counter == 8)
					{
						byte[] sendArray = new byte[32691];
				        // get response
				        DatagramPacket packet = new DatagramPacket(sendArray, sendArray.length);
				        socket.receive(packet);
						newFile.seek((long) 512000);
						newFile.write(sendArray, 0, 32691);
					}
					counter++;
					System.out.println("got it");
				}
				
			} catch (Exception e)
			{
				e.printStackTrace();
			}
	        socket.close();
		}
		catch (Exception e)
		{
			System.err.println("poes");
		}
	}
}
