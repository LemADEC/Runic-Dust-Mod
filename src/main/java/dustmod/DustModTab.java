package dustmod;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class DustModTab extends CreativeTabs {

	public DustModTab() {
		super("dustmod");
	}
	
	@Override
	public Item getTabIconItem() {
		return DustMod.tome;
	}
	


    @SideOnly(Side.CLIENT)
    public String getTabLabel()
    {
        return "Runic Dust Mod";
    }

    @SideOnly(Side.CLIENT)

    /**
     * Gets the translated Label.
     */
    public String getTranslatedTabLabel()
    {
        return getTabLabel();
    }

}
