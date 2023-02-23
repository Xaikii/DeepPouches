package net.perpetualeve.deeppouches.item;

import org.jetbrains.annotations.Nullable;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.network.NetworkHooks;
import net.perpetualeve.deeppouches.DeepPouches;
import net.perpetualeve.deeppouches.item.alphapouch.AlphaPouchMenu;

public class AlphaPouch extends Item {

	public AlphaPouch(Properties p_41383_) {
		super(p_41383_);
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		if (player.containerMenu != player.inventoryMenu) {
			player.closeContainer();
		}
		if(player instanceof ServerPlayer splayer) {
			NetworkHooks.openScreen(splayer, new SimpleMenuProvider((w,p,pl) -> new AlphaPouchMenu(w, p, new IItemStackWrapper(new PouchItemStackHandler(pl.getItemInHand(hand), DeepPouches.alpha_slots))), Component.literal("")));
			return InteractionResultHolder.fail(player.getItemInHand(hand));
		}
		return InteractionResultHolder.pass(player.getItemInHand(hand));
	}
	
	@Override
	public @Nullable ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
		return new PouchItemStackHandler(stack, DeepPouches.alpha_slots);
	}
}
