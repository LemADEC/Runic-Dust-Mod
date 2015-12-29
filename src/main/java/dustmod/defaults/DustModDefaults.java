package dustmod.defaults;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import dustmod.defaults.inscriptions.BlinkerInscription;
import dustmod.defaults.inscriptions.BounceInscription;
import dustmod.defaults.inscriptions.EnderInscription;
import dustmod.defaults.inscriptions.ForesightInscription;
import dustmod.defaults.inscriptions.RespawnInscription;
import dustmod.defaults.inscriptions.RocketLaunch;
import dustmod.defaults.inscriptions.VoidInscription;
import dustmod.defaults.runes.REBait;
import dustmod.defaults.runes.REBomb;
import dustmod.defaults.runes.REBounce;
import dustmod.defaults.runes.RECage;
import dustmod.defaults.runes.RECampFire;
import dustmod.defaults.runes.REChargeInscription;
import dustmod.defaults.runes.RECompression;
import dustmod.defaults.runes.REDawn;
import dustmod.defaults.runes.REEarthSprite;
import dustmod.defaults.runes.REEggifier;
import dustmod.defaults.runes.REFarm;
import dustmod.defaults.runes.REFireBowEnch;
import dustmod.defaults.runes.REFireRain;
import dustmod.defaults.runes.REFireSprite;
import dustmod.defaults.runes.REFireTrap;
import dustmod.defaults.runes.REFlatten;
import dustmod.defaults.runes.REForcefield;
import dustmod.defaults.runes.REFortuneEnch;
import dustmod.defaults.runes.REHeal;
import dustmod.defaults.runes.REHideout;
import dustmod.defaults.runes.RELiftTerrain;
import dustmod.defaults.runes.RELightning;
import dustmod.defaults.runes.RELillyBridge;
import dustmod.defaults.runes.RELumberjack;
import dustmod.defaults.runes.RELunar;
import dustmod.defaults.runes.REMiniTele;
import dustmod.defaults.runes.REObelisk;
import dustmod.defaults.runes.REPit;
import dustmod.defaults.runes.REPoisonTrap;
import dustmod.defaults.runes.REPowerRelay;
import dustmod.defaults.runes.REResurrection;
import dustmod.defaults.runes.RESilkTouchEnch;
import dustmod.defaults.runes.RESpawnRecord;
import dustmod.defaults.runes.RESpawnTorch;
import dustmod.defaults.runes.RESpawnerCollector;
import dustmod.defaults.runes.RESpawnerReprog;
import dustmod.defaults.runes.RESpeed;
import dustmod.defaults.runes.RESpiritTool;
import dustmod.defaults.runes.RETeleportation;
import dustmod.defaults.runes.RETimeLock;
import dustmod.defaults.runes.REVoid;
import dustmod.defaults.runes.REWall;
import dustmod.defaults.runes.REXP;
import dustmod.defaults.runes.REXPStore;
import dustmod.inscriptions.InscriptionEvent;
import dustmod.inscriptions.InscriptionManager;
import dustmod.runes.XMLRuneShapeReader;

/**
 * This pack is meant for testing runes & inscriptions as a separate download to
 * make sure that the added content is balanced and fair.
 * 
 * @author billythegoat101
 * 
 */
@Mod(modid = "DustModDefaults", name = "Dust mod default Rune Pack", version = "1.6.1", dependencies = "after:DustMod")
public class DustModDefaults {

	@Instance("DustModDefaults")
	public static DustModDefaults instance;

	@EventHandler
	public void postInit(FMLPostInitializationEvent evt) {
		registerDusts();
		registerRunes();
		registerInscriptions();
	}

	public void registerDusts() {
		// Default dusts come with the actual mod to start
	}
	
	private static final String DATA_DIR = "assets/dustmod/runes/data/";

	public void registerRunes() {
		
		XMLRuneShapeReader.readAndRegisterShape(DATA_DIR + "baiting.xml", new REBait());
		
		XMLRuneShapeReader.readAndRegisterShape(DATA_DIR + "torch.xml", new RESpawnTorch());
		
		XMLRuneShapeReader.readAndRegisterShape(DATA_DIR + "rabbit.xml", new REHideout());
		
		XMLRuneShapeReader.readAndRegisterShape(DATA_DIR + "healing.xml", new REHeal());
		
		XMLRuneShapeReader.readAndRegisterShape(DATA_DIR + "lumber.xml", new RELumberjack());
		
		XMLRuneShapeReader.readAndRegisterShape(DATA_DIR + "campfire.xml", new RECampFire());
		
		XMLRuneShapeReader.readAndRegisterShape(DATA_DIR + "depths.xml", new REPit());
		
		XMLRuneShapeReader.readAndRegisterShape(DATA_DIR + "heights.xml", new REObelisk());
		
		XMLRuneShapeReader.readAndRegisterShape(DATA_DIR + "farm.xml", new REFarm());
		
		XMLRuneShapeReader.readAndRegisterShape(DATA_DIR + "leapfrog.xml", new RELillyBridge());
		
		XMLRuneShapeReader.readAndRegisterShape(DATA_DIR + "dawn.xml", new REDawn());
		
		XMLRuneShapeReader.readAndRegisterShape(DATA_DIR + "dusk.xml", new RELunar());
		
		XMLRuneShapeReader.readAndRegisterShape(DATA_DIR + "trap.fire.xml", new REFireTrap());
		
		XMLRuneShapeReader.readAndRegisterShape(DATA_DIR + "trap.lightning.xml", new RELightning());
		
		XMLRuneShapeReader.readAndRegisterShape(DATA_DIR + "trap.poison.xml", new REPoisonTrap());
		
		XMLRuneShapeReader.readAndRegisterShape(DATA_DIR + "trap.detonation.xml", new REBomb());
		
		XMLRuneShapeReader.readAndRegisterShape(DATA_DIR + "trap.entrap.xml", new RECage());
		
		XMLRuneShapeReader.readAndRegisterShape(DATA_DIR + "timelock.xml", new RETimeLock());
		
		XMLRuneShapeReader.readAndRegisterShape(DATA_DIR + "void.xml", new REVoid());
		
		XMLRuneShapeReader.readAndRegisterShape(DATA_DIR + "wall.xml", new REWall());
		
		XMLRuneShapeReader.readAndRegisterShape(DATA_DIR + "wisdom.xml", new REXPStore());
		
		XMLRuneShapeReader.readAndRegisterShape(DATA_DIR + "speed.xml", new RESpeed());
		
		XMLRuneShapeReader.readAndRegisterShape(DATA_DIR + "compression.xml", new RECompression());
		
		XMLRuneShapeReader.readAndRegisterShape(DATA_DIR + "firerain.xml", new REFireRain());
		
		XMLRuneShapeReader.readAndRegisterShape(DATA_DIR + "eggifier.xml", new REEggifier());
		
		XMLRuneShapeReader.readAndRegisterShape(DATA_DIR + "resurrection.xml", new REResurrection());
		
		XMLRuneShapeReader.readAndRegisterShape(DATA_DIR + "power_relay.xml", new REPowerRelay());
		
		XMLRuneShapeReader.readAndRegisterShape(DATA_DIR + "charge_inscriptions.xml", new REChargeInscription());
		
		XMLRuneShapeReader.readAndRegisterShape(DATA_DIR + "spawner_collection.xml", new RESpawnerCollector());
		
		XMLRuneShapeReader.readAndRegisterShape(DATA_DIR + "spawner_reassignment.xml", new RESpawnerReprog());
		
		XMLRuneShapeReader.readAndRegisterShape(DATA_DIR + "teleport.xml", new RETeleportation());
		
		XMLRuneShapeReader.readAndRegisterShape(DATA_DIR + "minitele.xml", new REMiniTele());
		
		XMLRuneShapeReader.readAndRegisterShape(DATA_DIR + "sprite.fire.xml", new REFireSprite());
		
		XMLRuneShapeReader.readAndRegisterShape(DATA_DIR + "sprite.earth.xml", new REEarthSprite());
		
		XMLRuneShapeReader.readAndRegisterShape(DATA_DIR + "bounce.xml", new REBounce());
		
		XMLRuneShapeReader.readAndRegisterShape(DATA_DIR + "spawn_record.xml", new RESpawnRecord());
		
		XMLRuneShapeReader.readAndRegisterShape(DATA_DIR + "spirit_tools.xml", new RESpiritTool());
		
		XMLRuneShapeReader.readAndRegisterShape(DATA_DIR + "ench.firebow.xml", new REFireBowEnch());
		
		XMLRuneShapeReader.readAndRegisterShape(DATA_DIR + "ench.silktouch.xml", new RESilkTouchEnch());
		
		XMLRuneShapeReader.readAndRegisterShape(DATA_DIR + "ench.fortune.xml", new REFortuneEnch());
		
		XMLRuneShapeReader.readAndRegisterShape(DATA_DIR + "protection.xml", new REForcefield());
		
		XMLRuneShapeReader.readAndRegisterShape(DATA_DIR + "level_earth.xml", new REFlatten());
		
		XMLRuneShapeReader.readAndRegisterShape(DATA_DIR + "lift_terrain.xml", new RELiftTerrain());
		
		XMLRuneShapeReader.readAndRegisterShape(DATA_DIR + "sarlacc.xml", new REXP());

		// last id used: 46
		// notes for reanimation:
		// all numbers are cut off at the end of the name to preserve Lexicon
		// page picture names

	}

	public void registerInscriptions() {
		int N = -1;
		int P = 100;
		int G = 200;
		int L = 300;
		int B = 400;

		InscriptionEvent evt = null;
		int[][] design;


		design = new int[][] {
				{ 0, 0, 0, G, G, 0, 0, 0 }, 
				{ 0, 0, 0, G, G, 0, 0, 0 }, 
				{ 0, 0, G, 0, 0, G, 0, 0 }, 
				{ 0, P, G, G, G, G, P, 0 }, 
				{ 0, 0, P, G, G, P, 0, 0 }, 
				{ 0, P, P, 0, 0, P, P, 0 }, 
				{ 0, P, P, 0, 0, P, P, 0 }, 
				{ P, 0, 0, 0, 0, 0, 0, P } };
		evt = new RocketLaunch(design, "leapI", "Leap I", 0, 1);
		InscriptionManager.registerInscriptionEvent(evt);


		design = new int[][] {
				{ 0, L, 0, 0, L, 0 }, 
				{ 0, L, G, G, L, 0 }, 
				{ L, 0, G, G, 0, L }, 
				{ 0, G, L, L, G, 0 }, 
				{ L, G, 0, 0, G, L }, 
				{ L, L, 0, 0, L, L }, 
				{ L, L, 0, 0, L, L }, 
				{ G, L, 0, 0, L, G } };
		evt = new RocketLaunch(design, "leapII", "Leap II", 1, 2);
		InscriptionManager.registerInscriptionEvent(evt);


		design = new int[][] {

				{ 0, 0, 0, G, L, L, 0, L, 0, 0, 0, 0 },
				{ 0, 0, G, G, L, G, L, L, L, L, 0, 0 },
				{ G, G, G, G, L, G, G, 0, G, 0, L, 0 },
				{ 0, L, 0, G, 0, G, G, L, G, G, G, G },
				{ 0, 0, L, L, L, L, G, L, G, G, 0, 0 },
				{ 0, 0, 0, 0, L, 0, L, L, G, 0, 0, 0 }, };
		evt = new RespawnInscription(design, "respawn", "Return I", 3);
		InscriptionManager.registerInscriptionEvent(evt);

		design = new int[][] { 
				{0, 0, L, 0, 0, 0, 0, L, 0, 0 },
				{0, G, L, L, 0, 0, L, L, G, 0 },
				{G, G, L, G, L, L, G, L, G, G },
				{0, L, G, 0, G, G, 0, G, L, 0 },
				{0, L, G, 0, G, G, 0, G, L, 0 },
				{G, G, L, G, L, L, G, L, G, G },
				{0, G, L, L, 0, 0, L, L, G, 0 },
				{0, 0, L, 0, 0, 0, 0, L, 0, 0 }
		};
		evt = new VoidInscription(design, "voidinscription",
				"Void I", 4);
		InscriptionManager.registerInscriptionEvent(evt);

		design = new int[][] { 
				{0, 0, 0, P, P, 0, 0, 0 },
				{0, G, G, G, P, P, P, 0 },
				{0, G, P, G, G, P, P, 0 },
				{P, P, G, P, 0, G, G, G },
				{P, P, P, 0, G, P, G, G },
				{0, G, G, P, P, G, P, 0 },
				{0, G, G, G, P, P, P, 0 },
				{0, 0, 0, G, G, 0, 0, 0 }
		};
		evt = new BounceInscription(design, "bouncy",
				"Bounce I", 5);
		InscriptionManager.registerInscriptionEvent(evt);

		design = new int[][] { 
				{0, 0, 0, 0, G, 0, 0, 0, G, 0, 0, 0 },
				{0, 0, 0, G, P, P, 0, P, P, G, 0, 0 },
				{0, P, P, P, P, G, G, P, P, P, P, P },
				{G, G, G, G, G, P, P, G, G, G, G, 0 },
				{0, 0, P, G, G, 0, G, G, P, 0, 0, 0 },
				{0, 0, 0, P, 0, 0, 0, P, 0, 0, 0, 0 }
		};
		evt = new EnderInscription(design, "blinkI",
				"Blink I", 6);
		InscriptionManager.registerInscriptionEvent(evt);

		design = new int[][] { 
				{0, 0, 0, 0, G, G, 0, G, G, G, 0, 0 },
				{0, 0, L, L, G, G, L, L, 0, G, 0, 0 },
				{0, G, G, G, G, L, G, L, L, L, L, L },
				{L, L, L, L, L, G, L, G, G, G, G, 0 },
				{0, G, 0, 0, L, L, G, G, L, L, 0, 0 },
				{0, G, G, G, 0, 0, G, G, 0, 0, 0, 0 }
		};
		evt = new BlinkerInscription(design, "blinkII",
				"Blink II", 7);
		InscriptionManager.registerInscriptionEvent(evt);

		design = new int[][] {
				{0, 0, G, 0, 0, 0},
				{0, 0, G, L, 0, 0},
				{0, 0, G, G, 0, 0},
				{0, G, 0, G, L, 0},
				{0, G, G, G, L, 0},
				{G, G, L, L, G, G},
				{G, G, L, L, G, G},
				{0, L, G, G, G, 0},
				{0, L, G, 0, G, 0},
				{0, 0, G, G, 0, 0},
				{0, 0, L, G, 0, 0},
				{0, 0, 0, G, 0, 0},
		};
		evt = new ForesightInscription(design, "foresight", "Foresight I", 8);
		evt.setAuthor("billythegoat101");
		InscriptionManager.registerInscriptionEvent(evt);
		
		//Last ID  used: 8
	}

}
