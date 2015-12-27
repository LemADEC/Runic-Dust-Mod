package dustmod.inscriptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.config.Configuration;
import cpw.mods.fml.common.registry.LanguageRegistry;
import dustmod.DustMod;
import dustmod.items.ItemInscription;

public class InscriptionManager {

	public static ArrayList<InscriptionEvent> events = new ArrayList<InscriptionEvent>();
	public static ArrayList<InscriptionEvent> eventsRemote = new ArrayList<InscriptionEvent>();

	public static HashMap<UUID, ItemStack> lastArmor = new HashMap<UUID, ItemStack>();

    public static Configuration config;
	public static void registerInscriptionEvent(InscriptionEvent evt) {
		if (getEvent(evt.id) != null) {
			throw new IllegalArgumentException("Inscription ID already taken! "
					+ evt.idName);
		}
		events.add(evt);
		
		LanguageRegistry.instance().addStringLocalization("item.insc." + evt.idName + ".name", "en_US", evt.properName);
		
		DustMod.logger.debug("Registering inscription " + evt.idName);
		if(config == null){
            config = new Configuration(DustMod.suggestedConfig);
            config.load();
            config.addCustomCategoryComment("INSCRIPTIONS", "Allow specific inscriptions to be used. Options: ALL, NONE, OPS");
            config.addCustomCategoryComment("RUNES", "Allow specific runes to be used. Options: ALL, NONE, OPS");
        }
            if (!evt.secret)
            {
            	String permission = config.get( "INSCRIPTIONS", "Allow-" + evt.getInscriptionName().replace(' ', '_'), evt.permission).getString().toUpperCase();
            	
            	if(permission.equals("ALL") || permission.equals("NONE") || permission.equals("OPS")){
            		evt.permission = permission;
            	}else
            		evt.permission = "NONE";
        		if(!evt.permission.equals("ALL")){
        			DustMod.logger.debug("Inscription permission for " + evt.idName + " set to " + evt.permission);
        		}
            }

        config.save();
	}

	public static void registerRemoteInscriptionEvent(InscriptionEvent evt) {
		eventsRemote.add(evt);
		DustMod.logger.info("Registering remote inscription {} as {} ", evt.idName, evt.properName);
		
		DustMod.proxy.checkInscriptionPage(evt);
		evt.isRemote = true;
	}

	public static void resetRemoteInscriptions() {
		DustMod.logger.debug("Reseting remote inscriptions.");
		
		eventsRemote = new ArrayList<InscriptionEvent>();
	}

	public static void tickInscription(EntityPlayer p, boolean[] buttons, ItemStack itemStack) {
		if(p.worldObj.isRemote) return;
		
		if(itemStack == null || itemStack.getItemDamage() == ItemInscription.max){
			return;
		}
		InscriptionEvent event = getEvent(p);
		ItemStack last = lastArmor.get(p.getGameProfile().getId());
		boolean equal = (last != null && itemStack.getItem() == last.getItem() && itemStack.getTagCompound().equals(last.getTagCompound()));
		if(event != null && equal) {
			event.onUpdate(p, itemStack, buttons);
		}
	}
	
	public static void tick(EntityPlayer entityPlayer, boolean[] buttons, ItemStack itemStack){
		if(itemStack == null || itemStack.getItemDamage() == ItemInscription.max){
			return;
		}
		InscriptionEvent event = getEvent(itemStack);
		ItemStack last = lastArmor.get(entityPlayer.getGameProfile().getId());
		boolean equal = (last != null && itemStack.getItem() == last.getItem() && itemStack.hasTagCompound() && itemStack.getTagCompound().equals(last.getTagCompound()));

		if (getEvent(last) != null && !equal) {
			getEvent(last).onRemoval(entityPlayer, last);
		}
		
		if (event != null) {
			if (!equal) {
				event.onEquip(entityPlayer, itemStack);
			}
		}
		
		lastArmor.put(entityPlayer.getGameProfile().getId(), itemStack);
	}

	public static InscriptionEvent getEvent(EntityPlayer p) {
		ItemStack item = p.inventory.getStackInSlot(38);
		
		if (item == null || item.getItem() != DustMod.getWornInscription())
			return null;
		
		if (item.getItemDamage() >= item.getMaxDamage() - 1)
			return null;
		
		return getEvent(item);
	}

	/**
	 * Get the event from this ItemWornInscription. If it is not
	 * already identified, it will be identified by the design and 
	 * then saved in the tag.
	 * @param itemStack
	 * @return
	 */
	public static InscriptionEvent getEvent(ItemStack itemStack) {
		if (itemStack == null || itemStack.getItem() != DustMod.getWornInscription() || !itemStack.hasTagCompound()) {
			return null;
		}
		
		NBTTagCompound tag = itemStack.getTagCompound();
		if (tag.hasKey("eventID")) {
			return getEvent(tag.getInteger("eventID"));
		} else {
			int[] ink;
			ink = ItemInscription.getDesign(itemStack);
			if (ink == null || ink.length != 16 * 16) {
				DustMod.logger.debug("Invalid incription: " + itemStack);
				return null;
			}
			InscriptionEvent eventFromItem = null;
			for (InscriptionEvent eventFromRegistry : events) {
				if (eventFromItem != null) {
					break;
				}
				int[] design = eventFromRegistry.referenceDesign;
				for (int itemX = 0; itemX < 16 - eventFromRegistry.width && eventFromItem == null; itemX++) {
					for (int itemY = 0; itemY < 16 - eventFromRegistry.height; itemY++) {
						boolean found = true;
						for (int registryX = 0; registryX < eventFromRegistry.width && found; registryX++) {
							for (int registryY = 0; registryY < eventFromRegistry.height; registryY++) {
								if (design[registryX + registryY * eventFromRegistry.width] != ink[itemX + itemY * 16]) {
									found = false;
									break;
								}
							}
						}
						if (found) {
							eventFromItem = eventFromRegistry;
							break;
						}
					}
				}
			}
			
			if (eventFromItem != null) {
				DustMod.logger.debug("Inscription Identified: " + eventFromItem.idName);
				tag = new NBTTagCompound();
				itemStack.setTagCompound(tag);
				tag.setInteger("eventID", eventFromItem.id);
				return eventFromItem;
			}
		}
		return null;
	}
	
	/**
	 * Rewrites/sets the inscription type of this inscription item 
	 * @param item
	 * @param inscription
	 */
	public static void setEvent(ItemStack item, String inscription){
		InscriptionEvent evt = null;
		for(InscriptionEvent e:events){
			if(e != null && e.idName.equals(inscription)){
				evt = e;
				break;
			}
		}
		if(evt != null){
			if(!item.hasTagCompound()) item.setTagCompound(new NBTTagCompound());
			NBTTagCompound tag = item.getTagCompound();
			tag.setInteger("eventID", evt.id);
		}
	}

	public static InscriptionEvent getEventInOrder(int ind) {
		return getEvents().get(ind);
	}

	public static InscriptionEvent getEvent(int id) {
		for (InscriptionEvent evt : events) {
			if (evt.id == id)
				return evt;
		}
		return null;
	}

	public static ArrayList<InscriptionEvent> getEvents() {
		if (DustMod.proxy.isClient())
			return eventsRemote;
		return events;
	}

	public static int getArmor(EntityPlayer player, ItemStack item) {
		InscriptionEvent event = getEvent(item);
		if (event == null)
			return 0;
		return event.getArmorPoints(player, item);
	}

	public static void onDamage(EntityLivingBase entity, ItemStack item,
			DamageSource source, int damage) {
		if(entity.worldObj.isRemote) return;

		InscriptionEvent event = getEvent(item);
		if (event == null)
			return;
		event.onDamage(entity, item, source, damage);
	}

	public static int getPreventedDamage(EntityLivingBase entity, ItemStack item, DamageSource source, int damage) {
		if(entity.worldObj.isRemote) return damage;

		InscriptionEvent event = getEvent(item);
		if (event == null)
			return damage;
		return event.getPreventedDamage(entity, item, source, damage);
	}

	public static void onEquip(EntityPlayer player, ItemStack item) {
		if(player.worldObj.isRemote) return;
		InscriptionEvent event = getEvent(item);
		if (event == null)
			return;
		event.onEquip(player, item);
	}

	public static void onRemoval(EntityPlayer player, ItemStack item) {
		if(player.worldObj.isRemote) return;
		InscriptionEvent event = getEvent(item);
		if (event == null)
			return;
		event.onRemoval(player, item);
	}

	public static void onCreate(EntityPlayer player, ItemStack item) {
		if(player.worldObj.isRemote) return;
		InscriptionEvent event = getEvent(item);
		if (event == null)
			return;
		event.onCreate(player, item);
	}

	public static ItemStack onItemPickup(EntityPlayer player, ItemStack item) {
		if(player.worldObj.isRemote) return item;

		InscriptionEvent event = getEvent(player);
		ItemStack insc = player.inventory.getStackInSlot(38);
		if (event == null)
			return item;
		return event.onItemPickup(player, insc, item);
	}

	public static ItemStack onItemRightClick(EntityPlayer player, ItemStack item) {
		if(player.worldObj.isRemote) return item;
		InscriptionEvent evt = getEvent(item);
		if(evt != null) return evt.onItemRightClick(player, item);
		return item;
	}
	

	public static void registerDefaultInscriptions() {
		// None yet! See the test pack.

	}

	public static boolean isEmpty() {
		return eventsRemote.isEmpty();
	}

}
