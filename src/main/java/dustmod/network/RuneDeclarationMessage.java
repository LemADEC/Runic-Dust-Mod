package dustmod.network;

import io.netty.buffer.ByteBuf;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import dustmod.runes.RuneShape;

public class RuneDeclarationMessage implements IMessage {

	private RuneShape shape;

	public RuneDeclarationMessage() {
		this.shape = null;
	}

	public RuneDeclarationMessage(RuneShape shape) {
		this.shape = shape;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		int w = buf.readInt();
		int h = buf.readInt();
		int l = buf.readInt();
		int id = buf.readInt();
		int cx = buf.readInt();
		int cy = buf.readInt();
		int pageNumber = buf.readInt();
		boolean powered = buf.readBoolean();
		boolean solid = buf.readBoolean();

		String name = NetworkUtil.readString(buf);
		String pName = NetworkUtil.readString(buf);
		String author = NetworkUtil.readString(buf);
		String notes = NetworkUtil.readString(buf);
		String desc = NetworkUtil.readString(buf);

		int[][][] design = new int[h][w][l];

		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				for (int z = 0; z < l; z++) {
					design[y][x][z] = buf.readInt();
				}
			}
		}

		shape = new RuneShape(design, name, solid, cx, cy, pageNumber, id);
		shape.setRuneName(pName);
		shape.setNotes(notes);
		shape.setDesc(desc);
		shape.setAuthor(author);
		shape.isPower = powered;

		shape.isRemote = true;
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(shape.width);
		buf.writeInt(shape.height);
		buf.writeInt(shape.length);
		buf.writeInt(shape.id);
		buf.writeInt(shape.cx);
		buf.writeInt(shape.cz);
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
	}

	public RuneShape getShape() {
		return shape;
	}

}
