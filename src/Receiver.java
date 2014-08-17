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
	
	public static void main(String[] args)
	{
		try
		{	
			DatagramSocket socket = new DatagramSocket(2000);
			byte[] sendArray = new byte[40779];
	        // get response
	        DatagramPacket packet = new DatagramPacket(sendArray, sendArray.length);
	        socket.receive(packet);
	        
			RandomAccessFile newFile = new RandomAccessFile("firstSend.jpg", "rw");
			try 
			{
				newFile.write(sendArray);
				System.out.println("got it");
				
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
