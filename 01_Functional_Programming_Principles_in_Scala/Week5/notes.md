## List Methods

Sublists and element access:

- `xs.length`: The number of elements of `xs`.
- `xs.last`: The list's last element, exception if `xs` is empty. 
- `xs.init`: A list consisting of all elements of `xs` except the last one, exception if `xs` is empty.
- `xs take n`: A list consisting of the first `n` elements of `xs`, or `xs` itself if it is shorter than `n`.
- `xs drop n`: A list consisting of the rest of the collection after dropping `n` elements.
- `xs(n)`: (or `xs apply n`). The element of `xs` at index `n`.

Creating new lists:

- `xs ++ ys`: The list consisting of all elements of `xs` followed by all elements of `ys`. (Concatenation)
- `xs.reverse`: The list consisting the elements of `xs` in reversed order.
- `xs updated (n, x)`: The list containing the same element as `xs`, except at index `n` where it contains `x`.

Finding elements:

- `xs indexOf x`: The index of the first element in `xs` equal to `x`, or -1 if `x` does not appear in `xs`.
- xs contains x: same as `xs indexOf >= 0` 

## Implementation of `last`

The complexity of `head` is (small) constant time.

What is the complexity of `last`?

To find out, let's write a possible implementation of `last` as a stand-alone function.

```
def last[T](xs: List[T]): T = xs match {
	case List() => throw new Error("last of empty list")
	case List(x) => x
	case y :: ys => last(ys)
}
```

So, `last` takes steps proportional to the length of the list `xs`.

## Exercise

Implement `init` as an external function, analogous to `last`.

```
def init[T](xs: List[T]): List[T] = xs match {
	case List() => throw new Error("init of empty list")
	case List(x) => List()
	case y :: ys => y :: init(ys)
}
```

So, `init` takes steps proportional to the length of the list `xs`.

## Implementation of `concat`

How can concatentation be implemented?

Let's try by writing a stand-alone function.

```
def concat[T](xs: List[T], ys: List[T]) = xs match {
	case List() => ys
	case z :: zs => z :: concat(zs, ys)
}
```
What is the complexity of concat? Ans: |xs|

## Implementation of `reverse`
How can reverse be implemented?

Let's try by writing a stand-alone function:

```
def reverse[T](xs: List[T]): List[T] = xs match {
	case List() => xs
	case y :: ys => reverse(ys) ++ List(y)
}
```

What is the complexity of `reverse`? Ans: O(N^2) 
(For each element in xs, we call reverse() and concat(), each taking O(N))

## Exercise

Remove the n-th element of a list `xs`. 
If `n` is out of bounds, return `xs` itself.

```
def removeAt[T](xs: List[T], n: Int) = (xs take n) ::: (xs drop n+1)
```

Usage example:
```
removeAt(1, List('a', 'b', 'c', 'd')) >> List('a', 'c', 'd')
```

---

## Sorting Lists Faster

As a non-trivial example, let's design a function to sort lists that is more efficient than insertion sort.

A good alogirhtm for this is `merge sort`. The idea is as follow:

If the list consists of zero or one elements, it is already sorted.

Otherwise:
- Separate the list into two sub-lists, each containing around half of the elements of the original list.
- Sort the two sub-lists.
- Merge the two sorted sub-lists into a single sorted list.

## First MergeSort Implementation

Here is the implementation of that algorithm in Scala:

```
def msort(xs: List[Int]): List[Int] = {
	val n = xs.length/2  // length of xs is 0 or 1
	if (n == 0) xs
	else {
		def merge(xs: List[Int], ys: List[Int]) = xs match {
			case Nil => ys
			case x :: xs1 => ys match {
				case Nil => xs
				case y :: ys1 => if (x < y) x :: merge(xs1, ys) 
								else y :: merge(xs, ys1)

			}
		}
		val (fst, snd) = xs splitAt n
		merge(msort(fst), msort(snd))
	}
}
```

## The `Split` Function

The `splitAt` function on lists returns two sublists
- the elements up to the given index
- the elements from that index

The lists are returned in a *pair*.

## Detour: Pair and Tuples

The pair consisting of `x` and `y` is written `(x, y)` in Scala.

Example:

```
val pair = ("answer", 42) >> pair: (String, Int) = (answer, 42)
```
The type of `pair` above is `(String, Int`.

Pairs can be also used as patterns:

```
val (label, value) = pair >> label: String = answer
							 value: Int = 42
```

This works analogously for tuples with more than two elements.

## Translation of Tuples

A tuple type `(T<sub>1</sub>, ..., T<sub>n</sub>)` is an abbreviation of the parameterized type `scala.Tuplen[T<sub></sub>, ..., T<sub>n</sub>]`.

A tuple expression `(e<sub>1</sub>, ..., e<sub>n</sub>)` is equivalent to the function application `scala.Tuplene(<sub>1</sub>, ..., e<sub>n</sub>)`

A tuple pattern `(p<sub>1</sub>, ..., p<sub>n</sub>)` is equivalent to the constructor pattern `scala.Tuplen(p<sub>1</sub>, ..., p<sub>n</sub>)`.
































