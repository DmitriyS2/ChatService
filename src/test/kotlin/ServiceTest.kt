import org.junit.Test

import org.junit.Assert.*
import org.junit.Before

class ServiceTest {

    @Before
    fun clearBeforeTest() {
        Service.clear()
    }

    @Test
    fun createSMS() {
        val res = Service.createMessage(1, 2, "Hello!")
        assertTrue(res)
    }

    @Test
    fun deleteSMS() {
        Service.createMessage(2, 1, "How are you?")
        val res = Service.deleteMessage(0)
        assertTrue(res)

    }

    @Test
    fun deleteWholeChat() {
        Service.createMessage(3, 4, "Спишь?")
        val res = Service.deleteChat(0)
        assertTrue(res)
    }

    @Test
    fun getUnreadChatCount() {
        Service.createMessage(1, 2, "Hello!")
        Service.createMessage(2, 1, "Hello!")
        Service.createMessage(3, 1, "Bye!")
        val res = Service.getUnReadChatsCount(1)
        assertEquals(2, res)

    }

    @Test
    fun getMeChats() {
        Service.createMessage(1, 2, "Hello!")
        Service.createMessage(2, 1, "Hello!")
        Service.createMessage(3, 1, "Bye!")
        val res = Service.getChats(1)
        val list1: List<Chat> =
            Service.listAllChats.filter { (it.idFirstUser == 1 || it.idSecondUser == 1) && it.aliveChat }
        assertEquals(list1, res)
    }

    @Test
    fun getMeListMessageIdChatNull() {
        val res = Service.getListMessageIdChat(0)
        assertNull(res)
    }

    @Test
    fun getMeListMessageIdChat() {
        Service.createMessage(1, 2, "Hello!")
        val res = Service.getListMessageIdChat(0)
        val list1 = Service.listAllChats[0].listMessage
        assertEquals(list1, res)
    }

    @Test
    fun getMeListMessageIdMessage() {
        Service.createMessage(1, 2, "Hello!")
        val res = Service.getListMessageIdMessage(0)
        val list1 = listOf(Service.listAllMessages[0])
        assertEquals(list1, res)
    }

    @Test
    fun getMeListMessageQuantityMessagesWoSms() {
        val res = Service.getListMessageQuantityMessages(10, 10, 5)
        assertNull(res)
    }

    @Test
    fun getMeListMessageQuantityMessagesWithSms() {
        Service.createMessage(1, 2, "Hello!")
        val res = Service.getListMessageQuantityMessages(0, 0, 5)
        val list1: List<Message> = listOf(Service.listAllMessages[0])
        assertEquals(list1, res)
    }

    @Test
    fun getMeLastMessagesNoSms() {
        val text = "нет сообщений"
        val res = Service.getLastMessages()
        assertEquals(text, res)
    }

    @Test
    fun getMeLastMessagesWithSms() {
        Service.createMessage(1, 2, "Hello!")
        val text = "[user#1 sent to user#2 sms Hello!]"
        val res = Service.getLastMessages()
        assertEquals(text, res)
    }
}