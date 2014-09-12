/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dustmod.defaults.runes;

import java.util.HashMap;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import dustmod.runes.EntityRune;
import dustmod.runes.PoweredEvent;

/**
 *
 * @author billythegoat101
 */
public class REBait extends PoweredEvent
{
    public REBait()
    {
        super();
    }

    public void initGraphics(EntityRune e){
    	super.initGraphics(e);

        e.setRenderStar(true);
        e.setStarScale(1.005F);
        e.setColorStarOuter(255, 1, 1);
    	
    	
    }
    
    public void onInit(EntityRune e)
    {
        super.onInit(e);
        int entClass = -1;
        List l = getEntities(e);

        for (Object o: l)
        {
            if (o instanceof EntityItem)
            {
                EntityItem ei = (EntityItem)o;
                ItemStack item = ei.getEntityItem();

                if (item.getItem() == Items.spawn_egg)
                {
                    entClass = item.getItemDamage();
                    item.stackSize--;

                    if (item.stackSize <= 0)
                    {
                        ei.setDead();
                    }
                }
                ei.setEntityItemStack(item);
            }
        }

        ItemStack[] req = new ItemStack[] {new ItemStack(Blocks.gold_block, 1)};
        req = this.sacrifice(e, req);

        if (!checkSacrifice(req) || entClass == -1 || !takeXP(e, 5))
        {
            e.fizzle();
            return;
        }

        e.data[0] = entClass;
        e.setStarScale(1.005F);
        e.setColorStarOuter(255, 1, 1);
    }

    public void onTick(EntityRune e)
    {
//        e.starScale += 0.001;
        super.onTick(e);
        List<Entity> bait = getEntities(e, 16D);

//        System.out.println("DATA " + e.data[0]);
        for (Entity k: bait)
        {
//                if(k instanceof EntityCreature) System.out.println("ENT " + EntityList.getEntityID(k));
            if (k instanceof EntityCreature && EntityList.getEntityID(k) == e.data[0])
            {
                EntityCreature el = (EntityCreature)k;

//                System.out.println("Found entity " + mod_DustMod.isAIEnabled(el));
//                if (!DustModBouncer.isAIEnabled(el))
//                {
//                    el.posY += 1;
//                    el.setPathToEntity(null);//e.worldObj.getPathToEntity(el, e, 16F));
//                    el.setTarget(e);
//                    el.setPathToEntity(e.worldObj.getEntityPathToXYZ(el, e.getX(), e.getY(), e.getZ(), 10F, true, false, false, true));
//                    DustModBouncer.updateState(el);
//                    el.motionY += 0.015;
//                    EntityLookHelper elh = el.getLookHelper();//func_46008_aG();
//                    elh.setLookPositionWithEntity(e, 0, 1);//func_46141_a(e, 1, 1);
//                    el.setMoveForward(16F);
//                    el.velocityChanged = true;
//                    DustModBouncer.setEntityToAttack(el, e);
//                    el.setHomeArea(e.getX(), e.getY(), e.getZ(), 0);
//                    DustModBouncer.setEntityToAttack(el, e);
//                    
//
//                    if(Math.random() < 0.2){
//                    	DustMod.spawnParticles(el.worldObj, "smoke", el.posX, el.posY+el.height/2, el.posZ,
//                    			0, Math.random() * 0.05, 0, (int)(Math.random()*20), 0.75, el.height/2, 0.75);
//                    }
//                    
//                }
//                else
//                {

                EntityAITasks tasks = el.tasks;
                List taskList = tasks.taskEntries;
                boolean hasTaskAlready = false;
                for(Object o:taskList){
                	EntityAIBase task = (EntityAIBase)o;
                	if(task.getClass() == EntityAIRuneFollowBaitRune.class){
                		hasTaskAlready = true;
                		break;
                	}
                }
                if(!hasTaskAlready){
                	tasks.addTask(-1, new EntityAIRuneFollowBaitRune(el, 0.22F));
                }
            }
        }
    }

    public static int getEntity(ItemStack is)
    {
//        System.out.println("CHECK " + is.itemID + " " + is.stackSize + " " + is.getItemDamage());
        for (ItemStack i: entdrops.keySet())
        {
//            System.out.println("grr " + i.itemID + " " + i.stackSize + " " + i.getItemDamage());
            if (i.getItem() == is.getItem() && i.getItemDamage() == is.getItemDamage())
            {
//                System.out.println("ent found");
                return entdrops.get(i);
            }
        }

//        System.out.println("ent not found");
        return -1;
    }

    public static HashMap<ItemStack, Integer> entdrops = new HashMap<ItemStack, Integer>();

    static
    {
        entdrops.put(new ItemStack(Items.porkchop, 0, 0), 90);
        entdrops.put(new ItemStack(Items.beef, 0, 0), 92);
        entdrops.put(new ItemStack(Items.chicken, 0, 0), 93);
        entdrops.put(new ItemStack(Items.dye, 0, 0), 94);
        entdrops.put(new ItemStack(Items.leather, 0, 0), 95);
        entdrops.put(new ItemStack(Blocks.red_mushroom, 0, 0), 96);
        entdrops.put(new ItemStack(Blocks.pumpkin, 0, 0), 97);
        entdrops.put(new ItemStack(Items.cooked_porkchop, 0, 0), 57);
        entdrops.put(new ItemStack(Items.gunpowder, 0, 0), 50);
        entdrops.put(new ItemStack(Items.bone, 0, 0), 51);
        entdrops.put(new ItemStack(Items.string, 0, 0), 52);
        entdrops.put(new ItemStack(Items.rotten_flesh, 0, 0), 54);
        entdrops.put(new ItemStack(Items.slime_ball, 0, 0), 55);
        entdrops.put(new ItemStack(Items.ghast_tear, 0, 0), 56);
        entdrops.put(new ItemStack(Items.ender_pearl, 0, 0), 58);
        entdrops.put(new ItemStack(Items.spider_eye, 0, 0), 59);
        entdrops.put(new ItemStack(Blocks.stonebrick, 0, 0), 60);
        entdrops.put(new ItemStack(Items.blaze_rod, 0, 0), 61);
        entdrops.put(new ItemStack(Items.magma_cream, 0, 0), 62);
        //entdrops.put(new ItemStack(Items.ender_eye, 0, 0), 63);
    }

    @Override
    public int getStartFuel()
    {
        return dayLength * 7;
    }

    @Override
    public int getMaxFuel()
    {
        return dayLength * 7;
    }

    @Override
    public int getStableFuelAmount(EntityRune e)
    {
        return dayLength * 5;
    }

    @Override
    public boolean isPaused(EntityRune e)
    {
        return false;
    }
}
