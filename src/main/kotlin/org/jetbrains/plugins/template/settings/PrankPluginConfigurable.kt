package org.jetbrains.plugins.template.settings

import com.intellij.openapi.options.Configurable
import com.intellij.openapi.ui.ComboBox
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBTextField
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
        return mySettingsComponent?.getSelectedTextVariant() != settings.selectedTextVariant ||
               mySettingsComponent?.getPrankMode() != settings.prankMode ||
               mySettingsComponent?.getButterFingersPercentage() != settings.butterFingersPercentage
    }
    
    override fun apply() {
        val settings = PrankPluginSettings.getInstance()
        mySettingsComponent?.getSelectedTextVariant()?.let {
            settings.selectedTextVariant = it
        }
        mySettingsComponent?.getPrankMode()?.let {
            settings.prankMode = it
        }
        settings.butterFingersPercentage = mySettingsComponent?.getButterFingersPercentage() ?: 10
    }
    
    override fun reset() {
        val settings = PrankPluginSettings.getInstance()
        mySettingsComponent?.setSelectedTextVariant(settings.selectedTextVariant)
        mySettingsComponent?.setPrankMode(settings.prankMode)
        mySettingsComponent?.setButterFingersPercentage(settings.butterFingersPercentage)
    }
    
    override fun disposeUIResources() {
        mySettingsComponent = null
    }
    
    private class PrankPluginSettingsComponent {
        private val panel: JPanel
        private val textVariantComboBox: ComboBox<PrankPluginSettings.TextVariant>
        private val prankModeComboBox: ComboBox<PrankPluginSettings.PrankMode>
        private val butterFingersPercentageField: JBTextField
        
        init {
            textVariantComboBox = ComboBox(DefaultComboBoxModel(PrankPluginSettings.TextVariant.values()))
            prankModeComboBox = ComboBox(DefaultComboBoxModel(PrankPluginSettings.PrankMode.values()))
            butterFingersPercentageField = JBTextField("10")
            
            panel = FormBuilder.createFormBuilder()
                .addLabeledComponent(JBLabel("Prank mode:"), prankModeComboBox, 1, false)
                .addLabeledComponent(JBLabel("Text variant to use:"), textVariantComboBox, 1, false)
                .addLabeledComponent(JBLabel("Butter fingers percentage (1-100):"), butterFingersPercentageField, 1, false)
                .addComponentFillVertically(JPanel(), 0)
                .panel
        }
        
        fun getPanel(): JPanel = panel
        
        fun getSelectedTextVariant(): PrankPluginSettings.TextVariant? = 
            textVariantComboBox.selectedItem as? PrankPluginSettings.TextVariant
        
        fun setSelectedTextVariant(textVariant: PrankPluginSettings.TextVariant) {
            textVariantComboBox.selectedItem = textVariant
        }
        
        fun getPrankMode(): PrankPluginSettings.PrankMode? =
            prankModeComboBox.selectedItem as? PrankPluginSettings.PrankMode
        
        fun setPrankMode(prankMode: PrankPluginSettings.PrankMode) {
            prankModeComboBox.selectedItem = prankMode
        }
        
        fun getButterFingersPercentage(): Int {
            return try {
                val value = butterFingersPercentageField.text.toInt()
                if (value in 1..100) value else 10
            } catch (e: NumberFormatException) {
                10
            }
        }
        
        fun setButterFingersPercentage(percentage: Int) {
            butterFingersPercentageField.text = percentage.toString()
        }
    }
}