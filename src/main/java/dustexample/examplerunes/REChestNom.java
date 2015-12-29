/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dustexample.examplerunes;

import java.util.List;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.world.World;
import dustmod.blocks.TileEntityDust;
import dustmod.runes.RuneEvent;
import dustmod.runes.EntityRune;

/**
 *
 * Rune of OmNomChest
 * 
 * Good example of: -Item sacrifices -Rune positions -Star graphics -What the unload function is for
 * 
 * @author billythegoat101
 */
public class REChestNom extends RuneEvent {
	
	/**
	 * Called to set the graphical components of the rune
	 * 
	 * @param entityRune
	 *            EntityDust instance
	 */
	@Override
	public void initGraphics(EntityRune entityRune) {
		super.initGraphics(entityRune);
		
		/*** GRAPHICS **/
		
		// To make it look cool. This enables the rune to have that glowing spinning star effect.
		entityRune.setRenderStar(true);
		
		// Make the star slightly bigger than the chest so that it shines through and looks cool.
		entityRune.setStarScale(2.2F);
		
		// Sets the color of the star (both inside and out) to look yellow.
		entityRune.setColorStar(255, 255, 0);
		// Use EntityDust.setColorInner and setColorOuter to change inner and outer separately.
	}
	
	/**
	 * Called when a rune of this type is created in the world
	 * 
	 * @param entityRune
	 *            EntityDust instance
	 */
	@Override
	public void onInit(EntityRune entityRune) {
		super.onInit(entityRune);
		
		/** SACRIFICES **/
		
		/** Item sacrifice **/
		
		//An array of all item/block sacrifices required.
		ItemStack[] requiredSacrifice = new ItemStack[] { new ItemStack(Blocks.chest, 1, 0), new ItemStack(Items.gold_ingot, 1, 0) };
		// This array is optional, You can just list all the itemstacks directly into takeItems
		// Like this : boolean success = takeItems(e,new ItemStack(Block.chest.blockID,1,0), new ItemStack(Item.ingotGold.itemID,1,0));
		
		// Automatically searches near the rune for the requested item sacrifices
		// For each item found, the stacksize in requiredSacrifice is brought down.
		// returns true if the full sacrifice was fulfilled
		if (!takeItems(entityRune, requiredSacrifice)) {
			// Sacrifice not fulfilled, therefore kill the rune.
			entityRune.fizzle();
			return;
		}
		
		/** XP Sacrifice **/
		int levels = 1;
		// Takes required levels from the nearby players
		if (!takeXP(entityRune, levels)) {
			// it returned false, therefore the level requirement was not met
			entityRune.fizzle();
			return;
		}
		
		/** Hunger Sacrifice **/
		int numHalfBars = 1;
		if (!takeHunger(entityRune, numHalfBars)) {
			entityRune.fizzle();
			return;
		}
		
		// If we've made it here then the sacrifice must have been fulfilled.
		// so now we place that chest
		World world = entityRune.worldObj;
		world.setBlock(entityRune.getX(), entityRune.getY(), entityRune.getZ(), Blocks.chest, 0, 3);
	}
	
	/**
	 * Called by the EntityRune instance that has been assigned this RuneEvent type
	 * 
	 * @param entityRune
	 *            EntityRune instance
	 */
	@Override
	public void onTick(EntityRune entityRune) {
		super.onTick(entityRune);
		// Used to help free up the framerate a little. You don't need to check
		// for items every single tick, it will just cause lag. This checks ever 1/4 second
		
		if (entityRune.ticksExisted % 5 == 0) {
			World world = entityRune.worldObj;
			// Getting the block position of the EntityDust
			int x = entityRune.getX();
			int y = entityRune.getY();
			int z = entityRune.getZ();
			
			// confirm chest is still present
			if (world.getBlock(x, y, z) != Blocks.chest) {
				// Kills the rune.
				entityRune.fizzle();
				return;
			}
			TileEntityChest tileEntityChest = null;
			tileEntityChest = (TileEntityChest) world.getTileEntity(x, y, z);
			
			// Get all items within 2 blocks of this EntityDust
			List<EntityItem> entityItems = getItems(entityRune, 4.0D);
			
			// If there are items detected nearby, trigger the openChest animation
			if (entityItems.size() > 0) {
				tileEntityChest.openInventory();
				entityRune.data[0] = entityRune.ticksExisted + 20; //Used to delay-trigger the closing chest animation
			}
			
			// Loop through all detected dropped items nearby
			for (EntityItem entityItem : entityItems) {
				// Getting the actual ItemStack of the dropped item.
				ItemStack itemStackToAdd = entityItem.getEntityItem();
				
				// Loop through all the slots in the chest
				for (int slotIndex = 0; slotIndex < tileEntityChest.getSizeInventory() && itemStackToAdd.stackSize > 0; slotIndex++) {
					ItemStack itemStackInChest = tileEntityChest.getStackInSlot(slotIndex);
					
					// If the item in that chest slot matches the one dropped, 
					if (itemStackInChest != null && itemStackInChest.stackSize < itemStackInChest.getMaxStackSize() && itemStackInChest.getItem() == itemStackToAdd.getItem()
							&& itemStackInChest.getItemDamage() == itemStackToAdd.getItemDamage() && ItemStack.areItemStackTagsEqual(itemStackInChest, itemStackToAdd)) {
						// Then add the dropped item to that itemstack in the chest.
						itemStackInChest.stackSize += itemStackToAdd.stackSize;
						itemStackToAdd.stackSize = 0;
						if (itemStackInChest.stackSize > itemStackInChest.getMaxStackSize()) {
							itemStackToAdd.stackSize = itemStackInChest.stackSize - itemStackInChest.getMaxStackSize();
							itemStackInChest.stackSize = itemStackInChest.getMaxStackSize();
						}
						if (itemStackToAdd.stackSize <= 0) {
							entityItem.setDead();
							break;
						}
					}
				}
				
				// Loop through the slots again if there is still an amount of items in the dropped itemstack
				for (int slotIndex = 0; slotIndex < tileEntityChest.getSizeInventory() && itemStackToAdd.stackSize > 0; slotIndex++) {
					ItemStack itemStackInChest = tileEntityChest.getStackInSlot(slotIndex);
					// If the slot is empty, put the dropped itemstack there.
					if (itemStackInChest == null || itemStackInChest.getItem() == null) {
						tileEntityChest.setInventorySlotContents(slotIndex, itemStackToAdd);
						entityItem.setDead();
						break;
					}
				}
				entityItem.setEntityItemStack(itemStackToAdd);
			}
		}
		
		// Triggering of the closeChest animation after the delay
		if (entityRune.ticksExisted >= entityRune.data[0]) {
			World world = entityRune.worldObj;
			int x = entityRune.getX();
			int y = entityRune.getY();
			int z = entityRune.getZ();
			
			if (world.getBlock(x, y, z) == Blocks.chest) {
				TileEntityChest tec = (TileEntityChest) world.getTileEntity(x, y, z);
				tec.closeInventory();
			}
		}
	}
	
	/**
	 * Called if an activated rune is ever right-clicked
	 * 
	 * @param entityRune
	 *            EntityDust instance
	 * @param tileEntityDust
	 *            The TileEntityDust that was right-clicked
	 * @param entityPlayer
	 *            The player who clicked
	 */
	@Override
	public void onRightClick(EntityRune entityRune, TileEntityDust tileEntityDust, EntityPlayer entityPlayer) {
		// No purpose with this rune.
		super.onRightClick(entityRune, tileEntityDust, entityPlayer);
	}
	
	/**
	 * Called when a rune is destroyed or caused by any means to stop.
	 * 
	 * @param entityRune
	 *            The EntityDust instance
	 */
	@Override
	public void onUnload(EntityRune entityRune) {
		super.onUnload(entityRune);
		
		// This just finds the TileEntityChest (if it still exists) and closes
		// it because otherwise there's a chance it will be left glitched open
		// forever.
		World world = entityRune.worldObj;
		int x = entityRune.getX();
		int y = entityRune.getY();
		int z = entityRune.getZ();
		
		TileEntityChest tileEntityChest = null;
		if (world.getBlock(x, y, z) == Blocks.chest) {
			tileEntityChest = (TileEntityChest) world.getTileEntity(x, y, z);
			tileEntityChest.closeInventory();
		}
	}
}
