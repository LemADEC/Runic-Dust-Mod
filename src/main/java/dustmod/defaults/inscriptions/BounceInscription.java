package dustmod.defaults.inscriptions;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import dustmod.DustMod;
import dustmod.inscriptions.InscriptionEvent;
import dustmod.runes.RuneEvent;
import dustmod.runes.EntityRune;

public class BounceInscription extends InscriptionEvent {
	
	public BounceInscription(int[][] design, String idName, String properName, int id) {
		super(design, idName, properName, id);
		this.setAuthor("billythegoat101");
		this.setDescription("Description\n" + "Greatly reduce your fall damage by automatically bouncing back up upon landing.\n" + "Cancel the effect by crouching.");
		this.setNotes("Sacrifice\n" + "- 8x Feathers + 1x Leather Boots");
	}
	
	@Override
	public boolean callSacrifice(RuneEvent rune, EntityRune entityRune, ItemStack itemStack) {
		ItemStack[] req = new ItemStack[] { new ItemStack(Items.slime_ball, 8), new ItemStack(Items.leather_boots, 1) };
		req = rune.sacrifice(entityRune, req);
		if (!rune.checkSacrifice(req))
			return false;
		itemStack.setItemDamage(0);
		return true;
	}
	
	@Override
	public void onUpdate(EntityLivingBase wearer, ItemStack itemStack, boolean[] buttons) {
		super.onUpdate(wearer, itemStack, buttons);
		if (!wearer.onGround) {
			if (getLastYVel(itemStack) > wearer.motionY)
				setFalling(itemStack, true, getFallDist(itemStack) + wearer.fallDistance, (float) wearer.motionX, (float) wearer.motionY, (float) wearer.motionZ);
			wearer.fallDistance = 0;
		} else if (wearer.isSneaking()) {
			wearer.fallDistance = getFallDist(itemStack);
			setFalling(itemStack, false, 0, 0, 0, 0);
		} else if (wasFalling(itemStack) && getLastYVel(itemStack) < -0.75f) {
			wearer.fallDistance = getFallDist(itemStack) / 2f;
			wearer.motionX = -getLastXVel(itemStack) * 0.76D;
			wearer.motionY = -getLastYVel(itemStack) * 0.76D;
			wearer.motionZ = -getLastZVel(itemStack) * 0.76D;
			DustMod.sendEntMotionTraits(wearer);
			setFalling(itemStack, false, 0, 0, 0, 0);
			
			this.damage((EntityPlayer) wearer, itemStack, 8);
		}
	}
	
	private void setFalling(ItemStack item, boolean val, float dist, float xVel, float yVel, float zVel) {
		item.getTagCompound().setBoolean("falling", val);
		item.getTagCompound().setFloat("fallDist", dist);
		item.getTagCompound().setFloat("xVel", xVel);
		item.getTagCompound().setFloat("yVel", yVel);
		item.getTagCompound().setFloat("zVel", zVel);
	}
	
	private boolean wasFalling(ItemStack item) {
		if (item.getTagCompound().hasKey("falling"))
			return item.getTagCompound().getBoolean("falling");
		else {
			item.getTagCompound().setBoolean("falling", false);
			return false;
		}
	}
	
	private float getFallDist(ItemStack item) {
		if (item.getTagCompound().hasKey("fallDist"))
			return item.getTagCompound().getFloat("fallDist");
		else {
			item.getTagCompound().setFloat("fallDist", 0f);
			return 0f;
		}
	}
	
	private float getLastXVel(ItemStack item) {
		if (item.getTagCompound().hasKey("xVel"))
			return item.getTagCompound().getFloat("xVel");
		else {
			item.getTagCompound().setFloat("xVel", 0);
			return 0;
		}
	}
	
	private float getLastYVel(ItemStack item) {
		if (item.getTagCompound().hasKey("yVel"))
			return item.getTagCompound().getFloat("yVel");
		else {
			item.getTagCompound().setFloat("yVel", 0);
			return 0;
		}
	}
	
	private float getLastZVel(ItemStack item) {
		if (item.getTagCompound().hasKey("zVel"))
			return item.getTagCompound().getFloat("zVel");
		else {
			item.getTagCompound().setFloat("zVel", 0);
			return 0;
		}
	}
	
}
