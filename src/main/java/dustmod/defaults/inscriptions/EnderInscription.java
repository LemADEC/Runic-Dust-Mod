package dustmod.defaults.inscriptions;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.util.Vec3;
import dustmod.DustMod;
import dustmod.inscriptions.InscriptionEvent;
import dustmod.runes.RuneEvent;
import dustmod.runes.EntityRune;

public class EnderInscription extends InscriptionEvent {
	public EnderInscription(int[][] design, String idName, String properName, int id) {
		super(design, idName, properName, id);
		this.setAuthor("billythegoat101");
		this.setDescription("Description\n"
				+ "Blink like an enderman! Shift + RightClick with a bare hand to teleport randomly in that direction. Safety not guaranteed. It will cost 1 heart per blink but you will not take fall damage.");
		this.setNotes("Sacrifice\n" + "- 8x Ender Pearl + 1x Quartz Block + 2x Blaze Rod + 10 XP levels");
	}
	
	@Override
	public boolean callSacrifice(RuneEvent rune, EntityRune e, ItemStack item) {
		ItemStack[] req = new ItemStack[0];
		int xp = 10;
		req = new ItemStack[] { new ItemStack(Items.ender_pearl, 8), new ItemStack(Blocks.quartz_block, 1), new ItemStack(Items.blaze_rod, 2) };
		
		req = rune.sacrifice(e, req);
		if (!rune.checkSacrifice(req))
			return false;
		if (!rune.takeXP(e, xp))
			;
		item.setItemDamage(0);
		return true;
	}
	
	@Override
	public void onUpdate(EntityLivingBase wearer, ItemStack item, boolean[] buttons) {
		super.onUpdate(wearer, item, buttons);
		
		if (wasFalling(item) && wearer.ticksExisted - item.getTagCompound().getInteger("lastTele") > 20) {
			if (wearer.onGround) {
				setFalling(item, false);
			}
		}
		if (!wearer.onGround && wasFalling(item)) {
			wearer.fallDistance = 5F;
		}
		
		if (((EntityPlayer) wearer).getCurrentEquippedItem() == null && wearer.isSneaking() && buttons[0] && !getLastMouse(item)) {
			boolean canTele = canTele(item, wearer);
			double[] testLoc = new double[3];
			
			Vec3 look = wearer.getLookVec();
			look = Vec3.createVectorHelper(look.xCoord + Math.random() * 2 - 1, look.yCoord, look.zCoord + Math.random() * 2 - 1);
			double dist = Math.random() * 9 + 9D;
			
			testLoc[0] = wearer.posX + dist * look.xCoord;
			testLoc[1] = wearer.posY + dist * look.yCoord + wearer.getEyeHeight();
			testLoc[2] = wearer.posZ + dist * look.zCoord;
			
			int[] target = getClickedBlock(wearer, item);
			double x = wearer.posX;
			double y = wearer.posY;
			double z = wearer.posZ;
			
			if (target != null) {
				double distT = (target[0] - x) * (target[0] - x) + (target[1] - y) * (target[1] - y) + (target[2] - z) * (target[2] - z);
				//				double distL = (testLoc[0] - x)*(testLoc[0]-x) + (testLoc[1]-y)*(testLoc[1] - y) + (testLoc[2]-z)*(testLoc[2]-z);
				if (distT < dist * dist) {
					dist = Math.sqrt(distT) - 1;
					testLoc[0] = wearer.posX + dist * look.xCoord;
					testLoc[1] = wearer.posY + dist * look.yCoord + wearer.getEyeHeight();
					testLoc[2] = wearer.posZ + dist * look.zCoord;
				}
			}
			
			double newY = testLoc[1];
			if (!wearer.worldObj.isAirBlock((int) testLoc[0], (int) testLoc[1], (int) testLoc[2])) {
				for (int i = 0; i < 3; i++) {
					if (wearer.worldObj.isAirBlock((int) testLoc[0], (int) Math.floor(testLoc[1]) + i, (int) testLoc[2])) {
						newY = Math.floor(testLoc[1]) + i;
						break;
					}
				}
			} else {
				for (int i = 0; i < 64; i++) {
					if (!wearer.worldObj.isAirBlock((int) testLoc[0], (int) Math.floor(testLoc[1]) - i, (int) testLoc[2])) {
						newY = Math.floor(testLoc[1]) - i + 1;
						break;
					}
				}
			}
			
			canTele &= dist > 2.5;
			if (canTele) {
				onTele(item, wearer);
				//Play at both before and after
				wearer.worldObj.playSoundEffect(wearer.posX, wearer.posY, wearer.posZ, "mob.endermen.portal", 1.0F, 1.0F);
				wearer.playSound("mob.endermen.portal", 1.0F, 1.0F);
				
				wearer.setPositionAndUpdate(testLoc[0], (testLoc[1] > newY ? testLoc[1] : newY), testLoc[2]);
				wearer.fallDistance = 5.0F;
				
				wearer.worldObj.playSoundEffect(wearer.posX, wearer.posY, wearer.posZ, "mob.endermen.portal", 1.0F, 1.0F);
				wearer.playSound("mob.endermen.portal", 1.0F, 1.0F);
				
				this.damage((EntityPlayer) wearer, item, 20);
				setFalling(item, true);
			}
			recordMouseClick(item, buttons[0]);
		} else if (!buttons[0]) {
			recordMouseClick(item, buttons[0]);
		}
	}
	
	private void recordMouseClick(ItemStack item, boolean mouse) {
		item.getTagCompound().setBoolean("mouse", mouse);
	}
	
	private boolean getLastMouse(ItemStack item) {
		if (item.getTagCompound().hasKey("mouse"))
			return item.getTagCompound().getBoolean("mouse");
		else {
			item.getTagCompound().setBoolean("mouse", false);
			return false;
		}
	}
	
	private void onTele(ItemStack item, EntityLivingBase wearer) {
		item.getTagCompound().setInteger("lastTele", wearer.ticksExisted);
	}
	
	private boolean canTele(ItemStack item, EntityLivingBase wearer) {
		int last = 0;
		if (item.getTagCompound().hasKey("mouse"))
			last = item.getTagCompound().getInteger("lastTele");
		else {
			item.getTagCompound().setInteger("lastTele", wearer.ticksExisted);
			last = wearer.ticksExisted;
		}
		
		if (wearer.ticksExisted - last > 10) {
			return true;
		} else {
			if (wearer.ticksExisted < last) {
				onTele(item, wearer);
				return true;
			}
			return false;
		}
		
	}
	
	private void setFalling(ItemStack item, boolean val) {
		item.getTagCompound().setBoolean("falling", val);
	}
	
	private boolean wasFalling(ItemStack item) {
		if (item.getTagCompound().hasKey("falling"))
			return item.getTagCompound().getBoolean("falling");
		else {
			item.getTagCompound().setBoolean("falling", false);
			return false;
		}
	}
	
	public int[] getClickedBlock(Entity wearer, ItemStack item) {
		MovingObjectPosition click = DustMod.getWornInscription().getMovingObjectPositionFromPlayer(wearer.worldObj, (EntityPlayer) wearer, false);
		if (click != null && click.typeOfHit == MovingObjectType.BLOCK) {
			int tx = click.blockX;
			int ty = click.blockY;
			int tz = click.blockZ;
			return new int[] { tx, ty, tz };
		}
		return null;
	}
	
}
