package tankWar;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

public class MissileNewMsg implements Msg {
	
	/*
	 * ÅÚµ¯»ù±¾ÊôÐÔ
	 */
	int tankId;
	Missile missile = null;
	TankClient tc;
	public MissileNewMsg(int tankId, Missile missile, TankClient tc) {
		this.tankId = tankId;
		this.missile = missile;
		this.tc = tc;
	}
	public MissileNewMsg( TankClient tc) {
		this.tc = tc;
	}
	public void send(DatagramSocket ds, String ip, int uDP_NUMBER) {
		ByteArrayOutputStream bas = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(bas);
		try {
			dos.writeInt(Msg.MISSILE_NEW_MSG);
			dos.writeInt(tankId);
			dos.writeInt(missile.id);
			dos.writeInt(missile.x);
			dos.writeInt(missile.y);
			dos.writeInt(missile.dir.ordinal());
			dos.writeBoolean(missile.good);
		} catch (IOException e) {
			e.printStackTrace();
		}
		DatagramPacket dp = new DatagramPacket(bas.toByteArray(),bas.toByteArray().length,new InetSocketAddress(ip, uDP_NUMBER));
		try {
			ds.send(dp);
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			try {
				dos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	public void prase(DataInputStream dis) {
		try {
			int tankID = dis.readInt();
			int ID = dis.readInt();
			System.out.println("resive a new_missile.." + tankID+"..."+ID);
			int x = dis.readInt();
			int y = dis.readInt();
			Direction dir = Direction.values()[dis.readInt()];
			boolean good = dis.readBoolean();
			if( tc.mytank.ID == tankID)
				return;
			Missile m = new Missile(tankID,x, y, dir, tc, good);
			m.id = ID;
			tc.missiles.add(m);
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
}
