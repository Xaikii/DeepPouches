package net.perpetualeve.deeppouches.network;

import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkEvent.Context;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.PacketDistributor.TargetPoint;
import net.minecraftforge.network.simple.SimpleChannel;
import net.perpetualeve.deeppouches.DeepPouches;

public class DPPacketManager {

	public static final DPPacketManager MANAGER = new DPPacketManager();

	public SimpleChannel channel;
	final String version = "1.0.0";

	public void init() {
		channel = NetworkRegistry.newSimpleChannel(new ResourceLocation(DeepPouches.MODID), () -> version,
				version::equals, version::equals);
		registerPacket(1, DPConfigSyncPacket.class, DPConfigSyncPacket::new);
	}

	public <T extends DPPacket> void registerPacket(int index, Class<T> packet, Supplier<T> creator) {
		channel.registerMessage(index, packet, this::writePacket, K -> readPacket(K, creator), this::handlePacket);
	}

	protected void writePacket(DPPacket packet, FriendlyByteBuf buffer) {
		try {
			packet.write(buffer);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected <T extends DPPacket> T readPacket(FriendlyByteBuf buffer, Supplier<T> values) {
		try {
			T packet = values.get();
			packet.read(buffer);
			return packet;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	protected void handlePacket(DPPacket packet, Supplier<NetworkEvent.Context> provider) {
		try {
			Context context = provider.get();
			Player player = getPlayer(context);
			context.enqueueWork(() -> packet.handlePacket(player));
			context.setPacketHandled(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected Player getPlayer(Context cont) {
		Player entity = cont.getSender();
		return entity != null ? entity : getClientPlayer();
	}

	@OnlyIn(Dist.CLIENT)
	protected Player getClientPlayer() {
		return Minecraft.getInstance().player;
	}

	public void sendToServer(DPPacket packet) {
		channel.send(PacketDistributor.SERVER.noArg(), packet);
	}

	public void sendToPlayer(DPPacket packet, Player player) {
		if (!(player instanceof ServerPlayer)) {
			throw new RuntimeException("Sending a Packet to a Player from client is not allowed");
		}
		channel.send(PacketDistributor.PLAYER.with(() -> ((ServerPlayer) player)), packet);
	}

	public void sendToAllPlayers(DPPacket packet) {
		channel.send(PacketDistributor.ALL.noArg(), packet);
	}

	public void sendToAllDim(Level level, DPPacket packet) {
		channel.send(PacketDistributor.DIMENSION.with(() -> level.dimension()), packet);
	}

	public void sendToAllChunkWatchers(LevelChunk chunk, DPPacket packet) {
		channel.send(PacketDistributor.TRACKING_CHUNK.with(() -> chunk), packet);
	}

	public void sendToAllEntityWatchers(Entity entity, DPPacket packet) {
		channel.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> entity), packet);
	}

	public void sendToNearby(Level level, BlockPos pos, int radius, DPPacket packet) {
		sendToNearby(new TargetPoint(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5, radius, level.dimension()),
				packet);
	}

	public void sendToNearby(TargetPoint point, DPPacket packet) {
		channel.send(PacketDistributor.NEAR.with(() -> point), packet);
	}
}
