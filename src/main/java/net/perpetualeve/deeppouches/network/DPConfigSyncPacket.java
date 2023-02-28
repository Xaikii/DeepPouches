package net.perpetualeve.deeppouches.network;

import java.util.Set;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;
import net.perpetualeve.deeppouches.DeepPouches;

public class DPConfigSyncPacket implements DPPacket {

	int alpha_slots;
	int beta_slots;
	Set<Item> alpha_items;
	Set<Item> beta_items;
	
	public DPConfigSyncPacket(int alpha_slots, int beta_slots, Set<Item> alpha_items, Set<Item> beta_items) {
		super();
		this.alpha_slots = alpha_slots;
		this.beta_slots = beta_slots;
		this.alpha_items = alpha_items;
		this.beta_items = beta_items;
	}
	
	public DPConfigSyncPacket() {
		
	}

	@Override
	public void write(FriendlyByteBuf buf) {
		buf.writeInt(alpha_slots);
		buf.writeInt(beta_slots);
		buf.writeInt(alpha_items.size());
		for(Item item : alpha_items) {
			buf.writeRegistryIdUnsafe(ForgeRegistries.ITEMS, item);
		}
		buf.writeInt(beta_items.size());
		for(Item item : beta_items) {
			buf.writeRegistryIdUnsafe(ForgeRegistries.ITEMS, item);
		}
	}

	@Override
	public void read(FriendlyByteBuf buf) {
		alpha_slots = buf.readInt();
		beta_slots = buf.readInt();
		int num = buf.readInt();
		for(int i = 0; i<num; i++) {
			alpha_items.add(buf.readRegistryIdUnsafe(ForgeRegistries.ITEMS));
		}
		num = buf.readInt();
		for(int i = 0; i<num; i++) {
			beta_items.add(buf.readRegistryIdUnsafe(ForgeRegistries.ITEMS));
		}
	}

	@Override
	public void handlePacket(Player player) {
		DeepPouches.alpha_slots = alpha_slots;
		DeepPouches.beta_slots = beta_slots;
		DeepPouches.alpha_items = alpha_items;
		DeepPouches.beta_items = beta_items;
	}
	
}
