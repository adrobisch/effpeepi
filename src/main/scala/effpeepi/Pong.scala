package effpeepi

trait PongContext[RadiusType] {
  def paddleDim: Pong.Dimension
  def ballRadius: RadiusType
  def canvas: Pong.Dimension
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

  def mirror(vector: Vector2D): (Double, Double) = (vector._1, -vector._2)

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
    val (height,width) = pongContext.canvas

    val topOfBallY = ballY - radius
    val bottomOfBallY = ballY + radius

    val hitCeiling = topOfBallY <= 0
    val hitFloor = bottomOfBallY >= height

    val withDirectionUpdated = if (hitCeiling || hitFloor) {
      state.copy(ballDirection = mirror(state.ballDirection))
    } else state

    // collision with paddles

    // we need to advance the ball
    withDirectionUpdated
  }

}
