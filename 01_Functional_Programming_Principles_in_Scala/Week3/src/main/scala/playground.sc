import objsets._
import scala.language.postfixOps

def time[R](block: => R): R = {
  val t0 = System.nanoTime()
  val result = block    // call-by-name
  val t1 = System.nanoTime()
  println("Elapsed time: " + (t1 - t0) / 1000000 + "s")
  result
}



val result = time { sum }


