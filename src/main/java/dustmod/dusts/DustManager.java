package dustmod.dusts;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.common.registry.GameRegistry;
import dustmod.DustMod;
import dustmod.items.ItemInk;

public class DustManager {
	
	public static Dust[] dusts = new Dust[1000];
	public static Dust[] remoteDusts = new Dust[1000];
	
	/**
	 * Register a new dust type to the system.
	 * You'll have to manually set the recipe/method of getting the dust.
	 * The item will just be DustMod.idust with the damage value equal to this passed value parameter. 
	 * 
	 * @param value	Worth of the dust. Bigger number means more worth (999 max)
	 * @param primaryColor	Color of the base of the item dust
	 * @param secondaryColor	Color of the sparkles on the item dust 
	 * @param floorColor	Color of the dust when placed on the ground.
	 * @param lightLevel lightLevel of the dust.
	 */
	public static void registerDust(int value, String name, String idName, int primaryColor, int secondaryColor, int floorColor, int lightLevel) {

		if (dusts[value] != null) {
			throw new IllegalArgumentException("[DustMod] Dust value already taken! " + value);
		}

		Dust dust = new Dust(idName, name, primaryColor, secondaryColor, floorColor, lightLevel);

		dusts[value] = remoteDusts[value] = dust;

		GameRegistry.addShapelessRecipe(ItemInk.getInk(value), new ItemStack(Items.potionitem, 1, 0), new ItemStack(DustMod.itemDust, 1, value), Items.ghast_tear );
		GameRegistry.addShapelessRecipe(ItemInk.getInk(value), new ItemStack(Items.potionitem, 1, 0), new ItemStack(DustMod.pouch, 1, value * 2 + 1), Items.ghast_tear );

		ItemStack craft = new ItemStack(DustMod.pouch, 1, value * 2);
		GameRegistry.addRecipe(craft, " s ", "ldl", " l ", 's', new ItemStack(Items.string, 1), 'd', new ItemStack(DustMod.itemDust, 1, value), 'l', new ItemStack(Items.leather, 1) );
		GameRegistry.addShapelessRecipe(new ItemStack(DustMod.itemDust, 1, value), new ItemStack(DustMod.pouch, 1, value * 2 + 1));
	}

	public static void registerRemoteDust(int value, Dust dust) {
		if (dusts[value] != null) {
			throw new IllegalArgumentException("[DustMod] Remote error! Dust value already taken! " + value);
		}
		
		remoteDusts[value] = dust;

		GameRegistry.addShapelessRecipe(ItemInk.getInk(value), new ItemStack(Items.potionitem, 1, 0), new ItemStack(DustMod.itemDust, 1, value), Items.ghast_tear );
		GameRegistry.addShapelessRecipe(ItemInk.getInk(value), new ItemStack(Items.potionitem, 1, 0), new ItemStack(DustMod.pouch, 1, value * 2 + 1), Items.ghast_tear );

		ItemStack craft = new ItemStack(DustMod.pouch, 1, value * 2);
		GameRegistry.addRecipe(craft, " s ", "ldl", " l ", 's', new ItemStack(Items.string, 1), 'd', new ItemStack(DustMod.itemDust, 1, value), 'l', new ItemStack(Items.leather, 1) );
		GameRegistry.addShapelessRecipe(new ItemStack(DustMod.itemDust, 1, value), new ItemStack(DustMod.pouch, 1, value * 2 + 1));
	}
	
	public static boolean hasDust(int value) {
		
		return value >= 0 && value < remoteDusts.length && remoteDusts[value] != null;
	}
	
	public static String getId(int value) {
		if (value <= 0)
			return null;
		if (value >= remoteDusts.length || remoteDusts[value] == null)
			return null;
		return remoteDusts[value].getId();
	}
	
	public static String getName(int value) {
		if (value <= 0)
			return null;
		if (value >= remoteDusts.length || remoteDusts[value] == null)
			return null;
		return remoteDusts[value].getName();
	}
	
	public static int getPrimaryColor(int value) {
		if (value <= 0)
			return 0x8F25A2;
		if (value >= remoteDusts.length || remoteDusts[value] == null)
			return 0;
		return remoteDusts[value].getPrimaryColor();
	}

	public static int getSecondaryColor(int value) {
		if (value <= 0)
			return 0xDB73ED1;
		if (value >= remoteDusts.length || remoteDusts[value] == null)
			return 0;
		return remoteDusts[value].getSecondaryColor();
	}

	public static int getFloorColor(int value) {
		if (value <= 0)
			return 0xCE00E0;
		if (value >= remoteDusts.length || remoteDusts[value] == null)
			return 0;
		return remoteDusts[value].getFloorColor();
	}

	public static int[] getFloorColorRGB(int value) {
		if (value <= 0)
			return new int[] { 206, 0, 224 }; // 00CE00E0 variable

		if (value >= remoteDusts.length || remoteDusts[value] == null)
			return new int[] { 0, 0, 0 };

		int[] rtn = new int[3];

		int col = remoteDusts[value].getFloorColor();

		rtn[0] = (col & 0xFF0000) >> 16;
		rtn[1] = (col & 0xFF00) >> 8;
		rtn[2] = (col & 0xFF);

		return rtn;
	}
	
	public static float[] getFloorRenderColor(int value) {
		if (value <= 0)
			return new float[] { 206 / 255.0F, 0, 224 / 255.0F }; // 00CE00E0 variable

		if (value >= remoteDusts.length || remoteDusts[value] == null)
			return new float[] { 0, 0, 0 };
		
		return remoteDusts[value].getFloorRenderColors();
	}

	public static void reset() {
		DustMod.logger.debug("Reseting remote dusts.");

		remoteDusts = new Dust[1000];
	}

	public static void registerDefaultDusts() {
		registerDust(1, "(old, place or craft to update)", "plantdustold", 0x629B26, 0x8AD041, 0xC2E300, 0);
		registerDust(100, "Plant", "plantdust", 0x629B26, 0x8AD041, 0xC2E300, 0); // Migrating to space out
		registerDust(2, "(old, place or craft to update)", "gundustold", 0x696969, 0x979797, 0x666464, 0);
		registerDust(200, "Gunpowder", "gundust", 0x696969, 0x979797, 0x666464, 0); // Migrating to space out
		registerDust(3, "(old, place or craft to update)", "lapisdustold", 0x345EC3, 0x5A82E2, 0x0087FF, 0);
		registerDust(300, "Lapis", "lapisdust", 0x345EC3, 0x5A82E2, 0x0087FF, 0); // Migrating to space out
		registerDust(4, "(old, place or craft to update)", "blazedustold", 0xEA8A00, 0xFFFE31, 0xFF6E1E, 0);
		registerDust(400, "Blaze", "blazedust", 0xEA8A00, 0xFFFE31, 0xFF6E1E, 0); // Migrating to space out
	}
}
