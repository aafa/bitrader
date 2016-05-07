package app.bitrader

import diode.ActionResult.ModelUpdate
import diode.{ActionHandler, Circuit}

/**
  * Created by Alex Afanasev
  */
object AppCircuit extends Circuit[RootModel] {
  def initialModel = RootModel(Vector.empty[Message])

  val messagesHandler = new ActionHandler(zoomRW(_.messages)((m, v) => m.copy(messages = v))) {
    override def handle = {
      case AddMessage(a) =>
        println(s"AddMessage $a")
        updated(value :+ a)
    }
  }

  override val actionHandler = combineHandlers(messagesHandler)
}

case class RootModel(messages: Vector[Message])

case class Message(t: String)


// actions

case class AddMessage(m: Message)
case class SubscribeToChannel(t: String)
case class UnsubscribeFromChannel(t: String)