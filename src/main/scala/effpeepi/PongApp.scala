package effpeepi

import org.scalajs.dom
import org.scalajs.dom.html
import org.scalajs.dom.html.Canvas

object PongApp {
  def main(args: Array[String]): Unit = {
    val pongContext = DefaultPongContext(5.0)
    val gameState = Pong.initalState

    val canvas: Canvas = dom.window.document.getElementById("game-canvas").asInstanceOf[html.Canvas]
    drawGame(canvas, gameState, pongContext)
  }

  def drawGame(c: html.Canvas, gameState: CurrentState, pongContext: DefaultPongContext): Unit = {
      type Ctx2D =
        dom.CanvasRenderingContext2D
      val ctx = c.getContext("2d")
        .asInstanceOf[Ctx2D]
    val w = pongContext.canvas.x.toInt

      c.width = w
      c.height = pongContext.canvas.y.toInt

      ctx.strokeStyle = "red"
      ctx.lineWidth = 3
      ctx.beginPath()
      ctx.moveTo(w/3, 0)
      ctx.lineTo(w/3, w/3)
      ctx.moveTo(w*2/3, 0)
      ctx.lineTo(w*2/3, w/3)
      ctx.moveTo(w, w/2)
      ctx.arc(w/2, w/2, w/2, 0, 3.14)

      ctx.stroke()
  }
}
