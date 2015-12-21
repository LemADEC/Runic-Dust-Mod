package dustmodtestpack.runes;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import dustmod.runes.RuneEvent;
import dustmod.runes.EntityRune;

public class LaunchTestRune extends RuneEvent {
	
	@Override
	protected void onInit(EntityRune e) {
		// TODO Auto-generated method stub
		super.onInit(e);
	}
	
	@Override
	protected void initGraphics(EntityRune e) {
		// TODO Auto-generated method stub
		super.initGraphics(e);
	}
	
	@Override
	protected void onTick(EntityRune e) {
		super.onTick(e);
		
		List<Entity> ents = this.getEntities(e);
		if (e.ticksExisted % 3 == 0)
			for (Entity i : ents) {
				e.fallDistance = 0;
				if (i.onGround) {
					i.setPosition(e.posX, Math.floor(e.posY) + 0.5, e.posZ);
					launchToward(i, e.posX + 16, i.posY, 0);
					
				}
			}
	}
	
	//G = 1.56800003052
	
	public void launchToward(Entity entity, double xf, double yf, double zf) {
		double d;
		double vi;
		double g;
		double theta;
		
		double xi, yi, zi;
		xi = entity.posX;
		yi = entity.posY;
		zi = entity.posZ;
		
		g = 0.03999999910593033D;
		theta = 0.558505361;//3.14/3;
		d = Math.sqrt((xi - xf) * (xi - xf) + (yi - yf) * (yi - yf) + (zi - zf) * (zi - zf));
		
		double num = d * g;
		double denum = Math.sin(2 * theta);
		vi = Math.sqrt(num / denum);
		
		entity.addVelocity(-entity.motionX, -entity.motionY, -entity.motionZ);
		
		double horizVel = Math.cos(theta) * vi;
		double rotTheta = Math.atan(xf / zf);
		entity.addVelocity(Math.sin(rotTheta) * horizVel, Math.sin(theta) * vi, Math.cos(rotTheta) * horizVel);
		
		if (entity instanceof EntityPlayer) {
			EntityPlayer entityPlayer = (EntityPlayer) entity;
			entityPlayer.addMovementStat(Math.sin(rotTheta) * horizVel, Math.sin(theta) * vi, Math.cos(rotTheta) * horizVel);
		}
	}
}
