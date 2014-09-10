package dustmod.network;

import io.netty.buffer.ByteBuf;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import dustmod.InscriptionEvent;

public class InscriptionDeclarationMessage implements IMessage {

	private InscriptionEvent inscription;

	public InscriptionDeclarationMessage() {
		this.inscription = null;
	}

	public InscriptionDeclarationMessage(InscriptionEvent inscription) {
		this.inscription = inscription;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		int w, h, id;
		String idname, name, author, notes, desc;

		w = buf.readInt();
		h = buf.readInt();
		id = buf.readInt();

		idname = NetworkUtil.readString(buf);
		name = NetworkUtil.readString(buf);
		desc = NetworkUtil.readString(buf);
		notes = NetworkUtil.readString(buf);
		author = NetworkUtil.readString(buf);

		int[][] design = new int[w][h];

		for (int x = 0; x < w; x++) {
			for (int y = 0; y < h; y++) {
				design[x][y] = buf.readInt();
			}
		}

		inscription = new InscriptionEvent(design, idname, name, id);
		inscription.setAuthor(author);
		inscription.setDescription(desc);
		inscription.setNotes(notes);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(inscription.referenceDesign.length);
		buf.writeInt(inscription.referenceDesign[0].length);
		buf.writeInt(inscription.id);

		NetworkUtil.writeString(buf, inscription.getIDName());
		NetworkUtil.writeString(buf, inscription.getInscriptionName());
		NetworkUtil.writeString(buf, inscription.getDescription());
		NetworkUtil.writeString(buf, inscription.getNotes());
		NetworkUtil.writeString(buf, inscription.getAuthor());

		int w = inscription.referenceDesign.length;
		int h = inscription.referenceDesign[0].length;
		for (int x = 0; x < w; x++) {
			for (int y = 0; y < h; y++) {
				buf.writeInt(inscription.referenceDesign[x][y]);
			}
		}
	}
	
	public InscriptionEvent getInscription() {
		return inscription;
	}

}
