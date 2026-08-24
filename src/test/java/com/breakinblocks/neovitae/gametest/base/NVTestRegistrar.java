package com.breakinblocks.neovitae.gametest.base;

import net.minecraft.core.Holder;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.gametest.framework.TestData;
import net.minecraft.gametest.framework.TestEnvironmentDefinition;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Rotation;
import net.neoforged.neoforge.event.RegisterGameTestsEvent;
import com.breakinblocks.neovitae.NeoVitae;

import java.util.List;
import java.util.function.Consumer;

public final class NVTestRegistrar {

    private static final Identifier STRUCTURE = NeoVitae.rl("empty_5x5x7");
    private static final Identifier ISOLATED_STRUCTURE = NeoVitae.rl("empty_24x5x24");

    private final RegisterGameTestsEvent event;
    private final Holder<TestEnvironmentDefinition<?>> environment;

    public NVTestRegistrar(RegisterGameTestsEvent event) {
        this.event = event;
        this.environment = event.registerEnvironment(NeoVitae.rl("default"), new TestEnvironmentDefinition.AllOf(List.of()));
    }

    public void add(String name, int maxTicks, Consumer<GameTestHelper> body) {
        add(name, maxTicks, 0, body);
    }

    public void addIsolated(String name, int maxTicks, Consumer<GameTestHelper> body) {
        add(name, maxTicks, 0, ISOLATED_STRUCTURE, body);
    }

    public void add(String name, int maxTicks, int setupTicks, Consumer<GameTestHelper> body) {
        add(name, maxTicks, setupTicks, STRUCTURE, body);
    }

    public void add(String name, int maxTicks, int setupTicks, Identifier structure, Consumer<GameTestHelper> body) {
        TestData<Holder<TestEnvironmentDefinition<?>>> info = new TestData<>(
                environment,
                structure,
                maxTicks,
                setupTicks,
                true,
                Rotation.NONE
        );
        try {
            event.registerTest(NeoVitae.rl(name), new NVInlineTest(info, body));
        } catch (Throwable t) {
            System.err.println("[NVGameTest] Failed to register " + name + ": " + t);
            t.printStackTrace();
        }
    }
}
