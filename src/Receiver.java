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
	
	public static void main(String[] args) throws IOException
	{
		RandomAccessFile test = null;
		//RandomAccessFile newFile = new RandomAccessFile("test.png", "rw");
		RandomAccessFile newFile = new RandomAccessFile("ntw.mkv", "rw");
		byte[] recbuff = new byte[1000];
		try {
			//test = new RandomAccessFile("ta.png", "r");
			test = new RandomAccessFile("otw.mkv", "r");
			while (test.getFilePointer() < test.length())
			{	
				test.read(recbuff);
				newFile.write(recbuff);
			}
			
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		System.out.println(test.length());
	}

}
