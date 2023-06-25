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
    override fun toString(): String {
        return "user#$idSender sent to user#$idRecipient sms $text"
    }
}

data class Chat(
    val idChat: Int = ++uniqueChat,
    var aliveChat: Boolean = true,
    var idFirstUser: Int = 0,
    var idSecondUser: Int = 0,
    val listMessage: MutableList<Message> = mutableListOf()
)

var listAllMessages: MutableList<Message> = mutableListOf()
var listAllChats: MutableList<Chat> = mutableListOf()

var uniqueIdMessage = -1
var uniqueChat = -1

object Service {

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
        val listAliveMessage = listAllChats[idChatForCheck].listMessage.filter { it.aliveMessage }
        if (listAliveMessage.isEmpty()) {
            listAllChats[idChatForCheck].aliveChat = false
        }
    }

    fun deleteChat(idChatForDelete: Int): Boolean {
        if (idChatForDelete in 0..uniqueChat) {
            for (i in 0 until listAllChats[idChatForDelete].listMessage.size) {
                listAllChats[idChatForDelete].listMessage[i].aliveMessage = false
            }
            listAllChats[idChatForDelete].aliveChat = false
            return true
        }
        return false
    }

    fun getUnReadChatsCount(idUser: Int): Int {
        val listMessageThisUserRecipient = listAllMessages.filter {
            it.idRecipient == idUser
                    && it.unReadMessage && it.aliveMessage
        }
        val setChats: HashSet<Chat> = HashSet()
        if (listMessageThisUserRecipient.isNotEmpty()) {
            for (element in listMessageThisUserRecipient) {
                setChats.add(listAllChats[element.idChat])
            }
            return setChats.size
        }

        return -1
    }

    fun getChats(idUser: Int): List<Chat> {
        return listAllChats.filter { (it.idFirstUser == idUser || it.idSecondUser == idUser) && it.aliveChat }
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
        return listAllMessages.filter { it.idMessage >= messageId && it.aliveMessage }
    }

    fun getListMessageQuantityMessages(count: Int): List<Message> {
        val idStart: Int = if ((uniqueIdMessage - count) > 0) (uniqueIdMessage - count) else 0
        val listMessage = listAllMessages.filter { it.idMessage >= idStart && it.aliveMessage }
        for (i in idStart..uniqueIdMessage) {
            listAllMessages[i].unReadMessage = false
        }
        return listMessage
    }

    fun getLastMessages(): String {
        val listLastMessages: MutableList<Message> = mutableListOf()
        for (item in listAllChats) {
            if (item.listMessage[item.listMessage.size - 1].aliveMessage) {
                listLastMessages.add(item.listMessage[item.listMessage.size - 1])
            }
        }
        return if (listLastMessages.isNotEmpty()) listLastMessages.toString() else "нет сообщений"
    }
}


fun main(args: Array<String>) {
    val res = Service.createMessage(1, 2, "Hello")
    val mes = Service.getListMessageIdMessage(0)
    val chat = Service.getChats(1)
    println(mes)
    println(chat)
    println(listAllMessages)
    println(res)
    println()
    Service.createMessage(1, 2, "Bye")
    val mes2 = Service.getListMessageIdMessage(1)
    val chat2 = Service.getChats(1)
    println(mes2)
    println(chat2)
    println(listAllMessages)
    println(listAllChats)
    println(uniqueIdMessage)
    println(uniqueChat)
    println()
    Service.createMessage(3, 4, "How are you?")
    val mes3 = Service.getListMessageIdMessage(0)
    val chat3 = Service.getChats(1)
    println(mes3)
    println(chat3)
    println(listAllMessages)
    println(listAllChats)
    println()

    println(Service.getUnReadChatsCount(1))
    println(Service.getUnReadChatsCount(2))
    println(Service.getLastMessages())
    println(Service.getListMessageQuantityMessages(1))
    println(Service.getListMessageIdChat(0))
    println()

    Service.deleteChat(1)
    println(Service.getChats(3))
    println()

    Service.deleteMessage(0)
    println(Service.getListMessageQuantityMessages(0))
    println(Service.getChats(1))
    println(Service.getListMessageIdChat(1))
}