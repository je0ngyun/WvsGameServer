package wvs.wz;

import java.io.FileNotFoundException;
import java.io.IOException;

public class WzArchive {
	private WzInputStream stream;
	private WzNameSpace root;
	
	public WzArchive(String path) throws FileNotFoundException {
		stream = new WzInputStream(path);
		root = new WzPackage(stream, path, 0, WzNameSpaceType.DIRECTORY);
	}
	
	public WzNameSpace getRoot() {
		return root;
	}
	
	public WzNameSpace getItem(String name) {
		return root.getItem(name);
	}
	
	public WzNameSpace getItemFromPath(String path) {
		return root.getItemFromPath(path);
	}
	
	public void close() throws IOException {
		stream.close();
		root = null;
	}

}
