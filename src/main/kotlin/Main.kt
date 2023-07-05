import Service.uniqueChat
import Service.uniqueIdMessage
import java.lang.IndexOutOfBoundsException

data class Message(
    val idMessage: Int = ++uniqueIdMessage,
    val text: String,
    var aliveMessage: Boolean = true,
    var unReadMessage: Boolean = true,
    var idRecipient: Int = 0,
    var idSender: Int = 0,
    var idChat: Int = 0
) {
    /*override fun toString(): String {
        return "user#$idSender sent to user#$idRecipient sms $text"
    }*/
}

data class Chat(
    val idChat: Int = ++uniqueChat,
    var aliveChat: Boolean = true,
    var idFirstUser: Int = 0,
    var idSecondUser: Int = 0,
    val listMessage: MutableList<Message> = mutableListOf()
)


object Service {

    var listAllMessages: MutableList<Message> = mutableListOf()
    var listAllChats: MutableList<Chat> = mutableListOf()

    var uniqueIdMessage = -1
    var uniqueChat = -1

    fun clear() {
        listAllMessages = mutableListOf()
        listAllChats = mutableListOf()
        uniqueIdMessage = -1
        uniqueChat = -1
    }

    fun createMessage(idSend: Int, idRecip: Int, text: String): Boolean {
        if (idSend == idRecip) return false

        val resChat: Chat? = listAllChats.find {
            (it.idFirstUser == idSend || it.idFirstUser == idRecip) &&
                    (it.idSecondUser == idSend || it.idSecondUser == idRecip)
        }

        val message: Message
        val newChat: Chat

        if (resChat != null) {
            message = Message(
                text = text, idRecipient = idRecip,
                idSender = idSend, idChat = resChat.idChat
            )
            listAllChats[resChat.idChat].listMessage.add(message)
        } else {
            newChat = Chat(idFirstUser = idRecip, idSecondUser = idSend)
            message = Message(
                text = text, idRecipient = idRecip,
                idSender = idSend, idChat = newChat.idChat
            )
            newChat.listMessage.add(message)
            listAllChats.add(newChat)
        }
        listAllMessages.add(message)
        return true
    }


    fun deleteMessage(idMessageForDelete: Int): Boolean {
        if (idMessageForDelete in 0..uniqueIdMessage) {
            listAllMessages[idMessageForDelete].aliveMessage = false
            checkAliveChat(listAllMessages[idMessageForDelete].idChat)
            return true
        }
        return false
    }

    private fun checkAliveChat(idChatForCheck: Int) {
        val listAliveMessage = listAllChats[idChatForCheck].listMessage
            .filter { it.aliveMessage }

        if (listAliveMessage.isEmpty()) {
            listAllChats[idChatForCheck].aliveChat = false
        }
    }

    fun deleteChat(idChatForDelete: Int): Boolean {
        if (idChatForDelete in 0..uniqueChat) {
            val result = listAllMessages.asSequence()
                .filter { it.idChat == idChatForDelete }
                .onEach { it.aliveMessage = false }

            listAllChats[idChatForDelete].aliveChat = false
            return true
        }
        return false
    }

    fun getUnReadChatsCount(idUser: Int): Int {
        val setChats: HashSet<Int> = HashSet()

        listAllMessages
            .filter { it.idRecipient == idUser && it.unReadMessage && it.aliveMessage }
            .map { it.idChat }
            .onEach { setChats.add(it) }

        return setChats.size
    }


    fun getChats(idUser: Int): List<Chat> {
        return listAllChats
            .filter {
                (it.idFirstUser == idUser || it.idSecondUser == idUser)
                        && it.aliveChat
            }
    }

    fun getListMessageIdChat(chatId: Int): MutableList<Message>? {
        try {
            return if (listAllChats[chatId].aliveChat) listAllChats[chatId].listMessage else null
        } catch (e: IndexOutOfBoundsException) {
            println("Нет чата с ID $chatId")
        }
        return null
    }

    fun getListMessageIdMessage(messageId: Int): List<Message> {
        return listAllMessages
            .filter { it.idMessage >= messageId && it.aliveMessage }
    }

    fun getListMessageQuantityMessages(idChatSearch: Int, idLastMessage: Int, count: Int): String? {
        if (idLastMessage < 0 || idLastMessage > uniqueIdMessage || idChatSearch < 0 || idChatSearch > uniqueChat
        ) {
            return null
        }
        /*val idStop: Int = if ((idLastMessage + count) >
            listAllChats[idChatSearch].listMessage[listAllChats[idChatSearch].listMessage.size - 1].idMessage
        )
            (listAllChats[idChatSearch].listMessage[listAllChats[idChatSearch].listMessage.size - 1].idMessage)
        else (idLastMessage + count)*/

        val result = listAllMessages.asSequence()
            .filter { it.idChat == idChatSearch && it.idMessage >= idLastMessage } //Оставить только те сообщения, у которых совпадает id чата и их собственный id превышает требуемый
            .take(count) //Возьми только нужное количество, или столько сколько получится, если не хватит
            .onEach { it.unReadMessage = false } //для каждого установи unReadMessage в положение false
            .joinToString(separator = "\n") { it.text } // вернуть текст сообщений

        /*val listMessage =
            listAllChats[idChatSearch].listMessage.filter { it.idMessage >= idLastMessage && it.aliveMessage }
        for (i in idLastMessage..idStop) {
            listAllMessages[i].unReadMessage = false
        }*/
        return result
    }

    fun getLastMessages(): String {
        val noMessages: String = "Нет сообщений"
        val result = listAllChats.asSequence()
            .filter { it.listMessage[it.listMessage.size - 1].aliveMessage }
            .joinToString(separator = "\n") { it.listMessage[it.listMessage.size - 1].text }
            .ifEmpty { noMessages }
        //.let { Chat(idChat = -1, listMessage.add(Message(-1,text = noMessages))) }
        return result
        /*for (item in listAllChats) {
            if (item.listMessage[item.listMessage.size - 1].aliveMessage) {
                listLastMessages.add(item.listMessage[item.listMessage.size - 1])
            }
        }
        return if (listLastMessages.isNotEmpty()) listLastMessages.toString() else "нет сообщений"*/
    }
}


fun main(args: Array<String>) {
    println(Service.getLastMessages())
    val res = Service.createMessage(1, 2, "Hello")
    val mes = Service.getListMessageIdMessage(0)
    val chat = Service.getChats(1)
    println(mes)
    println(chat)
    println(Service.listAllMessages)
    println(res)
    println()
    Service.createMessage(1, 2, "Bye")
    val mes2 = Service.getListMessageIdMessage(1)
    val chat2 = Service.getChats(1)
    println(mes2)
    println(chat2)
    println(Service.listAllMessages)
    println(Service.listAllChats)
    println(uniqueIdMessage)
    println(uniqueChat)
    println()
    Service.createMessage(3, 4, "How are you?")
    val mes3 = Service.getListMessageIdMessage(0)
    val chat3 = Service.getChats(1)
    println(mes3)
    println(chat3)
    println(Service.listAllMessages)
    println(Service.listAllChats)
    println()

    println(Service.getUnReadChatsCount(1))
    println(Service.getUnReadChatsCount(2))
    println(Service.getLastMessages())
    println(Service.getListMessageQuantityMessages(1, 1, 5))
    println(Service.getListMessageIdChat(0))
    println()

    Service.deleteChat(1)
    println(Service.getChats(3))
    println()

    Service.deleteMessage(0)
    println(Service.getListMessageQuantityMessages(0, 1, 5))
    println(Service.getChats(1))
    println(Service.getListMessageIdChat(1))
}