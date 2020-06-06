val tolerance = 0.0001

def isCloseEnough(x: Double, y: Double) =
  math.abs((x-y)/x)/x < tolerance

def fixedPoint(f: Double => Double)(firstGuess: Double) = {
  @scala.annotation.tailrec
  def iterate(guess: Double): Double={
    val next = f(guess)
    if (isCloseEnough(guess, next)) next
    else iterate(next)
  }
  iterate(firstGuess)
}

def averageDamp(f: Double=>Double)(x: Double) = (x + f(x))/2

def sqrt(x: Int) = fixedPoint(averageDamp(y=>x/y))(1.0)
sqrt(2)

