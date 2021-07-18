package wvs.map;

import java.util.ArrayList;

import wvs.WvsPlayer;
import wvs.net.PacketWriter;
import wvs.wz.WzNameSpace;

public class WvsMap {
	private int id;
	
	private MiniMap miniMap;
	private ArrayList<WvsPortal> portals = new ArrayList<>();
	private ArrayList<WvsPlayer> players = new ArrayList<>();
	
	public WvsMap(int id, WzNameSpace property) {
		this.id = id;
		try {
			miniMap = new MiniMap(property.getItem("miniMap"));
		} catch (Exception e) {
			miniMap = new MiniMap();
		}
		WzNameSpace portalProperty = property.getItem("portal");
		for (WzNameSpace n : portalProperty.getChilds()) {
			portals.add(new WvsPortal(n, miniMap));
		}
	}
	
	public void sendPacketToAll(PacketWriter packet) {
		for (WvsPlayer p : players) {
			p.sendPacket(packet.getPacket());
		}
	}
	
	public int getId() {
		return id;
	}
	
	public ArrayList<WvsPlayer> getPlayers() {
		return players;
	}
	
	public WvsPortal getPortal(String name) {
		for (WvsPortal p : portals) {
			if(p.getName().equals(name)) {
				return p;
			}
		}
		return null;
	}
	
	public ArrayList<WvsPortal> getPortals() {
		return portals;
	}
	
	public void removePlayer(WvsPlayer player) {
		players.remove(player);
		for (WvsPlayer p : players) {
			PacketWriter packet = new PacketWriter();
			packet.writeShort(7);
			packet.writeInt(player.getPlayerId());
			p.sendPacket(packet.getPacket());
		}
	}
	
	public void addPlayer(WvsPlayer player) {
		players.add(player);
		for (WvsPlayer p : players) {
			PacketWriter packet = new PacketWriter();
			packet.writeShort(6);
			packet.writeInt(player.getPlayerId());
			packet.writeInt(player.getFace());
			packet.writeInt(player.getX());
			packet.writeInt(player.getY());
			p.sendPacket(packet.getPacket()); //각각의 player 객체는 자신의 outputstream 와 연결된 sendPacket 매서드로 전송
		}
	}

}
