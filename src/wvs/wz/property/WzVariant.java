package wvs.wz.property;

import wvs.wz.WzInputStream;
import wvs.wz.WzNameSpace;
import wvs.wz.WzNameSpaceType;

public class WzVariant<T> extends WzNameSpace {
	
	private T value;
	
	public WzVariant(String name, T data) {
		super(null, name, 0, null);
		this.value = data;
	}

	@Override
	public boolean load() {
		return true;
	}
	
	public T getValue() {
		return value;
	}

}
