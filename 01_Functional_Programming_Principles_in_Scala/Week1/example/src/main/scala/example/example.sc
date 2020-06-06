import scala.annotation.tailrec

def abs(x: Double): Double = {
  if (x < 0) -x else x
}

def sqrt(x: Double) : Double = {
  def isGoodEnough(guess: Double, x: Double): Boolean = {
    abs(guess * guess - x) < (x / 1000)
  }

  def improve(guess: Double, x: Double): Double = {
    (guess + x / guess) / 2
  }

  def sqrtIter(guess: Double, x: Double): Double = {
    if (isGoodEnough(guess, x)) guess
    else sqrtIter(improve(guess, x), x)
  }

  sqrtIter(1.0, x)
}



// If x is too small, the returned value is imprecise.
// If x is too large, we get non-termination.
sqrt(0.001)
sqrt(0.1e-20)
sqrt(1.0e20)
sqrt(1.0e50)


def factorial(n : Int) : Int = {
  @tailrec
  def loop(acc: Int, n: Int): Int = {
    if (n == 0) acc
    else loop(acc * n, n - 1)
  }
  loop(1, n)
}
factorial(4)
