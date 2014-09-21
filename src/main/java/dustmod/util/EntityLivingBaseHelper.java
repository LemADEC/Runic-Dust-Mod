package dustmod.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

public class EntityLivingBaseHelper {
	
    private Method getExperiencePointsMethod; //func_70693_a
    
    private Field recentlyHit; //field_70718_bc
    
    public void init() {
		try {
			getExperiencePointsMethod = EntityLivingBase.class.getDeclaredMethod("func_70693_a", EntityPlayer.class);
			getExperiencePointsMethod.setAccessible(true);
		} catch (NoSuchMethodException e) {
		} catch (SecurityException e) {
		}
		
		try {
			recentlyHit = EntityLivingBase.class.getDeclaredField("field_70718_bc");
			recentlyHit.setAccessible(true);
		} catch (NoSuchFieldException e) {
		} catch (SecurityException e) {
		}
    }
    
    public int getExperiencePoints(EntityLivingBase entity, EntityPlayer player) {
    	if (getExperiencePointsMethod == null) {
    		return 0;
    	}
    	
    	try {
			return (Integer) getExperiencePointsMethod.invoke(entity, player);
		} catch (IllegalAccessException e) {
			return 0;
		} catch (IllegalArgumentException e) {
			return 0;
		} catch (InvocationTargetException e) {
			return 0;
		}
    }
    
    public void setRecentlyHit(EntityLivingBase entity, int value) {
    	if (recentlyHit == null) {
    		return;
    	}
    	
    	try {
			recentlyHit.set(entity, value);
		} catch (IllegalArgumentException e) {
		} catch (IllegalAccessException e) {
		}
    }

}
