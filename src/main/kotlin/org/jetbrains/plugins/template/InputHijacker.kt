package org.jetbrains.plugins.template

import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.actionSystem.TypedAction
import com.intellij.openapi.editor.actionSystem.TypedActionHandler
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity
import java.io.InputStreamReader

class InputHijacker : ProjectActivity {
    
    private var originalHandler: TypedActionHandler? = null
    private var loremIpsumText: String = ""
    private var currentIndex = 0
    
    override suspend fun execute(project: Project) {
        loadLoremIpsum()
        hijackTypedAction()
    }
    
    private fun loadLoremIpsum() {
        try {
            val inputStream = javaClass.classLoader.getResourceAsStream("lorem-ipsum.txt")
            if (inputStream != null) {
                loremIpsumText = InputStreamReader(inputStream).readText().replace("\n", " ").replace("\r", "")
            }
        } catch (e: Exception) {
            loremIpsumText = "Lorem ipsum dolor sit amet, consectetur adipiscing elit."
        }
    }
    
    private fun hijackTypedAction() {
        val typedAction = TypedAction.getInstance()
        originalHandler = typedAction.rawHandler
        
        typedAction.setupRawHandler(object : TypedActionHandler {
            override fun execute(editor: Editor, charTyped: Char, dataContext: DataContext) {
                if (loremIpsumText.isNotEmpty()) {
                    val charToInsert = loremIpsumText[currentIndex % loremIpsumText.length]
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
        })
    }
}