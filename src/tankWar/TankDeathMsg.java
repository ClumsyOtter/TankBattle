package tankWar;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

public class TankDeathMsg implements Msg {
	
	int ID;
	TankClient tc;
	public TankDeathMsg(TankClient tc) {
		this.tc = tc;
	}

	public TankDeathMsg(int iD, TankClient tc) {
		ID = iD;
		this.tc = tc;
	}
	
	public void send(DatagramSocket ds, String ip, int uDP_NUMBER) {
		ByteArrayOutputStream bas = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(bas);
		try {
			dos.writeInt(Msg.TANK_DEATH_MSG);
			dos.writeInt(ID);
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
			int ID = dis.readInt();
			if( tc.mytank.ID == ID)
				return;
			for(int i = 0; i < tc.tanks.size(); i++){
				Tank t = tc.tanks.get(i);
				if( ID == t.ID ){
					t.setLive(false);
					tc.explodes.add(new Explode(t.x, t.y, tc));
					break;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}

}
