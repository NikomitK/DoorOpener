package tk.nikomitk.dooropenerhalfnew.messagetypes

data class LoginMessage(val type: String, val pin: Int, val isNewDevice: Boolean)
