package org.jetbrains.plugins.template.startup

import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.components.service
import com.intellij.openapi.editor.actionSystem.TypedAction
import com.intellij.openapi.editor.actionSystem.TypedActionHandler
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity
import org.jetbrains.plugins.template.services.SocketIOService
import org.jetbrains.plugins.template.settings.PrankPluginSettings
import java.io.InputStreamReader

class InputHijacker : ProjectActivity {

    private var originalHandler: TypedActionHandler? = null
    private var currentIndex = 0

    init {
        // Call the service so we get a connection
        service<SocketIOService>()
    }

    override suspend fun execute(project: Project) {
        hijackTypedAction()
    }

    private fun getSelectedText(): String {
        val settings = PrankPluginSettings.Companion.getInstance()
        val fileName = when (settings.selectedTextVariant) {
            PrankPluginSettings.TextVariant.LOREM_IPSUM -> "lorem-ipsum.txt"
            PrankPluginSettings.TextVariant.STEAMED_HAMS -> "steamed-hams.txt"
            PrankPluginSettings.TextVariant.BEE_MOVIE -> "bee-movie.txt"
            PrankPluginSettings.TextVariant.DISABLED -> return ""
        }

        return try {
            val inputStream = javaClass.classLoader.getResourceAsStream(fileName)
            if (inputStream != null) {
                InputStreamReader(inputStream).readText().replace("\n", " ").replace("\r", "")
            } else {
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit."
            }
        } catch (e: Exception) {
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit."
        }
    }

    private fun hijackTypedAction() {
        val typedAction = TypedAction.getInstance()

        if (originalHandler == null) {
            originalHandler = typedAction.rawHandler
        }

        typedAction.setupRawHandler { editor, charTyped, dataContext ->
            val selectedText = getSelectedText()
            if (selectedText.isNotEmpty()) {
                val charToInsert = selectedText[currentIndex % selectedText.length]
                currentIndex++

                WriteCommandAction.runWriteCommandAction(editor.project) {
                    val document = editor.document
                    val caretModel = editor.caretModel
                    val offset = caretModel.offset

                    document.insertString(offset, charToInsert.toString())
                    caretModel.moveToOffset(offset + 1)
                }
            } else {
                originalHandler?.execute(editor, charTyped, dataContext)
            }
        }
    }
}
