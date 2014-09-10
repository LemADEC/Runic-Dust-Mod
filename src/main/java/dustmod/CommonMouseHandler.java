package dustmod;

import java.util.HashMap;
import java.util.UUID;

import com.mojang.authlib.GameProfile;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerManager;
import net.minecraft.world.GameRules;
import net.minecraftforge.common.MinecraftForge;

public class CommonMouseHandler {

	public HashMap<UUID, boolean[]> buttonsPressed;

	public CommonMouseHandler() {
		buttonsPressed = new HashMap<UUID, boolean[]>();
	}

	public void checkPlayer(EntityPlayer p) {
		buttonsPressed.put(p.getGameProfile().getId(), new boolean[3]);
	}

	public void tick() {
		for (Object playerObj : MinecraftServer.getServer().getConfigurationManager().playerEntityList) {
			if (playerObj instanceof EntityPlayer) {
				EntityPlayer player = (EntityPlayer) playerObj;
				boolean[] buttons = buttonsPressed.get(player.getGameProfile().getId());
				DustMod.inscriptionManager.tick(player, buttons, player.getCurrentArmor(2));
			}
		}
	}

	public boolean[] getButtons(UUID playerId) {
		boolean[] rtn = buttonsPressed.get(playerId);
		if (rtn == null)
			rtn = new boolean[3];
		return rtn;
	}

	public void setKey(EntityPlayer p, int key, boolean pressed) {
		UUID id = p.getGameProfile().getId();
		if (!buttonsPressed.containsKey(id))
			checkPlayer(p);
		buttonsPressed.get(id)[key] = pressed;
	}
}
