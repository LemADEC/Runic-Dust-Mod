package dustmod.common;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import dustmod.DustMod;
import dustmod.dusts.DustItemManager;
import dustmod.inscriptions.InscriptionEvent;
import dustmod.inscriptions.InscriptionManager;
import dustmod.items.ItemPouch;
import dustmod.network.DustDeclarationMessage;
import dustmod.network.InscriptionDeclarationMessage;
import dustmod.network.RuneDeclarationMessage;
import dustmod.runes.RuneEvent;
import dustmod.runes.RuneManager;
import dustmod.runes.RuneShape;

public class PlayerEventHandler {
	
	@SubscribeEvent
	public void onLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
		
		DustMod.keyHandler.checkPlayer(event.player);
		
		if (event.player instanceof EntityPlayerMP) {
			EntityPlayerMP player = (EntityPlayerMP) event.player;
			for (int i = 0; i < DustItemManager.dusts.length; i++) {
				if (DustItemManager.dusts[i] != null) {
					DustMod.networkWrapper.sendTo(new DustDeclarationMessage(i, DustItemManager.dusts[i]), player);
				}
			}
			
			for (RuneShape shape : RuneManager.shapes) {
				RuneEvent e = RuneManager.getEvents().get(shape.name);
				if (e.canPlayerKnowRune(player.getUniqueID()) && !e.secret) {
					DustMod.networkWrapper.sendTo(new RuneDeclarationMessage(shape), player);
				}
			}
	
			for (InscriptionEvent evt : InscriptionManager.events) {
				if (evt.canPlayerKnowInscription((EntityPlayer) player) && !evt.secret) {
					DustMod.networkWrapper.sendTo(new InscriptionDeclarationMessage(evt), player);
				}
			}
		}
	}
	
	@SubscribeEvent
	public void onCrafting(PlayerEvent.ItemCraftedEvent event) {
		ItemStack pouch = null;
		boolean hasPouch = false;
		for(int i = 0; i < event.craftMatrix.getSizeInventory(); i++){
			ItemStack inv = event.craftMatrix.getStackInSlot(i);
			if(inv != null && inv.getItem() == DustMod.pouch){
				pouch = inv;
				hasPouch = true;
			}
		}
		
		if (hasPouch && (event.crafting.getItem() == DustMod.idust || event.crafting.getItem() == DustMod.ink)){
			ItemPouch.subtractDust(pouch, 1);
			DustMod.pouch.setContainerItemstack(pouch);
		}
		
//		else if(hasPouch && item.itemID == DustMod.pouch.itemID){
//			DustMod.pouch.setContainerItem(null);
//			DustMod.pouch.setContainerItemstack(null);
//			ItemPouch.setAmount(item, ItemPouch.getDustAmount(pouch) + 1);
//		}

		if(hasPouch){
			int dust = ItemPouch.getValue(pouch);
			if(ItemPouch.getDustAmount(pouch) == 0){
				pouch.setItemDamage(dust*2);
			}else{
				pouch.setItemDamage(dust*2+1);
			}
		}
	}

	@SubscribeEvent
	public void onItemPickup(EntityItemPickupEvent evt){
		EntityPlayer player = evt.entityPlayer;
		ItemStack item = evt.item.getEntityItem();
		
		int dust = item.getItemDamage();
		if(item.getItem() == DustMod.idust && 
				(player.inventory.hasItemStack(new ItemStack(DustMod.pouch, 1, dust * 2)) 
				|| player.inventory.hasItemStack(new ItemStack(DustMod.pouch, 1, dust * 2 +1)))){
			InventoryPlayer inv = player.inventory;
			for(int i = 0; i < inv.getSizeInventory(); i++){
				ItemStack check = inv.getStackInSlot(i);
				if(check != null && check.getItem() == DustMod.pouch && (check.getItemDamage() == dust*2 || check.getItemDamage() == dust*2+1)){
					int left = ItemPouch.addDust(check, item.stackSize);
					item.stackSize = left;
				}
			}
		}
		
		evt.item.setEntityItemStack(InscriptionManager.onItemPickup(player, item));
	}
	
	
	@SubscribeEvent
	public void onLivingUpdate(LivingUpdateEvent evt){

		Entity ent = evt.entityLiving;
    	if(ent instanceof EntityPlayer){
    		EntityPlayer p = (EntityPlayer) ent;
    		ItemStack item = p.inventory.getStackInSlot(38);

    		if(item != null && item.getItem() == DustMod.wornInscription) {
    			boolean[] buttons = DustMod.keyHandler.getButtons(p.getGameProfile().getId()); 
    			InscriptionManager.tickInscription(p, buttons, p.inventory.getStackInSlot(38));
    		}
    	}
	}
	
	/*
	 * 		DustMod.log(Level.FINER, "Resetting due to opened connection.1");
		DustManager.resetMultiplayerRunes();
		DustItemManager.reset();
		InscriptionManager.resetRemoteInscriptions();
	 */

}
