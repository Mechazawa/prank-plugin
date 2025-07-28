package org.jetbrains.plugins.template.settings

import com.intellij.openapi.options.Configurable
import com.intellij.openapi.ui.ComboBox
import com.intellij.ui.components.JBLabel
import com.intellij.util.ui.FormBuilder
import javax.swing.DefaultComboBoxModel
import javax.swing.JComponent
import javax.swing.JPanel

class PrankPluginConfigurable : Configurable {
    
    private var mySettingsComponent: PrankPluginSettingsComponent? = null
    
    override fun getDisplayName(): String = "Scrumble Prank"
    
    override fun createComponent(): JComponent? {
        mySettingsComponent = PrankPluginSettingsComponent()
        return mySettingsComponent?.getPanel()
    }
    
    override fun isModified(): Boolean {
        val settings = PrankPluginSettings.getInstance()
        return mySettingsComponent?.getSelectedTextVariant() != settings.selectedTextVariant
    }
    
    override fun apply() {
        val settings = PrankPluginSettings.getInstance()
        mySettingsComponent?.getSelectedTextVariant()?.let {
            settings.selectedTextVariant = it
        }
    }
    
    override fun reset() {
        val settings = PrankPluginSettings.getInstance()
        mySettingsComponent?.setSelectedTextVariant(settings.selectedTextVariant)
    }
    
    override fun disposeUIResources() {
        mySettingsComponent = null
    }
    
    private class PrankPluginSettingsComponent {
        private val panel: JPanel
        private val textVariantComboBox: ComboBox<PrankPluginSettings.TextVariant>
        
        init {
            textVariantComboBox = ComboBox(DefaultComboBoxModel(PrankPluginSettings.TextVariant.values()))
            
            panel = FormBuilder.createFormBuilder()
                .addLabeledComponent(JBLabel("Text variant to use:"), textVariantComboBox, 1, false)
                .addComponentFillVertically(JPanel(), 0)
                .panel
        }
        
        fun getPanel(): JPanel = panel
        
        fun getSelectedTextVariant(): PrankPluginSettings.TextVariant? = 
            textVariantComboBox.selectedItem as? PrankPluginSettings.TextVariant
        
        fun setSelectedTextVariant(textVariant: PrankPluginSettings.TextVariant) {
            textVariantComboBox.selectedItem = textVariant
        }
    }
}