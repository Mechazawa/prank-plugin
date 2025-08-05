package org.jetbrains.plugins.template.settings

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.util.xmlb.XmlSerializerUtil
import java.net.InetAddress
import java.util.UUID

@Service
@State(name = "PrankPluginSettings", storages = [Storage("PrankPluginSettings.xml")])
class PrankPluginSettings : PersistentStateComponent<PrankPluginSettings> {

    enum class TextVariant(val displayName: String) {
        LOREM_IPSUM("Lorem Ipsum"),
        STEAMED_HAMS("Steamed Hams"),
        BEE_MOVIE("Bee Movie"),
        DISABLED("Disabled")
    }

    var selectedTextVariant: TextVariant = TextVariant.DISABLED

    var clientName: String = try {
        InetAddress.getLocalHost().hostName
    } catch (e: Exception) {
        "change-me-${UUID.randomUUID()}"
    }

    override fun getState(): PrankPluginSettings = this

    override fun loadState(state: PrankPluginSettings) {
        XmlSerializerUtil.copyBean(state, this)
    }

    companion object {
        fun getInstance(): PrankPluginSettings =
            ApplicationManager.getApplication().getService(PrankPluginSettings::class.java)
    }
}
