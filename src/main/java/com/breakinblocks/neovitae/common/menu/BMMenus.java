package com.breakinblocks.neovitae.common.menu;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import com.breakinblocks.neovitae.NeoVitae;

public class BMMenus {
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(BuiltInRegistries.MENU, NeoVitae.MODID);

    public static final DeferredHolder<MenuType<?>, MenuType<ARCMenu>> ARC = MENUS.register("arc_menu", () -> IMenuTypeExtension.create(ARCMenu::new));

    public static final DeferredHolder<MenuType<?>, MenuType<TrainerMenu>> TRAINER = MENUS.register("trainer", () -> IMenuTypeExtension.create(TrainerMenu::new));

    public static final DeferredHolder<MenuType<?>, MenuType<TeleposerMenu>> TELEPOSER = MENUS.register("teleposer", () -> IMenuTypeExtension.create(TeleposerMenu::new));

    public static final DeferredHolder<MenuType<?>, MenuType<AlchemyTableMenu>> ALCHEMY_TABLE = MENUS.register("alchemy_table", () -> IMenuTypeExtension.create(AlchemyTableMenu::new));

    public static final DeferredHolder<MenuType<?>, MenuType<SoulForgeMenu>> SOUL_FORGE = MENUS.register("soul_forge", () -> IMenuTypeExtension.create(SoulForgeMenu::new));

    public static final DeferredHolder<MenuType<?>, MenuType<SigilHoldingMenu>> SIGIL_HOLDING = MENUS.register("sigil_holding", () -> IMenuTypeExtension.create(SigilHoldingMenu::new));

    public static final DeferredHolder<MenuType<?>, MenuType<RoutingNodeMenu>> ROUTING_NODE = MENUS.register("routing_node", () -> IMenuTypeExtension.create(RoutingNodeMenu::new));

    public static final DeferredHolder<MenuType<?>, MenuType<MasterRoutingNodeMenu>> MASTER_ROUTING_NODE = MENUS.register("master_routing_node", () -> IMenuTypeExtension.create(MasterRoutingNodeMenu::new));

    public static final DeferredHolder<MenuType<?>, MenuType<FilterMenu>> FILTER = MENUS.register("filter", () -> IMenuTypeExtension.create(FilterMenu::new));

    public static void register(IEventBus modbus) {
        MENUS.register(modbus);
    }
}
