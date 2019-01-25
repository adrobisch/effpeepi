package effpeepi

import org.scalajs.dom.{Document, KeyboardEvent}

object PongInput {
  sealed trait PongAction

  case object RightPlayerUp extends PongAction
  case object RightPlayerDown extends PongAction
  case object LeftPlayerUp extends PongAction
  case object LeftPlayerDown extends PongAction
  case object NoAction extends PongAction

  def init(document: Document)(handler: PongAction => Any): Unit = {
    document.addEventListener("keydown", (event: KeyboardEvent) => {
      val action = event.keyCode match {
        case 38 => RightPlayerUp// arrow up
        case 40 => RightPlayerDown // arrow down
        case 81 => LeftPlayerUp // q
        case 65 => LeftPlayerDown // a
        case _ => NoAction
      }

      handler(action)
    })
  }
}
