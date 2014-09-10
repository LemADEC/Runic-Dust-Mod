package dustmod.network;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.common.registry.GameData;

public class RenderBrokenToolHandler implements IMessageHandler<RenderBrokenToolMessage, IMessage> {

	@Override
	public IMessage onMessage(RenderBrokenToolMessage message, MessageContext ctx) {

		EntityPlayer ent = (EntityPlayer) Minecraft.getMinecraft().theWorld.getEntityByID(message.getEntityId());

		if (ent != null) {
			Item item = GameData.getItemRegistry().getObjectById(message.getItemId());
			
			ent.renderBrokenItemStack(new ItemStack(item, 1, message.getDamage()));
			ent.addStat(StatList.objectBreakStats[message.getItemId()], 1);
			ent.sendPlayerAbilities();
		}

		return null;
	}

}
