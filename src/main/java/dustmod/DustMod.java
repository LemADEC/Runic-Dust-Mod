package dustmod;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.world.WorldEvent;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.relauncher.Side;
import dustmod.blocks.BlockDust;
import dustmod.blocks.BlockDustTable;
import dustmod.blocks.BlockRut;
import dustmod.blocks.TileEntityDust;
import dustmod.blocks.TileEntityDustTable;
import dustmod.blocks.TileEntityRut;
import dustmod.common.CommonMouseHandler;
import dustmod.common.CommonProxy;
import dustmod.common.GuiHandler;
import dustmod.defaults.runes.VoidStorageManager;
import dustmod.defaults.runes.VoidTeleManager;
import dustmod.dusts.DustItemManager;
import dustmod.entities.EntityBlock;
import dustmod.inscriptions.InscriptionManager;
import dustmod.items.DustModItem;
import dustmod.items.ItemChisel;
import dustmod.items.ItemDust;
import dustmod.items.ItemInk;
import dustmod.items.ItemInscription;
import dustmod.items.ItemPlaceScroll;
import dustmod.items.ItemPouch;
import dustmod.items.ItemRunicTome;
import dustmod.items.ItemSpiritPickaxe;
import dustmod.items.ItemSpiritSword;
import dustmod.items.ItemWornInscription;
import dustmod.network.DustDeclarationHandler;
import dustmod.network.DustDeclarationMessage;
import dustmod.network.InscriptionDeclarationHandler;
import dustmod.network.InscriptionDeclarationMessage;
import dustmod.network.MouseHandler;
import dustmod.network.MouseMessage;
import dustmod.network.ParticleHandler;
import dustmod.network.ParticleMessage;
import dustmod.network.RenderBrokenToolHandler;
import dustmod.network.RenderBrokenToolMessage;
import dustmod.network.RuneDeclarationHandler;
import dustmod.network.RuneDeclarationMessage;
import dustmod.network.SetInscriptionHandler;
import dustmod.network.SetInscriptionMessage;
import dustmod.network.SetVelocityHandler;
import dustmod.network.SetVelocityMessage;
import dustmod.network.UseInkHandler;
import dustmod.network.UseInkMessage;
import dustmod.runes.RuneManager;
import dustmod.runes.EntityRune;
import dustmod.runes.EntityRuneManager;

@Mod(modid = DustMod.MOD_ID, name = "Runic Dust Mod", version = "2.0.0")
public class DustMod {
	
	public static final String MOD_ID = "DustMod";

	@Instance(MOD_ID)
	public static DustMod instance;
	
	public static SimpleNetworkWrapper networkWrapper;
	
	public static Logger logger = LogManager.getLogger(MOD_ID);

	public static final int warpVer = 1;
	public static boolean debug = false;

	public static int plantDID = 1;
	public static int gunDID = 2;
	public static int lapisDID = 3;
	public static int blazeDID = 4;

	public static String spritePath = "dustmod:";
	public static File suggestedConfig;
	public static int[] tex;
	public static int groundTex;
	public static boolean allTex = true;

	public static int DustMetaDefault = 0;
	public static int DustMetaUsing = 1;
	public static int DustMetaUsed = 2;

	public static int ENTITY_FireSpriteID = 149;
	public static int ENTITY_BlockEntityID = 150;
	public static boolean Enable_Render_Flames_On_Dust = true;
	public static boolean Enable_Render_Flames_On_Ruts = true;
	public static boolean Enable_Decorative_Ruts = false;

	public static boolean verbose = false;

	public static Block dust;
	protected static Block dustTable;
	public static Block rutBlock;
	public static DustModItem idust;
	public static DustModItem tome;
	public static DustModItem dustScroll;
	public static Item spiritPickaxe;
	public static Item spiritSword;
	public static DustModItem chisel;
	public static DustModItem negateSacrifice;
	public static DustModItem runicPaper;
	public static ItemInscription inscription;
	public static ItemInk ink;
	public static ItemWornInscription wornInscription;
	public static ItemPouch pouch;

	public static DustModTab creativeTab;

	public static int prevVoidSize;
	public static HashMap<String, ArrayList<ItemStack>> voidInventory;
	public static ArrayList<int[]> voidNetwork;
	public static int skipWarpTick = 0;

	public static int numSec = 0; // number of secret runes

	@SidedProxy(clientSide = "dustmod.client.ClientProxy", serverSide = "dustmod.CommonProxy")
	public static CommonProxy proxy;
	public static CommonMouseHandler keyHandler = new CommonMouseHandler();
	public static InscriptionManager inscriptionManager = new InscriptionManager();
	public static Random random = new Random();

	@EventHandler
	public void preInit(FMLPreInitializationEvent evt) {

		networkWrapper = new SimpleNetworkWrapper(MOD_ID);
		networkWrapper.registerMessage(DustDeclarationHandler.class, DustDeclarationMessage.class, 1, Side.CLIENT);
		networkWrapper.registerMessage(InscriptionDeclarationHandler.class, InscriptionDeclarationMessage.class, 2, Side.CLIENT);
		networkWrapper.registerMessage(RuneDeclarationHandler.class, RuneDeclarationMessage.class, 3, Side.CLIENT);
		networkWrapper.registerMessage(ParticleHandler.class, ParticleMessage.class, 4, Side.CLIENT);
		networkWrapper.registerMessage(RenderBrokenToolHandler.class, RenderBrokenToolMessage.class, 5, Side.CLIENT);
		networkWrapper.registerMessage(SetVelocityHandler.class, SetVelocityMessage.class, 6, Side.CLIENT);
		
		networkWrapper.registerMessage(MouseHandler.class, MouseMessage.class, 101, Side.SERVER);
		networkWrapper.registerMessage(SetInscriptionHandler.class, SetInscriptionMessage.class, 102, Side.SERVER);
		networkWrapper.registerMessage(UseInkHandler.class, UseInkMessage.class, 103, Side.SERVER);
		
		suggestedConfig = new File(evt.getSuggestedConfigurationFile()
				.getParent() + File.separator + "DustModConfigv2.cfg");
		// suggestedConfig.renameTo(new File("DustModConfigv2.cfg"));

		creativeTab = new DustModTab();

		// System.out.println("CONFIG " + suggestedConfig.getParent());
		Configuration config = new Configuration(suggestedConfig);
		try {
			// File f = new File(configPath);
			// f.mkdirs();
			config.load();

			ENTITY_FireSpriteID = config.get(Configuration.CATEGORY_GENERAL,
					"FireSpriteEntityID", ENTITY_FireSpriteID).getInt(
					ENTITY_FireSpriteID);
			ENTITY_BlockEntityID = config.get(Configuration.CATEGORY_GENERAL,
					"BlockEntityID", ENTITY_BlockEntityID).getInt(
					ENTITY_BlockEntityID);
			Enable_Decorative_Ruts = config.get("config", "DecorativeRuts",
					Enable_Decorative_Ruts).getBoolean(Enable_Decorative_Ruts);
			verbose = config.get("config", "verbose", verbose).getBoolean(
					verbose);
		} catch (Exception e) {
			FMLLog.log(Level.FATAL, e, "[DustMod] : Error loading config.");
		} finally {
			config.save();
		}

		dust = new BlockDust();
		
		idust = (DustModItem) (new ItemDust(dust)).setUnlocalizedName("idust").setCreativeTab(creativeTab);
		
		dustTable = ((Block) new BlockDustTable()).setBlockName("dustTable").setCreativeTab(creativeTab);
		
		tome = (DustModItem) (new ItemRunicTome()).setUnlocalizedName("dustlibrary").setCreativeTab(creativeTab);
		
		negateSacrifice = (DustModItem) new DustModItem().setUnlocalizedName("negateSacrifice").setCreativeTab(creativeTab);
		
		runicPaper = (DustModItem) (new DustModItem()).setUnlocalizedName("runicPaper").setCreativeTab(creativeTab);
		
		dustScroll = (DustModItem) (new ItemPlaceScroll()).setUnlocalizedName("dustscroll").setCreativeTab(creativeTab);
		
		rutBlock = new BlockRut().setBlockName("dustrutblock").setHardness(3.0F).setResistance(5.0F);
		
		chisel = (DustModItem) new ItemChisel().setUnlocalizedName("itemdustchisel").setCreativeTab(creativeTab);
		
		spiritPickaxe = (new ItemSpiritPickaxe(ToolMaterial.EMERALD)).setUnlocalizedName("spiritPickaxe").setCreativeTab(creativeTab);
		
		spiritSword = (new ItemSpiritSword()).setUnlocalizedName("spiritSword").setCreativeTab(creativeTab);
		
		inscription = (ItemInscription) (new ItemInscription()).setUnlocalizedName("runicinscription").setCreativeTab(creativeTab);
		
		ink = (ItemInk) new ItemInk().setUnlocalizedName("itemInk");
		
		wornInscription = new ItemWornInscription();
		wornInscription.setCreativeTab(creativeTab);
		wornInscription.setUnlocalizedName("wornInscription");
		
		pouch = new ItemPouch(dust);
		pouch.setCreativeTab(creativeTab);
		pouch.setUnlocalizedName("dustPouch");

	}

	@EventHandler
	public void load(FMLInitializationEvent evt) {

		NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());

		proxy.registerEventHandlers();

		GameRegistry.registerBlock(dust, ItemBlock.class, dust.getUnlocalizedName());
		GameRegistry.registerBlock(dustTable, ItemBlock.class, dustTable.getUnlocalizedName());
		GameRegistry.registerBlock(rutBlock, ItemBlock.class, rutBlock.getUnlocalizedName());
		
		GameRegistry.registerItem(idust, idust.getUnlocalizedName());
		GameRegistry.registerItem(tome, tome.getUnlocalizedName());
		GameRegistry.registerItem(negateSacrifice, negateSacrifice.getUnlocalizedName());
		GameRegistry.registerItem(runicPaper, runicPaper.getUnlocalizedName());
		GameRegistry.registerItem(dustScroll, dustScroll.getUnlocalizedName());
		GameRegistry.registerItem(chisel, chisel.getUnlocalizedName());
		GameRegistry.registerItem(spiritPickaxe, spiritPickaxe.getUnlocalizedName());
		GameRegistry.registerItem(spiritSword, spiritSword.getUnlocalizedName());
		GameRegistry.registerItem(inscription, inscription.getUnlocalizedName());
		GameRegistry.registerItem(ink, ink.getUnlocalizedName());
		GameRegistry.registerItem(wornInscription, wornInscription.getUnlocalizedName());
		GameRegistry.registerItem(pouch, pouch.getUnlocalizedName());

		GameRegistry.registerTileEntity(TileEntityDust.class, "dusttileentity");
		GameRegistry.registerTileEntity(TileEntityDustTable.class,
				"dusttabletileentity");
		GameRegistry.registerTileEntity(TileEntityRut.class,
				"dustruttileentity");

		proxy.registerTileEntityRenderers();

		LanguageRegistry lang = LanguageRegistry.instance();
		lang.addStringLocalization("tile.dust.name", "en_US",
				"[DustMod] :Do not use this");

		lang.addStringLocalization(dustTable.getUnlocalizedName() + ".name", "en_US",
				"Runic Lexicon");
		lang.addStringLocalization(tome.getUnlocalizedName() + ".name", "en_US",
				"Runic Tome");
		lang.addStringLocalization(negateSacrifice.getUnlocalizedName() + ".name",
				"en_US", "Negate Rune Sacrifice");
		lang.addStringLocalization(runicPaper.getUnlocalizedName() + ".name", "en_US",
				"Scroll Paper");
		lang.addStringLocalization(spiritPickaxe.getUnlocalizedName() + ".name",
				"en_US", "Spirit Pickaxe");
		lang.addStringLocalization(spiritSword.getUnlocalizedName() + ".name",
				"en_US", "Spirit Sword");
		lang.addStringLocalization(chisel.getUnlocalizedName() + ".name", "en_US",
				"Hammer&Chisel");
		lang.addStringLocalization("pouchblank.name", "en_US",
				"ERROR Runic Pouch");
		// lang.addStringLocalization(inscription.getItemName() + ".name",
		// "en_US", "Blank Runic Inscription");
		lang.addStringLocalization("emptyinsc.name", "en_US",
				"Blank Runic Inscription");
		lang.addStringLocalization("driedinsc.name", "en_US", "Dried Drawing");
		lang.addStringLocalization("dryinginsc.name", "en_US",
				"Drying Inscription");

		GameRegistry.addRecipe(new ItemStack(dustTable, 1), new Object[] {
				"dwd", "wbw", "dwd", 'd', new ItemStack(idust, 1, -1), 'w',
				new ItemStack(Blocks.planks, 1, -1), 'b',
				new ItemStack(tome, -1) });
		GameRegistry.addRecipe(new ItemStack(dustTable, 1), new Object[] {
				"wdw", "dbd", "wdw", 'd', new ItemStack(idust, 1, -1), 'w',
				new ItemStack(Blocks.planks, 1, -1), 'b',
				new ItemStack(tome, -1) });
		GameRegistry.addRecipe(new ItemStack(chisel, 1), new Object[] { "st",
				"i ", 's', new ItemStack(Blocks.cobblestone, 1), 't',
				new ItemStack(Items.stick, 1), 'i',
				new ItemStack(Items.iron_ingot, 1) });
		GameRegistry.addRecipe(new ItemStack(inscription, 1), new Object[] {
				"s", "p", "p", 's', new ItemStack(Items.string, 1), 'p',
				new ItemStack(runicPaper, 1) });

		GameRegistry.addShapelessRecipe(new ItemStack(idust, 8, 100),
				new Object[] { new ItemStack(Items.coal, 1, -1),
						new ItemStack(Blocks.tallgrass, 1, -1),
						new ItemStack(Blocks.tallgrass, 1, -1) /* , mortar */});
		GameRegistry.addShapelessRecipe(new ItemStack(idust, 8, 100),
				new Object[] { new ItemStack(Items.coal, 1, -1),
						new ItemStack(Blocks.leaves, 1, -1),
						new ItemStack(Blocks.leaves, 1, -1) /* , mortar */});
		GameRegistry.addShapelessRecipe(new ItemStack(idust, 8, 100),
				new Object[] { new ItemStack(Items.coal, 1, -1),
						new ItemStack(Blocks.sapling, 1, -1),
						new ItemStack(Blocks.sapling, 1, -1) /* , mortar */});
		GameRegistry.addShapelessRecipe(new ItemStack(idust, 8, 100),
				new Object[] { new ItemStack(Items.coal, 1, -1),
						Items.wheat_seeds, Items.wheat_seeds /* , mortar */});
		GameRegistry.addShapelessRecipe(new ItemStack(idust, 8, 100),
				new Object[] { new ItemStack(Items.coal, 1, -1),
						Blocks.cactus, Blocks.cactus /* , mortar */});
		GameRegistry.addShapelessRecipe(new ItemStack(idust, 8, 100),
				new Object[] { new ItemStack(Items.coal, 1, -1),
						Blocks.cactus, Items.wheat_seeds });
		GameRegistry.addShapelessRecipe(new ItemStack(idust, 8, 100),
				new Object[] { new ItemStack(Items.coal, 1, -1),
						Blocks.cactus, new ItemStack(Blocks.sapling, 1, -1) });
		GameRegistry.addShapelessRecipe(new ItemStack(idust, 8, 100),
				new Object[] { new ItemStack(Items.coal, 1, -1),
						Blocks.cactus, new ItemStack(Blocks.leaves, 1, -1) });
		GameRegistry.addShapelessRecipe(new ItemStack(idust, 8, 100),
				new Object[] { new ItemStack(Items.coal, 1, -1),
						Blocks.cactus, new ItemStack(Blocks.tallgrass, 1, -1) });
		GameRegistry.addShapelessRecipe(new ItemStack(idust, 8, 100),
				new Object[] { new ItemStack(Items.coal, 1, -1),
						Items.wheat_seeds, new ItemStack(Blocks.sapling, 1, -1) });
		GameRegistry.addShapelessRecipe(new ItemStack(idust, 8, 100),
				new Object[] { new ItemStack(Items.coal, 1, -1),
						Items.wheat_seeds, new ItemStack(Blocks.leaves, 1, -1) });
		GameRegistry.addShapelessRecipe(new ItemStack(idust, 8, 100),
				new Object[] { new ItemStack(Items.coal, 1, -1),
						Items.wheat_seeds, new ItemStack(Blocks.tallgrass, 1, -1) });
		GameRegistry.addShapelessRecipe(new ItemStack(idust, 8, 100),
				new Object[] { new ItemStack(Items.coal, 1, -1),
						new ItemStack(Blocks.sapling, 1, -1),
						new ItemStack(Blocks.leaves, 1, -1) /* , mortar */});
		GameRegistry.addShapelessRecipe(new ItemStack(idust, 8, 100),
				new Object[] { new ItemStack(Items.coal, 1, -1),
						new ItemStack(Blocks.sapling, 1, -1),
						new ItemStack(Blocks.tallgrass, 1, -1) /* , mortar */});
		GameRegistry.addShapelessRecipe(new ItemStack(idust, 8, 100),
				new Object[] { new ItemStack(Items.coal, 1, -1),
						new ItemStack(Blocks.leaves, 1, -1),
						new ItemStack(Blocks.tallgrass, 1, -1) /* , mortar */});
		GameRegistry.addShapelessRecipe(new ItemStack(idust, 12, 200),
				new Object[] { Items.gunpowder, new ItemStack(idust, 1, 100),
						new ItemStack(idust, 1, 100) /* , mortar */});
		GameRegistry.addShapelessRecipe(new ItemStack(idust, 8, 300),
				new Object[] { new ItemStack(Items.coal, 1, -1),
						new ItemStack(Items.dye, 2, 4),
						new ItemStack(Items.dye, 2, 4),
						new ItemStack(Items.dye, 2, 4) });
		GameRegistry.addShapelessRecipe(new ItemStack(idust, 12, 400),
				new Object[] { Items.blaze_powder, new ItemStack(idust, 1, 300),
						new ItemStack(idust, 1, 300),
						new ItemStack(idust, 1, 300) /* , mortar */});
		GameRegistry.addShapelessRecipe(new ItemStack(tome, 1, 0),
				new Object[] { new ItemStack(idust, 1, -1), Items.book });
		GameRegistry.addShapelessRecipe(new ItemStack(runicPaper, 1),
				new Object[] { Items.paper, Items.gold_nugget, Items.gold_nugget });

		for (int i = 1; i < 5; i++) {
			// Migration from old system
			GameRegistry.addShapelessRecipe(new ItemStack(idust, 1, i * 100),
					new ItemStack(idust, 1, i));
		}

		EntityRegistry.registerModEntity(EntityRune.class, "dustentity",
				ENTITY_FireSpriteID, this, 192, 2, false);
		// EntityRegistry.registerGlobalEntityID(EntityDust.class, "dustentity",
		// ENTITY_FireSpriteID);
		EntityRegistry.registerModEntity(EntityBlock.class, "dustblockentity",
				ENTITY_BlockEntityID, this, 64, 1, false);
		// EntityRegistry.registerGlobalEntityID(EntityBlock.class,
		// "dustblockentity", ENTITY_BlockEntityID);

		// NetworkRegistry.instance().registerGuiHandler(instance, proxy);

		proxy.registerRenderInformation();

		DustItemManager.registerDefaultDusts();
		RuneManager.registerDefaultShapes();
		InscriptionManager.registerDefaultInscriptions();
		lang.addStringLocalization("inscblank.name", "Doodle");

		MinecraftForge.EVENT_BUS.register(this);
	}

	@EventHandler
	public void modsLoaded(FMLPostInitializationEvent evt) {
		// if(FMLCommonHandler.instance().getSide() == Side.CLIENT){
		// try{//Debugging
		// Class c = Class.forName("net.minecraft.src.World");
		// Minecraft.getMinecraft().session.username = "BILLYTG101";
		// System.err.println("[DustMod] WARNING: This is being run in a debug environment!");
		// }catch(Exception e){
		// //not debugging
		// }
		//
		// }
	}

	@SubscribeEvent
	public void onWorldEvent(WorldEvent.Load evt) {

		if (evt.world.isRemote)
			return;
		// System.out.println("World event Load " + evt.world);

		ISaveHandler save = evt.world.getSaveHandler();
		int nameLength = (new StringBuilder())
				.append(save.getWorldDirectoryName()).append(".dat").length();

		File mapFile = save.getMapFileFromName(save.getWorldDirectoryName());
		if (mapFile == null)
			return;
		String savePath = mapFile.getPath();
		savePath = savePath.substring(0, savePath.length() - nameLength);

		VoidStorageManager.load(savePath);
		VoidTeleManager.load(savePath);
		EntityRuneManager.load(savePath);
	}

	public static void spawnParticles(World world, String type, double x,
			double y, double z, double velx, double vely, double velz, int amt,
			double radius) {
		spawnParticles(world, type, x, y, z, velx, vely, velz, amt, radius,
				radius, radius);
	}

	public static void spawnParticles(World world, String type, double x,
			double y, double z, double velx, double vely, double velz, int amt,
			double rx, double ry, double rz) {
		spawnParticles(world, type, new double[] { x, y, z }, velx, vely, velz,
				amt, rx, ry, rz);
	}

	public static void spawnParticles(World world, String type,
			double[] locations, double velx, double vely, double velz, int amt,
			double rx, double ry, double rz) {
		ParticleMessage message = new ParticleMessage(type, (short) 0,
				locations, velx, vely, velz, amt, rx, ry, rz);
		
		networkWrapper.sendToDimension(message, world.provider.dimensionId);
	}

	/**
	 * 
	 * Compares the two dust's values to find which is better.
	 * 
	 * @param base
	 *            The reference dust
	 * @param dust
	 *            The check dust
	 * @return 0 if dusts are equal, -1 if the reference is worth less than the
	 *         check, and 1 if the reference is worth more than the check
	 */
	public static int compareDust(int base, int dust) {

		if (base == -1 || dust == -1) {
			throw new IllegalArgumentException("Invalid dust ID.");
		}

		if (base == dust) {
			return 0;
		}

		if (base > dust) {
			return -1;
		}

		if (dust > base) {
			return 1;
		}

		return -1;
	}

	/**
	 * Syncs player inventory of the client with server. Theoretically.
	 * 
	 * @param p
	 *            Player's inventory to sync.
	 */
	public static void sendPlayerInventory(EntityPlayer p) {

		//TODO
		/*int var1;

		if (!p.worldObj.isRemote)
			for (var1 = 0; var1 < 5; ++var1) {
				ItemStack var2 = p.getCurrentItemOrArmor(var1);
				((WorldServer) p.worldObj).getEntityTracker()
						.sendPacketToAllPlayersTrackingEntity(
								p,
								new Packet5PlayerInventory(p.entityId, var1,
										var2));
			}*/
	}

	public static void sendEntMotionTraits(EntityLivingBase wearer) {
		networkWrapper.sendToDimension(new SetVelocityMessage(wearer), wearer.worldObj.provider.dimensionId);
	}
	
	public static void sendRenderBreakItem(EntityPlayer ent, ItemStack tool){
		networkWrapper.sendToDimension(new RenderBrokenToolMessage(ent, tool), ent.worldObj.provider.dimensionId);
	}

	/**
	 * Returns the itemstack that represents the given entity
	 * 
	 * @param entityID
	 *            The EntityID of the mob
	 * @return The itemstack that represents that mob
	 */
	public static ItemStack getDrop(int entityID) {
		for (ItemStack i : entdrops.keySet()) {
			if (entdrops.get(i) == entityID)
				return new ItemStack(i.getItem(), i.stackSize, i.getItemDamage());
		}
		return null;
	}

	/**
	 * Gets the entityID for a certain mob drop type.
	 * 
	 * @param is
	 *            The item to check
	 * @param mul
	 *            The multiplier for the itemstack size
	 * @return -1 if not found or the item stacksize isn't big enough, else the
	 *         entityID
	 */
	public static int getEntityIDFromDrop(ItemStack is, int mul) {
		// System.out.println("CHECK " + is + " " + is.stackSize + " " +
		// is.getItemDamage());
		for (ItemStack i : entdrops.keySet()) {
			// System.out.println("grr " + i + " " + i.stackSize + " " +
			// i.getItemDamage());
			if (i == is
					&& (is.stackSize >= i.stackSize * mul || is.stackSize == -1)
					&& (i.getItemDamage() == is.getItemDamage() || i
							.getItemDamage() == -1)) {
				// System.out.println("ent found");
				return entdrops.get(i);
			}
		}

		// System.out.println("ent not found");
		return -1;
	}

	/**
	 * Checks to see if a mob is hostile.
	 * 
	 * @param id
	 *            The EntityID of the mob
	 * @return true if hostile, false if not
	 */
	public static boolean isMobIDHostile(int id) {
		Entity ent = EntityList.createEntityByID(id, null);

		if (ent instanceof IMob) {
			return true;
		}

		return false;
	}

	/**
	 * Register an item that should represent the given mob. Used for runes like
	 * the resurrection rune where an item is sacrificed to determine which mob
	 * to spawn. The stacksize should be an amount related to the worth of the
	 * mob/item. For example, to spawn a chicken you need relatively less to
	 * chickenRaw(4) items than you need blazerods to spawn a blaze(16)
	 * 
	 * @param item
	 *            The item that should represent the entity (generally the item
	 *            that the mob should drop)
	 * @param entityID
	 *            The entityID that should be represented by the item.
	 */
	public static void registerNewEntityDropForSacrifice(ItemStack item,
			int entityID) {
		entdrops.put(item, entityID);
	}

	public static ItemWornInscription getWornInscription() {
		return wornInscription;
	}

	public static Item getItemDust() {
		// if(!hasLoaded){
		// instance.preInit(null);
		// }
		return idust;
	}

	public static Item getNegator() {
		// if(!hasLoaded){
		// instance.preInit(null);
		// }
		return negateSacrifice;
	}

	public static HashMap<ItemStack, Integer> entdrops;

	public static void log(String msg, Object... objs) {
		String message = "[DustMod] " + msg;
		for (Object o : Arrays.asList(objs)) {
			message += " " + o;
		}
		FMLLog.log(Level.INFO, message);
	}

	static {
		entdrops = new HashMap<ItemStack, Integer>();
		entdrops.put(new ItemStack(Items.chicken, 4, 0), 93); // chicken
		entdrops.put(new ItemStack(Items.beef, 4, 0), 92); // cow
		entdrops.put(new ItemStack(Blocks.red_mushroom_block, 16, -1), 96); // mooshroom
		entdrops.put(new ItemStack(Items.fish, 8, 0), 98); // ocelot
		entdrops.put(new ItemStack(Items.porkchop, 4, 0), 90); // pig
		entdrops.put(new ItemStack(Blocks.wool, 8, -1), 91); // sheep
		entdrops.put(new ItemStack(Items.dye, 4, 0), 94); // squid
		entdrops.put(new ItemStack(Blocks.brick_block, 8, 0), 120); // villager
		entdrops.put(new ItemStack(Items.ender_pearl, 8, 0), 58); // enderman
		entdrops.put(new ItemStack(Items.leather, 16, 0), 95); // wolf
		entdrops.put(new ItemStack(Items.gold_nugget, 16, 0), 57); // zombie
																				// pigman
		entdrops.put(new ItemStack(Items.blaze_rod, 16, 0), 61); // blaze
		entdrops.put(new ItemStack(Items.spider_eye, 8, 0), 59); // cave
																			// spider
		entdrops.put(new ItemStack(Items.gunpowder, 8, 0), 50); // creeper
		entdrops.put(new ItemStack(Items.ghast_tear, 8, 0), 56); // ghast
		entdrops.put(new ItemStack(Items.magma_cream, 8, 0), 62); // magma
																				// slime
		entdrops.put(new ItemStack(Blocks.stonebrick, 16, 1), 60); // silverfish
		entdrops.put(new ItemStack(Items.bone, 16, 0), 51); // skeleton
		entdrops.put(new ItemStack(Items.slime_ball, 16, 0), 55); // slime
		entdrops.put(new ItemStack(Items.string, 16, 0), 52); // spider
		entdrops.put(new ItemStack(Items.rotten_flesh, 8, 0), 54); // zombie
		entdrops.put(new ItemStack(Blocks.snow, 8, 0), 97); // snow golem
		entdrops.put(new ItemStack(Blocks.iron_block, 8, 0), 99); // iron
																			// golem
		entdrops.put(new ItemStack(Blocks.dragon_egg, 64, 0), 63); // ender
																			// dragon
		entdrops.put(new ItemStack(Blocks.diamond_block, 64, 0), 53); // giant
	}

	public static boolean isDust(Block block) {
		return block == dust;
	}
}
