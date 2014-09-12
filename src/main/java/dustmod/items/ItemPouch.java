package dustmod.items;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import dustmod.DustMod;
import dustmod.dusts.DustItemManager;

public class ItemPouch extends DustModItem {

	public static final int max = 6400;

    private Block block;
    private ItemStack container = null;
    
    private IIcon bagIIcon;
    private IIcon mainIIcon;
    private IIcon subIIcon;
	public ItemPouch(Block block) {
		super();
        this.block = block;
		this.hasSubtypes = true;
		this.setMaxStackSize(1);
	}

	@Override
    public boolean onItemUse(ItemStack item, EntityPlayer p, World world, int i, int j, int k, int face, float x, float y, float z)
    {
		if(!world.canMineBlock(p, i, j, k)) return false;
		
        Block var11 = world.getBlock(i, j, k);

        if (var11 == Blocks.snow)
        {
            face = 1;
        }
        else if (var11 != Blocks.vine && var11 != Blocks.tallgrass && var11 != Blocks.deadbush)
        {
            if (face == 0)
            {
                --j;
            }

            if (face == 1)
            {
                ++j;
            }

            if (face == 2)
            {
                --k;
            }

            if (face == 3)
            {
                ++k;
            }

            if (face == 4)
            {
                --i;
            }

            if (face == 5)
            {
                ++i;
            }
        }

        if (!p.canPlayerEdit(i, j, k, 7, item))
        {
            return false;
        }
        else if (getDustAmount(item) <= 0)
        {
            return false;
        }
        else
        {
            if (world.canPlaceEntityOnSide(block, i, j, k, false, face, (Entity)null, item))
            {
                int var13 = block.onBlockPlaced(world, i, j, k, face, x, y, z, 0);

                if (world.setBlock(i, j, k, block, 0, 3))
                {
                    if (world.getBlock(i, j, k) == block)
                    {
                    	block.onBlockPlacedBy(world, i, j, k, p, item);
                    	block.onPostBlockPlaced(world, i, j, k, var13);
                    }
                    DustMod.dust.onBlockActivated(world, i, j, k, p, face, x, y, z);

                    world.playSoundEffect((double)((float)i + 0.5F), (double)((float)j + 0.5F), (double)((float)k + 0.5F), block.stepSound.getStepResourcePath(), (block.stepSound.getVolume() + 1.0F) / 6.0F, block.stepSound.getPitch() * 0.99F);
//                    if(!p.capabilities.isCreativeMode)subtractDust(item,1);
                }
            }

            return true;
        }
    }
    
	
    @Override
    public String getUnlocalizedName(ItemStack itemstack)
    {
		int dust = getValue(itemstack);
		return DustItemManager.hasDust(dust) ? "pouch." + DustItemManager.getId(dust) : "pouchblank";
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack item,
    		EntityPlayer player, List list, boolean flag) {
    	super.addInformation(item, player, list, flag);
    	int amt = ItemPouch.getDustAmount(item);
    	if(amt != 0) list.add("Contains " + amt + " dust");
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    /**
     * returns a list of items with the same ID, but different meta (eg: dye returns 16 items)
     */
    public void getSubItems(Item par1, CreativeTabs par2CreativeTabs, List par3List)
    {
        for (int i = 5; i < 1000; ++i) //i > 4 for migration from old system
        {
        	if(DustItemManager.hasDust(i)){
                par3List.add(new ItemStack(par1, 1, i*2));
        	}
        }
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public int getColorFromItemStack(ItemStack stack, int pass) {
    	int meta = stack.getItemDamage();
    	meta = meta >> 1;
    	if(pass == 0) return super.getColorFromItemStack(stack, pass);
    	return pass == 1 ? DustItemManager.getPrimaryColor(meta) : DustItemManager.getSecondaryColor(meta);
    }


    @SideOnly(Side.CLIENT)
    public boolean requiresMultipleRenderPasses()
    {
        return true;
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamageForRenderPass(int par1, int rend) {
    	switch(rend){
    	case 0: return bagIIcon;
    	case 1: return mainIIcon;
    	case 2: return subIIcon;
    	}
        return subIIcon;
    }
    

    public void setContainerItemstack(ItemStack item){
    	container = item;
    }

    public boolean doesContainerItemLeaveCraftingGrid(ItemStack par1ItemStack)
    {
        return false;
    }
    
    @Override
    public Item getContainerItem() {
    	return DustMod.pouch;
    }
    @Override
    public boolean hasContainerItem() {
    	return true;
    }
    
    @Override
    public ItemStack getContainerItem(ItemStack itemStack) {
    	// TODO Auto-generated method stub
    	return container;
    }
    
    @SideOnly(Side.CLIENT)

    /**
     * allows items to add custom lines of information to the mouseover description
     */
    public void addInformation(ItemStack item, List info)
    {
        if (item.hasTagCompound())
        {
//            NBTTagCompound tag = item.getTagCompound();
//            NBTTagString author = (NBTTagString)tag.getTag("author");
            int amt = getDustAmount(item);
//            if (author != null)
//            {
                info.add("\u00a77 Holding " + amt + " piles.");//String.format(StatCollector.translateToLocalFormatted("book.byAuthor", new Object[] {author.data}), new Object[0]));
//            }
        }
    }
    
    
    public static int getDustAmount(ItemStack pouch){
    	if(pouch.getItem() == DustMod.idust){
    		return pouch.stackSize;
    	}
    	if(pouch.getItem() != DustMod.pouch) return -1;
    	
    	if(pouch.getItemDamage() %2 == 0) return 0;
    	
    	if(!pouch.hasTagCompound()){
    		pouch.setTagCompound(new NBTTagCompound());
    	}
    	NBTTagCompound tag = pouch.getTagCompound();
    	return tag.getInteger("dustamount");
    }
    
    public static boolean subtractDust(ItemStack pouch, int sub){
    	if(pouch.getItem() == DustMod.idust){
    		if(pouch.stackSize >= sub){
    			pouch.stackSize -= sub;
    			return true;
    		} else
    			return false;
    	}
    	if(pouch.getItem() != DustMod.pouch) return false;
    	
    	if(!pouch.hasTagCompound()){
    		pouch.setTagCompound(new NBTTagCompound());
    	}
    	NBTTagCompound tag = pouch.getTagCompound();

    	int amt = tag.getInteger("dustamount");
    	if(amt >= sub){
    		tag.setInteger("dustamount", amt-sub);
    		int dust = getValue(pouch);
    		if(amt-sub == 0) {
    			pouch.setItemDamage(dust*2);
    		}else{
    			pouch.setItemDamage(dust*2+1);
    		}
    		return true;
    	} else 
    		return false;
    }

    /*
     * @return amount remaining if attempting to add more than pouch can contain
     */
    public static int addDust(ItemStack pouch, int add){
    	if(pouch.getItem() == DustMod.idust){
			pouch.stackSize += add;
			return 0;
    	}
    	if(pouch.getItem() != DustMod.pouch) return -1;
    	
    	int rtn = 0;
    	
    	if(!pouch.hasTagCompound()){
    		pouch.setTagCompound(new NBTTagCompound());
    	}
    	NBTTagCompound tag = pouch.getTagCompound();

    	int amt = tag.getInteger("dustamount");
    	if(amt + add > max){
    		rtn = add - (max - amt);
    		add -= rtn;
    	}
		tag.setInteger("dustamount", amt+add);
		int dust = getValue(pouch);
		if(amt + add == 0) {
			pouch.setItemDamage(dust*2);
		}else{
			pouch.setItemDamage(dust*2+1);
		}
		return rtn;
    }
    
    public static void setAmount(ItemStack pouch, int amt){
    	if(pouch.getItem() == DustMod.idust){
			pouch.stackSize -= amt;
    	}
    	if(pouch.getItem() != DustMod.pouch) return;
    	
    	if(!pouch.hasTagCompound()){
    		pouch.setTagCompound(new NBTTagCompound());
    	}
    	NBTTagCompound tag = pouch.getTagCompound();
		tag.setInteger("dustamount", amt);
		int dust = getValue(pouch);
		if(amt > 0){
			pouch.setItemDamage(dust*2+1);
		}else{
			pouch.setItemDamage(dust*2);
		}
    }
    
    public static int getValue(ItemStack pouch){
    	int damage = pouch.getItemDamage();
    	return damage >> 1;
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister IIconRegister) {
    	this.bagIIcon = IIconRegister.registerIcon(DustMod.spritePath + "dustPouch_back");
    	this.mainIIcon = IIconRegister.registerIcon(DustMod.spritePath + "dustPouch_main");
    	this.subIIcon = IIconRegister.registerIcon(DustMod.spritePath + "dustPouch_sub");
    }
}
