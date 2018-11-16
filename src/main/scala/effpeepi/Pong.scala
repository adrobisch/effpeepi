package effpeepi

trait PongContext[RadiusType] {
  def paddleDim: Pong.Dimension
  def ballRadius: RadiusType
  def canvas: Pong.Dimension // height, width
  def paddleMargin: Double = 30.0
}

final case class GameState(score: (Int, Int),
                           leftPlayerPos: Double,
                           rightPlayerPos: Double,
                           ballCenterPos: Pong.Vector2D,
                           ballDirection: Pong.Vector2D)

object Pong {
  type Vector2D = (Double, Double)
  type Dimension = (Double, Double)

  val initalState = GameState(
    score = (0,0),
    leftPlayerPos = 0.0,
    rightPlayerPos = 0.0,
    ballCenterPos = (0.0, 0.0),
    ballDirection = (1.0, 1.0)
  )

  trait Axis
  case object X_AXIS extends Axis
  case object Y_AXIS extends Axis

  def mirror(vector: Vector2D, axis: Axis): (Double, Double) = {
    val factor = axis match {
      case Y_AXIS => 1
      case X_AXIS => -1
    }
    (factor * vector._1,  factor * (-vector._2))
  }

  /**
    * @param delta time elapsed since last apply
    */
  def applyTime[R : Numeric](delta: Long,
                   state: GameState,
                   pongContext: PongContext[R]): GameState = {
    // we need to check for ball collision
    // 1. collision with ceiling

    val radiusNumeric = implicitly[Numeric[R]]

    val radius = radiusNumeric.toDouble(pongContext.ballRadius)
    val (ballX,ballY) = state.ballCenterPos
    val (height, width) = pongContext.canvas

    val topOfBallY = ballY - radius
    val bottomOfBallY = ballY + radius

    val hitCeiling = topOfBallY <= 0
    val hitFloor = bottomOfBallY >= height

    val withDirectionUpdated = if (hitCeiling || hitFloor) {
      state.copy(ballDirection = mirror(state.ballDirection, Y_AXIS))
    } else state

    // collision with paddles

    val leftPaddle = Rectangle(topLeft = (pongContext.paddleMargin, state.leftPlayerPos), pongContext.paddleDim)
    val rightPaddle = Rectangle(topLeft = (width - pongContext.paddleMargin, state.rightPlayerPos), pongContext.paddleDim)

    val withPaddleCollison = if (leftPaddle.overlaps(state.ballCenterPos) || rightPaddle.overlaps(state.ballCenterPos)) {
      withDirectionUpdated.copy(ballDirection = mirror(state.ballDirection, X_AXIS))
    } else withDirectionUpdated

    // we need to advance the ball
    withPaddleCollison
  }

  final case class Rectangle(topLeft: Vector2D, dimension: Dimension) {
    def overlaps(point: Vector2D): Boolean = ???
  }

}
