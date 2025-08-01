package org.jetbrains.plugins.template.settings

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.util.xmlb.XmlSerializerUtil

@Service
@State(name = "PrankPluginSettings", storages = [Storage("PrankPluginSettings.xml")])
class PrankPluginSettings : PersistentStateComponent<PrankPluginSettings> {
    
    enum class TextVariant(val displayName: String) {
        LOREM_IPSUM("Lorem Ipsum"),
        STEAMED_HAMS("Steamed Hams"),
        BEE_MOVIE("Bee Movie")
    }
    
    enum class PrankMode(val displayName: String) {
        REWRITE("Rewrite (Current)"),
        BUTTER_FINGERS("Butter Fingers")
    }
    
    var selectedTextVariant: TextVariant = TextVariant.LOREM_IPSUM
    var prankMode: PrankMode = PrankMode.REWRITE
    var butterFingersPercentage: Int = 10
    
    override fun getState(): PrankPluginSettings = this
    
    override fun loadState(state: PrankPluginSettings) {
        XmlSerializerUtil.copyBean(state, this)
    }
    
    companion object {
        fun getInstance(): PrankPluginSettings = 
            ApplicationManager.getApplication().getService(PrankPluginSettings::class.java)
    }
}