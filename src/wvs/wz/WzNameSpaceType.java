package wvs.wz;

public enum WzNameSpaceType {
	DIRECTORY(3), PROPERTY(4);
	int value;
	
	WzNameSpaceType(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return value;
	}
	
}
