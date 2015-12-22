/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dustexample;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import dustexample.examplerunes.REChestNom;
import dustexample.examplerunes.REIceSprite;
import dustexample.examplerunes.REMakeBlockFromDustLevel;
import dustexample.examplerunes.REMakeBlockFromItem;
import dustexample.examplerunes.REMakeItRain;
import dustexample.examplerunes.REPain;
import dustmod.DustMod;
import dustmod.dusts.DustManager;
import dustmod.runes.RuneManager;
import dustmod.runes.RuneShape;
import dustmod.runes.XMLRuneShapeReader;

/**
 * 
 * These runes are meant only to be part of a demonstration of the rune system
 * to be used as a resource for modders looking to make custom runes of their
 * own. These runes are not meant to enhance gameplay or to be used as tools in
 * a real playthough of the Runic Dust mod, just as examples.
 * 
 * If you are a modder(which you probably are) then you will find that I list
 * off what each rune in here will provide a good example of at the top of the
 * DustEvent class in a similar comment block as this one. Good luck! And I hope
 * This helps!
 * 
 * 
 * @author billythegoat101
 */

@Mod(modid = "DustExample", name = "Dust Mod Example 1", version = "1.0", dependencies = "after:DustMod")
public class DustExample {
	@Instance("DustExample")
	public static DustExample instance;
	
	@EventHandler
	public void postInit(FMLPostInitializationEvent evt) {
		registerDusts();
		registerRunes();
		registerInscriptions();
	}

	public void registerDusts() {
		DustManager.registerDust(350, "Glowing Runic Dust", "glowdust",
				0xEEEE00, 0xFFFF00, 0xFFFF00, 15);
		
		// Register recipe for our dust (2xGlowstoneDust + 1xCoal)
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(DustMod.getItemDust(), 4, 350),
				"coal",
				"dustGlowstone",
				"dustGlowstone" ));
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(DustMod.getItemDust(), 10, 350),
				"coal",
				"coal",
				"glowstone" ));
	}
	
	/**
	 * Here is where you register all your runes with the DustManager.
	 */
	public void registerRunes() {
		
		// Note that the order in which these runes appear in the tome is
		// dependent upon the order by which they are registered into the system.
		
		//Read and load properties from the XML file for the rune, then register it to the DustEvent.
		XMLRuneShapeReader.readAndRegisterShape("assets/dustexample/examplerunes/data/omnom.xml", new REChestNom());
		
		XMLRuneShapeReader.readAndRegisterShape("assets/dustexample/examplerunes/data/cheating1_make_block.xml", new REMakeBlockFromItem());
		
		XMLRuneShapeReader.readAndRegisterShape("assets/dustexample/examplerunes/data/rain_maker.xml", new REMakeItRain());
		
		XMLRuneShapeReader.readAndRegisterShape("assets/dustexample/examplerunes/data/pain.xml", new REPain());
		
		XMLRuneShapeReader.readAndRegisterShape("assets/dustexample/examplerunes/data/sprite.ice.xml", new REIceSprite());
		
		// Note that the solid flag is now changed to true because it is all variable and 
		// we don't want people cheating the rune by making some blaze and some plant
		
		// There is also a new tag <allowedVariable> which specifies which types of dusts 
		// are allowed to substitute the variable dust
		XMLRuneShapeReader.readAndRegisterShape("assets/dustexample/examplerunes/data/cheating2_change_block.xml", new REMakeBlockFromDustLevel());
		
		// ******Notes for reanimation*******
		// Adding numbers to the end of your rune's IDName (or just changing the
		// name in general) will force all existing runes of that type to
		// reanimate. Use this in case you make a major changes/updates to that
		// rune.
	}
	
	public void registerInscriptions() {
	}
	
}
