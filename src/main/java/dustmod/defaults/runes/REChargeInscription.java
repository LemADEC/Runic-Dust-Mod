package dustmod.defaults.runes;

import java.util.List;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import dustmod.DustMod;
import dustmod.inscriptions.InscriptionEvent;
import dustmod.inscriptions.InscriptionManager;
import dustmod.runes.RuneEvent;
import dustmod.runes.EntityRune;

public class REChargeInscription extends RuneEvent {

	
	@Override
    public void initGraphics(EntityRune e){
    	super.initGraphics(e);

        e.setRenderBeam(true);
        e.setRenderStar(true);
        e.setColorStarOuter(0,0,255);
        e.setColorBeam(0,0,255);
    	
    }
	
	@Override
	protected void onInit(EntityRune e) {
		super.onInit(e);
        e.setRenderBeam(true);
        e.setRenderStar(true);
        e.setColorStarOuter(0,0,255);
        e.setColorBeam(0,0,255);
	}
	
	@Override
	protected void onTick(EntityRune entityRune) {
		super.onTick(entityRune);
        entityRune.setStarScale(entityRune.getStarScale() + 0.005F);
        if (entityRune.ticksExisted > 20)
        {
        	ItemStack inscription;
        	List<EntityItem> items = this.getItems(entityRune, 1.0D);
        	
        	for(EntityItem ei:items){
        		ItemStack i = ei.getEntityItem();
        		if(i.getItem() == DustMod.getWornInscription() && i.getItemDamage() != 0){ //If the inscription is charged, it is ignored
        			InscriptionEvent evt = InscriptionManager.getEvent(i);
//        			DustMod.log("Charge ins found", evt);
        			if(evt != null){
        				boolean sucess = evt.callSacrifice(this,entityRune, i);
//        				DustMod.log("Charging:", sucess);
        				ei.setEntityItemStack(i);
        				if(sucess) {
        					entityRune.fade();
        					return;
        				}
        			}
        		}
        	}
        	entityRune.fizzle();
        	return;
        }
	}
}
