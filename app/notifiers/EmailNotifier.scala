package notifiers

import com.typesafe.plugin._
import play.api.Play.current
import play.api.Play
import play.Logger

object EmailNotifier {
  
  def sendLoginMail(recipient: String) = {
    val mail = use[MailerPlugin].email
    mail.setSubject("Login Notification")
    mail.addRecipient(recipient)
    mail.addFrom("noreply@reminder.com")
    mail.send("You have logged in to our service. Welcome dude!")
  }
  
}