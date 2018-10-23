
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

public class UDPserver extends Thread
{

	private DatagramSocket socket;
	
	public UDPserver (int port) throws SocketException
	{
		socket=new DatagramSocket(port);
		socket.setSoTimeout(1000);
		
	}
	
	public void run()
	{
		byte[] bufferRequest= new byte[1];
		DatagramPacket request=new DatagramPacket(bufferRequest, bufferRequest.length);
		byte[] bufferResponse=new byte[12];
		DatagramPacket answer;
		String risposta;
		String protocolloLetto;
		TextFile file;
		int protocollo;
		while (!interrupted())
		{
			try 
			{
				socket.receive(request);		
				bufferRequest=request.getData();
				if(bufferRequest[0]==63)
				{
					file=new TextFile("protocollo.txt", 'r');
					protocolloLetto=file.fromFile();
					file.closeFile();
					protocollo=Integer.parseInt(protocolloLetto);
					protocollo+=1;
					risposta=Integer.toString(protocollo);				
				}
					
				else
					risposta="Error";
				file =new TextFile("protocollo.txt",'w');
				file.toFile(risposta);
				file.closeFile();
				bufferResponse=risposta.getBytes("ISO-8859-1");
				answer=new DatagramPacket(bufferResponse, bufferResponse.length, request.getAddress(), request.getPort());
				socket.send(answer);
				
			} 
			catch (SocketTimeoutException e) 
			{
				System.err.println("Timeout");
			}
			catch (IOException e) 
			{
				
				e.printStackTrace();
			} 
			catch (EccezioneFile e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			catch (EccezioneTextFileEOF e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		closeSocket();
		
	}
	
	public void closeSocket()
	{
		socket.close();
	}
	public static void main(String[] args)
	{
		ConsoleInput tastiera= new ConsoleInput();
		try 
		{
			UDPserver echoServer= new UDPserver(7);
			echoServer.start();
			tastiera.readLine();
			echoServer.interrupt();
			
		} 
		catch (SocketException e) 
		{
			System.err.println("Impossibile istanziare il socket");
		} 
		catch (IOException e) 
		{
			System.out.println("Errore generico di I/O dalla tastiera");
		}

	}

}
