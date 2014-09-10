package dustmod.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.EntityLivingBase;
import cpw.mods.fml.common.network.simpleimpl.IMessage;

public class SetVelocityMessage implements IMessage {
	
	private int entityId;
	
	private float motionX;
	
	private float motionY;
	
	private float motionZ;
	
	private float jumpMovementFactor;
	
	public SetVelocityMessage() {
	}

	public SetVelocityMessage(EntityLivingBase e) {
		this.entityId = e.getEntityId();
		this.motionX = (float) e.motionX;
		this.motionY = (float) e.motionY;
		this.motionZ = (float) e.motionZ;
		this.jumpMovementFactor = e.jumpMovementFactor;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		entityId = buf.readInt();
		motionX = buf.readFloat();
		motionY = buf.readFloat();
		motionZ = buf.readFloat();
		jumpMovementFactor = buf.readFloat();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(entityId);
		buf.writeFloat(motionX);
		buf.writeFloat(motionY);
		buf.writeFloat(motionZ);
		buf.writeFloat(jumpMovementFactor);
	}

	public int getEntityId() {
		return entityId;
	}

	public float getMotionX() {
		return motionX;
	}

	public float getMotionY() {
		return motionY;
	}

	public float getMotionZ() {
		return motionZ;
	}

	public float getJumpMovementFactor() {
		return jumpMovementFactor;
	}

}
