package com.arc.cinexpert.chat

data class ChatRequest(val prompt: String, val max_tokens: Int, val model: String = "gpt-3.5-turbo")
data class Choice(val text: String)
data class ChatResponse(val choices: List<Choice>)