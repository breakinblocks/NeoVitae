package com.breakinblocks.neovitae.gametest;

import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;
import com.breakinblocks.neovitae.api.soul.AnimaTicket;
import com.breakinblocks.neovitae.common.datacomponent.Anima;
import com.breakinblocks.neovitae.common.datacomponent.Binding;
import com.breakinblocks.neovitae.common.datacomponent.NVDataComponents;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.breakinblocks.neovitae.common.item.sigil.ISigil;
import com.breakinblocks.neovitae.common.item.sigil.SigilItem;
import com.breakinblocks.neovitae.ritual.RitualHelper;
import com.breakinblocks.neovitae.util.helper.AnimaHelper;

import java.util.UUID;

@GameTestHolder("neovitae")
@PrefixGameTestTemplate(false)
public class SigilFakePlayerTests {

    private static final int COST = 100;

    private static FakePlayer fakePlayer(GameTestHelper helper) {
        return RitualHelper.createRitualFakePlayer(helper.getLevel(), UUID.randomUUID(), "NeoVitae Test");
    }

    private static Anima network(GameTestHelper helper, UUID owner, int ev) {
        Anima anima = AnimaHelper.getAnima(owner);
        if (anima == null) {
            helper.fail("Could not create an anima for the test owner");
            return null;
        }
        anima.set(AnimaTicket.create(ev), Integer.MAX_VALUE);
        return anima;
    }

    @GameTest(template = "empty_5x5x7", timeoutTicks = 40)
    public void fakePlayerSpendsOwnerEvThenStops(GameTestHelper helper) {
        UUID owner = UUID.randomUUID();
        Anima anima = network(helper, owner, COST * 2);
        if (anima == null) return;

        FakePlayer fake = fakePlayer(helper);

        if (!anima.syphonAndDamage(fake, AnimaTicket.create(COST)).success()) {
            helper.fail("A fake player should be able to spend the owner's stored EV");
            return;
        }
        if (anima.getCurrentEV() != COST) {
            helper.fail("Expected " + COST + " EV left, got " + anima.getCurrentEV());
            return;
        }

        if (!anima.syphonAndDamage(fake, AnimaTicket.create(COST)).success()) {
            helper.fail("The second draw should still be covered by stored EV");
            return;
        }
        if (anima.getCurrentEV() != 0) {
            helper.fail("Expected an empty network, got " + anima.getCurrentEV());
            return;
        }

        if (anima.syphonAndDamage(fake, AnimaTicket.create(COST)).success()) {
            helper.fail("A fake player kept using the sigil after the owner's EV ran out");
            return;
        }
        helper.succeed();
    }

    @GameTest(template = "empty_5x5x7", timeoutTicks = 40)
    public void realPlayerStillPaysWithHealth(GameTestHelper helper) {
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        Anima anima = network(helper, player.getUUID(), 0);
        if (anima == null) return;

        float before = player.getHealth();
        if (!anima.syphonAndDamage(player, AnimaTicket.create(COST)).success()) {
            helper.fail("A real player should still cover the shortfall with health");
            return;
        }
        if (player.getHealth() >= before) {
            helper.fail("The shortfall was reported paid but the player took no damage");
            return;
        }
        helper.succeed();
    }

    @GameTest(template = "empty_5x5x7", timeoutTicks = 40)
    public void bindingRefusesFakePlayer(GameTestHelper helper) {
        FakePlayer fake = fakePlayer(helper);
        SigilItem sigil = NVItems.SIGIL_LAVA.get();
        ItemStack stack = new ItemStack(sigil);

        sigil.bind(fake, stack);

        if (sigil.getBinding(stack) != null) {
            helper.fail("A sigil bound itself to a fake player");
            return;
        }
        helper.succeed();
    }

    @GameTest(template = "empty_5x5x7", timeoutTicks = 40)
    public void existingFakeBindingIsRevoked(GameTestHelper helper) {
        FakePlayer fake = fakePlayer(helper);
        SigilItem sigil = NVItems.SIGIL_LAVA.get();
        ItemStack stack = new ItemStack(sigil);
        stack.set(NVDataComponents.BINDING.get(), new Binding(fake.getUUID(), "[NeoVitae Test]"));

        ISigil.revokeFakeBinding(stack, fake);

        if (sigil.getBinding(stack) != null) {
            helper.fail("A binding naming the fake player holding the sigil survived");
            return;
        }
        helper.succeed();
    }

    @GameTest(template = "empty_5x5x7", timeoutTicks = 40)
    public void ownerBindingSurvivesFakePlayerHolding(GameTestHelper helper) {
        FakePlayer fake = fakePlayer(helper);
        SigilItem sigil = NVItems.SIGIL_LAVA.get();
        ItemStack stack = new ItemStack(sigil);
        UUID owner = UUID.randomUUID();
        stack.set(NVDataComponents.BINDING.get(), new Binding(owner, "Owner"));

        ISigil.revokeFakeBinding(stack, fake);

        Binding binding = sigil.getBinding(stack);
        if (binding == null || !binding.uuid().equals(owner)) {
            helper.fail("A real owner's binding was cleared just because a fake player held it");
            return;
        }
        helper.succeed();
    }
}
