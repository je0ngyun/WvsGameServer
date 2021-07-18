package wvs;

public class WvsGameApp {

	public static void main(String[] args) {
		WvsGame server = new WvsGame(9090);
		server.run();
	}
}
