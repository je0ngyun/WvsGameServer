package wvs.wz;

import java.io.IOException;

import wvs.wz.property.WzProperty;
import wvs.wz.property.WzVariant;

public class WzPackage extends WzNameSpace {
	private int size;
	private int checkSum;

	public WzPackage(WzInputStream stream, String name, long offset, WzNameSpaceType type) {
		super(stream, name, offset, type);
		try {
			stream.readStringByLength(4);
			stream.readLong(); // 파일크기
			stream.setBeginPos(stream.readInt());
			stream.readNullTerminatedString();
			stream.setKey(1);
			this.offset = stream.getChannel().position();
			load();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public WzPackage(WzInputStream stream, String name, WzNameSpaceType type, int size, int checkSum, long offset) {
		super(stream, name, offset, type);
		this.size = size;
		this.checkSum = checkSum;
	}

	@Override
	public boolean load() {
		try {
			stream.getChannel().position(offset);
			if (type == WzNameSpaceType.DIRECTORY) {
				loadDirectory();
			} else if (type == WzNameSpaceType.PROPERTY) {
				loadProperty();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return true;
		
	}
	
	private void loadDirectory() throws IOException {
		int num = stream.readCompressedInt();
		for (int i = 0; i < num; i++) {
			byte type = stream.readByte();
			String name = "";
			int size;
			int checkSum;
			long offset;
			long rememberPos = 0;
			
			if (type == 1) {
				stream.readInt();
				stream.readShort();
				stream.readInt();
				continue;
			} else if (type == 2) {
				int stringOffset = stream.readInt();
				rememberPos = stream.getChannel().position();
				stream.getChannel().position(stream.getBeginPos() + stringOffset);
				type = stream.readByte();
				name = stream.readString();
			} else if (type == 3 || type == 4) {
				name = stream.readString();
				rememberPos = stream.getChannel().position();
			}
			stream.getChannel().position(rememberPos);
			size = stream.readCompressedInt();
			checkSum = stream.readCompressedInt();
			offset = stream.readOffset();
			if (type == WzNameSpaceType.DIRECTORY.getValue()) {
				child.put(name, new WzPackage(stream, name, WzNameSpaceType.DIRECTORY, size, checkSum, offset));
			} else if (type == WzNameSpaceType.PROPERTY.getValue()) {
				child.put(name, new WzPackage(stream, name, WzNameSpaceType.PROPERTY, size, checkSum, offset));
			}
		}
	}
	
	private void loadProperty() throws IOException {
		String propertyType = stream.readStringBlock(offset);
		if (propertyType.equals("Property")) {
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

}
