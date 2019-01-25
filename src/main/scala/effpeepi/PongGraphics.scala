package effpeepi

import effpeepi.PongApp.Ctx2D
import org.scalajs.dom
import org.scalajs.dom.html

object PongGraphics {
  def drawGame(c: html.Canvas, gameState: GameState, pongContext: DefaultPongContext): Unit = gameState match {
    case current: CurrentState =>
      val ctx = c.getContext("2d")
        .asInstanceOf[Ctx2D]
      val w = pongContext.canvas.x.toInt

      c.width = w
      c.height = pongContext.canvas.y.toInt

      drawPaddle(ctx, pongContext.paddleMargin, current.leftPlayerPos, pongContext.paddleDim)
      drawPaddle(ctx, c.width - pongContext.paddleMargin, current.rightPlayerPos, pongContext.paddleDim)
      drawMiddleLine(ctx, c.width, c.height)
      drawBall(ctx, current.ballCenterPos.x, current.ballCenterPos.y, pongContext.ballRadius)
      drawScore(ctx, current, pongContext)
    case GameOver =>
      dom.window.alert("Game Over!")
  }

  def drawScore(ctx: Ctx2D, currentState: CurrentState, pongContext: PongContext[_]) = {
    ctx.font = "20pt Major Mono Display"
    ctx.fillText(currentState.score._1.toString, pongContext.canvas.x / 2 - 70, 30)
    ctx.fillText(currentState.score._2.toString, pongContext.canvas.x / 2 + 50, 30)
  }

  def drawBall(ctx: Ctx2D, x: Double, y: Double, radius: Double) = {
    ctx.beginPath()
    ctx.arc(x, y, radius, 0, 2 * scala.math.Pi, false)
    ctx.fillStyle = "black"
    ctx.fill()
    ctx.lineWidth = 5
    ctx.strokeStyle = "#000000"
    ctx.stroke()
  }

  def drawMiddleLine(ctx: Ctx2D, width: Double, height: Double) = {
    ctx.beginPath()
    ctx.moveTo(width / 2, 0)
    ctx.lineTo(width / 2, height)
    ctx.stroke()
  }

  def drawPaddle(ctx: Ctx2D, x: Double, y: Double, dimension: Math.Vector2D[Double]) = {
    ctx.beginPath()
    ctx.rect(x, y, dimension.x, dimension.y)
    ctx.fillStyle = "white"
    ctx.fill()
    ctx.lineWidth = 2
    ctx.strokeStyle = "black"
    ctx.stroke()
  }
}
