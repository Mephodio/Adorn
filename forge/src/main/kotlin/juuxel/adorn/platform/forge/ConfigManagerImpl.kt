package juuxel.adorn.platform.forge

import juuxel.adorn.config.ConfigManager
import net.minecraftforge.fml.loading.FMLPaths

object ConfigManagerImpl : ConfigManager() {
    override val configDirectory = FMLPaths.CONFIGDIR.get()
}
