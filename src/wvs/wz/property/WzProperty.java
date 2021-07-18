package wvs.wz.property;

import java.io.IOException;

import wvs.wz.WzInputStream;
import wvs.wz.WzNameSpace;
import wvs.wz.WzNameSpaceType;

public class WzProperty extends WzNameSpace {
	private long propertyOffset;

	public WzProperty(WzInputStream stream, String name, long offset, long propertyOffset) {
		super(stream, name, offset, WzNameSpaceType.PROPERTY);
		this.propertyOffset = propertyOffset;
	}

	@Override
	public boolean load() {
		try {
			stream.getChannel().position(propertyOffset);
			String propertyType = stream.readStringBlock(offset);
			if (propertyType.equals("Property")) {
				loadProperty();
			} else if (propertyType.equals("Canvas")) {
				
			} else if (propertyType.equals("Shape2D#Convex2D")) {
				
			} else if (propertyType.equals("Shape2D#Vector2D")) {
				
			} else if (propertyType.equals("UOL")) {
				
			} else if (propertyType.equals("Sound_DX8")) {
				
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}
	
	private void loadProperty() throws IOException {
		short b = stream.readShort();
		if (b != 0) {
			return;
		}
		
		int num = stream.readCompressedInt();
		for (int i = 0; i < num; i++) {
			String name = stream.readStringBlock(offset);
			byte type = stream.readByte();
			switch (type) {
			case 0:
				child.put(name, new WzVariant<Byte>(name, (byte) 0));
				break;
			case 2: 
			case 11: {
				short data = stream.readShort();
				child.put(name, new WzVariant<Short>(name, data));
				break; }
			case 3:
			case 19: {
				int data = stream.readCompressedInt();
				child.put(name, new WzVariant<Integer>(name, data));
				break; }
			case 20: {
				long data = stream.readLong();
				child.put(name, new WzVariant<Long>(name, data));
				break; }
			case 4: {
				float data;
				if (stream.read() == 0x80) 
					data = stream.readFloat();
				else
					data = 0.0f;
				child.put(name, new WzVariant<Float>(name, data));
				break; }
			case 5: {
				double data = stream.readDouble();
				child.put(name, new WzVariant<Double>(name, data));
				break; }
			case 8: {
				String data = stream.readStringBlock(offset);
				child.put(name, new WzVariant<String>(name, data));
				break; }
			case 9:
				int size = stream.readInt();
		        long pos = stream.getChannel().position() + (int) size;
		        child.put(name, new WzProperty(stream, name, offset, stream.getChannel().position()));
		        stream.getChannel().position(pos);
				break;
			}
		}
	}

}
