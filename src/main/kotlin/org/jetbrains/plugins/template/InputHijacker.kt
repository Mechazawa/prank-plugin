package org.jetbrains.plugins.template

import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.actionSystem.TypedAction
import com.intellij.openapi.editor.actionSystem.TypedActionHandler
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity
import org.jetbrains.plugins.template.settings.PrankPluginSettings
import java.io.InputStreamReader
import kotlin.random.Random

class InputHijacker : ProjectActivity {
    
    private var originalHandler: TypedActionHandler? = null
    private var currentIndex = 0
    
    private val qwertyAdjacentKeys = mapOf(
        'q' to listOf('w', 'a', 's'),
        'w' to listOf('q', 'e', 'a', 's', 'd'),
        'e' to listOf('w', 'r', 's', 'd', 'f'),
        'r' to listOf('e', 't', 'd', 'f', 'g'),
        't' to listOf('r', 'y', 'f', 'g', 'h'),
        'y' to listOf('t', 'u', 'g', 'h', 'j'),
        'u' to listOf('y', 'i', 'h', 'j', 'k'),
        'i' to listOf('u', 'o', 'j', 'k', 'l'),
        'o' to listOf('i', 'p', 'k', 'l'),
        'p' to listOf('o', 'l'),
        'a' to listOf('q', 'w', 's', 'z'),
        's' to listOf('q', 'w', 'e', 'a', 'd', 'z', 'x'),
        'd' to listOf('w', 'e', 'r', 's', 'f', 'x', 'c'),
        'f' to listOf('e', 'r', 't', 'd', 'g', 'c', 'v'),
        'g' to listOf('r', 't', 'y', 'f', 'h', 'v', 'b'),
        'h' to listOf('t', 'y', 'u', 'g', 'j', 'b', 'n'),
        'j' to listOf('y', 'u', 'i', 'h', 'k', 'n', 'm'),
        'k' to listOf('u', 'i', 'o', 'j', 'l', 'm'),
        'l' to listOf('i', 'o', 'p', 'k'),
        'z' to listOf('a', 's', 'x'),
        'x' to listOf('s', 'd', 'z', 'c'),
        'c' to listOf('d', 'f', 'x', 'v'),
        'v' to listOf('f', 'g', 'c', 'b'),
        'b' to listOf('g', 'h', 'v', 'n'),
        'n' to listOf('h', 'j', 'b', 'm'),
        'm' to listOf('j', 'k', 'n')
    )
    
    override suspend fun execute(project: Project) {
        hijackTypedAction()
    }
    
    private fun getSelectedText(): String {
        val settings = PrankPluginSettings.getInstance()
        val fileName = when (settings.selectedTextVariant) {
            PrankPluginSettings.TextVariant.LOREM_IPSUM -> "lorem-ipsum.txt"
            PrankPluginSettings.TextVariant.STEAMED_HAMS -> "steamed-hams.txt"
            PrankPluginSettings.TextVariant.BEE_MOVIE -> "bee-movie.txt"
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
    
    private fun getAdjacentKey(char: Char): Char? {
        val adjacentKeys = qwertyAdjacentKeys[char]
        return if (adjacentKeys != null && adjacentKeys.isNotEmpty()) {
            adjacentKeys[Random.nextInt(adjacentKeys.size)]
        } else {
            null
        }
    }
    
    private fun hijackTypedAction() {
        val typedAction = TypedAction.getInstance()
        originalHandler = typedAction.rawHandler
        
        typedAction.setupRawHandler(object : TypedActionHandler {
            override fun execute(editor: Editor, charTyped: Char, dataContext: DataContext) {
                val settings = PrankPluginSettings.getInstance()
                
                when (settings.prankMode) {
                    PrankPluginSettings.PrankMode.REWRITE -> {
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
                    PrankPluginSettings.PrankMode.BUTTER_FINGERS -> {
                        val shouldMistype = Random.nextInt(100) < settings.butterFingersPercentage
                        val charToInsert = if (shouldMistype) {
                            getAdjacentKey(charTyped.lowercaseChar()) ?: charTyped
                        } else {
                            charTyped
                        }
                        
                        WriteCommandAction.runWriteCommandAction(editor.project) {
                            val document = editor.document
                            val caretModel = editor.caretModel
                            val offset = caretModel.offset
                            
                            document.insertString(offset, charToInsert.toString())
                            caretModel.moveToOffset(offset + 1)
                        }
                    }
                }
            }
        })
    }
}