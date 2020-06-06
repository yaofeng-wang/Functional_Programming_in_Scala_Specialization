package recfun

object RecFun extends RecFunInterface {

  def main(args: Array[String]): Unit = {
    println("Pascal's Triangle")
    for (row <- 0 to 10) {
      for (col <- 0 to row)
        print(s"${pascal(col, row)} ")
      println()
    }
  }

  /**
   * Exercise 1
   */
  def pascal(c: Int, r: Int): Int = {
    if ((c == r) || (c == 0)) 1
    else pascal(c-1, r-1) + pascal(c, r-1)
  }

  /**
   * Exercise 2
   */
  def balance(chars: List[Char]): Boolean = {

    def f(chars: List[Char], num_open_brackets: Int): Boolean = {
      if (chars.isEmpty) num_open_brackets == 0
      else if (chars.head == '(') f(chars.tail, num_open_brackets + 1)
      else if (chars.head == ')') ((num_open_brackets - 1) >= 0) && f(chars.tail, num_open_brackets - 1)
      else f(chars.tail, num_open_brackets)
    }

    f(chars, 0)
  }

  /**
   * Exercise 3
   */
  def countChange(money: Int, coins: List[Int]): Int = {

    if (coins.isEmpty || money < 0) 0
    else if (money == 0) 1
    else countChange(money - coins.head, coins) +
      countChange(money, coins.tail)
  }
}
