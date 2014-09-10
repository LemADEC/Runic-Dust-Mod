package dustmod.network;

import com.google.common.base.Charsets;

import io.netty.buffer.ByteBuf;

public class NetworkUtil {
	
	public static String readString(ByteBuf buf) {
		
		int length = buf.readInt();
		
        if (length > 0) {
        	byte[] buffer = new byte[length];
        	buf.readBytes(buffer);
        	return new String(buffer, Charsets.UTF_8);
        } else {
        	return "";
        }
	}
	
	public static void writeString(ByteBuf buf, String value) {
		
		byte[] bytes = value.getBytes(Charsets.UTF_8);
		buf.writeInt(bytes.length);
		buf.writeBytes(bytes);
	}

}
