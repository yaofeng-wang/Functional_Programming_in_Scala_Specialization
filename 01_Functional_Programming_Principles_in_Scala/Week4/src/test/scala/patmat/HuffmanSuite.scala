package patmat

import org.junit._
import org.junit.Assert.assertEquals

class HuffmanSuite {
  import Huffman._

  trait TestTrees {
    val t1 = Fork(Leaf('a',2), Leaf('b',3), List('a','b'), 5)
    val t2 = Fork(Fork(Leaf('a',2), Leaf('b',3), List('a','b'), 5), Leaf('d',4), List('a','b','d'), 9)
  }

  @Test def `weight of a larger tree (10pts)`: Unit =
    new TestTrees {
      assertEquals(5, weight(t1))
    }

  @Test def `chars of a larger tree (10pts)`: Unit =
    new TestTrees {
      assertEquals(List('a','b','d'), chars(t2))
    }

  @Test def `string2chars hello world`: Unit =
    assertEquals(List('h', 'e', 'l', 'l', 'o', ',', ' ', 'w', 'o', 'r', 'l', 'd'), string2Chars("hello, world"))

  @Test def `test times`: Unit =
    assertEquals(times(List('a', 'b', 'a')), List(('a', 2), ('b', 1)))

  @Test def `make ordered leaf list for some frequency table (15pts)`: Unit =
    assertEquals(List(Leaf('e',1), Leaf('t',2), Leaf('x',3)), makeOrderedLeafList(List(('x', 3), ('t', 2), ('e', 1))))

  @Test def `test singleton`: Unit =
    new TestTrees {
      assert(!singleton(List()))
      assert(singleton(List(t1)))
      assert(!singleton(List(t1, t2)))
    }

  @Test def `combine of some leaf list (15pts)`: Unit = {
    val leaflist = List(Leaf('e', 1), Leaf('t', 2), Leaf('x', 4))
    assertEquals(List(Fork(Leaf('e',1),Leaf('t',2),List('e', 't'),3), Leaf('x',4)), combine(leaflist))
  }

  @Test def `test until`: Unit = {
    val leaflist = List(Leaf('e', 1), Leaf('t', 2), Leaf('x', 4))
    assertEquals(List(Fork(Fork(Leaf('e',1),Leaf('t',2),List('e', 't'),3), Leaf('x',4), List('e', 't', 'x'), 7)), until(singleton, combine)(leaflist))
  }

  @Test def `test createCodeTree`: Unit = {
    val charlist = List('e', 't', 't', 'x', 'x', 'x', 'x')
    assertEquals(Fork(Fork(Leaf('e',1),Leaf('t',2),List('e', 't'),3), Leaf('x',4), List('e', 't', 'x'), 7), createCodeTree(charlist))
  }

  @Test def `test decode`: Unit = {
    val charlist = List('e', 't', 't', 'x', 'x', 'x', 'x')
    assertEquals(decode(createCodeTree(charlist), List(0, 0, 0, 1, 1)), List('e', 't', 'x'))
  }

  @Test def `decode and encode a very short text should be identity (10pts)`: Unit =
    new TestTrees {
      assertEquals("ab".toList, decode(t1, encode(t1)("ab".toList)))
    }

  @Test def `test convert`: Unit =
    new TestTrees {
      assertEquals(List(('a', List(0)), ('b',List(1))), convert(t1))
  }

  @Test def `test mergeCodeTables`: Unit =
    new TestTrees {
      assertEquals(List(('a', List(0)), ('b', List(1)), ('a', List(0)), ('b', List(1))),
        mergeCodeTables(convert(t1), convert(t1)))
    }

  @Test def `test decode and quickEncode`: Unit =
    new TestTrees {
      assertEquals("ab".toList, decode(t1, quickEncode(t1)("ab".toList)))
    }


  @Rule def individualTestTimeout = new org.junit.rules.Timeout(10 * 1000)
}
