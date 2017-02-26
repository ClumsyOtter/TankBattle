package tankWar;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

public class MissileDeathMsg implements Msg {
	
	int id;
	TankClient tc;
	int tankId;
	public MissileDeathMsg(TankClient tc) {
		this.tc = tc;
	}

	public MissileDeathMsg(int iD, int tankId) {
		this.id = iD;
		this.tankId = tankId;
	}
	
	public void send(DatagramSocket ds, String ip, int uDP_NUMBER) {
		ByteArrayOutputStream bas = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(bas);
		try {
			dos.writeInt(Msg.Missile_DEATH_MSG);
			dos.writeInt(id);
			dos.writeInt(tankId);
		} catch (IOException e) {
			e.printStackTrace();
		}
		DatagramPacket dp = new DatagramPacket(bas.toByteArray(),bas.toByteArray().length,new InetSocketAddress(ip, uDP_NUMBER));
		try {
			ds.send(dp);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
	public void prase(DataInputStream dis) {
		
		try {
			int id = dis.readInt();
			int tankId = dis.readInt();
			System.out.println("resiver a Death missile from " + tankId +"..." + id);
			for(int i = 0; i < tc.missiles.size(); i++){
				Missile t = tc.missiles.get(i);
				if(t.id == id && t.tankID == tankId){
					t.setLive(false);
					break;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}

}
