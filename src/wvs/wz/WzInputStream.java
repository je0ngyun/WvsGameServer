package wvs.wz;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class WzInputStream extends FileInputStream {
	private long beginPos;
	private int key;
	
	public WzInputStream(String name) throws FileNotFoundException {
		super(name);
		// TODO Auto-generated constructor stub
	}
	
	public byte[] readBytes(int len) throws IOException {
		byte[] result = new byte[len];
		read(result);
		return result;
	}
	
	public byte readByte() throws IOException {
		return (byte) read();
	}
	
	public short readShort() throws IOException {
		return (short) (read() + (read() << 8));
	}
	
	public int readInt() throws IOException {
		return (int) (read() + (read() << 8) + (read() << 16) + (read() << 24));
	}
	
	public long readLong() throws IOException {
		return (long) (read() + (read() << 8) + (read() << 16) + (read() << 24) + (read() << 32) + (read() << 40) + (read() << 48) + (read() << 56));
	}
	
	public char readChar() throws IOException {
		return (char) readShort();
	}
	
	public float readFloat() throws IOException {
		return Float.intBitsToFloat(readInt());
	}
	
	public double readDouble() throws IOException {
		return Double.longBitsToDouble(readLong());
	}
	
	public int readCompressedInt() throws IOException {
		byte num = (byte) read();
		
		if (num == -128) {
			return readInt();
		} else {
			return (int) num;
		}
	}
	
	public String readStringByLength(int len) throws IOException {
		String result = "";
		for (int i = 0; i < len; i++) {
			result += (char) read();
		}
		return result;
	}
	
	public String readNullTerminatedString() throws IOException {
		String result = "";
		byte b = (byte) read();
		while (b != 0) {
			result += (char) b;
			b = (byte) read();
		}
		return result;
	}
	
	public String readString() throws IOException {
		String result = "";
		int len = (int) readByte();
		if (len == 0) {
			return result;
		} else if (len > 0) {
			int mask = 0xAAAA;
			if (len == 127) {
				len = readInt();
			}
			if (len <= 0) {
				return result;
			}
			for (int i = 0; i < len; i++) {
				char encryptedChar = readChar();
				encryptedChar ^= mask;
				encryptedChar ^= 0;
				result += encryptedChar;
				mask++;
			}
		} else {
			byte mask = (byte) 0xAA;
			if (len == -128) {
				len = readInt();
			} else {
				len = -len;
			}
			if (len <= 0) {
				return result;
			}
			for (int i = 0; i < len; i++) {
				byte encryptedChar = readByte();
				encryptedChar ^= mask;
				encryptedChar ^= 0;
				result += (char) encryptedChar;
				mask++;
			}
		}
		return result;
	}
	
	public String readStringBlock(long offset) throws IOException {
		String result = "";
		
		byte key = readByte();
		switch (key) {
		case 0:
		case 0x73:
			result = readString();
			break;
		case 1:
		case 0x1B:
			int inc = readInt();
			long temp = getChannel().position();
			getChannel().position(offset + inc);
			result = readString();
			getChannel().position(temp);
			break;
		default:
			break;
		}
		
		return result;
	}
	
	public long readOffset() throws IOException {
		long offset = getChannel().position() & 0xFFFFFFFFL;
		offset = ((offset - beginPos) ^ 0xFFFFFFFFL) & 0xFFFFFFFFL;
		offset = (offset * key) & 0xFFFFFFFFL;
		offset = (offset - 0x581C3F6D) & 0xFFFFFFFFL;
		offset = ((offset << (offset & 0x1F)) | (offset >> (32 - offset & 0x1F))) & 0xFFFFFFFFL;
		offset = (offset ^ readInt()) & 0xFFFFFFFFL;
		offset = (offset + beginPos * 2) & 0xFFFFFFFFL;
		return offset;
	}

	public int getKey() {
		return key;
	}
	
	public void setBeginPos(int pos) {
		this.beginPos = pos;
	}
	
	public long getBeginPos() {
		return beginPos;
	}

	public void setKey(int version) throws IOException {
		int encVersion = (int) readShort();
		int versionHash = 0;
		int decVersion = 0;
		String versionStr = Integer.toString(version);
		
		int a = 0, b = 0, c = 0, d = 0, l = 0;
		
		l = versionStr.length();
		for (int i = 0; i < l; i++) {
			versionHash = (32 * versionHash) + (int) (versionStr.charAt(i) + 1);
		}
		a = (versionHash >> 24) & 0xFF;
		b = (versionHash >> 16) & 0xFF;
		c = (versionHash >> 8) & 0xFF;
		d = versionHash & 0xFF;
		
		decVersion = (0xff ^ a ^ b ^ c ^ d);
		if (encVersion == decVersion)
		{
			key = versionHash;
		}
		else
		{
			key = 0;
		}
	}
}
