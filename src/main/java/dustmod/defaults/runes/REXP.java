/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dustmod.defaults.runes;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import dustmod.DustMod;
import dustmod.runes.EntityRune;
import dustmod.runes.PoweredEvent;

/**
 *
 * @author billythegoat101
 */
public class REXP extends PoweredEvent
{
    public REXP()
    {
        super();
    }
	
	@Override
    public void initGraphics(EntityRune e){
    	super.initGraphics(e);

		e.setRenderStar(true);
		e.setRenderBeam(true);
        e.setColorFire(0,255,0);
		e.setRenderFireOnRune(true);
        e.setColorBeam(255, 255, 255);
		
    }

    public void onInit(EntityRune e)
    {
        super.onInit(e);
		e.setRenderStar(true);
		e.setRenderBeam(true);
        e.setColorBeam(255, 255, 255);
        e.data[0] = 24000;
        e.posY += 1D;

        ItemStack[] req = new ItemStack[]{new ItemStack(Items.nether_star, 1)};
        req = this.sacrifice(e, req);
        if (!checkSacrifice(req))
        {
            e.fizzle();
            return;
        }
    }

    public void onTick(EntityRune e)
    {
        super.onTick(e);

        if (e.ticksExisted < 60)
        {
    		e.setRenderBeam(false);
            return;
        }
        else
        {
    		e.setRenderBeam(true);
        }

        EntityPlayer player = e.getSummoner();

        if (player == null)
        {
            return;
        }

        double percent = (double)e.getFuel() / 2400D;
        int col = (int)Math.floor(percent * 255);
        e.setColorBeam(255, col, col);
        int x = e.getX();
        int y = e.getY();
        int z = e.getZ();
        List<Entity> omnom = getEntitiesExcluding(e, 3D);

        for (Entity et: omnom)
        {
            if (et.posY <= e.getY() - 2 || et.posY >= e.getY() + 1.5D)
            {
                continue;
            }

            if (et instanceof EntityItem)
            {
                et.setDead();
                this.addFuel(e, ((EntityItem)et).getEntityItem().stackSize * 10);
            }

            if (et instanceof EntityLivingBase && et != player)
            {
                if (et.motionY >= 0)
                {
                    continue;
                }

                EntityLivingBase el = (EntityLivingBase)et;

                if (el.getHealth() <= 0)
                {
                    continue;
                }

                int exp = DustMod.entityLivingHelper.getExperiencePoints(el, player);
                DustMod.entityLivingHelper.setRecentlyHit(el, 0);
                el.attackEntityFrom(DustMod.destroyDrops, 10000000);

                for (int mul = 0; mul < 2; mul++)
                    for (int i = exp; i > 0;)
                    {
                        int k = EntityXPOrb.getXPSplit(i);
                        i -= k;
                        double tx = x + ((Math.random() > 0.5D) ? 1 : -1) + Math.random() * 0.4D - 0.2D;
                        double tz = z + ((Math.random() > 0.5D) ? 1 : -1) + Math.random() * 0.4D - 0.2D;
                        EntityXPOrb ex = new EntityXPOrb(e.worldObj, tx + 0.5D, y, tz + 0.5D, k);
                        ex.motionX = ex.motionY = ex.motionZ = 0;
                        e.worldObj.spawnEntityInWorld(ex);
                    }

                this.addFuel(e, 1000);
                et.setDead();
                break;
            }
            else if (et == player)
            {
                if (e.ticksExisted % 20 == 0 && e.ticksExisted != 0)
                {
                    ((EntityPlayer)player).attackEntityFrom(DamageSource.magic, 2);
                }
            }
        }
    }

    @Override
    public int getStartFuel()
    {
        return dayLength;
    }

    @Override
    public int getMaxFuel()
    {
        return dayLength * 7;
    }

    @Override
    public int getStableFuelAmount(EntityRune e)
    {
        return dayLength + dayLength / 2;
    }

    @Override
    public boolean isPaused(EntityRune e)
    {
        return false;
    }
}
