package funsets

import org.junit._

/**
 * This class is a test suite for the methods in object FunSets.
 *
 * To run this test suite, start "sbt" then run the "test" command.
 */
class FunSetSuite {

  import FunSets._

  @Test def `contains is implemented`: Unit = {
    assert(contains(x => true, 100))
  }

  /**
   * When writing tests, one would often like to re-use certain values for multiple
   * tests. For instance, we would like to create an Int-set and have multiple test
   * about it.
   *
   * Instead of copy-pasting the code for creating the set into every test, we can
   * store it in the test class using a val:
   *
   *   val s1 = singletonSet(1)
   *
   * However, what happens if the method "singletonSet" has a bug and crashes? Then
   * the test methods are not even executed, because creating an instance of the
   * test class fails!
   *
   * Therefore, we put the shared values into a separate trait (traits are like
   * abstract classes), and create an instance inside each test method.
   *
   */

  trait TestSets {
    val s1 = singletonSet(1)
    val s2 = singletonSet(2)
    val s3 = singletonSet(3)
  }

  /**
   * This test is currently disabled (by using @Ignore) because the method
   * "singletonSet" is not yet implemented and the test would fail.
   *
   * Once you finish your implementation of "singletonSet", remove the
   * @Ignore annotation.
   */
  @Test def `singleton set one contains one`: Unit = {

    /**
     * We create a new instance of the "TestSets" trait, this gives us access
     * to the values "s1" to "s3".
     */
    new TestSets {
      /**
       * The string argument of "assert" is a message that is printed in case
       * the test fails. This helps identifying which assertion failed.
       */
      assert(contains(s1, 1), "Singleton")
    }
  }

  @Test def `union contains all elements of each set`: Unit = {
    new TestSets {
      val s = union(s1, s2)
      assert(contains(s, 1), "Union 1")
      assert(contains(s, 2), "Union 2")
      assert(!contains(s, 3), "Union 3")
    }
  }

  trait TestSets2 {
    val s1 = (x: Int) => (x >= 0 && x <= 5)
    val s2 = (x: Int) => (x >= 3 && x <= 8)
  }

  @Test def `intersect contains 3, 4, 5`: Unit = {
    new TestSets2 {
      val s = intersect(s1, s2)
      assert(!contains(s, 2), "Contains 2")
      assert(contains(s, 3), "Does not contain 3")
      assert(contains(s, 4), "Does not contain 4")
      assert(contains(s, 5), "Does not contain 5")
      assert(!contains(s, 6), "Contains 6")
    }
  }

  @Test def `diff contains 0, 1, 2`: Unit = {
    new TestSets2 {
      val s = diff(s1, s2)
      assert(contains(s, 0), "Does not contain 0")
      assert(contains(s, 1), "Does not contain 1")
      assert(contains(s, 2), "Does not contain 2")
      assert(!contains(s, 3), "Contains 3")
    }
  }

  @Test def `filter contains 0, 2, 4`: Unit = {
    new TestSets2 {
      val s = filter(s1, (x: Int) => x % 2 == 0)
      assert(contains(s, 0), "Does not contain 0")
      assert(contains(s, 2), "Does not contain 2")
      assert(contains(s, 4), "Does not contain 4")
    }
  }

  @Test def `forall returns true`: Unit = {
    new TestSets2 {
      assert(forall(s1, (x: Int) => x > -1), "Not all elements in s1 are > -1")

      assert(contains(s1, 0), "s1 does not contain 0")
      assert(!forall(s1, (x: Int) => x > 0), "All elements in s1 are > 0")
    }
  }

  @Test def `exists returns true`: Unit = {
    new TestSets2 {
      assert(contains(s1, 5), "s1 does not contain 5")
      assert(exists(s1, (x: Int) => (x == 5)), "There does not exists a 5 in s1")
    }
  }

  @Test def `map contains 1 to 6`: Unit = {
    new TestSets2 {
      val s = map(s1, (x: Int) => x + 1)
      assert(!contains(s, 0), "Contains 0")
      assert(contains(s, 1), "Does not contain 1")
      assert(contains(s, 2), "Does not contain 2")
      assert(contains(s, 3), "Does not contain 3")
      assert(contains(s, 4), "Does not contain 4")
      assert(contains(s, 5), "Does not contain 5")
      assert(contains(s, 6), "Does not contain 6")
      assert(!contains(s, 7), "Contains 7")
    }
  }

  @Rule def individualTestTimeout = new org.junit.rules.Timeout(10 * 1000)
}
