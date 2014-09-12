package dustmod.runes;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;

import dustmod.DustMod;

public class EntityRuneManager {


	protected static HashMap<Long, EntityRune> entMap;
	protected static long nextDustEntID;
	protected static Properties propGeneral;
	protected static File generalFS;

	public static Long getNextDustEntityID() {
		nextDustEntID++;
		if (propGeneral == null) {
			DustMod.logger.warn("General property file is null!");
//			System.out.println("[DustMod] General property file is null!");
		}
		propGeneral.setProperty("entDustNID", "" + nextDustEntID);

		try {
			propGeneral.store(new FileOutputStream(generalFS),
					null);
		} catch (Exception e) {
			DustMod.logger.error("Error loading world properties.", e);
		}

		return nextDustEntID - 1;
	}

	public static void registerEntityDust(EntityRune ent, long id) {
		entMap.put(id, ent);
	}

	public static EntityRune getDustAtID(long id) {
		if (entMap.containsKey(id)) {
			return entMap.get(id);
		} else {
			return null;
		}
	}
	
	public static void load(String savePath){
		entMap = new HashMap<Long, EntityRune>();
		
        propGeneral = new Properties();
        if (generalFS == null || !generalFS.exists())
        {
            try
            {
                generalFS = new File((new StringBuilder()).append(savePath).append("dustmodgeneral.dat").toString());

                if (generalFS.createNewFile())
                {
                    if (propGeneral == null)
                    {
                        propGeneral = new Properties();
                    }

                    propGeneral.store(new FileOutputStream(generalFS), null);
                }
            }
            catch (IOException ioexception)
            {
            	DustMod.logger.catching(ioexception);
            }
        }

        try
        {
            propGeneral.load(new FileInputStream(generalFS));
            entMap = new HashMap<Long, EntityRune>();
            nextDustEntID = Long.valueOf(propGeneral.getProperty("entDustNID"));
        }
        catch (IOException ex)
        {
        	DustMod.logger.catching(ex);
        }
        catch (NumberFormatException ex)
        {
            nextDustEntID = 0;
            propGeneral.setProperty("entDustNID", "" + nextDustEntID);
        }
	}
}
