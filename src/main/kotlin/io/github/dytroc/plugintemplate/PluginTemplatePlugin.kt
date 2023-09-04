package io.github.dytroc.plugintemplate

import io.github.monun.kommand.kommand
import net.kyori.adventure.text.Component
import org.bukkit.plugin.java.JavaPlugin

class PluginTemplatePlugin : JavaPlugin() {
    companion object {
        lateinit var instance: PluginTemplatePlugin
            private set
    }

    override fun onEnable() {
        super.onEnable()

        instance = this

        kommand {
            "test" {
                executes {
                    sender.sendMessage(Component.text("Hello, world!"))
                }
            }
        }
    }
}