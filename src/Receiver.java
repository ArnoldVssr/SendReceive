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
			byte[] sendArray = new byte[CHUNKSIZE];
	        // get response
	        DatagramPacket packet = new DatagramPacket(sendArray, sendArray.length);
	        socket.receive(packet);
	        
			RandomAccessFile newFile = new RandomAccessFile("kots.txt", "rw");
			int counter = 0;
			try 
			{
				while (counter < 2)
				{
					if (counter == 0)
					{
						newFile.write(sendArray, 0, CHUNKSIZE);
					}
					else if (counter == 1)
					{
						newFile.write(sendArray, CHUNKSIZE, 51200);
					}
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
