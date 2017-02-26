package tankWar;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

public class TankMoveMessage implements Msg {
	
	TankClient tc = null;
	Tank tank= null;
	int msgType = 0;
	Direction dir;
	private int ID;
	private int x,y;
	Direction barrel = Direction.R;
	public TankMoveMessage(Direction barrel,Direction dir,int ID,int x, int y){
		this.ID = ID;
		this.dir = dir;
		this.x = x;
		this.y = y;
		this.barrel = barrel;
	}
	public TankMoveMessage(TankClient tc) {
		this.tc = tc;
	}
	public void send(DatagramSocket ds, String ip, int uDP_NUMBER) {
		ByteArrayOutputStream bas = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(bas);
		try {
			dos.writeInt(Msg.TANK_MOVE_MSG);
			dos.writeInt(ID);
			dos.writeInt(x);
			dos.writeInt(y);
			dos.writeInt(dir.ordinal());
			dos.writeInt(barrel.ordinal());
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

	@Override
	public void prase(DataInputStream dis) {

		try {
			int ID = dis.readInt();
			int x = dis.readInt();
			int y = dis.readInt();
			Direction dir = Direction.values()[dis.readInt()];
			Direction barrel = Direction.values()[dis.readInt()];
			System.out.println("resive a MOVE form " + ID +"...You are "+tc.mytank.ID + ".. "+dir);
			if( tc.mytank.ID == ID)
				return;
			
			for(int i = 0; i < tc.tanks.size(); i++){
				Tank t = tc.tanks.get(i);
				if( ID == t.ID ){
					t.dir =dir;
					t.x = x;
					t.y = y;
					t.barrel = barrel;
					break;
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
}
