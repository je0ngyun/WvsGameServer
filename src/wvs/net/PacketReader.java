package wvs.net;

import java.io.ByteArrayInputStream;

public class PacketReader {
	private ByteArrayInputStream bais;
	
	public PacketReader(byte[] data) {
		bais = new ByteArrayInputStream(data);
	}
	
	public byte readByte() {
		return (byte) bais.read();
	}
	
	/*public short readShort() {
		return (short) (readByte() + (readByte() << 8));
	}*/
	public short readShort() {
		short byte1 = (short) (readByte() & 0xFF);
		short byte2 = (short) (readByte() & 0xFF);
		return (short) ((byte1 << 8) + byte2);
	}
	
	public char readChar() {
		return (char) readShort();
	}
	
	/*public int readInt() {
		return (int) (readByte() + (readByte() << 8) + (readByte() << 16) + (readByte() << 24));
	}*/
	
	public int readInt() {
		int byte1 = readByte() & 0xFF;
		int byte2 = readByte() & 0xFF;
		int byte3 = readByte() & 0xFF;
		int byte4 = readByte() & 0xFF;
		return (int) ((byte1 << 24) + (byte2 << 16) + (byte3 << 8) + byte4);
	}
	
	public long readLong() {
		long byte1 = readByte();
        long byte2 = readByte();
        long byte3 = readByte();
        long byte4 = readByte();
        long byte5 = readByte();
        long byte6 = readByte();
        long byte7 = readByte();
        long byte8 = readByte();
        return (byte8 << 56) + (byte7 << 48) + (byte6 << 40) + (byte5 << 32) + (byte4 << 24) + (byte3 << 16) + (byte2 << 8) + byte1;
	}
	
	public float readFloat() {
		return Float.intBitsToFloat(readInt());
	}
	
	public double readDouble() {
		return Double.longBitsToDouble(readLong());
	}
	
	public String readString() {
		int n = (int) readShort();
		char ret[] = new char[n];
		for (int i = 0; i < n; i++) {
			ret[i] = (char) readByte();
		}
		return String.valueOf(ret);
		
	}
	
	public void skip(int num) {
		for (int i = 0; i < num; i++) {
			readByte();
		}
	}
}
