package net.perpetualeve.deeppouches;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig.Type;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.perpetualeve.deeppouches.item.AlphaPouch;
import net.perpetualeve.deeppouches.item.BetaPouch;
import net.perpetualeve.deeppouches.item.alphapouch.AlphaPouchMenu;
import net.perpetualeve.deeppouches.item.alphapouch.AlphaPouchScreen;
import net.perpetualeve.deeppouches.item.betapouch.BetaPouchMenu;
import net.perpetualeve.deeppouches.item.betapouch.BetaPouchScreen;

@Mod(DeepPouches.MODID)
public class DeepPouches {

	public static final String MODID = "deeppouches";
	
	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
	
	public static final RegistryObject<Item> ALPHA_POUCH = ITEMS.register("alpha_pouch", () -> new AlphaPouch(new Item.Properties()));
	public static final RegistryObject<Item> BETA_POUCH = ITEMS.register("beta_pouch", () -> new BetaPouch(new Item.Properties()));
	public static final MenuType<AlphaPouchMenu> ALPHA_POUCH_MENU = new MenuType<>(AlphaPouchMenu::new);
	public static final MenuType<BetaPouchMenu> BETA_POUCH_MENU = new MenuType<>(BetaPouchMenu::new);

	public static int alpha_slots;
	IntValue alpha_slots_cfg;
	public static Set<Item> alpha_items = new HashSet<>();
	ConfigValue<List<? extends String>> alpha_items_cfg;
	
	public static int beta_slots;
	IntValue beta_slots_cfg;
	public static Set<Item> beta_items = new HashSet<>();
	ConfigValue<List<? extends String>> beta_items_cfg;
	
	public static ForgeConfigSpec CONFIG;
	
	public DeepPouches() {
		ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
		builder.push("general");

		builder.comment("How many slots should the Alpha Pouch have");
		builder.worldRestart();
		alpha_slots_cfg = builder.defineInRange("alpha_slots", 5, 1, 54);
		builder.comment("Which items should the Alpha Pouch accept, \"itemID1\",\"itemID2\"..., \"minecraft:apple\"");
		alpha_items_cfg = builder.defineList("alpha_items_accepted", Arrays.asList("minecraft:apple"), T -> true);
		
		builder.comment("How many slots should the Beta Pouch have");
		builder.worldRestart();
		beta_slots_cfg = builder.defineInRange("beta_slots", 8, 1, 54);
		builder.comment("Which items should the Beta Pouch accept, \"itemID1\",\"itemID2\"..., \"minecraft:apple\"");
		beta_items_cfg = builder.defineList("beta_items_accepted", Arrays.asList("minecraft:stick"), T -> true);
		
		builder.pop();
		CONFIG = builder.build();
		ModLoadingContext.get().registerConfig(Type.COMMON, CONFIG, "DeepPouches.toml");

		IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
		register(bus);
		bus.addListener(this::onLoad);
		bus.addListener(this::onFileChange);
		MinecraftForge.EVENT_BUS.register(this);
		
		ForgeRegistries.MENU_TYPES.register("alpha_pouch", ALPHA_POUCH_MENU);
		MenuScreens.register(ALPHA_POUCH_MENU, AlphaPouchScreen::new);
		
		ForgeRegistries.MENU_TYPES.register("beta_pouch", BETA_POUCH_MENU);
		MenuScreens.register(BETA_POUCH_MENU, BetaPouchScreen::new);
	}
	
	
	public static void register(IEventBus bus) {
		ITEMS.register(bus);
	}

	public void onLoad(ModConfigEvent.Loading configEvent) {
		alpha_slots = alpha_slots_cfg.get();
		beta_slots = beta_slots_cfg.get();
		reloadConfig();
	}

	public void onFileChange(ModConfigEvent.Reloading configEvent) {
		reloadConfig();
	}
	
	public void reloadConfig() {
//		if(!isAllowedToLoad()) return; 
//		alpha_slots = alpha_slots_cfg.get();
//		beta_slots = beta_slots_cfg.get();
		alpha_items.clear();
		for(String f:alpha_items_cfg.get()) {
			alpha_items.add(ForgeRegistries.ITEMS.getValue(new ResourceLocation(f)));
		}
		beta_items.clear();
		for(String f:beta_items_cfg.get()) {
			beta_items.add(ForgeRegistries.ITEMS.getValue(new ResourceLocation(f)));
		}
		
//		MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
//		if(server == null) return;
//		GARPacketManager.MANAGER.sendToAllPlayers(new GARConfigSyncPacket(forward, left, overrides));
	}
}
