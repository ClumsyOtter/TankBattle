package tankWar;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

public class TankNewMessage implements Msg{
	
	TankClient tc = null;
	Tank tank = null;
	int x,y,ID;
	Direction dir = Direction.STOP;
	boolean good = true;
	public TankNewMessage(Tank tank) {
		this.tank = tank;
	}
	public TankNewMessage( TankClient tc){
		this.tc = tc;	
	}
	public void send(DatagramSocket ds, String ip, int uDP_NUMBER) {
		ByteArrayOutputStream bas = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(bas);
		try {
			dos.writeInt(Msg.TANK_NEW_MSG);
			dos.writeInt(tank.ID);
			dos.writeInt(tank.x);
			dos.writeInt(tank.y);
			dos.writeInt(tank.dir.ordinal());
			dos.writeBoolean(tank.good);
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
			int ID = dis.readInt();
			System.out.println("resive a new_MEG" + ID);
			int x = dis.readInt();
			int y = dis.readInt();
			Direction dir = Direction.values()[dis.readInt()];
			boolean good = dis.readBoolean();
			if( tc.mytank.ID == ID)
				return;
			boolean exist = false;
			for(int i = 0; i < tc.tanks.size(); i++ ){
				Tank t = tc.tanks.get(i);
				if( t.ID == ID){
					exist = true;
					break;
				}
			}
			if(!exist){
				TankNewMessage tnm = new TankNewMessage(tc.mytank);
				tc.nc.send(tnm);
				Tank t = new Tank(x, y, tc, good, dir);
				t.ID = ID;
				tc.tanks.add(t);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
}
