package org.jetbrains.plugins.template.services

import com.intellij.openapi.Disposable
import com.intellij.openapi.components.Service
import com.intellij.openapi.diagnostic.thisLogger
import io.socket.client.IO
import io.socket.client.Socket
import org.jetbrains.plugins.template.settings.PrankPluginSettings
import org.json.JSONObject
import java.net.URI

@Service
class SocketIOService : Disposable {
    private val socket: Socket

    init {
        thisLogger().info("Trying socket connection....")
        val uri = URI.create("https://prank-plugin.home.duncte123.lgbt")
//        val uri = URI.create("http://localhost:3000")
        val options: IO.Options? = IO.Options.builder()
//            .setExtraHeaders(mapOf(
//                "X-client-name" to listOf(
//                    PrankPluginSettings.getInstance().clientName
//                )
//            ))
            .build()

        socket = IO.socket(uri, options)

        socket.on("connect") {
            thisLogger().info("Connected to socket")
            loginClient()
        }

        socket.on("change-setting") { args ->
            if (args.isNotEmpty()) {
                val selectedSetting = PrankPluginSettings.TextVariant.valueOf(args[0].toString())
                PrankPluginSettings.getInstance().selectedTextVariant = selectedSetting
            }
        }

        socket.connect()
    }

    fun loginClient() {
        val settings = PrankPluginSettings.getInstance()

        val obj = JSONObject()
            .put("name", settings.clientName)
            .put("mode", settings.selectedTextVariant.name)

        socket.emit("login", obj)
    }

    fun changeClientName(newName: String) {
        val obj = JSONObject()
            .put("name", newName)
        socket.emit("set-client-name", obj)
    }

    fun logoutClient() {
        // TODO: do we need to log out?
    }

    fun onSocketMessage() {
        //
    }

    override fun dispose() {
        logoutClient()
        socket.close()
    }
}
