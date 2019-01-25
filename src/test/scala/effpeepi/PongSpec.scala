package effpeepi

import effpeepi.Math.Vector2D
import org.scalatest.{FlatSpec, Matchers}

class PongSpec extends FlatSpec with Matchers {
  "Pong" should "mirror a vector" in {
    Math.mirror(Vector2D(1,1), X_AXIS) should be((1, -1))
  }
}
