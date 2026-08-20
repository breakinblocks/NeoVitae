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
                return false;
            }
            Files.write(path, List.of(
                    "#Experimental. Read before block registration; changing it needs a full game restart.",
                    "#Set to true to let Essentia Vitae fill waterloggable blocks. Some other mods'",
                    "#blocks can crash the game at startup once Neo Vitae adds its essentia_logged",
                    "#blockstate property to them.",
                    "essentiaLogging = false"
            ));
        } catch (IOException e) {
            LOGGER.warn("Could not read {}; essentia logging stays disabled", path, e);
        }
        return false;
    }
}
