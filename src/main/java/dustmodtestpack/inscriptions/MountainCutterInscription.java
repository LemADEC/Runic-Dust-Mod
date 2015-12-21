package dustmodtestpack.inscriptions;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import dustmod.DustMod;
import dustmod.inscriptions.InscriptionEvent;
import dustmod.runes.RuneEvent;
import dustmod.runes.EntityRune;

public class MountainCutterInscription extends InscriptionEvent {

	public MountainCutterInscription(int[][] design, String idName,
			String properName, int id) {
		super(design, idName, properName, id);
		this.setAuthor("billythegoat101");
		this.setDescription("Description\n" +
				"Chop a hole through a freaking mountain! Hiyah!\n" +
				"Design is still a WIP");
		this.setNotes("Sacrifice\n" +
				"To be defined");
	}
	
	@Override
	public boolean callSacrifice(RuneEvent rune, EntityRune e, ItemStack item) {
		ItemStack[] req = rune.sacrifice(e, new ItemStack[] {new ItemStack(Blocks.iron_block, 8, -1)});

        if (req[0].stackSize != 0)
        {
            e.fizzle();
            return false;
        }
        
        item.setItemDamage(1);
        
		return true;
	}
	
	public void onUpdate(EntityLivingBase wearer, ItemStack item, boolean[] buttons){
//		wearer.setPositionAndUpdate(wearer.posX,  wearer.posY+2, wearer.posZ);
		if(wearer.isSneaking() && buttons[0]){
			chopChop((EntityPlayer)wearer);
			damage((EntityPlayer)wearer, item, 10);
		}
	}
	


	public static synchronized void chopChop(EntityPlayer ep) {

//		System.out.println("DERP");
//		if(ep.worldObj.getWorldTime() %3 == 0)
		if (ep.getCurrentEquippedItem() == null && ep.isSneaking()) {
			try {
				MovingObjectPosition click = DustMod.getWornInscription().getMovingObjectPositionFromPlayer(ep.worldObj, ep, true);
				
				if (click != null && click.typeOfHit == MovingObjectType.BLOCK) {

					int x, y, z;
					x = click.blockX;
					y = click.blockY;
					z = click.blockZ;
					int r = 1;
					for (int i = -r; i <= r; i++) {
						for (int j = -r; j <= r; j++) {
							for (int k = -r; k <= r; k++) {
								ep.worldObj.setBlockToAir(x + i, y + j, z + k);
							}
						}
					}
				}
//				
//				float ticks = 1F;
//				double distance = 64D;
//				// if(ep.ticksExisted %3 != 0) return;
//				Vec3 pos = Vec3.getVec3Pool().getVecFromPool(ep.posX, ep.posY,
//						ep.posZ);
//
//				pos.yCoord += ep.getEyeHeight();
//				Vec3 look = ep.getLook(ticks);
//				Vec3 result = pos.addVector(look.xCoord * distance, look.yCoord
//						* distance, look.zCoord * distance);
//
//				MovingObjectPosition click = ep.rayTrace(distance,ticks);
//
//				if (click != null) {
//					int x, y, z;
//					x = click.blockX;
//					y = click.blockY;
//					z = click.blockZ;
//					int r = 1;
//					for (int i = -r; i <= r; i++) {
//						for (int j = -r; j <= r; j++) {
//							for (int k = -r; k <= r; k++) {
//								ep.worldObj.setBlockWithNotify(x + i, y + j, z
//										+ k, 0);
//							}
//						}
//					}
//				}
			} catch (Exception e) {
			}
		}
	}
}
