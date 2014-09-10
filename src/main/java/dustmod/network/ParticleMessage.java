package dustmod.network;

import io.netty.buffer.ByteBuf;
import cpw.mods.fml.common.network.simpleimpl.IMessage;

public class ParticleMessage implements IMessage {

	private String type;
	private short formation;
	private double[] loc;
	private double velx;
	private double vely;
	private double velz;
	private int amt;
	private double rx;
	private double ry;
	private double rz;

	public ParticleMessage() {
	}

	public ParticleMessage(String type, short formation, double[] loc, double velx, double vely, double velz, int amt, double rx, double ry, double rz) {
		this.type = type;
		this.formation = formation;
		this.loc = loc;
		this.velx = velx;
		this.vely = vely;
		this.velz = velz;
		this.amt = amt;
		this.rx = rx;
		this.ry = ry;
		this.rz = rz;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		loc = new double[buf.readInt()];
		for (int i = 0; i < loc.length; i++) {
			loc[i] = buf.readFloat();
		}
		
		formation = buf.readShort();
		velx = buf.readFloat();
		vely = buf.readFloat();
		velz = buf.readFloat();
		amt = buf.readInt();
		rx = buf.readFloat();
		ry = buf.readFloat();
		rz = buf.readFloat();
		
		type = NetworkUtil.readString(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(loc.length);
		
		for (double i : loc) {
			buf.writeFloat((float) i);
		}
		
		buf.writeShort(formation);
		buf.writeFloat((float) velx);
		buf.writeFloat((float) vely);
		buf.writeFloat((float) velz);
		buf.writeInt(amt);
		buf.writeFloat((float) rx);
		buf.writeFloat((float) ry);
		buf.writeFloat((float) rz);

		NetworkUtil.writeString(buf, type);
	}

	public String getType() {
		return type;
	}

	public short getFormation() {
		return formation;
	}

	public double getLoc(int index) {
		return loc[index];
	}
	
	public int getLocLength() {
		return loc.length;
	}

	public double getVelx() {
		return velx;
	}

	public double getVely() {
		return vely;
	}

	public double getVelz() {
		return velz;
	}

	public int getAmt() {
		return amt;
	}

	public double getRx() {
		return rx;
	}

	public double getRy() {
		return ry;
	}

	public double getRz() {
		return rz;
	}

}
