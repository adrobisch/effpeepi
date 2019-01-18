package effpeepi

trait PongContext[RadiusType] {
  def paddleDim: Math.Vector2D[Double] = Math.Vector2D(10, 50)
  def ballRadius: RadiusType
  def canvas: Math.Vector2D[Double] = Math.Vector2D(640, 480)
  def paddleMargin: Double = 30.0
}

case class DefaultPongContext(ballRadius: Double) extends PongContext[Double]

trait GameState


final case class CurrentState(score: (Int, Int),
                              leftPlayerPos: Double,
                              rightPlayerPos: Double,
                              ballCenterPos: Math.Vector2D[Double],
                              ballDirection: Math.Vector2D[Double]) extends GameState

case object GameOver extends GameState

trait Axis
case object X_AXIS extends Axis
case object Y_AXIS extends Axis

trait Side
case object LeftSide extends Side
case object RightSide extends Side

trait Collision
case object PaddleCollision extends Collision
case class HorizontalCollision(side: Side) extends Collision
case object VerticalCollision extends Collision
case object NoCollision extends Collision

case class Winner(side: Side)

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

  def mirror(vector: Math.Vector2D[Double], axis: Axis): Math.Vector2D[Double] = {
    val factor = axis match {
      case Y_AXIS => 1
      case X_AXIS => -1
    }
    Math.Vector2D(factor * vector.x,  factor * (-vector.y))
  }

}

object Pong {

  val initalState = CurrentState(
    score = (0,0),
    leftPlayerPos = 0.0,
    rightPlayerPos = 0.0,
    ballCenterPos = Math.Vector2D(0.0, 0.0),
    ballDirection = Math.Vector2D(1.0, 1.0)
  )

  /**
    * @param delta time elapsed since last apply
    */
  def applyTime[R : Numeric](delta: Double,
                             state: CurrentState,
                             pongContext: PongContext[R]): GameState = {

    val onCollisionState = updateStateOnCollision(state, pongContext, collision)

    endGame(state) match {
      case Some(Winner(LeftSide)) => { println("Player 1 won!"); GameOver }
      case Some(Winner(RightSide)) => { println("Player 2 won!"); GameOver }
      case _ => advanceBall(onCollisionState, delta)
    }
  }


  def endGame(state: CurrentState): Option[Winner] = {
    state.score match {
      case (10.0, _) => Some(Winner(LeftSide))
      case (_, 10.0) => Some(Winner(RightSide))
      case _ => None
    }
  }

  def verticalWallCollision(radius: Double, height: Double, state: CurrentState) : Boolean = {
    val topOfBallY = state.ballCenterPos.y - radius
    val bottomOfBallY = state.ballCenterPos.y + radius

    val hitCeiling = topOfBallY <= 0
    val hitFloor = bottomOfBallY >= height

    hitCeiling || hitFloor
  }

  def horizontalWallCollision(radius: Double, width: Double, state: CurrentState) : (Boolean, Boolean) = {
    val leftOfBallX = state.ballCenterPos.x - radius
    val rightOfBallX = state.ballCenterPos.x + radius

    val hitWallLeft = leftOfBallX <= 0
    val hitWallRight = rightOfBallX >= width

    (hitWallLeft, hitWallRight)
  }

  def paddlesCollision[R: Numeric](state: CurrentState, pongContext: PongContext[R]) : Boolean = {
    val width = pongContext.canvas.x
    val leftPaddle = Math.Rectangle(topLeft = Math.Vector2D(pongContext.paddleMargin, state.leftPlayerPos), pongContext.paddleDim)
    val rightPaddle = Math.Rectangle(topLeft = Math.Vector2D(width - pongContext.paddleMargin, state.rightPlayerPos), pongContext.paddleDim)

    leftPaddle.overlaps(state.ballCenterPos) || rightPaddle.overlaps(state.ballCenterPos)
  }

  def collision[R: Numeric](state: CurrentState, pongContext: PongContext[R]) : Collision = {
    val radiusNumeric = implicitly[Numeric[R]]

    val radius = radiusNumeric.toDouble(pongContext.ballRadius)
    val (width, height) = (pongContext.canvas.x, pongContext.canvas.y)

    if(paddlesCollision(state, pongContext)){
      return PaddleCollision
    }

    if(verticalWallCollision(radius, height, state)){
      return VerticalCollision
    }

    horizontalWallCollision(radius, width, state) match {
      case (true, false) => return HorizontalCollision(LeftSide)
      case (false, true) => return HorizontalCollision(RightSide)
    }

    return NoCollision
  }

  def updateStateOnCollision[R: Numeric](state: CurrentState, pongContext: PongContext[R], collisionDetector: (CurrentState, PongContext[R]) => Collision) : CurrentState = {
    collisionDetector(state, pongContext) match {
      case NoCollision => state
      case PaddleCollision => state.copy(ballDirection = Math.mirror(state.ballDirection, X_AXIS))
      case VerticalCollision => state.copy(ballDirection = Math.mirror(state.ballDirection, Y_AXIS))
      case col: HorizontalCollision => updateStateOnScore(state, col)
    }
  }

  def updateStateOnScore(state: CurrentState, collision: HorizontalCollision) : CurrentState = {
    state.copy(
      score = if(collision.side == LeftSide) (state.score._1+1,state.score._2) else (state.score._1,state.score._2+1),
      ballCenterPos = Math.Vector2D(0.0, 0.0),
      ballDirection = Math.Vector2D(1.0, 1.0),
      leftPlayerPos = 0.0,
      rightPlayerPos = 0.0,
    )
  }

  def advanceBall(state: CurrentState, delta: Double) : CurrentState = {
    state.copy(ballCenterPos = state.ballCenterPos + (state.ballDirection * delta))
  }
}
