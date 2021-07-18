package wvs.map;

import java.awt.geom.Rectangle2D;

import wvs.wz.WzNameSpace;
import wvs.wz.property.WzVariant;

/*
 * 해당 포탈을 타면 targetMap에 있는 포탈들 중 name이 targetName인 포탈을 찾아서
 * 플레이어의 좌표값을 그 포탈의 x, y로 변경시킨다. */

public class WvsPortal {
	private String name;
	private int type; // 0 = 스폰 1, 2 = 이동
	private int targetMap;
	private String targetName;
	private int x;
	private int y;
	private Rectangle2D rect;

	@SuppressWarnings("unchecked")
	public WvsPortal(WzNameSpace property, MiniMap miniMap) {
		this.name = ((WzVariant<String>) property.getItem("pn")).getValue();
		this.type = ((WzVariant<Integer>) property.getItem("pt")).getValue();
		this.targetMap = ((WzVariant<Integer>) property.getItem("tm")).getValue();
		this.targetName = ((WzVariant<String>) property.getItem("tn")).getValue();
		this.x = ((WzVariant<Integer>) property.getItem("x")).getValue() + miniMap.getCenterX();
		this.y = ((WzVariant<Integer>) property.getItem("y")).getValue() + miniMap.getCenterY();
		rect = new Rectangle2D.Double(x - 30, y - 60, 60, 60);
	}

	public Rectangle2D getRect() {
		return rect;
	}

	public String getName() {
		return name;
	}

	public int getType() {
		return type;
	}

	public int getTargetMap() {
		return targetMap;
	}

	public String getTargetName() {
		return targetName;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}
}
