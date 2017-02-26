package tankWar;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

public class TankWarServer {
	public static int TANK_PORT = 8888;
	public static int UPP_PORT = 2333;
	private static int ID = 100; 
	List<client> clients = new ArrayList<client>();		//创建容器储存客户信息
	
	@SuppressWarnings("resource")
	private void start() {
		new Thread(new tankThread()	).start();
		System.out.println("tankThread start");
		ServerSocket ss = null;
		try {
			ss = new ServerSocket(TANK_PORT);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		while(true){
			Socket s = null;
			try {	
				s = ss.accept();
				DataInputStream dis = new DataInputStream(s.getInputStream());
				 int udp = dis.readInt();
				 String ip = s.getInetAddress().getHostAddress();
				 client c = new client(ip,udp);
				 clients.add(c);
				 DataOutputStream dos = new DataOutputStream(s.getOutputStream());
				 dos.writeInt(ID++);
				System.out.println(s.getInetAddress().getHostAddress()+"............connect" + "UDP..." + udp);
			} catch (IOException e) {
				e.printStackTrace();
			}finally{
					try {
						if(s != null){
							s.close();
							s = null;
						}
					} catch (IOException e) {
						e.printStackTrace();
					}				
			}
		}
	}
	public static void main(String[] args){
			new TankWarServer().start();
	}
	private class client{	
		int udpNumber;
		String ip;
		public client(String ip, int udpNumber) {
			this.ip = ip;
			this.udpNumber = udpNumber;
		}	
	}
	private class tankThread implements Runnable{
		public void run() {
			byte[] buf = new byte[1024];
			DatagramSocket ds = null;
			try {
				 ds = new DatagramSocket(UPP_PORT);
			} catch (SocketException e) {
				e.printStackTrace();
			}
			while( ds != null ){
				DatagramPacket dp = new DatagramPacket(buf, buf.length);
				try {
					ds.receive(dp);
					System.out.println("resiver a packet");
					for(int i= 0; i < clients.size(); i++){
						client c = clients.get(i);
						dp.setSocketAddress(new InetSocketAddress(c.ip, c.udpNumber));
						ds.send(dp);
						System.out.println("send clients.."+i+"..packet");
					}	
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}	
	}
}
