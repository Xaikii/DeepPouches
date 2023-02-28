package net.perpetualeve.deeppouches.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;

public interface DPPacket {

	void write(FriendlyByteBuf buf);
	void read(FriendlyByteBuf buf);
	void handlePacket(Player player);
}
