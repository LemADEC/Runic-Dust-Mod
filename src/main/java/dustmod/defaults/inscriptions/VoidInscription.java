package dustmod.defaults.inscriptions;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import dustmod.defaults.runes.VoidStorageManager;
import dustmod.inscriptions.InscriptionEvent;
import dustmod.runes.RuneEvent;
import dustmod.runes.EntityRune;

public class VoidInscription extends InscriptionEvent {

	public VoidInscription(int[][] design, String idName, String properName,
			int id) {
		super(design, idName, properName, id);
		this.setAuthor("billythegoat101");
		this.setDescription("Description\n"
				+ "Automatically store all picked-up items into the void. Use the Void Storage rune to summon them back.");
		this.setNotes("Sacrifice\n" +
				"- 4x Obsidian Block + 2x Ender Pearl + 5 XP levels");
	}
	
	@Override
	public boolean callSacrifice(RuneEvent rune, EntityRune e, ItemStack item) {
		ItemStack[] req = new ItemStack[]{new ItemStack(Blocks.obsidian,4), new ItemStack(Items.ender_pearl, 2)};
		req = rune.sacrifice(e, req);
		if(!rune.checkSacrifice(req)) return false;
		if(!rune.takeXP(e, 5));
		item.setItemDamage(0);
		return true;
	}

	/* TODO: reimplement void storage
	@Override
	public ItemStack onItemPickup(EntityLivingBase wearer, ItemStack insc, ItemStack pickedup) {
		VoidStorageManager.addItemToVoidInventory(
				((EntityPlayer) wearer).getGameProfile().getId(), pickedup);
		ItemStack rtn = pickedup.copy();
		this.damage((EntityPlayer)wearer, insc, pickedup.stackSize);
		rtn.stackSize = 0;
		return rtn;
	}
		/**/

}
