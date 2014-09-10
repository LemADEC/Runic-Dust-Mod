package dustmod.network;

import io.netty.buffer.ByteBuf;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import dustmod.Dust;

public class DustDeclarationMessage implements IMessage {

	private Dust dust;
	private int value;

	public DustDeclarationMessage() {
		this.dust = null;
	}

	public DustDeclarationMessage(int value, Dust dust) {
		this.dust = dust;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		value = buf.readInt();
		
		int primaryColor = buf.readInt();
		int secondaryColor = buf.readInt();
		int floorColor = buf.readInt();
		
		String id = NetworkUtil.readString(buf);
		String name = NetworkUtil.readString(buf);
		
		dust = new Dust(id, name, primaryColor, secondaryColor, floorColor);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(value);

		buf.writeInt(dust.getPrimaryColor());
		buf.writeInt(dust.getSecondaryColor());
		buf.writeInt(dust.getFloorColor());

		NetworkUtil.writeString(buf, dust.getId());
		NetworkUtil.writeString(buf, dust.getName());
	}

	public Dust getDust() {
		return dust;
	}
	
	public int getValue() {
		return value;
	}

}
