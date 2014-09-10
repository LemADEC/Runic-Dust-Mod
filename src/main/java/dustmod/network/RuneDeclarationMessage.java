package dustmod.network;

import io.netty.buffer.ByteBuf;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import dustmod.DustShape;

public class RuneDeclarationMessage implements IMessage {

	private DustShape shape;

	public RuneDeclarationMessage() {
		this.shape = null;
	}

	public RuneDeclarationMessage(DustShape shape) {
		this.shape = shape;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		int w, h, l, id, ox, oy, cx, cy, pageNumber;
		String name, pName, author, notes, desc;
		boolean powered, solid;

		w = buf.readInt();
		h = buf.readInt();
		l = buf.readInt();
		id = buf.readInt();
		ox = buf.readInt();
		oy = buf.readInt();
		cx = buf.readInt();
		cy = buf.readInt();
		pageNumber = buf.readInt();
		powered = buf.readBoolean();
		solid = buf.readBoolean();

		name = NetworkUtil.readString(buf);
		pName = NetworkUtil.readString(buf);
		author = NetworkUtil.readString(buf);
		notes = NetworkUtil.readString(buf);
		desc = NetworkUtil.readString(buf);

		int[][][] design = new int[h][w][l];

		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				for (int z = 0; z < l; z++) {
					design[y][x][z] = buf.readInt();
				}
			}
		}

		int[] manRot = new int[8];
		for (int i = 0; i < 8; i++) {
			manRot[i] = buf.readInt();
		}

		DustShape shape = new DustShape(w, l, name, solid, ox, oy, cx, cy, pageNumber, id);
		shape.setData(design);
		shape.setRuneName(pName);
		shape.setNotes(notes);
		shape.setDesc(desc);
		shape.setAuthor(author);
		shape.isPower = powered;
		shape.rotationMatrix = manRot;

		shape.isRemote = true;
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(shape.width);
		buf.writeInt(shape.height);
		buf.writeInt(shape.length);
		buf.writeInt(shape.id);
		buf.writeInt(shape.ox);
		buf.writeInt(shape.oy);
		buf.writeInt(shape.cx);
		buf.writeInt(shape.cy);
		buf.writeInt(shape.pageNumber);
		buf.writeBoolean(shape.isPower);
		buf.writeBoolean(shape.solid);

		NetworkUtil.writeString(buf, shape.name);
		NetworkUtil.writeString(buf, shape.getRuneName());
		NetworkUtil.writeString(buf, shape.getAuthor());
		NetworkUtil.writeString(buf, shape.getNotes());
		NetworkUtil.writeString(buf, shape.getDescription());

		for (int y = 0; y < shape.height; y++) {
			for (int x = 0; x < shape.width; x++) {
				for (int z = 0; z < shape.length; z++) {
					buf.writeInt(shape.getDataAt(x, y, z));
				}
			}
		}

		for (int i = 0; i < 8; i++) {
			buf.writeInt(shape.rotationMatrix[i]);
		}
	}

	public DustShape getShape() {
		return shape;
	}

}
