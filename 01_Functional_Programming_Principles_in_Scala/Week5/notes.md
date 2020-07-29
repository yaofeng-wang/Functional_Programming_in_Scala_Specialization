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

A tuple type (T<sub>1</sub>, ..., T<sub>n</sub>) is an abbreviation of the parameterized type scala.Tuplen[T<sub>1</sub>, ..., T<sub>n</sub>].

A tuple expression (e<sub>1</sub>, ..., e<sub>n</sub>) is equivalent to the function application scala.Tuplen(e<sub>1</sub>, ..., e<sub>n</sub>).

A tuple pattern (p<sub>1</sub>, ..., p<sub>n</sub>) is equivalent to the constructor pattern scala.Tuplen(p<sub>1</sub>, ..., p<sub>n</sub>).

## The Tuple class

Here, all `Tuplen` classes are modeled after the following pattern:

```
case class Tuple2[T1, T2](_1: +T1, _2: +T2) {
	override def toString = "(" + _1 + "," + _2 + ")"
}
```

The fields of a tuple can be accessed with names `_1`, `_2`, ...

So instead of the pattern binding `val (label, value) = pair`, 
one could also have written: 

```
val label = pair._1
val label = pair._1
```
But the pattern matching form is generally preferred.

## Exercise

The merge function as given uses a nested pattern match.

This does not reflect the inherent symmetry of the merge algorithm.

Rewrite `merge` using a pattern matching over pairs.

```
def merge(xs: List[Int], ys: List[Int]): List[Int] = 
	(xs, ys) match {
		case (Nil, zs) | (zs, Nil) => zs
		case (x :: xs1, y :: ys1) => 
			if (x < y) x :: merge(xs1, ys) 
			else y :: merge(xs, ys1)
	}
```

---

## Making Sort more General

Problem: How to parameterize `msort` so that it can be used for lists with elements other than `Int`?

```
def msort[T](xs: List[T]): List][T] = ...
```
does not work, because the comparison `<` in `merge` is not defined for abitrary types `T`.

Idea: Parameterize `merge` with the necessary comparison function.

## Parameterization of Sort

The most flexible design is to make the function `msort` polymorphic and to pass the comparison operation as an additional parameter:

```
def msort[T](xs: List[T])(lt: (T, T) => Boolean) = {
	...
	merge(msort(fst))
}
```

Merge then needs to be adapted as follows:

```
def merge(xs: List[T], ys: List[T]) = (xs, ys) match {
	...

	case (x :: xs1, y :: ys1) =>
		if (lt(x, y)) ...
		else ...
}
```
and we can call `msort`:

```
val fruits = List("apple", "pineapple", "orange", "banana")
msort(fruits)((x: String, y: String) => x.compareTo(y) < 0)
```

When we pass in the `lt` function, we can leave out the type of the parameters:

```
msort(fruits)((x, y) => x.compareTo(y) < 0)
```

The compiler is able to figure out that `x` and `y` need to have type 
`String` since `msort` is called on `fruits`, which is a list of `String`. 

## Parameterization with Ordered

There is already a class in the standard library that represents ordering
`scala.math.Ordering[T]` provides ways to compare elements of type `T`. So instead of parameterizing with the `lt` operation directly, we could parameterize with `Ordering` instead.

```
import math.Ordering

def msort[T](xs: List[T])(ord: Ordering) =
	
	def merge(xs: List[T], ys: List[T]) =
		... if (ord.lt(x,y)) ...

		... merge(msort(fst)(ord), msort(snd)(ord))

msort(fruits)(Ordering.String)
```

## Aside: Implicit Parameters
Problem: Passing around `lt` or `ord` values is cumbersome.

We can avoid this by making `ord` an implicit paramter.

```
def msort[T](xs: List[T])(implicit ord: Ordering) =
	
	def merge(xs: List[T], ys: List[T]) =
		... if (ord.lt(x, y)) ...

		... merge(msort(fst), msort(snd)) ... # definition visible at 
		                                      # the point of function call
```
Then calls to `msort` can avoid the ordering parameters:

```
msort(fruits) # defintion of Ordering in companion class
```

The compiler will figure out the right implicit to pass based on the demanded type.

## Rules for Implicit Parameters

Say, a function takes an implicit parameter of type `T`.

The compiler will search an implicit definition that

- is marked `implicit`
- has a type compatible with `T`
- is visible at the point of the function call, or is defined in a companion object associated with `T`.

If there is a single (most specific) definition, it will be taken as actual argument for the implicit parameter.

Otherwise it's an error.

Consider the following line of the definition of `msort`:
```
... merge(msort(fst), msort(snd)) ...

```

Which implicit argument is inserted?
- Ordering.Int
- Ordering.String
- the "ord" parameter of "msort" (Correct)

---

## Recurring Patterns for Computations on Lists

The examples have shown that functions on lists often have similar structures.

We can identify several recurring patterns, like,

- transforming each element in a list in a certain way
- retrieving a list of all elements satisfying a criterion
- combining the elements of a list using an operator

Functional lanuages allow programmers to write generic functions 
that implement patterns such as these using *higher-order functions*.

## Applying a Function to Elements of a List

A common operation is to transform each element of a list and then return the list of results.

For example, to multiply each element of a list by the same factor, you could write:

```
def scaleList(xs: List[Double], factor: Double): List[Double] = xs match {
	case Nil => xs
	case y :: ys => y * factor :: scale(ys, factor)
}
```

## Map

This scheme can be generalized to the method `map` of the `List` class.

A simple way to define `map` is as follows:

```
abstract class List[T] {
	def map[U](f: T => U): List[U] = this match {
		case Nil => this
		case x :: xs => f(x) :: xs.map(f)
	}
}
```

(in fact, the actual definition of `map` is a bit more complicated, because it is tail-recursive, and also because it works for arbitrary collections, not just lists).

Using `map`, `scaleList` can be written more concisely.

```
def scaleList(xs: List[Double], factor: Double) = xs map (x => x * factor)
```

Exercise:

Consider a function to square each element of a list, and `return` the result. 
Complete the two following equivalent definitions of `squareList`.

```
def squareList(xs: List[Int]): List[Int] = xs match {
	case Nil => xs
	case y :: ys => y * y :: squareList(ys)  
}

def squareList(xs: List[Int]): List[Int] = xs map (x => x * x)
```

## Filtering

Another common operation on lists is the selection of all elements 
satisfying a given condition. For example:

```
def posElems(xs: List[Int]): List[Int] = xs match {
	case Nil => xs
	case y :: ys => if (y > 0) y :: posElems(ys) else posElems(ys)
}
```

## Filter

This pattern is generalized by the method `filter` of the `List` class:

```
abstract class List[T] {
	...
	def filter(p: T => Boolean): List[T] = this match {
		case Nil => this
		case x :: xs => if (p(x)) x :: xs.filter(p) else xs.filter(p)
	}
}
```

Using `filter`, `posElems` can be written more concisely.

```
def posElems(xs: List[Int]): List[Int] = xs filter (x => x > 0)

```

## Variation of Filter

Besides filter, there are also the following methods that extract sublists based on a predicate:

- `xs filterNot p`: Same as `xs filter (x => !p(x))`. The list consisting of those elements of `xs` that do not satisfy the predicate `p`.
- `xs partition p`: Same as `(xs filter p`, `xs filterNot p)`, but computed in a single traversal of the list xs.
- `xs takeWhile p`: The longest prefix of list `xs` consisting of elements that all satisfy the predicate `p`.
- `xs dropWhile p`: The remainder of the list `xs` after any leading elements satisfying `p` have been removed.
- `xs span p`: Same as `(xs takeWhile p`, `xs dropWhile p)` but computed in a single traversal of the list `xs`.

## Exercise

Write a function pack that packs consecutive duplicates of list elements into sublists. For instance,

```
pack(List("a", "a", "a", "b", "c", "c", "a"))
```
should give
```
List(List("a", "a", "a"), List("b"), List("c", "c"), List("a"))
```

You can use the following template:

```
def pack[T](xs: List[T]): List[List[T]] = xs match {
	case Nil => xs
	case x :: xs1 =>  
		val (first, rest) = xs span (y => y == x)
		first :: pack(rest)
}
```

## Exercise

Using `pack`, write a function `encode` that produces the run-length encoding of a list.

The idea is to encode `n` consecutive duplicates of an element `x` as a pair `(x, n)`. For instance,

```
encode(List("a", "a", "a", "b", "c", "c", "a"))

```
should give

```
List(("a", 3), ("b", 1), ("c", 2), ("a", 1))

```

```
def encode[T](xs: List[T]): List((T, Int)) = 
	pack(xs).map(ys => (ys.head, ys.length))

```

---

## Reduction of Lists

Another common operation on lists is to combine the elements of a list using a given operator.

For example:

```
sum(List(x1, ..., xn))
product(List(x1, ..., xn))
```
We can implement this with the usual recursive schema:

```
def sum(xs: List[Int]): Int = xs match {
	case Nil => 0
	case y :: ys => y + sum(ys)
}
```

## ReduceLeft

This pattern can be abstracted out using the generic method `reduceLeft`:

`reduceLeft` inserts a given binary operator between adjacent elements of a list:

```
List(x1, ..., xn) reduceLeft op = (... (x1 op x2) op ... ) op xn

```

Using `reduceLeft`, we can simplify:

```
def sum(xs: List[Int]) = (0 :: xs) reduceLeft ((x, y) => x + y)
def product(xs: List[Int]) = (1 :: xs) reduceLeft ((x, y) => x * y)
```

## A Shorter Way to Write Functions

Instead of `((x, y) => x * y)`, we can also write shorter: `(_ * _)`

Every `_` represents a new parameter, going from left to right.

The parameters are defined at the next outer pair of parentheses (or the whole expression if there are no enclosing parentheses).

So, `sum` and `product` can also be expressed like this:

```
def sum(xs: List[Int]) = (0 :: xs) reduceLeft (_ + _)
def product(xs: List[Int]) = (1 :: xs) reduceLeft (_ * _)
``` 

## FoldLeft

The function `reduceLeft` is defined in terms of a more general function, `foldLeft`.

`foldLeft` is like `reduceLeft` but takes an `accumulator`, `z`, as an additional parameter, which is returned when `foldLeft` iss called on an empty list.

```
(List(x1, ..., xn) foldLeft z)(op) = (... (z op x1) op ... ) op xn
```

So, `sum` and `product` can also be defined as follows:
```
def sum(xs: List[Int]) = (xs foldLeft 0) (_ + _)
def product(xs: List[Int]) = (xs foldLeft 1) (_ * _)
```

## Implementations of ReduceLeft and FoldLeft

`foldLeft` and `reduceLeft` can be implemented in class `List` as follows.

```
abstract class List[T] {

	def reduceLeft(op: (T, T) => T): T = this match {
		case Nil => throw new Error("Nil.reduceLeft")
		case x :: xs => (xs foldLeft x)(op)
	}

	def foldLeft[U](z: U)(op: (U, T) => U): U = this match {
		case Nil => z
		case x :: xs => (xs foldLeft op(z, x))(op)
	}
}
```

## FoldRight and ReduceRight

Applications of `foldLeft` and `reduceLeft` unfold on treees that lean to the left.

They have two dual functions, `foldRight` and `reduceRight`, which produces trees which lean to the right, i.e.,

```
List(x1, ..., x{n-1}, xn) reduceRight op = x1 op ( ... (x{n-1} op xn) ...)
(List(x1, ..., xn) foldRight acc)(op) = x1 op ( ... (xn op acc) ...)
```

## Implementation of FoldRight and ReduceRight

They are defined as follows:

```
def reduceRight(op: (T, T) => T): T = this match {
	case Nil => throw new Error("Nil.reduceRight")
	case x :: Nil => x
	case x :: xs => op(x, xs reduceRight op)
}

def foldRight[U](z: U)(op: (T, U) => U): U = this match {
	case Nil => z
	case x :: xs => op(x, (xs foldRight z)(op))
}
```

## Difference between FoldLeft and FoldRight

For operators that are associative and commutative, `foldLeft` and `foldRight` are equivalent (even though they may be a difference in efficiency).

But sometimes, only one of the two operators is appropriate.


## Exercise

Here is another formualtion of `concat`:

```
def concat[T](xs: List[T], ys: List[T]): List[T] = (xs foldRight ys) (_ :: _)

```

Here, it isn't possible to replace `foldRight` by `foldLeft`. Why?

- The types would not work out (Correct)
- The resulting function would not terminate 
- The result would be reversed

---

## Laws of Concat

Recall the concatenation operation `++` on lists.

We would like to verify that concatenation is associative, and that it admits the empty list `Nil` as neutral elements to the left and to the right:

```
(xs ++ ys) ++ xs == xs ++ (ys ++ zs)
xs ++ Nil = xs
Nil ++ xs = xs
```

Question: How can we prove properties like these?

Answer: By *structural induction* on lists.

## Reminder: Natural Induction

Recall the principle of proof by `natural induction`:

To show a property `P(n)` for all the integeres `n >= b`,

- Show that we *P(b)* (*base case*),
- for all integers *n >= b* show the *induction step*:
	if one has *P(n)*, then one also has *P(n + 1)*.

## Example

Given:

```
def factorial(n: Int): Int = 
	if (n == 0) 1                 // 1st clause
	else n * factorial(n-1)       // 2nd clause
```

Show that, for all `n >= 4`, `factorial(n) >= power(2, n)`.

Base case: 4.

This case is established by simple calculations:
`factorial(4) = 24 >= 16 = power(2,4)`

Induction step: n+1.

We have for n>= 4,

```
factorial(n + 1)
>= (n + 1) * factorial(n)        // by 2nd clause in factorial
> 2 * factorial(n)               // by calculating
>= 2 * power(2, n)               // by induction hypothesis
= power(2, n+1)                  // by definition of power
```

## Referential Transparency

Note that a proof can freely apply reduction steps as equalities to some part of a term.

That works because pure functional programs don't have side effects; so that a term is equivalent to the term to which it reduces.

This principle is called *referential transparency*.

## Structural Induction

The principle of structural induction is analogous to natural induction:

To prove a property *P(xs)* for all lists *xs*,

- show that *P(Nil)* holds (*base case*),
- for a list *xs* and some element *x*, show the *induction step*: 
if *P(xs)* hold, then *P(x :: xs)* also holds.

## Example

Let's show that, for lists *xs*, *ys*, *zs*:

```
(xs ++ ys) ++ zs = xs ++ (ys ++ zs)

```

To do this, use structural induction on `xs`. 

From the previous implementation of `concat`,

```
def concat[T](xs: List[T], ys: List[T]) = xs match {
	case List() => ys
	case x :: xs1 => x :: concat(xs1, ys)
}
```

distill two defining clauses of `++`:

```
Nil ++ ys = ys 							// 1st clause
(x :: xs1) ++ ys = x :: (xs1 ++ ys)		// 2nd clause
```

## Base Case

Base case: Nil

For the left-hand side we have:
```
(Nil ++ ys) ++ zs 
= ys ++ zs                    // by 1st clause of ++

```

For the right-hand side, we have:
```
Nil ++ (ys ++ zs)
= ys ++ zs                   // by 1st clause of ++

```

This case is therefore established.

## Induction Step
Induction step: x :: xs.

For the left-hand side, we have:

```
((x :: xs) ++ ys) ++ zs
= (x :: (xs ++ ys)) ++ zs       // by 2nd clause of ++
= x :: ((xs ++ ys) ++ zs)       // by 2nd clause of ++
= x :: (xs ++ (ys ++ zs))       // by induction hypothesis
= (x :: xs) ++ (ys ++ zs)       // by 2nd clause of ++
```

## Exercise

Show by induction on  `xs` that `xs ++ Nil = xs`

How many equations do you need for the inductive step?

- 2 (Correct)
- 3
- 4

Base case: Nil
```
Nil ++ Nil
= Nil                    // by 1st clause
```

Induction step: x :: xs
```
(x :: xs) ++ Nil 
= x :: (xs ++ Nil)       // by 2nd clause
= x :: xs                // by induction hypothesis
```

---

## A Law of Reverse

For a more difficult example, let's consider the `reverse` function.

We pick its inefficent definition, because its more amenable to equational proofs:

```
Nil.reverse = Nil                              // 1st clause
(x :: xs).reverse = xs.reverse ++ List(x)      // 2nd clause
```

We'd like to prove the following proposition:

```
xs.reverse.reverse = xs
```

## Proof

By induction on `xs`. The base case is easy:
```
Nil.reverse.reverse
= Nil.reverse                // by 1st clause of reverse
= Nil                        // by 1st clause of reverse
```

For the induction step, let's try:
```
(x :: xs).reverse.reverse
= (xs.reverse ++ List(x)).reverse         // by 2nd clause of reverse
```

We can't do anything more with this expression, therefore we turn to the right-hand side:
```
x :: xs
= x :: xs.reverse.reverse            // by induction hypothesis
```

Both sides are simplified in different expressions.

## To Do

We still need to show:

```
(xs.reverse ++ List(x)).reverse = x :: xs.reverse.reverse
```

Trying to prove it directly by induction doesn't work.

We must instead try to *generalize* the equation. For *any* list `ys`,

```
(ys ++ List(x)).reverse = x :: ys.reverse

```

This equation can be proved by a second indcution argument on `ys`.

## Auxiliary Equation, Base Case

```
(Nil ++ List(x)).reverse           // to show: = x :: Nil.reverse
= List(x).reverse                  // by 1st clause of ++
= (x :: Nil).reverse               // by definition of List
= Nil.reverse ++ List(x)           // by 2nd clause of reverse
= Nil ++ (x :: Nil)                // by 1nd clause of reverse
= x :: Nil                         // by 1st clause of ++
= x :: Nil.reverse                 // by 1st clause of reverse
```

## Auxiliary Equation, Inductive Step
```
((y :: ys) ++ List(x)).reverse           // to show: = x :: (y :: ys).reverse
= (y :: (ys ++ List(x))).reverse         // by 2nd clause of ++
= (ys ++ List(x)).reverse ++ List(y)     // by 2nd clause of reverse
= (x :: ys.reverse) ++ List(y)           // by the induction hypothesis
= x :: (ys.reverse ++ List(y))           // by 1st clause of ++
= x :: (y :: ys).reverse                 // by 2nd clause of reverse
```

This establishes the auxiliary equation, and with it the main proposition.

## Exercise 

Prove the following distribution law for `map` over concatenation.

For any lists `xs`, `ys`, function `f`:

```
(xs ++ ys) map f  = (xs map f) ++ (ys map f)
```
You will need the clauses of `++` as well as the following clauses for `map`:

```
Nil map f = Nil
(x :: xs) map f = f(x) :: (xs map f)
```
