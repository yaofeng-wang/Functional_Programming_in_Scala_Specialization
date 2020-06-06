package funsets

object Main extends App {
  import FunSets._
  def forall(s: FunSet, p: Int => Boolean): Boolean = {
    def iter(a: Int): Boolean = {
      val res = p(a)
      println(s"$a: $res")
      if (a>bound) true
      else if (!p(a) || !contains(s, a)) {
        println(a)
        false
      }
      else iter(a+1)
    }
    iter(-1)
  }

  val s1 = (x: Int) => (x >= 0 && x <= 5)
  assert(contains(s1, 0), "Does not contain 0")
  assert(!forall(s1, (x: Int) => x > 5), "Not all elements are > 5 ")
}
