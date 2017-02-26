package tankWar;

import java.awt.Button;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Label;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;


public class TankClient extends Frame {

	/**
	 * @author 程锐
	 * @version
	 */
	private static final long serialVersionUID = -6758343604444170663L;
	static int WIDE = 800; // 游戏屏幕宽
	static int HIGH = 600; // 游戏屏幕高
	ArrayList<Missile> missiles = new ArrayList<Missile>(); // 炮弹容器
	ArrayList<Explode> explodes = new ArrayList<Explode>(); // 爆炸容器
	ArrayList<Tank> tanks = new ArrayList<Tank>(); // 敌方坦克容器
	Tank mytank = new Tank(30, 60, this,true,Direction.STOP); // 创建坦克
	Random r = new Random(new Date().getTime());
	Image offscream = null;

	NetClient nc = new NetClient(this); //网络客户端
	connectSet cs = new connectSet();
	/*
	 * 创建线程进行定时
	 */
	private class threadPaint implements Runnable {

		@Override
		public void run() {
			while (true) {
				repaint();
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/*
	 * 覆盖paint方法 每次重画是会自动调用 
	 * Color c = g.getColor();
	 * g.setColor(Color.BLUE);
	 * g.drawString("missiles"+missiles.size(), 100, 100);
	 */
	@Override
	public void paint(Graphics g) {
		 Color c = g.getColor();
		 g.setColor(Color.MAGENTA);
		 g.setColor(c);
		mytank.draw(g);
		for (int i = 0; i < tanks.size(); i++) {
			Tank m = tanks.get(i);
			m.draw(g);
		}
		for (int i = 0; i < missiles.size(); i++) {
			Missile m = missiles.get(i);
			if(m.hit(mytank)){
				TankDeathMsg tdm = new TankDeathMsg(mytank.ID,this);
				this.nc.send(tdm);
				MissileDeathMsg mdm = new MissileDeathMsg(m.id, m.tankID);
				this.nc.send(mdm);
			}
			m.draw(g);
		}
		for (int i = 0; i < explodes.size(); i++) {
			Explode m = explodes.get(i);
			m.draw(g);
		}
		//crashExolode();
	}

	/*
	 * 双重缓冲，消除闪烁
	 */
	public void update(Graphics g) {
		if (offscream == null) {
			offscream = this.createImage(WIDE, HIGH);
		}
		Graphics Offimg = offscream.getGraphics();
		Color c = Offimg.getColor();
		Offimg.setColor(Color.gray);
		Offimg.fillRect(0, 0, WIDE, HIGH);
		paint(Offimg);
		g.drawImage(offscream, 0, 0, null);
		g.setColor(c);
	}

	/*
	 * 创建窗口
	 */
	public void launchFream() {
		this.setLocation(500, 200);
		this.setSize(WIDE, HIGH);

		// 关闭按钮事件设定
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		// 改变标题栏的文字
		this.setTitle("坦克大战");
		this.setResizable(false);
		this.setVisible(true);
		this.setBackground(Color.GRAY);
		this.addKeyListener(new keylisten());
		//mytank.addtank(5);
		new Thread(new threadPaint()).start();
	}

	public static void main(String[] args) {
		TankClient tc = new TankClient();
		tc.launchFream();
	}

	/*
	 * 加入键盘监听
	 */
	private class keylisten extends KeyAdapter {
		/*
		 * 键盘压下的事件
		 */
		public void keyPressed(KeyEvent e) {
			mytank.keyevent(e);
			
		}
		/*
		 * 键盘弹起的事件
		 */
		public void keyReleased(KeyEvent e) {
			int keycount = e.getKeyCode();
			if( keycount == KeyEvent.VK_Q){
				cs.setVisible(true);
			}
			mytank.keyRelease(e);
		}
	}
	/*
	 * 对话框
	 */
	@SuppressWarnings("serial")
	private class connectSet extends Dialog {
		TextField ipNumber = new TextField("127.0.0.1",12);
		TextField udpNumber = new TextField("2222",5);
		TextField serverNumber = new TextField(""+TankWarServer.TANK_PORT,5);
		public connectSet() {
			super(TankClient.this,true);
			Button b = new Button("确定");
			this.setLayout(new FlowLayout());
			this.add(new Label("IP"));
			this.add(ipNumber);
			this.add(new Label("Port"));
			this.add(serverNumber);
			this.add(new Label("My Port"));
			this.add(udpNumber);
			this.setLocation(600, 400);
			this.add(b);
			this.pack();
			b.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					String ip = ipNumber.getText().trim();
					int servernumber = Integer.parseInt(serverNumber.getText().trim());
					int udpNumbers = Integer.parseInt(udpNumber.getText().trim());
					nc.setUDP_NUMBER(udpNumbers);	
					nc.connect(ip, servernumber);
					setVisible(false);
				}
			});
			this.addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					setVisible(false);
				}	
			});
		}	
	}
	/*
	 * 坦克碰撞后会爆炸
	 */
	public void crashExolode(){
		for (int i = 0; i < tanks.size(); i++) {
			Tank m = tanks.get(i);
			if( mytank.getrect().intersects(m.getrect()) && m.isLive() && mytank.isLive() ){
				Explode explode = new Explode(m.x, m.y, this);
				Explode explode1 = new Explode(mytank.x, mytank.y, this);
				this.explodes.add(explode1);
				this.explodes.add(explode);
				mytank.setLive(false);
				m.setLive(false);
			}
		}	
	}
}
