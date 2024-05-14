package com.arc.cinexpert

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.arc.cinexpert.chat.ChatRequest
import com.arc.cinexpert.chat.ChatResponse
import com.arc.cinexpert.chat.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChatFragment : Fragment() {

    private lateinit var editTextMessage: EditText
    private lateinit var buttonSend: Button
    private lateinit var textViewResponse: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_chat, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        editTextMessage = view.findViewById(R.id.editTextMessage)
        buttonSend = view.findViewById(R.id.buttonSend)
        textViewResponse = view.findViewById(R.id.textViewResponse)

        buttonSend.setOnClickListener {
            val message = editTextMessage.text.toString()
            if (message.isNotEmpty()) {
                // Deshabilitar el botón para evitar múltiples clics
                buttonSend.isEnabled = false

                // Añadir retardo de 3 segundos
                Handler(Looper.getMainLooper()).postDelayed({
                    sendMessageToOpenAI(message)
                    // Volver a habilitar el botón después de enviar el mensaje
                    buttonSend.isEnabled = true
                }, 3000)
            } else {
                textViewResponse.text = "Please enter a message."
            }
        }
    }

    private fun sendMessageToOpenAI(message: String) {
        val chatRequest = ChatRequest(prompt = message, max_tokens = 50, model = "gpt-3.5-turbo")

        val call = RetrofitClient.openAIService.getCompletion(chatRequest)
        call.enqueue(object : Callback<ChatResponse> {
            override fun onResponse(call: Call<ChatResponse>, response: Response<ChatResponse>) {
                if (response.isSuccessful) {
                    val chatResponse = response.body()
                    textViewResponse.text = chatResponse?.choices?.get(0)?.text ?: "No response"
                } else {
                    textViewResponse.text = "Error: ${response.errorBody()?.string()}"
                }
            }

            override fun onFailure(call: Call<ChatResponse>, t: Throwable) {
                textViewResponse.text = "Failure: ${t.message}"
            }
        })
    }
}
