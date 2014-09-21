/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dustmod.runes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;
import cpw.mods.fml.common.registry.LanguageRegistry;
import dustmod.DustMod;
import dustmod.blocks.BlockDust;
import dustmod.blocks.TileEntityDust;
import dustmod.dusts.DustManager;

/**
 *
 * @author billythegoat101
 */
public class RuneManager
{
	
    public static HashMap<String, RuneEvent> events = new HashMap<String, RuneEvent>();
    public static List<String> names = new ArrayList<String>();
    public static List<String> namesRemote = new ArrayList<String>();

    public static ArrayList<RuneShape> shapes = new ArrayList<RuneShape>();
    public static ArrayList<RuneShape> shapesRemote = new ArrayList<RuneShape>();
    public static Configuration config;
    public RuneManager()
    {
    }



    public static int getNextPageNumber(){
    	return names.size();
    }
    
    public static List<String> getNames(){
    	if(DustMod.proxy.isClient()) return namesRemote;
        return names;
    }
    
    public static ArrayList<RuneShape> getShapes(){
    	if(DustMod.proxy.isClient()) return shapesRemote;
        return shapes;
    }
    public static HashMap<String, RuneEvent> getEvents(){
        return events;
    }
    
    public static EntityRune initiate(RuneShape shape, String name, double x, double y, double z, World world, List<Integer[]> points, int[][] map, UUID playerId,int rot)
    {
        RuneEvent evt = events.get(name);

        if (evt == null)
        {
            return null;
        }

        EntityRune result = new EntityRune(world,points);
        result.entityDustID = EntityRuneManager.getNextDustEntityID();
        EntityRuneManager.registerEntityDust(result, result.entityDustID);
        result.setPosition(x, y - 0.8, z);
//        result.posX = x;
//        result.posY = y-0.8;//EntityDust.yOffset;
//        result.posZ = z;
//        result.dustPoints = points;
        for(int i = 0; i < rot; i++){
        	map = RuneShape.rotateMatrixLeft(map);
        }
        result.dusts = map;
        result.runeWidth = map[0].length/4;
        result.runeLength = map.length/4;
        result.rot = rot;
        result.setSummonerId(playerId);

        for (Integer[] pos: points)
        {
            TileEntityDust ted = (TileEntityDust)world.getTileEntity(pos[0], pos[1], pos[2]);
            ted.setEntityDust(result);
        }

        result.dustID = -1;
        if (shape.solid)
        {
            boolean found = false;

            for (int i = 0; i < map.length && !found; i++)
            {
                for (int j = 0; j < map[0].length && !found; j++)
                {
                    int iter = map[i][j];

                    if (iter != 0)
                    {
                        result.dustID = iter;
                        found = true;
                    }
                }
            }
        }

        result.setEvent(evt, name);
        world.spawnEntityInWorld(result);
        
        
        if (evt.canPlayerKnowRune(playerId) && evt.permaAllowed)
        {
            evt.initGraphics(result);
            evt.init(result);
            result.updateDataWatcher();
        }
        else
        {
            EntityPlayer player = world.func_152378_a(playerId);
            result.reanimate = true;

            if (player != null)
            {
                player.addChatMessage(new ChatComponentText("This rune is disabled on this server."));
            }
        }

        result.dusts = null; //clearing so that i dont forget that it wont be saved and then try to access it in onTick()
        return result;
    }

    public static RuneEvent get(String name)
    {
        return events.get(name);
    }

    public static void add(String name, RuneEvent evt)
    {
        events.put(name, evt);
        names.add(name);
        evt.name = name;
//        System.out.println("Added event " + name +". Total:" + names.size() );
    }

    public static RuneShape getShape(int ind)
    {
    	if(DustMod.proxy.isClient()) return shapesRemote.get(ind);
        return shapes.get(ind);
    }
    public static RuneShape getShapeFromID(int id)
    {
    	ArrayList<RuneShape> s = (DustMod.proxy.isClient())? shapesRemote:shapes;
    	
        for (RuneShape i: s)
        {
            if (i.id == id)
            {
                return i;
            }
        }

        return null;
    }


    /**
     * Called upon joining a server to clear any events that were synced and 
     * registered from the last server.
     * 
     */
    public static void resetMultiplayerRunes(){
		DustMod.logger.debug("Reseting remote runes.");
//		System.out.println("[DustMod] Resetting remote runes.");
        namesRemote = new ArrayList<String>();
        shapesRemote = new ArrayList<RuneShape>();
        DustMod.proxy.resetPlayerTomePage();
    }
    
    
    /**
     * Register a new DustShape into the local system. The Lexicon image will be 
     * generated automatically if missing.
     * 
     * @param shape The DustShape object that stores all the shape information
     * @param eventInstance An instance of the event to call when the rune shape is made.
     */
    public static void registerLocalDustShape(RuneShape shape, RuneEvent eventInstance){
    	for(RuneShape i:shapes){
    		if(i.id == shape.id){
    			throw new IllegalArgumentException("[DustMod] Rune ID [" + shape.id + "] already occupied. " + i + " and " + shape);
    		}
    	}
    	
        add(shape.name, eventInstance);
        shapes.add(shape);
        DustMod.proxy.checkRunePage(shape);
        if(eventInstance != null && eventInstance instanceof PoweredEvent){
            shape.isPower = true;
        }

		DustMod.logger.debug("Registering rune: {}", shape.name);
//		System.out.println("[DustMod] Registering rune " + shape.name);
        
        if(config == null){
            config = new Configuration(DustMod.suggestedConfig);
            config.load();
            config.addCustomCategoryComment("INSCRIPTIONS", "Allow specific inscriptions to be used. Options: ALL, NONE, OPS");
            config.addCustomCategoryComment("RUNES", "Allow specific runes to be used. Options: ALL, NONE, OPS");
        }
            if (!eventInstance.secret)
            {
            	String permission = config.get( "RUNES", "Allow-" + shape.getRuneName().replace(' ', '_'),eventInstance.permission).getString().toUpperCase();

            	if(permission.equals("TRUE")) permission = "ALL"; //For backwards-compatibilities sake. Permission used to be a boolean
            	else if(permission.equals("FALSE")) permission = "OPS";
            	
            	if(permission.equals("ALL") || permission.equals("NONE") || permission.equals("OPS")){
            		eventInstance.permission = permission;
            	}else
            		eventInstance.permission = "NONE";

        		if(!eventInstance.permission.equals("ALL")){
        			DustMod.logger.debug("Rune permission for " + eventInstance.name + " set to " + eventInstance.permission);
//        			System.out.println("[DustMod] Rune permission for " + eventInstance.name + " set to " + eventInstance.permission);
        		}
            }

        config.save();
        
        LanguageRegistry.instance().addStringLocalization("tile.scroll" + shape.name + ".name", "en_US", shape.getRuneName() + " Placing Scroll");
        DustManager.reloadLanguage();
    }
    

    
    /**
     * Registers a new DustShape into the local system reserved for SMP. This is 
     * called by the packet from the server so that the available events are synced.
     * 
     * @param shape the DustShape to register.
     */
    public static void registerRemoteDustShape(RuneShape shape){
    	
        shapesRemote.add(shape);
        namesRemote.add(shape.name);
        DustMod.proxy.checkRunePage(shape);
		DustMod.logger.debug("Registering remote rune: " + shape.name);
//        System.out.println("[DustMod] Registering remote rune " + shape.name);
        LanguageRegistry.instance().addStringLocalization("tile.scroll" + shape.name + ".name", "en_US", shape.getRuneName() + " Placing Scroll");
        DustMod.logger.info("Add Loc {} for {}", shape.getRuneName() + " Placing Scroll", "tile.scroll" + shape.name + ".name");
        DustManager.reloadLanguage();
    }

	/**
	 * Called when a rune is activated. This checks to see if the rune shape is
	 * valid and then calls DustManager.initiate
	 * 
	 * @param i
	 *            The center x position of the rune
	 * @param j
	 *            The center y position of the rune
	 * @param k
	 *            The center Z position of the rune
	 * @param map
	 *            The untrimmed(leading and trailing empty space included) map
	 *            containing the full rune shape created when combining all the
	 *            shapes of adjacent TileEntityDusts
	 * @param points
	 *            The list of all BlockDust blocks that contributed to the rune.
	 * @param playerId
	 *            The Id of the GameProfile of the player who called the rune. Null if called
	 *            by redstone.
	 */
	public static void callShape(World world, double i, double j, double k,
			int[][] map, List<Integer[]> points, UUID playerId) {
		RuneShape found = null;
		// trim shape
		ArrayList<ArrayList<Integer>> temp = new ArrayList<ArrayList<Integer>>();
		int sx = map.length;
		int sz = map[0].length;
		int mx = 0;
		int mz = 0;
		boolean fnd = false;

		for (int x = 0; x < map.length; x++) {
			for (int z = 0; z < map[0].length; z++) {
				if ((x < sx || z < sz) && map[x][z] != 0) {
					if (x < sx) {
						sx = x;
					}

					if (z < sz) {
						sz = z;
					}

					fnd = true;
				}

				if (map[x][z] != 0) {
					if (x > mx) {
						mx = x;
					}

					if (z > mz) {
						mz = z;
					}
				}
			}
		}

		int dx = Math.abs(mx - sx) + 1;
		int dz = Math.abs(mz - sz) + 1;

		if (dx < 4) {
			sx = 0;
			mx = 3;
			dx = 4;
		}

		if (dz < 4) {
			sz = 0;
			mz = 3;
			dz = 4;
		}

		int[][] trim = new int[dx][dz];

		for (int x = sx; x <= mx; x++) {
			for (int z = sz; z <= mz; z++) {
				trim[x - sx][z - sz] = 0;
				trim[x - sx][z - sz] = map[x][z];
			}
		}

		for (int[] a : trim) {
			for (int b : a) {
				if (b == -2) {

					for (Integer[] p : points) {
						Block block = world.getBlock(p[0], p[1], p[2]);

						if (block == DustMod.dust) {
							world.setBlockMetadataWithNotify(p[0], p[1], p[2],
									BlockDust.DEAD_DUST,3);
						}
					}

					DustMod.logger.debug("Left variable dust in rune.");
					return;
				}
			}
		}
		int rot = 0;
		
		DustMod.logger.info("Trim: {}", Arrays.deepToString(trim));

		for (int iter = 0; iter < RuneManager.shapes.size(); iter++) {
			RuneShape s = RuneManager.shapes.get(iter);

			if (s.dataMatches(trim)) {
				found = s;
				break;
			}
		}

		if (found != null) {
			DustMod.logger.info("Found rune: {}", found.name);
			RuneManager.initiate(found, found.name, i, j, k, world, points,
					trim, playerId, rot);
		} else {

			for (Integer[] p : points) {
				Block block = world.getBlock(p[0], p[1], p[2]);

				if (block == DustMod.dust) {
					world.setBlockMetadataWithNotify(p[0], p[1], p[2], BlockDust.DEAD_DUST,3);
				}
			}

			DustMod.logger.info("No rune found.");
		}
	}
    

	public static void registerDefaultShapes() {
		//Moved! to DustModDefaults
	}



	public static boolean isEmpty() {
		return shapesRemote.isEmpty();
	}
	
}