package dustmod.items;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import dustmod.DustMod;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.Item;


public class DustModItem extends Item {

	public DustModItem() {
		super();
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister iconRegister) {
		this.itemIcon = iconRegister.registerIcon(DustMod.spritePath + this.getUnlocalizedName().replace("item.", ""));
	}
}
