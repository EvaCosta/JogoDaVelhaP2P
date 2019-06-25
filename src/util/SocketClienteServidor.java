package util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import logic.Posicao;

public class SocketClienteServidor implements AutoCloseable{
	private Socket socket;
	private ServerSocket serverSocket;
	private ObjectInputStream entrada;
	private ObjectOutputStream saida;
	
	public SocketClienteServidor(String ip) throws IOException {
		serverSocket = new ServerSocket(12345);
		Object monitor = new Object();
		
		//Thread cliente
		new Thread(()-> {
			try {
				Socket socket = new Socket(ip, 12345);
			
				synchronized (monitor) {
					this.socket = socket;
					serverSocket.close();
					serverSocket=null;
					monitor.notify();
				}
			} catch (IOException e) {
			
			}
			
		}).start();
		
		//Thread servidor
		new Thread(()-> {
			Socket socket;
			try {
				socket = serverSocket.accept();

                synchronized (monitor) {
                    this.socket = socket;
                    monitor.notify();
                }
				
			} catch (IOException e) {
				
			}
			
		}).start();
		
		synchronized (monitor) {
			try {
				monitor.wait();
				
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		saida = new ObjectOutputStream(socket.getOutputStream());
		entrada = new ObjectInputStream(socket.getInputStream());
		
		System.out.println("Conectado como " + 
				((serverSocket == null) ? "cliente" : "servidor" + " com: " + socket.getInetAddress().getHostAddress()));
					
	}
	
	public boolean isServidor() {
		return serverSocket != null;
	}
	
	public void send(Posicao pos) throws IOException {
		saida.flush();
		saida.writeObject(pos);
	}

	public Posicao receive() {
		Posicao posicao = null;
		try {
			posicao = (Posicao) entrada.readObject();

		} catch (Exception e) {
			return null;
		}

		return posicao;
	}

	@Override
	public void close() throws IOException {
		entrada.close();
		saida.close();
		socket.close();
		if (serverSocket != null)
			serverSocket.close();
	}
}
