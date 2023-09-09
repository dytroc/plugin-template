package io.github.dytroc.plugintemplate

import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin

class PluginTemplatePlugin : JavaPlugin(), Listener {
    companion object {
        lateinit var instance: PluginTemplatePlugin
            private set
    }

    override fun onEnable() {
        super.onEnable()

        instance = this
    }
}