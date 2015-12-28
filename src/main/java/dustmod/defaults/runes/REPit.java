/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dustmod.defaults.runes;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import dustmod.runes.RuneEvent;
import dustmod.runes.EntityRune;

/**
 *
 * @author billythegoat101
 */
public class REPit extends RuneEvent {
	public REPit() {
		super();
	}
	
	@Override
	public void initGraphics(EntityRune entityRune) {
		super.initGraphics(entityRune);
		
		entityRune.setRenderStar(true);
	}
	
	@Override
	public void onInit(EntityRune entityRune) {
		int dustID = entityRune.dustID;
		int dist = 1;
		
		ItemStack[] sacrifice;
		switch (dustID) {
		case 100:
			sacrifice = new ItemStack[] { new ItemStack(Blocks.log, 1, -1) };
			dist = 8;
			break;
		
		case 200:
			sacrifice = new ItemStack[] { new ItemStack(Blocks.log, 2, -1) };
			dist = 16;
			break;
		
		case 300:
			sacrifice = new ItemStack[] { new ItemStack(Items.coal, 1, -1) };
			dist = 32;
			break;
		
		case 400:
			sacrifice = new ItemStack[] { new ItemStack(Items.coal, 2, -1) };
			dist = 48;
			break;
		
		default:
			entityRune.fizzle();
			return;
		}
		
		sacrifice = sacrifice(entityRune, sacrifice);
		
		if (!checkSacrifice(sacrifice)) {
			entityRune.fizzle();
			return;
		}
		
		int x = entityRune.getX();
		int y = entityRune.getY() - 1;
		int z = entityRune.getZ();
		World world = entityRune.worldObj;
		
		// ensure there's an hole in the middle
		if (!world.isAirBlock(x, y, z)) {
			entityRune.fizzle();
			return;
		}
		
		world.addWeatherEffect(new EntityLightningBolt(world, x, y, z));
		
		for (int dy = 0; dy <= dist; dy++) {
			Block block = world.getBlock(x, y - dy, z);
			
			if (entityRune.canBreakBlockAnd_AirOrLiquidOrNotReinforced(x, y - dy, z)) {
				block.onBlockDestroyedByPlayer(world, x, y - dy, z, world.getBlockMetadata(x, y - dy, z));
				block.dropBlockAsItem(world, x, y - dy, z, world.getBlockMetadata(x, y - dy, z), 0);
				world.setBlockToAir(x, y - dy, z);
			} else {// don't dig further
				break;
			}
		}
	}
	
	@Override
	public void onTick(EntityRune entityRune) {
		List<Entity> ents = this.getEntities(entityRune, 5D);
		
		for (Entity i : ents) {
			if (i instanceof EntityPlayer) {
				((EntityPlayer) i).extinguish();
			}
		}
		
		if (entityRune.ticksExisted > 5) {
			entityRune.kill();
		}
	}
}
