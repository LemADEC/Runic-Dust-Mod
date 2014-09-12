package dustmod.network;

import io.netty.buffer.ByteBuf;
import cpw.mods.fml.common.network.simpleimpl.IMessage;

public class SetInscriptionMessage implements IMessage {
	
	private int[] design;
	
	public SetInscriptionMessage() {
		this.design = null;
	}
	
	public SetInscriptionMessage(int[] design) {
		this.design = design;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		design = new int[16*16];
		
		for (int i = 0; i < design.length; i++) {
			design[i] = buf.readInt();
		}
	}

	@Override
	public void toBytes(ByteBuf buf) {
		for (int i = 0; i < design.length; i++) {
			buf.writeInt(design[i]);
		}
	}
	
	public int[] getDesign() {
		return design;
	}

}
