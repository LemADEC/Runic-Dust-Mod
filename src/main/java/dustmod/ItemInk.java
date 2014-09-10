package dustmod;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemInk extends DustModItem {
	
	public static final int maxAmount = 32;
	
	private IIcon bottle;
	private IIcon[] main;
	private IIcon[] sub;
	
	public ItemInk()
    {
        super();
        setHasSubtypes(true);
        
        //[non-forge]
//        plantTex = ModLoader.addOverride("/gui/items.png", mod_DustMod.path + "/plantdust.png");
//        gunTex = ModLoader.addOverride("/gui/items.png", mod_DustMod.path + "/gundust.png");
//        lapisTex = ModLoader.addOverride("/gui/items.png", mod_DustMod.path + "/lapisdust.png");
//        blazeTex = ModLoader.addOverride("/gui/items.png", mod_DustMod.path + "/blazedust.png");
        
        //[forge]
        this.setMaxStackSize(1);
        this.setCreativeTab(DustMod.creativeTab);
    }
	

    

    @SideOnly(Side.CLIENT)

    /**
     * returns a list of items with the same ID, but different meta (eg: dye returns 16 items)
     */
    public void getSubItems(int par1, CreativeTabs par2CreativeTabs, List par3List)
    {
        for (int i = 5; i < 1000; ++i) //i > 4 for migration from old system
        {
        	if(DustItemManager.hasDust(i)){
                par3List.add(getInk(i));
        	}
        }
    }
    

    @Override
    public String getUnlocalizedName(ItemStack itemstack)
    {
    	int dustID = getDustID(itemstack);
    	return DustItemManager.hasDust(dustID) ? "tile.ink." + DustItemManager.getId(dustID) : "tile.ink";
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getColorFromItemStack(ItemStack stack, int pass) {
    	int meta = stack.getItemDamage();
    	int id = getDustID(meta);
    	if(pass == 0) return 16777215;
    	return pass == 1 ? DustItemManager.getPrimaryColor(id) : DustItemManager.getSecondaryColor(id);
    }


    @SideOnly(Side.CLIENT)
    public boolean requiresMultipleRenderPasses()
    {
        return true;
    }

    @SideOnly(Side.CLIENT)

    /**
     * Gets an IIcon index based on an item's damage value and the given render pass
     */
    public IIcon getIIconFromDamageForRenderPass(int meta, int rend)
    {
    	if(rend == 0) return bottle;
    	
    	int off = (maxAmount-1)-meta%maxAmount;
    	
    	off /= (maxAmount/8);
    	
    	if(rend == 1){
    		return main[off];
    	}else
    		return sub[off];
    }
    
    public static ItemStack getInk(int dustID){
    	return new ItemStack(DustMod.ink, 1, dustID*maxAmount + maxAmount-1);
    }
    
    public static int getDustID(ItemStack item){
    	return getDustID(item.getItemDamage());
    }
    public static int getDustID(int meta){
    	return (meta - (meta%maxAmount)) / maxAmount; 
    }
    
    public static boolean reduce(EntityPlayer p, ItemStack item, int amt){
    	if(p.capabilities.isCreativeMode) return true;
    	int fill = item.getItemDamage()%maxAmount;
    	int level = item.getItemDamage() - fill;
    	if(fill < amt) return false;
    	fill -= amt;
    	if(fill == 0) {
    		item.func_150996_a(Items.glass_bottle);
    		item.setItemDamage(0);
    	}else
    		item.setItemDamage(level + fill);
    	return true;
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister IIconRegister) {
    	this.bottle = IIconRegister.registerIcon(DustMod.spritePath + "inkBottle");
    	main = new IIcon[8];
    	sub = new IIcon[8];
    	for(int i = 0; i < main.length; i++){
    		main[i] = IIconRegister.registerIcon(DustMod.spritePath + "ink_main_" + i);
    		sub[i] = IIconRegister.registerIcon(DustMod.spritePath + "ink_sub_" + i); 
    	}
    }

}
