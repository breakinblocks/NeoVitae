package com.breakinblocks.neovitae.gametest;

import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import net.minecraft.core.Holder;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;
import com.breakinblocks.neovitae.common.datacomponent.UpgradeLimits;
import com.breakinblocks.neovitae.common.menu.TrainerMenu;
import com.breakinblocks.neovitae.common.sentient.SentientHelper;
import com.breakinblocks.neovitae.common.sentient.SentientUpgrade;

import java.util.List;

@GameTestHolder("neovitae")
@PrefixGameTestTemplate(false)
public class TrainingBraceletTests {

    @GameTest(template = "empty_5x5x7", timeoutTicks = 20)
    public void capsRoundTripAndClamp(GameTestHelper helper) {
        List<Holder<SentientUpgrade>> trainable = TrainerMenu.trainable(helper.getLevel().registryAccess());
        helper.assertTrue(trainable.size() > 10, "Expected the trainable list to hold the trainable upgrades, has " + trainable.size());
        Holder<SentientUpgrade> upgrade = trainable.getFirst();
        int max = SentientHelper.getMaxLevel(upgrade);

        UpgradeLimits limits = UpgradeLimits.EMPTY;
        helper.assertTrue(limits.capLevel(upgrade) == max, "An unset cap should read as Max");

        limits = limits.withCap(upgrade, 2, trainable);
        helper.assertTrue(limits.capLevel(upgrade) == Math.min(2, max), "Cap should read back as 2, got " + limits.capLevel(upgrade));

        limits = limits.withCap(upgrade, 0, trainable);
        helper.assertTrue(limits.capLevel(upgrade) == 0, "Off should read back as 0");
        helper.assertTrue(limits.getLimit(upgrade) == 0f, "Off should block training entirely");

        limits = limits.withCap(upgrade, max + 5, trainable);
        helper.assertTrue(limits.capLevel(upgrade) == max && !limits.limits().containsKey(upgrade),
                "A cap past the maximum should clamp to Max and clear the entry");

        UpgradeLimits legacy = new UpgradeLimits(false, new Object2FloatOpenHashMap<>());
        Holder<SentientUpgrade> other = trainable.get(1);
        UpgradeLimits migrated = legacy.withCap(upgrade, max, trainable);
        helper.assertTrue(migrated.capLevel(other) == 0 && migrated.capLevel(upgrade) == max,
                "Editing a 'deny others' bracelet should keep the other upgrades blocked");
        helper.succeed();
    }
}
