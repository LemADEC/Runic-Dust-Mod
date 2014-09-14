package dustmod.network;

import io.netty.buffer.ByteBuf;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import dustmod.inscriptions.InscriptionEvent;

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

		int[][] design = new int[h][w];

		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				design[y][x] = buf.readInt();
			}
		}

		inscription = new InscriptionEvent(design, idname, name, id);
		inscription.setAuthor(author);
		inscription.setDescription(desc);
		inscription.setNotes(notes);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(inscription.width);
		buf.writeInt(inscription.height);
		buf.writeInt(inscription.id);

		NetworkUtil.writeString(buf, inscription.getIDName());
		NetworkUtil.writeString(buf, inscription.getInscriptionName());
		NetworkUtil.writeString(buf, inscription.getDescription());
		NetworkUtil.writeString(buf, inscription.getNotes());
		NetworkUtil.writeString(buf, inscription.getAuthor());

		for (int i = 0; i < inscription.width * inscription.height; i++) {
			buf.writeInt(inscription.referenceDesign[i]);
		}
	}
	
	public InscriptionEvent getInscription() {
		return inscription;
	}

}
