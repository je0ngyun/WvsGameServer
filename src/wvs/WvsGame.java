package wvs;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.HashMap;

import wvs.map.WvsMap;
import wvs.wz.WzArchive;
import wvs.wz.WzNameSpace;
import wvs.wz.WzNameSpaceType;

public class WvsGame implements Runnable {
	private ServerSocket serverSocket;
	private int port;
	
	private HashMap<Integer, WvsMap> maps = new HashMap<>();
	
	public WvsGame(int port) {
		this.port = port;
	}

	@Override
	public void run() {
		try {
			serverSocket = new ServerSocket(port);
			System.out.println("맵 불러오는 중...");
			generateMap();
			System.out.println("맵 불러오기 완료");
			System.out.print("연결 대기중...");
			while (true) {
				WvsPlayer player = new WvsPlayer(serverSocket.accept(), this);
				player.start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void generateMap() throws IOException {
		try {
			WzArchive wz = new WzArchive("Data.wz");
			WzNameSpace n = wz.getItemFromPath("Map/Map");
			var c = n.getChilds();
			for (WzNameSpace nn : c) {
				if (nn.getType() == WzNameSpaceType.DIRECTORY) {
					for (WzNameSpace nnn : nn.getChilds()) {
						int mapId = Integer.parseInt(nnn.getName().substring(0, 9));
						maps.put(mapId, new WvsMap(mapId, nnn));
					}
				}
			}
			wz.close();
		} catch (FileNotFoundException e) {
			System.out.println("Data.wz 파일을 찾을 수 없습니다.");
		}	
	}
	
	public WvsMap getMap(int mapId) {
		return maps.get(mapId);
	}

}
