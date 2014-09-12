package dustmod.common;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.IGuiHandler;
import dustmod.DustMod;
import dustmod.client.GuiInscription;
import dustmod.inscriptions.InscriptionGuiContainer;

public class GuiHandler implements IGuiHandler {

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world,
			int x, int y, int z) {
		ItemStack item = player.getCurrentEquippedItem();
		if(item != null && item.getItem() == DustMod.inscription){
			return new InscriptionGuiContainer(player.inventory, DustMod.inscription.getInventory(item));
		}
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world,
			int x, int y, int z) {
		ItemStack item = player.getCurrentEquippedItem();
		if(item != null && item.getItem() == DustMod.inscription){
			return new GuiInscription(player, DustMod.inscription.getInventory(item));
		}
		return null;
	}

}
