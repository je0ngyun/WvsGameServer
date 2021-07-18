package wvs.map;

import wvs.wz.WzNameSpace;
import wvs.wz.property.WzVariant;

public class MiniMap {
	private int width;
	private int height;
	private int centerX;
	private int centerY;
	private int mag;
	
	public MiniMap() {
		this.width = 800;
		this.height = 600;
		this.centerX = 0;
		this.centerY = 0;
		this.mag = 0;
	}
	
	@SuppressWarnings("unchecked")
	public MiniMap(WzNameSpace property) {
		this.centerX = ((WzVariant<Integer>) property.getItem("centerX")).getValue();
		this.centerY = ((WzVariant<Integer>) property.getItem("centerY")).getValue();
		this.height = ((WzVariant<Integer>) property.getItem("height")).getValue();
		this.width = ((WzVariant<Integer>) property.getItem("width")).getValue();
		this.mag = ((WzVariant<Integer>) property.getItem("mag")).getValue();
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public int getCenterX() {
		return centerX;
	}
	
	public int getCenterY() {
		return centerY;
	}

}
