package objsets

import org.junit._
import org.junit.Assert.assertEquals
import scala.language.postfixOps

class TweetSetSuite {
  trait TestSets {
    val set1 = new Empty
    val set2 = set1.incl(new Tweet("a", "a body", 20))
    val set3 = set2.incl(new Tweet("b", "b body", 20))
    val c = new Tweet("c", "c body", 7)
    val d = new Tweet("d", "d body", 9)
    val set4c = set3.incl(c)
    val set4d = set3.incl(d)
    val set5 = set4c.incl(d)
  }

  def asSet(tweets: TweetSet): Set[Tweet] = {
    var res = Set[Tweet]()
    tweets.foreach(res += _)
    res
  }

  def size(set: TweetSet): Int = asSet(set).size

  @Test def `filter: on empty set`: Unit =
    new TestSets {
      assertEquals(0, size(set1.filter(tw => tw.user == "a")))
    }

  @Test def `filter: a on set5`: Unit =
    new TestSets {
      assertEquals(1, size(set5.filter(tw => tw.user == "a")))
    }

  @Test def `filter: twenty on set5`: Unit =
    new TestSets {
      assertEquals(2, size(set5.filter(tw => tw.retweets == 20)))
    }

  @Test def `union: set4c and set4d`: Unit =
    new TestSets {
      assertEquals(4, size(set4c.union(set4d)))
    }

  @Test def `union: with empty set1`: Unit =
    new TestSets {
      assertEquals(4, size(set5.union(set1)))
    }

  @Test def `union: with empty set2`: Unit =
    new TestSets {
      assertEquals(4, size(set1.union(set5)))
    }

  trait TestSets2 {
    val set1 = new Empty
    val firstTweet = new Tweet("a", "a body", 20)
    val set2 = set1.incl(firstTweet)
    val set_empty = set2.remove(firstTweet)
    val set3 = set2.incl(new Tweet("b", "b body", 10))
    val set4 = set3.incl(new Tweet("c", "c body", 7))
    val mostTweeted = new Tweet("d", "d body", 22)
    val set5 = set4.incl(mostTweeted)
  }

  @Test def `mostRetweeted: Tweet with 22 retweets`: Unit = {
    new TestSets2 {
      assertEquals(set5.mostRetweeted, mostTweeted)
    }
  }

  @Test(expected = classOf[java.util.NoSuchElementException])
  def`mostRetweeted: raise assertion error`: Unit = {
    new TestSets2 {
        set_empty.mostRetweeted
    }
  }

  @Test def `descending: set5`: Unit = {
    new TestSets {
      val trends = set5.descendingByRetweet
      assert(!trends.isEmpty)
      assert(trends.head.user == "a" || trends.head.user == "b")
    }
  }

  @Test
  def `filter on GoogleVSApple`: Unit = {
    def time[R](block: => R): R = {
      val t0 = System.nanoTime()
      val result = block    // call-by-name
      val t1 = System.nanoTime()
      println("Elapsed time: " + (t1 - t0)/1000000 + "ms")
      result
    }
    time {
      val googleTweets: TweetSet = GoogleVsApple.googleTweets
      val appleTweets: TweetSet = GoogleVsApple.appleTweets
      val trending: TweetList = (googleTweets union appleTweets).descendingByRetweet
    }
  }


  @Rule def individualTestTimeout = new org.junit.rules.Timeout(10 * 1000)
}
