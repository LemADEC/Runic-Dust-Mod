package dustmod.network;

import io.netty.buffer.ByteBuf;
import cpw.mods.fml.common.network.simpleimpl.IMessage;

public class UseInkMessage implements IMessage {
	
	private int slot;
	
	private int amount;
	
	public UseInkMessage() {
		this.slot = 0;
		this.amount = 0;
	}

	public UseInkMessage(int slot, int amount) {
		this.slot = slot;
		this.amount = amount;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		slot = buf.readInt();
		amount = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(slot);
		buf.writeInt(amount);
	}
	
	public int getSlot() {
		return slot;
	}
	
	public int getAmount() {
		return amount;
	}

}
