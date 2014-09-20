package dustmod.items;

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
import dustmod.DustMod;
import dustmod.dusts.DustManager;

public class ItemInk extends DustModItem {
	
	public static final int MAX_AMOUNT = 32;
	
	private IIcon bottle;
	private IIcon[] main;
	private IIcon[] sub;
	
	public ItemInk()
    {
        super();
        setHasSubtypes(true);
        
        this.setMaxStackSize(1);
        this.setCreativeTab(DustMod.creativeTab);
    }
	

	@SuppressWarnings( { "unchecked", "rawtypes" } )
	@SideOnly(Side.CLIENT)
    @Override
    public void getSubItems(Item item, CreativeTabs tabs, List list) {
		
        for (int i = 5; i < 1000; ++i) //i > 4 for migration from old system
        {
        	if(DustManager.hasDust(i)){
                list.add(getInk(i));
        	}
        }
    }

    @Override
    public String getUnlocalizedName(ItemStack itemstack)
    {
    	int dustID = getDustID(itemstack);
    	return DustManager.hasDust(dustID) ? "tile.ink." + DustManager.getId(dustID) : "tile.ink";
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getColorFromItemStack(ItemStack stack, int pass) {
    	int meta = stack.getItemDamage();
    	int id = getDustID(meta);
    	if(pass == 0) return 16777215;
    	return pass == 1 ? DustManager.getPrimaryColor(id) : DustManager.getSecondaryColor(id);
    }


    @SideOnly(Side.CLIENT)
    public boolean requiresMultipleRenderPasses()
    {
        return true;
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIconFromDamageForRenderPass(int meta, int rend) {
    	if(rend == 0) return bottle;
    	
    	int off = (MAX_AMOUNT-1)-meta%MAX_AMOUNT;
    	
    	off /= (MAX_AMOUNT/8);
    	
    	if(rend == 1){
    		return main[off];
    	}else
    		return sub[off];
    }
    
    public static ItemStack getInk(int dustID){
    	return new ItemStack(DustMod.ink, 1, dustID*MAX_AMOUNT + MAX_AMOUNT-1);
    }
    
    public static int getDustID(ItemStack item){
    	return getDustID(item.getItemDamage());
    }
    public static int getDustID(int meta){
    	return (meta - (meta%MAX_AMOUNT)) / MAX_AMOUNT; 
    }
    
    public static boolean reduce(EntityPlayer p, ItemStack item, int amt){
    	if(p.capabilities.isCreativeMode) return true;
    	int fill = item.getItemDamage()%MAX_AMOUNT;
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
