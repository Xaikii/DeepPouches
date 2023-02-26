package net.perpetualeve.deeppouches.item.alphapouch;

import java.util.List;

import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.network.protocol.game.ClientboundContainerSetContentPacket;
import net.minecraft.network.protocol.game.ClientboundContainerSetDataPacket;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerSynchronizer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.perpetualeve.deeppouches.DeepPouches;
import net.perpetualeve.deeppouches.item.SimplePouchContainer;

public class AlphaPouchMenu extends AbstractContainerMenu {
	private final Container container;
	public int count;
	public int offSetCount;
	private Player player;

	public AlphaPouchMenu(int id, Inventory inv) {
		this(id, inv, new SimplePouchContainer(DeepPouches.alpha_slots));
	}

	public AlphaPouchMenu(int id, Inventory inv, Container container) {
		super(DeepPouches.ALPHA_POUCH_MENU, id);
		this.container = container;
		container.startOpen(player = inv.player);

		int o = count = DeepPouches.alpha_slots;
		int q = (int) (54 - o) / 9;
		for (int i = 0; i < Math.min(o, 54); ++i) {
			int y = i / 9;
			int x = i % 9;

			if (i == 0)
				offSetCount = (y * 18 + q * 18);
			this.addSlot(new AlphaPouchSlot(container, i, 8 + x * 18, (y * 18 + q * 18) - 12));
		}

		for (int i = 0; i < 3; ++i) {
			for (int j = 0; j < 9; ++j) {
				this.addSlot(new Slot(inv, j + i * 9 + 9, 8 + j * 18, 112 + i * 18));
			}
		}

		for (int k = 0; k < 9; ++k) {
			this.addSlot(new Slot(inv, k, 8 + k * 18, 170));
		}
	}

	@Override
	public boolean stillValid(Player player) {
		return this.container.stillValid(player);
	}

	public void removed(Player p_40197_) {
		super.removed(p_40197_);
		this.container.stopOpen(p_40197_);
	}

	@Override
	public ItemStack quickMoveStack(Player p_38941_, int p_38942_) {
		
		/*
		 * Fix this to update the Stack Size
		 */
		
		ItemStack itemstack = ItemStack.EMPTY;
		Slot slot = this.slots.get(p_38942_);
		if (slot != null && slot.hasItem()) {
			ItemStack itemstack1 = slot.getItem();
			itemstack = itemstack1.copy();
			if (p_38942_ < this.container.getContainerSize()) {
				if (!this.moveItemStackTo(itemstack1, this.container.getContainerSize(), this.slots.size(), true)) {
					return ItemStack.EMPTY;
				}
			} else if (!this.moveItemStackTo(itemstack1, 0, this.container.getContainerSize(), false)) {
				return ItemStack.EMPTY;
			}

			if (itemstack1.isEmpty()) {
				slot.set(ItemStack.EMPTY);
			} else {
				slot.setChanged();
			}
		}

		return itemstack;
	}

	protected boolean moveItemStackTo(ItemStack p_38904_, int p_38905_, int p_38906_, boolean p_38907_) {
		boolean flag = false;
		int i = p_38905_;
		if (p_38907_) {
			i = p_38906_ - 1;
		}

		if (p_38904_.isStackable()) {
			while (!p_38904_.isEmpty()) {
				if (p_38907_) {
					if (i < p_38905_) {
						break;
					}
				} else if (i >= p_38906_) {
					break;
				}

				Slot slot = this.slots.get(i);
				ItemStack itemstack = slot.getItem();
				if (!itemstack.isEmpty() && ItemStack.isSameItemSameTags(p_38904_, itemstack)) {
					int j = itemstack.getCount() + p_38904_.getCount();
					int maxSize = slot.getMaxStackSize();
					if (j <= maxSize) {
						p_38904_.setCount(0);
						itemstack.setCount(j);
						slot.setChanged();
						flag = true;
					} else if (itemstack.getCount() < maxSize) {
						p_38904_.shrink(maxSize - itemstack.getCount());
						itemstack.setCount(maxSize);
						slot.setChanged();
						flag = true;
					}
				}

				if (p_38907_) {
					--i;
				} else {
					++i;
				}
			}
		}

		if (!p_38904_.isEmpty()) {
			if (p_38907_) {
				i = p_38906_ - 1;
			} else {
				i = p_38905_;
			}

			while (true) {
				if (p_38907_) {
					if (i < p_38905_) {
						break;
					}
				} else if (i >= p_38906_) {
					break;
				}

				Slot slot1 = this.slots.get(i);
				ItemStack itemstack1 = slot1.getItem();
				if (itemstack1.isEmpty() && slot1.mayPlace(p_38904_)) {
					if (p_38904_.getCount() > slot1.getMaxStackSize()) {
						slot1.set(p_38904_.split(slot1.getMaxStackSize()));
					} else {
						slot1.set(p_38904_.split(p_38904_.getCount()));
					}

					slot1.setChanged();
					flag = true;
					break;
				}

				if (p_38907_) {
					--i;
				} else {
					++i;
				}
			}
		}

		return flag;
	}

	@Override
	public void initializeContents(int p_182411_, List<ItemStack> p_182412_, ItemStack p_182413_) {
		for(int i = 0; i<p_182412_.size(); i++) {
			fixItemStack(p_182412_.get(i));
		}
		super.initializeContents(p_182411_, p_182412_, fixItemStack(p_182413_));
	}
	
	@Override
	public void setItem(int p_182407_, int p_182408_, ItemStack p_182409_) {
		super.setItem(p_182407_, p_182408_, fixItemStack(p_182409_));
	}
	
	@Override
	public void setSynchronizer(ContainerSynchronizer p_150417_) {
		if(player instanceof ServerPlayer player) {
			super.setSynchronizer(new Synch(player));
			return;
		}
		super.setSynchronizer(p_150417_);
	}

	private ItemStack fixItemStack(ItemStack stack) {
		CompoundTag tag = stack.getTag();
		System.out.println("Received: " + tag);
		if(tag != null && tag.contains("iSize")) {
			stack.setCount(tag.getInt("iSize"));
			tag.remove("iSize");
		}
		return stack;
	}
	
	static class Synch implements ContainerSynchronizer {

		ServerPlayer sp;
		
		public Synch(ServerPlayer player) {
			this.sp = player;
		}
		
		public void sendInitialData(AbstractContainerMenu p_143448_, NonNullList<ItemStack> p_143449_,
				ItemStack p_143450_, int[] p_143451_) {
			NonNullList<ItemStack> nnl = NonNullList.withSize(p_143449_.size(), ItemStack.EMPTY);
			
			for(int i = 0; i < nnl.size(); i++) {
				nnl.set(i, fixItemStack(p_143449_.get(i)));
			}
			sp.connection.send(new ClientboundContainerSetContentPacket(p_143448_.containerId,
					p_143448_.incrementStateId(), nnl, fixItemStack(p_143450_)));

			
			for (int i = 0; i < p_143451_.length; ++i) {
				this.broadcastDataValue(p_143448_, i, p_143451_[i]);
			}

		}

		public void sendSlotChange(AbstractContainerMenu p_143441_, int p_143442_, ItemStack p_143443_) {
			sp.connection.send(new ClientboundContainerSetSlotPacket(p_143441_.containerId,
					p_143441_.incrementStateId(), p_143442_, fixItemStack(p_143443_)));
		}

		public void sendCarriedChange(AbstractContainerMenu p_143445_, ItemStack p_143446_) {
			sp.connection
					.send(new ClientboundContainerSetSlotPacket(-1, p_143445_.incrementStateId(), -1, fixItemStack(p_143446_)));
		}

		public void sendDataChange(AbstractContainerMenu p_143437_, int p_143438_, int p_143439_) {
			this.broadcastDataValue(p_143437_, p_143438_, p_143439_);
		}

		private void broadcastDataValue(AbstractContainerMenu p_143455_, int p_143456_, int p_143457_) {
			sp.connection
					.send(new ClientboundContainerSetDataPacket(p_143455_.containerId, p_143456_, p_143457_));
		}
		
		private ItemStack fixItemStack(ItemStack stack) {
			ItemStack item = stack.copy();
			System.out.println("noot" + stack.getCount());
			if(!item.isEmpty()) {
				item.addTagElement("iSize", IntTag.valueOf(stack.getCount()));
				System.out.println("Send: " + item.getTag());
//				item.setCount(1);
			}
			return item;
		}
	}
}
