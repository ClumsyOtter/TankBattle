package tankWar;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;
import java.net.UnknownHostException;


public class NetClient{
	private TankClient tc;
	private static int UDP_NUMBER;
	String serverIp = "127.0.0.1";
	public static int getUDP_NUMBER() {
		return UDP_NUMBER;
	}
	public void setUDP_NUMBER(int uDP_NUMBER) {
		UDP_NUMBER = uDP_NUMBER;
	}

	DatagramSocket ds = null;
	int msgType = 0;
	public NetClient(TankClient tc) {
		this.tc = tc;
	}
	public void connect(String IP, int port){
		Socket s = null;
		this.serverIp = IP;
		try {
			ds = new DatagramSocket(UDP_NUMBER);
			s = new Socket(IP, port);
			DataOutputStream dos = new DataOutputStream(s.getOutputStream());
			dos.writeInt(UDP_NUMBER);
			DataInputStream dis = new DataInputStream(s.getInputStream());
			int ID = dis.readInt();
			tc.mytank.ID = ID;
			if(ID%2 == 0)
				tc.mytank.good = false;
			else
				tc.mytank.good = true;
			TankNewMessage tnm = new TankNewMessage(tc.mytank);
			tnm.send(ds,serverIp,TankWarServer.UPP_PORT);
			System.out.println("连接成功");
		} catch (UnknownHostException e) {
			e.printStackTrace();
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
		new Thread(new UdpResiveThread()).start();
	}
	private class UdpResiveThread implements Runnable {
		byte[] buf = new byte[1024];
		public void run() {
			while( ds != null ){
				DatagramPacket dp = new DatagramPacket(buf, buf.length);
				try {
					ds.receive(dp);
					parse(dp);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		private void parse(DatagramPacket dp) {
			ByteArrayInputStream bais = new ByteArrayInputStream(buf, 0, buf.length);
			DataInputStream dis = new DataInputStream(bais);
			try {
				msgType = dis.readInt();
				System.out.println(msgType);
			} catch (IOException e) {
				e.printStackTrace();
			}
			switch (msgType) {
			case Msg.TANK_NEW_MSG:
				TankNewMessage tmsg = new TankNewMessage(tc);
				tmsg.prase(dis);
				break;
			case Msg.TANK_MOVE_MSG:
				TankMoveMessage tmm = new TankMoveMessage(tc);
				tmm.prase(dis);
				break;
			case Msg.MISSILE_NEW_MSG:
				MissileNewMsg mnm = new MissileNewMsg(tc);
				mnm.prase(dis);
				break;
			case Msg.TANK_DEATH_MSG:
				TankDeathMsg tdm = new TankDeathMsg(tc);
				tdm.prase(dis);
				break;
			case Msg.Missile_DEATH_MSG:
				MissileDeathMsg mdm = new MissileDeathMsg(tc);
				mdm.prase(dis);
				break;
			default:
				break;
			}
		}	
	}
	
	public void send(Msg msg) {
			msg.send(ds,"127.0.0.1",TankWarServer.UPP_PORT);	
	}
/*
	private class synchronizationThread implements Runnable{

		public void run() {
			try {
				Thread.sleep(500);
				TankLoctionMessage tlm = new TankLoctionMessage(tc.mytank.x,tc.mytank.y,tc.mytank.ID);
				send(tlm);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}	
	}
	*/
}
