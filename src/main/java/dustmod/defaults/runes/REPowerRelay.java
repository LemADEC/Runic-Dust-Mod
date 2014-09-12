/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dustmod.defaults.runes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import dustmod.DustMod;
import dustmod.blocks.TileEntityDust;
import dustmod.runes.EntityRune;
import dustmod.runes.PoweredEvent;

/**
 *
 * @author billythegoat101
 */
public class REPowerRelay extends PoweredEvent
{
    public static int distance = 32;

    public REPowerRelay()
    {
        super();
        networks = new HashMap<EntityRune, List<EntityRune>>();
    }
	
	@Override
    public void initGraphics(EntityRune e){
    	super.initGraphics(e);
    	
		
    }

    @Override
    public void onInit(EntityRune e)
    {
        super.onInit(e);
        ItemStack[] req = new ItemStack[] {new ItemStack(Items.iron_ingot, 3, -1)};
        req = this.sacrifice(e, req);

        if (!checkSacrifice(req) || !takeXP(e, 15))
        {
            e.fizzle();
            return;
        }

        networks.put(e, new ArrayList<EntityRune>());
    }

    @Override
    public void onTick(EntityRune e)
    {
        super.onTick(e);
//        System.out.println("POWAH RELAY: " + e.getFuel());
        double powerPercent = (double)e.getFuel() / (double)this.getStableFuelAmount(e);
        int c = (int)(255D * powerPercent);

        if (c > 255)
        {
            c = 255;
        }

        e.setColorStar(255, c, c);

        if (e.ticksExisted % 10 == 0)
        {
            disperseFuel(e);
        }
//        drawConnections(e);
    }

    @Override
    public void onRightClick(EntityRune e, TileEntityDust ted, EntityPlayer p)
    {
        super.onRightClick(e, ted, p);
    }

    @Override
    public void onUnload(EntityRune e)
    {
        super.onUnload(e);
        networks.remove(e);
        List<EntityRune> ents = this.findDustEntities(e);

        if (ents == null)
        {
            return;
        }

        List<EntityRune> powEnts = new ArrayList<EntityRune>();
        int needPower = 0;
        int totalPowerRequest = 0;

        for (EntityRune i: ents)
        {
//            if(i.fueledExternally){
            i.fueledExternally = false;
//            }
        }
    }

    @Override
    public void subtractFuel(EntityRune e)
    {
//        super.subtractFuel(e);
    }

    @Override
    public void addFuel(EntityRune e, int amt)
    {
        super.addFuel(e, amt);
        disperseFuel(e);
    }

    public void disperseFuel(EntityRune e)
    {
        List<EntityRune> ents = this.findDustEntities(e);
        int needPower = 0;
        int totalPowerRequest = 0;
        e.data[0] = 0;

        for (EntityRune i: ents)
        {
            if (i.requiresFuel && i.event != this && i.event != null)
            {
                needPower++;
                int want = ((PoweredEvent)i.event).powerWanted(i);
                totalPowerRequest += want;
//                e.data[0] += ((PoweredEvent)i.event).getStableFuelAmount(i);
                registerSelfTo(e, i);
            }
            else if (i.event == this && i != e)
            {
//                if(i.getFuel() > e.getFuel()){
                int split = (i.getFuel() + e.getFuel()) / 2;
                i.setFuel(split);
                e.setFuel(split);
//                }
            }
        }

        checkNetwork(e);
        List<EntityRune> network = networks.get(e);

        if (network == null)
        {
            network = new ArrayList<EntityRune>();
            networks.put(e, network);
        }

        EntityRune[] arr = new EntityRune[network.size()];
        network.toArray(arr);
//        System.out.println("Amount need power: " + needPower + " " + ents.size());
        boolean hasEnough = e.getFuel() >= totalPowerRequest;
        int fuel = (hasEnough) ? totalPowerRequest : e.getFuel();

        for (EntityRune i: arr)
        {
            if (i.isDead || e.getDistanceSqToEntity(i) > distance * distance)
            {
                removeSelfFrom(e, i);
                i.fueledExternally = false;
                continue;
            }

//            System.out.println("DERP " + i.event.getClass());
            i.fueledExternally = true;
            PoweredEvent event = (PoweredEvent)i.event;
            e.data[0] += event.getStableFuelAmount(i);
            int want = event.powerWanted(i);

            if (want > 50)
            {
                want = 50;
            }

            if (hasEnough)
            {
                int prev = i.getFuel();
                i.setFuel(i.getFuel() + want);
                e.setFuel(e.getFuel() - want);
//                System.out.println("Giving fuel " + i.getFuel() + " " + prev + " " + want + " " + e.getFuel());
            }
            else
            {
//                System.out.println("Giving fuel");
                double percent = (double)want / (double)totalPowerRequest;
                int amt = (int)(percent * (double)fuel);
                i.setFuel(i.getFuel() + amt);
                e.setFuel(e.getFuel() - amt);
            }
        }
    }

    public static List<EntityRune> findDustEntities(EntityRune e)
    {
        int x = e.getX();
        int y = e.getY();
        int z = e.getZ();
        int radius = 32;
        List<EntityRune> rtn =
                e.worldObj.getEntitiesWithinAABB(e.getClass(),
                        AxisAlignedBB.getBoundingBox(
                                x, y, z, x + 1.0D, y + 1.0D, z + 1.0D)
                        .expand(radius, radius, radius));
        return rtn;
    }

    @Override
    public int getStartFuel()
    {
        return 0;
    }

    @Override
    public int getMaxFuel()
    {
        return Integer.MAX_VALUE;
    }

    @Override
    public int getStableFuelAmount(EntityRune e)
    {
        return e.data[0];
    }

    @Override
    public boolean isPaused(EntityRune e)
    {
        return true;
    }

    public void registerSelfTo(EntityRune parent, EntityRune node)
    {
        checkNetwork(parent);
        List<EntityRune> network = networks.get(parent);

        if (!network.contains(node))
        {
            network.add(node);
        }
    }
    public void removeSelfFrom(EntityRune parent, EntityRune node)
    {
        checkNetwork(parent);
        List<EntityRune> network = networks.get(parent);

        if (network.contains(node))
        {
            network.remove(node);
        }
    }
    private void checkNetwork(EntityRune e)
    {
        if (!networks.containsKey(e))
        {
            networks.put(e, new ArrayList<EntityRune>());
        }
    }

    public HashMap<EntityRune, List<EntityRune>> networks;
    
    
    public void drawConnections(EntityRune e){
        checkNetwork(e);
        List<EntityRune> network = networks.get(e);

        if (network == null)
        {
            network = new ArrayList<EntityRune>();
            networks.put(e, network);
        }
        EntityRune[] arr = new EntityRune[network.size()];
        network.toArray(arr);
        
        for(EntityRune i:arr){
            drawConnection(e.worldObj, new double[]{e.posX-0.5, e.posY, e.posZ-0.5}, new double[]{i.posX-1.5, i.posY, i.posZ-0.5});
        }
    }
    
    
    public void drawConnection(World world, double[] loc1, double[] loc2){
    	double x,y,a,b,c,d,h;
    	double scale = 4;
    	h = (loc2[0] - loc1[0])/8; //max height
    	c= loc1[1]; //initial height
    	
    	d = (loc1[0]-loc2[0])*(loc1[0]-loc2[0]) + (loc1[1]-loc2[1])*(loc1[1]-loc2[1]) + (loc1[2]-loc2[2])*(loc1[2]-loc2[2]);

    	d = Math.sqrt(d);
    	a = (-4*h+4*c)/d;
    	b = (-4*h+4*c)/Math.sqrt(d);
    	
    	a = (4*c-4*h)/((loc2[0]-loc1[0])*(loc2[0]-loc1[0]));
    	b = (4*h-4*c)/(loc2[0]-loc1[0]);
    	
//    	ArrayList<Double> particles = new ArrayList<Double>();
    	int iter = 0;
    	double[] locations = new double[((int)Math.abs((loc2[0]*2 - loc1[0])/0.5)+1)*3+1];
    	
    	
    	double minx = Math.min(loc1[0], loc2[0]);
    	double maxx = Math.max(loc1[0], loc2[0]);
    	for(x=minx;x < maxx; x+=0.5){
//    		y = a*x*x + b*x + c;
    		double perc = (x - minx)/(maxx*2-minx);
    		double z = loc1[2] + (loc2[2]-loc1[2])*perc;
    		y= -(x*x)/4 + h;
    		y = -Math.pow((x-((loc2[0]-loc1[0])/2)),2)/(d/h/3.9) + c + h;
    		
    		y = -Math.pow((x-((loc2[0]-loc1[0])/2)),2)/(d*2) + c + h;
//    		System.out.println("Penis " + x + " " + y);
//    		System.out.printf("Penis {0} {1}", x,y);
//    		DustMod.spawnParticles(world, "reddust", x, y, 0.5, 1, 0, 0, 5, 0.01);
    		locations[iter] = x;
    		locations[iter+1] = y;
    		locations[iter+2] = z;
    		iter += 3;
//    		particles.add(x);
//    		particles.add(y);
//    		particles.add(0.5);
    	}
//    	System.out.println("You're a dick " + iter + " " + locations.length);
    	
//		DustMod.spawnParticles(world, "reddust", x, y, 0.5, 1, 0, 0, 5, 0.01);
//    	Double[] locations = new Double[particles.size()];
//    	locations = particles.toArray(locations);
		DustMod.spawnParticles(world, "reddust", locations, 0, 1, 0, 2, 0.01, 0.01, 0.01);
    }
}
