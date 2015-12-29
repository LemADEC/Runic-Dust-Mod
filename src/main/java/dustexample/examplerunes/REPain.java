/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dustexample.examplerunes;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import dustmod.blocks.TileEntityDust;
import dustmod.runes.EntityRune;
import dustmod.runes.PoweredEvent;

/**
 *
 * Rune of Pain
 * 
 * Good example of:
 * -Powered runes
 * -Pausing powered runes
 * -Basic AOE entity effects
 * -Checking for a rune's creator
 * -Right-clicking 
 * 
 * @author billythegoat101
 */
public class REPain extends PoweredEvent //This is a Powered rune
{
    
    public REPain()
    {
        super();
    }
	
	/**
	 * Called to set the graphical components of the rune
     * @param entityRune EntityDust instance
	 */
	@Override
    public void initGraphics(EntityRune entityRune){
    	super.initGraphics(entityRune);
    	
		//no graphics!
    	
    }
    
    @Override
    public void onInit(EntityRune entityRune)
    {
        super.onInit(entityRune);
        
        //These 4 points are the 4 variable dusts in the center of the rune.
        int a,b,c,d;
        a = entityRune.dusts[3][3];
        b = entityRune.dusts[3][4];
        c = entityRune.dusts[4][4];
        d = entityRune.dusts[4][3];
        
        //First we check to make sure they are all equal
        if(a == b && b == c && c == d){
            //Then we store the dust for future reference
            entityRune.data[0] = a;
        }else{
        //If not we end here
            entityRune.fizzle();
            return;
        }
    }
    
    @Override
    public void onTick(EntityRune entityRune)
    {
        super.onTick(entityRune);
        
        //Doing entity checks every single tick just seems like it would cause 
        //an unnecessary amount of lag, so I don't do it
        if(entityRune.ticksExisted % 10 == 0)
        {
            
            //Set stats based off of the dust level set earlier
            int damage = 0;
            double rad = 0;
            switch(entityRune.data[0]){
                case 100:
                    damage = 1;
                    rad = 8D;
                    break;
                case 200:
                    damage = 2;
                    rad = 10D;
                    break;
                case 300:
                    damage = 4;
                    rad = 12D;
                    break;
                case 350:
                	damage = 5;
                	rad = 14D;
                	break;
                case 400:
                    damage = 6;
                    rad = 16D;
                    break;
            }
            
            //Get all entities within radius
			List<Entity> entities = this.getEntitiesExcluding(entityRune, rad);
            for(Entity entity : entities){
                if(entity instanceof EntityPlayer){
                    //Check if it is the summoning player
                    EntityPlayer entityPlayer = (EntityPlayer) entity;
                    if(entityRune.isSummoner(entityPlayer)){
                        continue; //Skip this entity, don't hurt them
                    }
                }
                
                //hurt the entity
                entity.attackEntityFrom(DamageSource.magic, damage);
            }
        }
    }
    
    @Override
    public void onRightClick(EntityRune entityRune, TileEntityDust tileEntityDust, EntityPlayer entityPlayer)
    {
        super.onRightClick(entityRune, tileEntityDust, entityPlayer);
        //Used to toggle paused.
        if(entityRune.data[1] == 0)
            entityRune.data[1] = 1;
        else
            entityRune.data[1] = 0;
    }
    
    @Override
    public void onUnload(EntityRune e)
    {
        super.onUnload(e);
    }

    /**
     * Get how much fuel this rune should begin with upon creation.
     * dayLength is the amount of fuel that will last 1 minecraft day
     * @return The amount of fuel to begin with
     */
    @Override
    public int getStartFuel()
    {
        return dayLength;
    }

    /**
     * Get the maximum amount of fuel this rune can ever have. Any more sacrificed
     * will be wasted and not be stored.
     * @return The maximum amount of fuel
     */
    @Override
    public int getMaxFuel()
    {
        return dayLength * 3;
    }

    /**
     * Get the amount of fuel this rune should try to have if possible.
     * If over this amount, power relay runes will put this rune on low priority
     * under others that might still need power
     * @param e EntityDust instance
     * @return An adequate amount of fuel for this rune.
     */
    @Override
    public int getStableFuelAmount(EntityRune e)
    {
        return dayLength / 2;
    }

    /**
     * Return true if the rune should be paused and therefore not consuming fuel.
     * @param e EntityDust instance
     * @return True if paused, false otherwise
     */
    @Override
    public boolean isPaused(EntityRune e)
    {
        return e.data[1] == 1;
    }
}
