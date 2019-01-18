package effpeepi

import org.scalajs.dom
import org.scalajs.dom.html
import org.scalajs.dom.html.Canvas

object PongApp {
  type Ctx2D =
    dom.CanvasRenderingContext2D

  def main(args: Array[String]): Unit = {
    val pongContext = DefaultPongContext(5.0, Math.Vector2D(640, 480))
    var gameState: GameState = Pong.initalState(pongContext)

    val canvas: Canvas = dom.window.document.getElementById("game-canvas").asInstanceOf[html.Canvas]
    drawGame(canvas, gameState, pongContext)

    var loopHandle: Option[Int] = None

    loopHandle = Some(dom.window.setInterval( () => {
      gameState = Pong.applyTime(10, gameState, pongContext)
      gameState match {
        case GameOver => loopHandle.foreach(dom.window.clearInterval)
        case _ =>
      }
      drawGame(canvas, gameState, pongContext)
    }, 100))
  }

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
