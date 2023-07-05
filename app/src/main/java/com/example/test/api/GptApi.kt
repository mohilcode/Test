package com.example.test.api


import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.chat.*
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import com.example.test.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

interface GptApi {
    suspend fun translate(text: String): GptResponse?
}

class GptApiClient: GptApi {

    companion object {
        private const val CHAT_GPT_API_KEY = BuildConfig.GPT_API_KEY
    }

    @OptIn(BetaOpenAI::class)
    override suspend fun translate(text: String): GptResponse? = withContext(Dispatchers.IO) {
        val openAI = OpenAI(CHAT_GPT_API_KEY)
        val instruction = "Condense and translate the product's name and description into one short English sentence. Format: 'Product Name: Product Description'. Emphasize essential features; disregard quantity, packaging, or other non-essential details. If no description is available, provide only the translated product name."
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
}

data class GptResponse(
    val translatedText: String
)

//package com.example.test.api
//
//import com.github.kittinunf.fuel.httpPost
//import com.github.kittinunf.fuel.gson.responseObject
//import com.github.kittinunf.result.Result
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.withContext
//import org.json.JSONObject
//import com.example.test.BuildConfig
//import com.google.gson.JsonParser
//
//interface GptApi {
//    suspend fun summarize(text: String): GptResponse?
//}
//
//class GptApiClient: GptApi {
//
//    companion object {
//        private const val COHERE_API_KEY = BuildConfig.GPT_API_KEY
//    }
//
//    override suspend fun summarize(text: String): GptResponse? = withContext(Dispatchers.IO) {
//        val cohereUrl = "https://api.cohere.ai/summarize"
//        val headers = mapOf("Authorization" to "Bearer $COHERE_API_KEY")
//        val body = mapOf(
//            "model" to "summarize-medium",
//            "prompt" to text,
//            "temperature" to 3.5,
//            "length" to "medium"
//        )
//
//        try {
//            val (_, response, result) = cohereUrl.httpPost()
//                .header(headers)
//                .header("Content-Type", "application/json")
//                .body(JSONObject(body).toString())
//                .response()
//
//            println("Raw server response: ${String(response.data)}")
//
//            when (result) {
//                is Result.Failure<*> -> {
//                    val ex = result.getException()
//                    ex.printStackTrace()
//                    null
//                }
//                is Result.Success<*> -> {
//                    val dataString = String(result.get())
//                    val jsonElement = JsonParser.parseString(dataString)
//                    val summary = jsonElement.asJsonObject.get("output").asJsonObject.get("summary").asString
//                    return@withContext GptResponse(summary)
//                }
//            }
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//        return@withContext null
//    }
//
//}
//
//data class GptResponse(
//    val summarizedText: String
//)


