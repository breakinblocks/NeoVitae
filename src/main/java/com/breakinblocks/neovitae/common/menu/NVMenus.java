package com.breakinblocks.neovitae.common.menu;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import com.breakinblocks.neovitae.NeoVitae;

public class NVMenus {
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(BuiltInRegistries.MENU, NeoVitae.MODID);

    public static final DeferredHolder<MenuType<?>, MenuType<AthanorMenu>> ARC = MENUS.register("arc_menu", () -> IMenuTypeExtension.create(AthanorMenu::new));

    public static final DeferredHolder<MenuType<?>, MenuType<TrainerMenu>> TRAINER = MENUS.register("trainer", () -> IMenuTypeExtension.create(TrainerMenu::new));

    public static final DeferredHolder<MenuType<?>, MenuType<TeleposerMenu>> TELEPOSER = MENUS.register("teleposer", () -> IMenuTypeExtension.create(TeleposerMenu::new));

    public static final DeferredHolder<MenuType<?>, MenuType<TabulaVitaeMenu>> TABULA_VITAE = MENUS.register("tabula_vitae", () -> IMenuTypeExtension.create(TabulaVitaeMenu::new));

    public static final DeferredHolder<MenuType<?>, MenuType<HellfireForgeMenu>> HELLFIRE_FORGE = MENUS.register("hellfire_forge", () -> IMenuTypeExtension.create(HellfireForgeMenu::new));

    public static final DeferredHolder<MenuType<?>, MenuType<ExperienceTomeMenu>> EXPERIENCE_TOME = MENUS.register("experience_tome", () -> IMenuTypeExtension.create(ExperienceTomeMenu::new));

    public static final DeferredHolder<MenuType<?>, MenuType<SigilHoldingMenu>> SIGIL_HOLDING = MENUS.register("sigil_holding", () -> IMenuTypeExtension.create(SigilHoldingMenu::new));

    public static final DeferredHolder<MenuType<?>, MenuType<RoutingNodeMenu>> ROUTING_NODE = MENUS.register("routing_node", () -> IMenuTypeExtension.create(RoutingNodeMenu::new));

    public static final DeferredHolder<MenuType<?>, MenuType<MasterRoutingNodeMenu>> MASTER_ROUTING_NODE = MENUS.register("master_routing_node", () -> IMenuTypeExtension.create(MasterRoutingNodeMenu::new));

    public static final DeferredHolder<MenuType<?>, MenuType<DungeonSealMenu>> DUNGEON_SEAL = MENUS.register("dungeon_seal", () -> IMenuTypeExtension.create(DungeonSealMenu::new));

    public static final DeferredHolder<MenuType<?>, MenuType<SpiritCacheMenu>> SPIRIT_CACHE = MENUS.register("spirit_cache", () -> IMenuTypeExtension.create(SpiritCacheMenu::new));

    public static final DeferredHolder<MenuType<?>, MenuType<RitualDivinerMenu>> RITUAL_DIVINER = MENUS.register("ritual_diviner", () -> IMenuTypeExtension.create(RitualDivinerMenu::new));

    public static final DeferredHolder<MenuType<?>, MenuType<RitualConfiguratorMenu>> RITUAL_CONFIGURATOR = MENUS.register("ritual_configurator", () -> IMenuTypeExtension.create(RitualConfiguratorMenu::new));

    public static final DeferredHolder<MenuType<?>, MenuType<AlternatorMenu>> ALTERNATOR = MENUS.register("alternator", () -> IMenuTypeExtension.create(AlternatorMenu::new));

    public static void register(IEventBus modbus) {
        MENUS.register(modbus);
    }
}
