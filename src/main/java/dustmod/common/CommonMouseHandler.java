package dustmod.common;

import java.util.HashMap;
import java.util.UUID;

import com.mojang.authlib.GameProfile;

import dustmod.DustMod;
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
		for (Object objectPlayer : MinecraftServer.getServer().getConfigurationManager().playerEntityList) {
			if (objectPlayer instanceof EntityPlayer) {
				EntityPlayer entityPlayer = (EntityPlayer) objectPlayer;
				boolean[] buttons = buttonsPressed.get(entityPlayer.getGameProfile().getId());
				DustMod.inscriptionManager.tick(entityPlayer, buttons, entityPlayer.getCurrentArmor(2));
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
