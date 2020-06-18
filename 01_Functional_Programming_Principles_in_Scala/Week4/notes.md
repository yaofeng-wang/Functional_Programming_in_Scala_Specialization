A **pure object-oriented language** is one in which every value is an object.

If the language is based on the classes, this means that the type of each value is a class.

Is Scala a pure object-oriented language?

At first glance, there seem to be some exception: primitive types, functions.

But let's look closer:

Conceptually, type such as `Int` or `Boolean` do not receive special treatment in Scala. They are like the other classes, defined in the package `scala`.

For reasons of efficiency, the Scala complier represents the value of type `scala.Int` by 32-bit integers, and the values of type `scala.Boolean` by Java Booleans, etc.

The `Boolean` type maps to the JVM's primitive type `boolean`. But one could define it as a class from first principles:

```
package idealized.scala
abstract class Boolean {
	def ifThenElse[T] (t: => T, e: => T): T
	// if (condition) te 
	// else ee
	// condition.ifThenEsle(te, ee)

	def && (x: => Boolean): Boolean = ifThenElse(x, false)
	// if the condition that calls && is true then return the expression
	// x, else return false.
	def || (x: => Boolean): Boolean = ifThenElse(true, x)
	// if the condition that calls || is true then return true, else
	// return false.
	def unary_!: Boolean            = ifThenElse(false, true)
	// if the condition that calls unary_! is true then return false,
	// else return true

	def == (x: Boolean): Boolean 	= ifThenElse(x, x.unary_!)
	// if the condition that calls == is true then return x, else
	// return x.unary_!

	def != (x: Boolean): Boolean 	= ifThenElse(x. unary_!, x)
	// if the condition that calls != is true then return x.unary_!, else
	// return x
}
```

Here are constants `true` and `false` that go with `Boolean` in the `idealized.scala`:

```
package idealized.scala

object true extends Boolean {
	def ifThenElse[T](t: => T, e: => T) = t
	// if (true) te else ee
	//                 = te
}

object false extends Boolean {
	def ifThenElse[T](t: => T, e: => T) = e
}
```

Exercise
Provide an implementation of the comparison operator `<` in class `idealized.scala.Boolean`.

Assume for this that `false < true`. 

```
def < (x: Boolean): Boolean = ifThenElse(false, x)


```

Here is a partial specification of the class `scala.Int`.
```
class Int {
	def + (that: Double): Double
	def + (that: Float): Float
	def + (that: Long): Long
	def + (that: Int): Int  		// same for -, *, /, %

	def << (cnt: Int): Int       	// same for >>, >>> 

	def & (that: Long): Long
	def & (that: Int): Int 			// same for |, ^

	def == (that: Double): Boolean
	def == (that: Float): Boolean
	def == (that: Long): Boolean 	// same for !=, <, >, <=, >=
}
```

Can `scala.Int` be represented as a class from first principles i.e. we can implement the methods without using primitive `ints`?


Exercise
Provide an implmentation fo the abstract class `Nat` that represents non-negative integers.
```
abstract class Nat {
	def isZero: Boolean
	def predecessor: Nat
	def successor: new Succ(this)
	def + (that: Nat): Nat
	def - (that: Nat): Nat
}

```

Do not use standard numerical classes in this implementation.
Rather, implement a sub-object and a sub-class:

```
object Zero extends Nat
class Succ(n: Nat) extends Nat
```

One for the number zero, the other for strictly positive numbers.
(this one is a bit more involved than previous quizzes)

```
// Peano numbers
abstract class Nat {

	def isZero: Boolean =
	def predecessor: Nat
	def successor: Nat
	def + (that: Nat): Nat
	def - (that: Nat): Nat
}

object Zero extends Nat {
	def isZero: Boolean = true
	def predecessor: Nat: throw new Error("0.predecessor")
	def + (that: Nat): Nat = that
	def - (that: Nat): Nat = if (that.isZero) this else throw new Error("negative number")
}

class Succ(n: Nat) extends Nat {
	def isZero: Boolean: false
	def predecessor: Nat = n
	def + (that: Nat): Nat = new Succ(n + that)
	def - (that: Nat): Nat = if (that.isZero) this else n - that.predecessor
```

From this exercise, we have seen a complete and rather concise definition definition of natural numbers from first principles. We didn't need anything else in there. There's actually a technical term for this construction, it's called "Peano numbers". And starting from these Peano numbers, we could also implement integers so that it includes negative number and even floating point numbers.

This shows that we can encode primitive types as classes.

---

We have seen that Scala's `numeric` types and the `Boolean` type can be implemented like normal classes.

But what about functions?

In fact functon values *are* treated as objects in Scala.

The functio type `A => B` is just an abbreviation for the class `scala.Function1[A, B]`, which is roughly defined as follows.

```
package scala
trait Function[A, B] {
	def apply(x: A): B
}
```

So functions are objects with `apply` methods.

There are also traits `Function2`,  `Function3`, ... for functions which take more parameters (currently up to 22).


An anonymous function such as `(x: Int) => x * x` 

is expanded to:

```
{ class AnonFun extends Function1[Int, Int] {
	def apply(x: Int) = x * x
  }
  new AnonFun

}
```
or, shorter, using `anonymous class syntax`:
```
new Function1[Int, Int] {
	def apply(x: Int) = x * x
}
```

A function call, such as `f(a,b)`, where f is a value of some class type, is expanded to `f.apply(a, b)`

So the OO-translation of
```
val f = (x: Int) => x * x
f(7)
```

would be

```
val f = new Function1[Int, Int] {
	def apply(x: Int) = x * x
}
f.apply(7)
```

Note that a method such as
```
def f(x: Int): Boolean = ...
```
is not itself a function value.

But if `f` used in a place where a Function type is expected, it is converted automaically to the function value

```
(x: Int) => f(x)
```
The transformation from a method to a function is called a "eta-expansion".

or expanded:

```
new Function1[Int, Boolean] {
	def apply(x: Int) = f(x)
}
```

Exercise
In package week 4, define an
```
object List {
	...
}
```
with 3 functions in it so that users can create lists of lengths 0 - 2 using syntax
```
List()			// the empty list
List(1)			// the list with single element 1
List(2, 3)		// the list with elements 2 and 3.
```

```
object list {
	// List()
	def apply[T] = new Nil


	def apply[T](x: T): List[T] = new Cons(x, new Nil)

	// List(1, 2) = List.apply(1, 2)
	def apply[T](x1: T, x2: T): List[T] = new Cons(x1, new Cons(x2, new Nil))



}
```

---

