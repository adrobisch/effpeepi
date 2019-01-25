package effpeepi

import cats.effect.IO
import fs2.Stream

object StreamTestApp extends App {
  val s1 = Stream.emit(0).repeat.flatMap(current => Stream.emit(current + 1)).take(42)

  IO {
    println(s1.compile.toList)
  }.unsafeRunSync()
}
