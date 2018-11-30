package effpeepi

trait PongContext[RadiusType] {
  def paddleDim: Math.Vector2D[Double] = Math.Vector2D(10, 50)
  def ballRadius: RadiusType
  def canvas: Math.Vector2D[Double] = Math.Vector2D(640, 480)
  def paddleMargin: Double = 30.0
}

case class DefaultPongContext(ballRadius: Double) extends PongContext[Double]

final case class GameState(score: (Int, Int),
                           leftPlayerPos: Double,
                           rightPlayerPos: Double,
                           ballCenterPos: Math.Vector2D[Double],
                           ballDirection: Math.Vector2D[Double])

object Math {
  final case class Vector2D[T](x: T, y: T) {
    def +(other: Vector2D[T])(implicit numeric: Numeric[T]): Vector2D[T] = {
      Vector2D(numeric.plus(x, other.x), numeric.plus(y, other.y))
    }

    def *(scalar: T)(implicit numeric: Numeric[T]): Vector2D[T] = {
      copy(x = numeric.times(scalar, x), y = numeric.times(scalar, y))
    }
  }

  final case class Rectangle(topLeft: Math.Vector2D[Double], dimension: Math.Vector2D[Double]) {
    def overlaps(point: Math.Vector2D[Double]): Boolean = {
      val bottomRight = topLeft + dimension
      point.x >= topLeft.x && point.x <= bottomRight.x && point.y >= topLeft.y && point.y <= bottomRight.y
    }
  }

}

object Pong {

  val initalState = GameState(
    score = (0,0),
    leftPlayerPos = 0.0,
    rightPlayerPos = 0.0,
    ballCenterPos = Math.Vector2D(0.0, 0.0),
    ballDirection = Math.Vector2D(1.0, 1.0)
  )

  trait Axis
  case object X_AXIS extends Axis
  case object Y_AXIS extends Axis

  def mirror(vector: Math.Vector2D[Double], axis: Axis): Math.Vector2D[Double] = {
    val factor = axis match {
      case Y_AXIS => 1
      case X_AXIS => -1
    }
    Math.Vector2D(factor * vector.x,  factor * (-vector.y))
  }

  /**
    * @param delta time elapsed since last apply
    */
  def applyTime[R : Numeric](delta: Double,
                   state: GameState,
                   pongContext: PongContext[R]): GameState = {
    // we need to check for ball collision
    // 1. collision with ceiling

    val radiusNumeric = implicitly[Numeric[R]]

    val radius = radiusNumeric.toDouble(pongContext.ballRadius)
    val (width, height) = (pongContext.canvas.x, pongContext.canvas.y)

    val topOfBallY = state.ballCenterPos.y - radius
    val bottomOfBallY = state.ballCenterPos.y + radius

    val hitCeiling = topOfBallY <= 0
    val hitFloor = bottomOfBallY >= height

    // collision with the wall
    val withDirectionUpdated = if (hitCeiling || hitFloor) {
      state.copy(ballDirection = mirror(state.ballDirection, Y_AXIS))
    } else state

    // collision with paddles
    val leftPaddle = Math.Rectangle(topLeft = Math.Vector2D(pongContext.paddleMargin, state.leftPlayerPos), pongContext.paddleDim)
    val rightPaddle = Math.Rectangle(topLeft = Math.Vector2D(width - pongContext.paddleMargin, state.rightPlayerPos), pongContext.paddleDim)

    val withPaddleCollision = if (leftPaddle.overlaps(state.ballCenterPos) || rightPaddle.overlaps(state.ballCenterPos)) {
      withDirectionUpdated.copy(ballDirection = mirror(state.ballDirection, X_AXIS))
    } else withDirectionUpdated

    // we need to advance the ball
    withPaddleCollision.copy(ballCenterPos = withPaddleCollision.ballCenterPos + (withPaddleCollision.ballDirection * delta))
  }

}
