package effpeepi

import org.scalatest.{FlatSpec, Matchers}

class PongSpec extends FlatSpec with Matchers {
  "Pong" should "mirror a vector" in {
    val aVector: Pong.Vector2D = (1,1)

    Pong.mirrorX(aVector) should be((1, -1))
  }
}
