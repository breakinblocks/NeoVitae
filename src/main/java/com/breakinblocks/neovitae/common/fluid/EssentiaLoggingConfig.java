package com.breakinblocks.neovitae.common.fluid;

import com.mojang.logging.LogUtils;
import net.neoforged.fml.loading.FMLPaths;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public final class EssentiaLoggingConfig {

    private static final Logger LOGGER = LogUtils.getLogger();

    private static final List<String> DEFAULT_FILE = List.of(
            "#Experimental. Read before block registration; changing it needs a full game restart.",
            "#Set to true to let Essentia Vitae fill waterloggable blocks. Some other mods'",
            "#blocks can crash the game at startup once Neo Vitae adds its essentia_logged",
            "#blockstate property to them.",
            "essentiaLogging = false"
    );

    private static final List<String> UNTOUCHED_1_1_10_FILE = List.of(
            "#Read before block registration; changing it needs a full game restart.",
            "#Set to false if another mod's blocks crash the game at startup once Neo Vitae",
            "#adds its essentia_logged blockstate property to them.",
            "essentiaLogging = true"
    );

    private static final boolean ENABLED = load();

    private EssentiaLoggingConfig() {
    }

    public static boolean isEnabled() {
        return ENABLED;
    }

    private static boolean load() {
        String override = System.getProperty("neovitae.essentiaLogging");
        if (override != null) {
            return Boolean.parseBoolean(override);
        }
        Path path = FMLPaths.CONFIGDIR.get().resolve("neovitae-startup.toml");
        try {
            if (!Files.exists(path)) {
                Files.write(path, DEFAULT_FILE);
                return false;
            }
            List<String> lines = Files.readAllLines(path);
            if (significantLines(lines).equals(UNTOUCHED_1_1_10_FILE)) {
                Files.write(path, DEFAULT_FILE);
                LOGGER.info("Rewrote {} to the current default. Neo Vitae 1.1.10 and 1.1.11 turned essentia logging on"
                        + " without being asked, and it can stop the game from starting.", path);
                return false;
            }
            for (String line : lines) {
                String trimmed = line.trim();
                if (trimmed.startsWith("essentiaLogging")) {
                    int eq = trimmed.indexOf('=');
                    if (eq >= 0) {
                        boolean enabled = Boolean.parseBoolean(trimmed.substring(eq + 1).trim());
                        if (enabled) {
                            LOGGER.warn("Essentia logging is on. It adds an essentia_logged blockstate property to every"
                                    + " waterloggable block, which some mods' blocks cannot cope with; the game then fails to"
                                    + " start with a NullPointerException in Block.isShapeFullBlock while registries freeze."
                                    + " Set essentiaLogging = false in {} if that happens.", path);
                        }
                        return enabled;
                    }
                }
            }
        } catch (IOException e) {
            LOGGER.warn("Could not read {}; essentia logging stays disabled", path, e);
        }
        return false;
    }

    private static List<String> significantLines(List<String> lines) {
        List<String> result = new ArrayList<>();
        for (String line : lines) {
            String trimmed = line.trim();
            if (!trimmed.isEmpty()) {
                result.add(trimmed);
            }
        }
        return result;
    }
}
