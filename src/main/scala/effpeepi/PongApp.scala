package effpeepi

import effpeepi.PongInput.{LeftPlayerDown, LeftPlayerUp, RightPlayerDown, RightPlayerUp}
import org.scalajs.dom
import org.scalajs.dom.html
import org.scalajs.dom.html.Canvas

object PongApp {
  type Ctx2D =
    dom.CanvasRenderingContext2D

  def main(args: Array[String]): Unit = {
    val pongContext = DefaultPongContext(5.0, Math.Vector2D(640, 480))
    var gameState: GameState = Pong.initalState(pongContext)

    PongInput.init(dom.window.document) { action =>
      gameState match {
        case current: CurrentState =>
          action match {
            case RightPlayerUp => gameState = current.copy(rightPlayerPos = current.rightPlayerPos - 10)
            case RightPlayerDown => gameState = current.copy(rightPlayerPos = current.rightPlayerPos + 10)
            case LeftPlayerUp => gameState = current.copy(leftPlayerPos = current.leftPlayerPos - 10)
            case LeftPlayerDown => gameState = current.copy(leftPlayerPos = current.leftPlayerPos + 10)
            case _ =>
          }
        case _ =>
      }
    }

    val canvas: Canvas = dom.window.document.getElementById("game-canvas").asInstanceOf[html.Canvas]

    PongGraphics.drawGame(canvas, gameState, pongContext)

    var loopHandle: Option[Int] = None

    loopHandle = Some(dom.window.setInterval( () => {
      gameState = Pong.applyTime(5, gameState, pongContext)
      gameState match {
        case GameOver => loopHandle.foreach(dom.window.clearInterval)
        case _ =>
      }
      PongGraphics.drawGame(canvas, gameState, pongContext)
    }, 50))
  }

}
