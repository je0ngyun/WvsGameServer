package wvs;

import java.awt.event.KeyEvent;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;

import wvs.map.WvsMap;
import wvs.map.WvsPortal;
import wvs.net.PacketReader;
import wvs.net.PacketWriter;

public class WvsPlayer extends Thread {
	private WvsGame game;

	private Socket socket;
	private InputStream input;
	private OutputStream out;

	private int playerId;
	private String name;
	private int mapId;
	private int face;

	private boolean up;
	private boolean down;
	private boolean left;
	private boolean right;

	private WvsMap currentMap;
	private int x;
	private int y;

	private Thread updateThread;
	private boolean cli_init_flag;

	public WvsPlayer(Socket socket, WvsGame game) {
		this.socket = socket;
		this.game = game;
		this.cli_init_flag = false;
		try {
			input = socket.getInputStream();
			out = socket.getOutputStream();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		updateThread = new Thread(new Runnable() {
			@Override
			public void run() {
				while (!socket.isClosed()) {
					update();
					try {
						sleep(2);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		});
	}

	public int getPlayerId() {
		return playerId;
	}

	public int getFace() {
		return face;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	private void update() {
		if (up) {
			y -= 1;
		}
		if (down) {
			y += 1;
		}
		if (left) {
			x -= 1;
		}
		if (right) {
			x += 1;
		}
		if (cli_init_flag) {
			sendPacket(mapInfoPacket(mapId).getPacket());
		} else {
			PacketWriter packet = new PacketWriter();
			packet.writeShort(10);
			packet.writeInt(playerId);
			packet.writeInt(face);
			packet.writeInt(x);
			packet.writeInt(y);
			currentMap.sendPacketToAll(packet);
		}
	}

	public void sendPacket(byte[] data) {
		try {
			out.write(data);
			out.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public PacketWriter mapInfoPacket(int mapId) {
		PacketWriter packet = new PacketWriter(1024);
		packet.writeShort(5);
		packet.writeInt(playerId);
		packet.writeString(name);
		packet.writeInt(mapId);
		packet.writeInt(face);
		packet.writeInt(x);
		packet.writeInt(y);
		packet.writeInt(currentMap.getPortals().size());
		for (WvsPortal p : currentMap.getPortals()) {
			packet.writeInt(p.getType());
			packet.writeInt(p.getX());
			packet.writeInt(p.getY());
		}
		return packet;
	}

	public void changeMap() {
		for (WvsPortal p : currentMap.getPortals()) {
			//포탈과 캐릭터 충돌여부
			if (p.getRect().contains(new Point2D.Double(x, y)) && (p.getType() == 1 || p.getType() == 2)) {
				// 서버쪽 맵 교체
				int beforeMapId = currentMap.getId();
				WvsMap new_map = game.getMap(p.getTargetMap());
				currentMap.removePlayer(this);
				new_map.addPlayer(this);
				currentMap = new_map; // 인스턴스 교체*/
				WvsPortal target = currentMap.getPortal(p.getTargetName());
				x = target.getX();
				y = target.getY() - 20;
				if (p.getTargetMap() != beforeMapId) { //다른 지역으로 이동시 맵정보 보내줌
					sendPacket(mapInfoPacket(p.getTargetMap()).getPacket());
				}
			}
		}
	}

	@Override
	public void run() {
		try {
			while (!socket.isClosed()) {
				if (socket.isClosed()) {
					break;
				}
				byte[] data = new byte[1024];
				int count = input.read(data);
				if (count <= 0) {
					continue; // 읽은 데이터가 없으면 continue
				}

				PacketReader reader = new PacketReader(data);
				short packetId = reader.readShort();
				switch (packetId) {
				case 5: {
					playerId = reader.readInt();
					name = reader.readString();
					mapId = reader.readInt();
					face = reader.readInt();
					currentMap = game.getMap(mapId);
					System.out.println(" " + playerId + "||" + name + "||" + mapId + "||" + face);
					cli_init_flag = true; //클라이언트에 캐릭터 초기화 해주라는 플레그
					updateThread.start(); //업데이트 쓰레드 Start
					Thread.sleep(100);
					cli_init_flag = false; //초기 몇번만 초기화 패킷 보내주고 중지
					currentMap.addPlayer(this);
				}
					break;
				case 10: {
					int keyCode = reader.readInt();
					if (keyCode == KeyEvent.VK_UP) {
						up = true;
					} else if (keyCode == KeyEvent.VK_DOWN) {
						down = true;
					} else if (keyCode == KeyEvent.VK_LEFT) {
						left = true;
					} else if (keyCode == KeyEvent.VK_RIGHT) {
						right = true;
					} else if (keyCode == KeyEvent.VK_SPACE) {
						changeMap();
					}
				}
					break;
				case 11: {
					int keyCode = reader.readInt();
					if (keyCode == KeyEvent.VK_UP) {
						up = false;
					} else if (keyCode == KeyEvent.VK_DOWN) {
						down = false;
					} else if (keyCode == KeyEvent.VK_LEFT) {
						left = false;
					} else if (keyCode == KeyEvent.VK_RIGHT) {
						right = false;
					}
				}
					break;
				default:
					System.out.printf("%d는 잘못된 패킷번호입니다.\n", packetId);
					break;
				}
			}
			currentMap.removePlayer(this);
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				currentMap.removePlayer(this);
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
