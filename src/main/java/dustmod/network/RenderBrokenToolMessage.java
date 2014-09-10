package dustmod.network;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import io.netty.buffer.ByteBuf;
import cpw.mods.fml.common.network.simpleimpl.IMessage;

public class RenderBrokenToolMessage implements IMessage {

	private int entityId;

	private int itemId;

	private int damage;

	public RenderBrokenToolMessage() {
	}

	public RenderBrokenToolMessage(EntityPlayer entity, ItemStack tool) {
		this.entityId = entity.getEntityId();
		this.itemId = Item.getIdFromItem(tool.getItem());
		this.damage = tool.getItemDamage();
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		entityId = buf.readInt();
		itemId = buf.readInt();
		damage = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(entityId);
		buf.writeInt(itemId);
		buf.writeInt(damage);
	}

	public int getEntityId() {
		return entityId;
	}

	public int getItemId() {
		return itemId;
	}

	public int getDamage() {
		return damage;
	}

}
