package dustmod.network;

import io.netty.buffer.ByteBuf;
import cpw.mods.fml.common.network.simpleimpl.IMessage;

public class MouseMessage implements IMessage {
	
	private int keyId;
	
	private boolean pressed;
	
	public MouseMessage() {
		this.keyId = 0;
		this.pressed = false;
	}
	
	public MouseMessage(int keyId, boolean pressed) {
		this.keyId = keyId;
		this.pressed = pressed;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		keyId = buf.readInt();
		pressed = buf.readBoolean();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(keyId);
		buf.writeBoolean(pressed);
	}
	
	public int getKeyId() {
		return keyId;
	}
	
	public boolean isPressed() {
		return pressed;
	}

}
