package net.perpetualeve.deeppouches.item;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;

public class PouchItemStackHandler implements IItemHandler, IItemHandlerModifiable, ICapabilityProvider {

	private final ItemStack stack;
	private final LazyOptional<IItemHandler> holder = LazyOptional.of(() -> this);
	private int size = 5;

	private CompoundTag cachedTag;
	private NonNullList<ItemStack> itemStacksCache;

	public PouchItemStackHandler(ItemStack stack, int size) {
		this.stack = stack;
		this.size = size;
	}

	@Override
	public int getSlots() {
		return size;
	}

	@Override
	@NotNull
	public ItemStack getStackInSlot(int slot) {
		validateSlotIndex(slot);
		return getItemList().get(slot);
	}

	@Override
	@NotNull
	public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
		if (stack.isEmpty())
			return ItemStack.EMPTY;

		if (!isItemValid(slot, stack))
			return stack;

		validateSlotIndex(slot);

		NonNullList<ItemStack> itemStacks = getItemList();

		ItemStack existing = itemStacks.get(slot);

		int limit = getSlotLimit(slot);

		if (!existing.isEmpty()) {
			if (!ItemHandlerHelper.canItemStacksStack(stack, existing))
				return stack;

			limit -= existing.getCount();
		}

		if (limit <= 0)
			return stack;

		boolean reachedLimit = stack.getCount() > limit;

		if (!simulate) {
			if (existing.isEmpty()) {
				itemStacks.set(slot, reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, limit) : stack);
			} else {
				existing.grow(reachedLimit ? limit : stack.getCount());
			}
			setItemList(itemStacks);
		}

		return reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, stack.getCount() - limit) : ItemStack.EMPTY;
	}

	@Override
	@NotNull
	public ItemStack extractItem(int slot, int amount, boolean simulate) {
		NonNullList<ItemStack> itemStacks = getItemList();
		if (amount == 0)
			return ItemStack.EMPTY;

		validateSlotIndex(slot);

		ItemStack existing = itemStacks.get(slot);

		if (existing.isEmpty())
			return ItemStack.EMPTY;

		int toExtract = amount;

		if (existing.getCount() <= toExtract) {
			if (!simulate) {
				itemStacks.set(slot, ItemStack.EMPTY);
				setItemList(itemStacks);
				return existing;
			} else {
				return existing.copy();
			}
		} else {
			if (!simulate) {
				itemStacks.set(slot, ItemHandlerHelper.copyStackWithSize(existing, existing.getCount() - toExtract));
				setItemList(itemStacks);
			}

			return ItemHandlerHelper.copyStackWithSize(existing, toExtract);
		}
	}

	private void validateSlotIndex(int slot) {
		if (slot < 0 || slot >= getSlots())
			throw new RuntimeException("Slot " + slot + " not in valid range - [0," + getSlots() + ")");
	}

	@Override
	public int getSlotLimit(int slot) {
		return Integer.MAX_VALUE;
	}

	@Override
	public boolean isItemValid(int slot, @NotNull ItemStack stack) {
		return stack.getItem().canFitInsideContainerItems();
	}

	@Override
	public void setStackInSlot(int slot, @NotNull ItemStack stack) {
		validateSlotIndex(slot);
		if (!isItemValid(slot, stack))
			throw new RuntimeException("Invalid stack " + stack + " for slot " + slot + ")");
		NonNullList<ItemStack> itemStacks = getItemList();
		itemStacks.set(slot, stack);
		setItemList(itemStacks);
	}

	private NonNullList<ItemStack> getItemList() {
		CompoundTag rootTag = stack.getTagElement("Inventory");
		if (cachedTag == null || !cachedTag.equals(rootTag))
			itemStacksCache = refreshItemList(rootTag);
		return itemStacksCache;
	}

	private NonNullList<ItemStack> refreshItemList(CompoundTag rootTag) {
		NonNullList<ItemStack> itemStacks = NonNullList.withSize(getSlots(), ItemStack.EMPTY);
		if (rootTag != null && rootTag.contains("Items", CompoundTag.TAG_LIST)) {
			loadAllItems(rootTag, itemStacks);
		}
		cachedTag = rootTag;
		return itemStacks;
	}

	public static CompoundTag saveAllItems(CompoundTag p_18977_, NonNullList<ItemStack> p_18978_, boolean p_18979_) {
		ListTag listtag = new ListTag();

		for (int i = 0; i < p_18978_.size(); ++i) {
			ItemStack itemstack = p_18978_.get(i);
			if (!itemstack.isEmpty()) {
				CompoundTag compoundtag = new CompoundTag();
				compoundtag.putByte("Slot", (byte) i);
				itemstack.save(compoundtag);
				compoundtag.putInt("iSize", itemstack.getCount());
				listtag.add(compoundtag);
				System.out.println("Save: " + compoundtag);
			}
		}

		if (!listtag.isEmpty() || p_18979_) {
			p_18977_.put("Items", listtag);
		}

		return p_18977_;
	}

	public static void loadAllItems(CompoundTag p_18981_, NonNullList<ItemStack> p_18982_) {
		ListTag listtag = p_18981_.getList("Items", 10);

		for (int i = 0; i < listtag.size(); ++i) {
			CompoundTag compoundtag = listtag.getCompound(i);
			int j = compoundtag.getByte("Slot") & 255;
			if (j >= 0 && j < p_18982_.size()) {
				ItemStack stack = ItemStack.of(compoundtag);
				stack.setCount(compoundtag.getInt("iSize"));
				p_18982_.set(j, stack);
				System.out.println("Load: " + compoundtag);
			}
		}

	}

	private void setItemList(NonNullList<ItemStack> itemStacks) {
		CompoundTag existing = stack.getTagElement("Inventory");
		CompoundTag rootTag = saveAllItems(existing == null ? new CompoundTag() : existing, itemStacks, true);
		setData(this.stack, rootTag);
		cachedTag = rootTag;
	}

	private void setData(ItemStack p_186339_, CompoundTag p_186341_) {
		if (p_186341_.isEmpty()) {
			p_186339_.removeTagKey("Inventory");
		} else {
			p_186339_.addTagElement("Inventory", p_186341_);
		}

	}

	@Override
	@NotNull
	public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
		return ForgeCapabilities.ITEM_HANDLER.orEmpty(cap, this.holder);
	}
}
