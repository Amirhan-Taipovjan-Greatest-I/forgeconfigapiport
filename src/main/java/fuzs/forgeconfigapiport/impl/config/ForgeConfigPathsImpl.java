package fuzs.forgeconfigapiport.impl.config;

import fuzs.forgeconfigapiport.api.config.v2.ForgeConfigPaths;
import fuzs.forgeconfigapiport.impl.ForgeConfigAPIPort;
import fuzs.forgeconfigapiport.mixin.accessor.LevelResourceAccessor;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.LevelResource;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class ForgeConfigPathsImpl implements ForgeConfigPaths {
    private static final LevelResource SERVER_CONFIG_LEVEL_KEY = LevelResourceAccessor.forgeconfigapiport$create("serverconfig");

    public Path getClientConfigPath() {
        return FabricLoader.getInstance().getConfigDir();
    }

    public Path getCommonConfigPath() {
        return FabricLoader.getInstance().getConfigDir();
    }

    public Path getServerConfigPath(final MinecraftServer server) {
        if (ForgeConfigApiPortConfig.getInstance().disableGlobalServerConfigs) {
            return FabricLoader.getInstance().getConfigDir();
        }
        final Path serverConfig = server.getWorldPath(SERVER_CONFIG_LEVEL_KEY);
        getOrCreateDirectory(serverConfig, "server config directory");
        return serverConfig;
    }

    public Path getDefaultConfigsPath() {
        Path defaultConfigs = FabricLoader.getInstance().getGameDir().resolve(ForgeConfigApiPortConfig.getInstance().defaultsConfigPath);
        getOrCreateDirectory(defaultConfigs, "default configs directory");
        return defaultConfigs;
    }

    private static Path getOrCreateDirectory(Path dirPath, String dirLabel) {
        if (!Files.isDirectory(dirPath.getParent())) {
            getOrCreateDirectory(dirPath.getParent(), "parent of "+dirLabel);
        }
        if (!Files.isDirectory(dirPath))
        {
            ForgeConfigAPIPort.LOGGER.debug("Making {} directory : {}", dirLabel, dirPath);
            try {
                Files.createDirectory(dirPath);
            } catch (IOException e) {
                if (e instanceof FileAlreadyExistsException) {
                    ForgeConfigAPIPort.LOGGER.error("Failed to create {} directory - there is a file in the way", dirLabel);
                } else {
                    ForgeConfigAPIPort.LOGGER.error("Problem with creating {} directory (Permissions?)", dirLabel, e);
                }
                throw new RuntimeException("Problem creating directory", e);
            }
            ForgeConfigAPIPort.LOGGER.debug("Created {} directory : {}", dirLabel, dirPath);
        } else {
            ForgeConfigAPIPort.LOGGER.debug("Found existing {} directory : {}", dirLabel, dirPath);
        }
        return dirPath;
    }
}