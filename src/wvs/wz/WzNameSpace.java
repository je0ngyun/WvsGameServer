package wvs.wz;

import java.util.HashMap;
import java.util.LinkedList;

public abstract class WzNameSpace {
	protected WzInputStream stream;
	protected String name;
	protected long offset;
	protected boolean isLoad;
	protected WzNameSpaceType type;
	protected HashMap<String, WzNameSpace> child;
	
	public WzNameSpace(WzInputStream stream, String name, long offset, WzNameSpaceType type) {
		this.stream = stream;
		this.name = name;
		this.offset = offset;
		this.isLoad = false;
		this.type = type;
		child = new HashMap<>();
	}
	
	
	public abstract boolean load();
	
	public String getName() {
		return name;
	}
	
	public WzNameSpaceType getType() {
		return type;
	}
	
	public WzNameSpace getItem(String name) {
		if (!isLoad) {
			isLoad = load();
		}
		
		return child.get(name);
	}
	
	public WzNameSpace getItemFromPath(String path) {
		String[] seperatedPath = path.split("/");
		WzNameSpace object = this;
		
		for (int i = 0; i < seperatedPath.length; i++) {
			if (object == null) {
				break;
			}
			object = object.getItem(seperatedPath[i]);
		}
		
		return object;
	}
	
	public LinkedList<WzNameSpace> getChilds() {
		if (!isLoad) {
			isLoad = load();
		}
		
		return new LinkedList<WzNameSpace>(child.values());
	}

}
