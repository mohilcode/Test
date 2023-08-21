package com.example.test.api


import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.chat.*
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import com.example.test.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL
import org.json.JSONObject

interface GptApi {
    suspend fun translate(text: String, language: String): GptResponse?
}

class GptApiClient: GptApi {

    @OptIn(BetaOpenAI::class)
    override suspend fun translate(text: String, language: String): GptResponse? = withContext(Dispatchers.IO) {
        val apikey = fetchGptApiKey() ?: return@withContext null
        val openAI = OpenAI(apikey)
        val instruction = "Condense and translate the product's name and description into one short $language sentence. " +
                "Format: 'Product Name: Product Description'. Emphasize essential features; disregard quantity, packaging, " +
                "or other non-essential details. If no description is available, provide only the translated product name."
        val userMessage = "$instruction\n$text"

        try {
            val chatCompletionRequest = ChatCompletionRequest(
                model = ModelId("gpt-3.5-turbo-0613"),
                messages = listOf(
                    ChatMessage(
                        role = ChatRole.System,
                        content = instruction
                    ),
                    ChatMessage(
                        role = ChatRole.User,
                        content = userMessage
                    )
                )
            )

            val completion: ChatCompletion = openAI.chatCompletion(chatCompletionRequest)

            val response = completion.choices.first().message?.content

            return@withContext GptResponse(response ?: "")
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return@withContext null
    }

    private suspend fun fetchGptApiKey(): String? = withContext(Dispatchers.IO) {
        val encryptUtil = EncryptUtil

        val decryptedUrl = BuildConfig.WUpy2M3lmyPQtMj2LyFBdPRT.let { encryptUtil.decrypt(it) }
        val decryptedToken = BuildConfig.YMNAIjPwiClJnEkLqUzbLTUkM.let { encryptUtil.decrypt(it) }

        val apiUrl = "$decryptedUrl/GPT_API_KEY"
        val url = URL(apiUrl)
        val connection = url.openConnection() as HttpURLConnection

        connection.setRequestProperty("Authorization", decryptedToken)

        try {
            connection.inputStream.bufferedReader().use {
                val json = it.readText()
                val jsonObject = JSONObject(json)
                return@withContext jsonObject.getString("api_key")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            connection.disconnect()
        }

        return@withContext null
    }
}

data class GptResponse(
    val translatedText: String
)

