package com.breakinblocks.neovitae.common.fluid;

import com.mojang.logging.LogUtils;
import net.neoforged.fml.loading.FMLPaths;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public final class EssentiaLoggingConfig {

    private static final Logger LOGGER = LogUtils.getLogger();
    private static final boolean ENABLED = load();

    private EssentiaLoggingConfig() {
    }

    public static boolean isEnabled() {
        return ENABLED;
    }

    private static boolean load() {
        Path path = FMLPaths.CONFIGDIR.get().resolve("neovitae-startup.toml");
        try {
            if (Files.exists(path)) {
                for (String line : Files.readAllLines(path)) {
                    String trimmed = line.trim();
                    if (trimmed.startsWith("essentiaLogging")) {
                        int eq = trimmed.indexOf('=');
                        if (eq >= 0) {
                            return Boolean.parseBoolean(trimmed.substring(eq + 1).trim());
                        }
                    }
                }
                return true;
            }
            Files.write(path, List.of(
                    "#Read before block registration; changing it needs a full game restart.",
                    "#Set to false if another mod's blocks crash the game at startup once Neo Vitae",
                    "#adds its essentia_logged blockstate property to them.",
                    "essentiaLogging = true"
            ));
        } catch (IOException e) {
            LOGGER.warn("Could not read {}; essentia logging stays enabled", path, e);
        }
        return true;
    }
}
