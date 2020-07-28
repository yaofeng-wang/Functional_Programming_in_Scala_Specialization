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
	// if (condition) t 
	// else e
	// is equvialent to
	// condition.ifThenElse(t, e)

	def && (x: => Boolean): Boolean = ifThenElse(x, false)
	// if the condition that calls && is true then return the expression
	// x, else return false.
	def || (x: => Boolean): Boolean = ifThenElse(true, x)
	// if the condition that calls || is true then return true, else
	// return x.
	def unary_!: Boolean = ifThenElse(false, true)
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

Here are constants `true` and `false` that go with `Boolean` in the package `idealized.scala`:

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

Exercise:

Provide an implementation of the comparison operator `<` in class 
`idealized.scala.Boolean`.

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
	def + (that: Int): Int  		  // same for -, *, /, %

	def << (cnt: Int): Int       	// same for >>, >>> 

	def & (that: Long): Long
	def & (that: Int): Int 			  // same for |, ^

	def == (that: Double): Boolean
	def == (that: Float): Boolean
	def == (that: Long): Boolean 	// same for !=, <, >, <=, >=
}
```

We have seen that the operations that we would like to perfrom on `scala.Int` can be expressed as methods above. However, in order to show that `scala.Int` can be represented as objects, we will still need to implement these functions.  


Exercise:

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
abstract class Nat {

	def isZero: Boolean
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

From this exercise, we have seen a complete and rather concise definition of natural numbers from first principles. 
We didn't need anything else in there. 
There's actually a technical term for this construction, it's called `Peano numbers`. And starting from these Peano numbers, we could also implement integers so that it includes negative number and even floating point numbers.

This shows that we can encode primitive types as classes.

---

We have seen that Scala's `numeric` types and the `Boolean` type can be implemented like normal classes.

But what about functions?

In fact functon values *are* treated as objects in Scala.

The function type `A => B` is just an abbreviation for the class `scala.Function1[A, B]`, which is roughly defined as follows.

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

But if `f` used in a place where a Function type is expected, it is converted automatically to the function value

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

In the previous sessions, we have already covered two forms of polymorphism. One was subtyping, which was usually associated with object oriented programming. The other was generics, which came originally from functional programming.

Two principal forms of polymorphism:
- subtyping: we can pass instances of a subtype where a base type was required
- generics: we can parameterize types with other types.

In this session we will look at their interactions.

Two main areas:
- bounds
- variance

Consider the method `assertAllPos` (assert all positive) which
- takes an `IntSet`
- returns the `IntSet` itself if all elements are positive
- throws an exception otherwise

What would be the best type you can give to `assertAllPos`? Maybe:
```
def assertAllPos(s: IntSet): IntSet
```
In most situations this is fine, but can one be more precise?

One might want to express that `assertAllPos` takes `Empty` sets to `Empty` sets and `NonEmpty` sets to `NonEmpty` sets.

A way to express this is:

```
def assertAllPos[S <: IntSet](r: S): S = ...
``` 

Here, `<: IntSet` is an *upper bound* of the type parameter `S`:

It means that `S` can be instantiated only for types that conform to `IntSet`.

Generally, the notation:
- `S <: T` means: `S` is a *subtype* of `T`, and
- `S >: T` means: `S` is a *supertype* of `T`, or `T` is a *subtype* of `S`.

You can also use a lower bound for a type variable. Example, `[S >: NonEmpty]` introduces a type parameter `S` that can range only over *supertypes* of *NonEmpty*.
So `S` could be one of `NonEmpty, IntSet, AnyRef,` or `Any`.

Finally, it is also possible to mix a lower bound with an upper bound. For instance, 
`[S >: NonEmpty <: IntSet]` would restrict `S` any type on the interval between `NonEmpty` and `IntSet`.


Now that we have looked a bounds, there is still another interaction between subtyping and type parameteres we need to consider. Given: `NonEmpty <: IntSet`, is `List[NonEmpty] <: List[IntSet]`?

Intuitively, this makes sense: A list of non-empty sets is a special case of a list of arbitrary sets. 

We call types for which this relationship holds `covariant` because their subtyping relationship varies exactly with the type parameter.

Does covariance make sense for all types, not just for `List`?

For perspective, let's look at arrays in Java (and C#).

Reminder:
- An array of `T` elements is written `T[]` in Java.
- In Scala we use paramterized type syntax Array[T] to refer to the same type.

Arrays in Java are covariant, so one could have: `NonEmpty[] <: IntSet[]`

But covariant array typing causes problems.
To see why, consider the Java code below:
```
NonEmpty[] a = new NonEmpty[]{new NonEmpty(1, Empty, Empty))}
IntSet[] b = a
b[0] = Empty
NonEmpty s = a[0]
```

It looks like we assigned in the last line an `Empty` set to a variable of type `NonEmpty`! What went wrong?

The 3rd line will throw a runtime ArrayStoreException!

When does it make sense for a type to be a subtype of another? And when should that rather not be the case?

The following principle, stated by Barbara Liskov, tells us when a type can be a subtype of another.

> If A <: B, then everything one can do with a value of type B, one should also be able to do with a value of type A.

The actual definition Liskov used is a bit more formal. It says: 

> Let `q(x)` be a property provable about objects `x` of type `B`. Then `q(y)` should be provable for objects `y` of type `A` where `A <: B`.

Exercise:

The problematic array example would be written as follows in Scala:

```
val a: Array[NonEmpty] = Array(new NonEmpty(1, Empty, Empty, Empty))
val b: Array[IntSet] = a
b(0) = Empty
val s: NonEmpty = a(0)
```

When you try out this example, what do you observe?
- A type error in line 1
- A type error in line 2 (Correct answer. Array[NonEmpty] is not a subtype of Array[IntSet])
- A type error in line 3
- A type error in line 4
- A program that compiles and throws an exception at run-time
- A program that compiles and runs without exception

---

You have seen in the previous session that some types shoud be covariant whereas others should not.

Roughly speaking, a type that accepts mutations of its elements should not be covariant.

But immutable types can be covariant, if some conditions on methods are met.

Say `C[T]` is a parameterized type and `A`, `B` are types such that `A <: B`.

In general, there are three possible relationships between `C[A]` and `C[B]`:

- `C[A] <: C[B]` : `C` is *covariant*.
- `C[A] >: C[B]` : `C` is *contravariant*.
- neither `C[A]` nor `C[B]` is a subtype of the other: `C` is *nonvariant*.

Scala lets you declare the variance of a `type` by annotating the type parameter:
```
class C[+A] { ... } // C is covariant
class C[-A] { ... } // C is contravariant
class C[A] { ... } // C is nonvariant
```

Exercise:

Say you have two function types,
```
type A = IntSet => NonEmpty
type B = NonEmpty => IntSet
```

According to the Liskov Substitution Principle, which of the following should be true?
- `A <: B` (True)
- `B <: A`
- `A` and `B`are unrelated

Generally, we have the following rule for subtyping between function types:

If `A2 <: A1` and `B1 <: B2`, then `A1 => B1 <: A2 => B2` 

So functions are `contravariant` in their argument type(s) and `covariant` in their result type.

This leads to the following revised definition of the `Function1` trait:
```
package scala
trait Function1[-T, +U] {
	def apply(x: T): U
}
```

We have seen in the array example that the combination of covariance with certain operations is unsound.

In this case the problematic operation was the update operation on an array.

If we turn `Array` into a class, and `update` into a method, it would look like this:
```
class Array[+T] {
	def update(x: T) ...
}
```

The problematic combination is:
- the covariant type parameter `T`
- which appears in parameter position of the method `update`.


The Scala compiler will check that there are no problematic combinations when compiling a class with variance annotations.

Roughly, 

- *covariant* type parameters can only appear in method results.
- *contravariant* type parameters can only appear in method parameters.
- *invaraint* type parameters can appear anywhere.

The precise rules are a bit more involved, fortunately the Scala compiler performs them for us.

Let's get back to the previous implementation of lists.

One shortcoming was that `Nil` had to be a class, whereas we would prefer it to be an object (after all, there is only one empty list).

Can we change that?

Yes, because we can make `List` covariant.

```
trait List[+T] {
	def isEmpty: Boolean
	def head: T
	def tail: List[T]
}

object Nil extends List[Nothing] {
	def isEmpty: Boolean = true
	def head: Nothing = throw new NoSuchElementException("Nil.head")
	def tail: Nothing = throw new NoSuchElementException("Nil.tail")
}

object test {
	val x: List[String] = Nil
	// We change List[T] to List[+T]
	// since Nothing <: String
	// we have List[Nothing] <: List[String]
	// this allows us to assign Nil (which has a type of List[Nothing]) to a variable of type List[String] 
}
```

Sometimes, we have to put in a bit of work to make a class covariant.

Consider adding a `prepend` method to `List` which prepends a given element, yielding a new list.

A first implementation of `prepend` could look like this:
```
trait List[+T] {
	def prepend(elem: T): List[T] = new Cons(elem, this)
}

```
But that does not work!

Why does the following code not type-check?

```
trait List[+T] {
	def prepend(elem: T): List[T] = new Cons(elem, this)
}
```
Possible answers:
- prepend turns `List` into a mutable class.
- prepend fails variance checking. (True)
- prepend's right-hand side contains a type error.

Indeed, the compiler is right to throw out `List` with prepend, because it violates the Liskov Substitution Principle:

Here's something one can do with a list `xs` of type `List[IntSet]`: 
`xs.prepend(Empty)`

But the same operation on a list `ys` of type `List[NonEmpty]` would lead to a type error for `ys.prepend(Empty)`.
So, `List[NonEmpty]` cannot be a subtype of `List[IntSet]`.

But prepend is a natural method to have on immutable lists!

Question: How can we make it variance-correct?

We can use a *lower bound*:

```
def prepend [U >: T] (elem: U): List[U] = new Cons(elem, this)
```

This passes variance checks, because:
- covariant type paramters may appear in lower bounds of method type parameter
- contravariant type parameters may appear in upper bounds of method.

Implement `prepend` as shown in trait `List`.

```
def prepend [U >: T] (elem: U): List[U] = new Cons(elem, this)
```

What is the result type of this function:

```
def f(xs: List[NonEmpty], x: Empty) = sx prepend x ?
```
Possible answers:
- does not type check
- List[NonEmpty]
- List[Empty]
- List[IntSet]  (Correct. U can be any supertype of NonEmpty and elem must have a type of this supertype. Since Empty is not a supertype NonEmpty, type inference will choose the next higher type for U, IntSet)
- List[Any]

---

Suppose you want to write a small interpreter for arithmetic expressions.

To keep it simple, let's restrict ourselves to numbers and additions.

Expressions can be represented as a class hierarchy, with a base trait `Expr` and two subclasses, `Number` and `Sum`.

To treat an expression, it's necessary to know the expression's shape and it's components.

This brings us to the following implementation.

```
trait Expr {
	def isNumber: Boolean   // classification
	def isSum: Boolean      // classification
	def numVal: Int         // accessor
	def leftOp: Expr        // accessor
	def rightOp: Expr       // accessor
}

class Number(n: Int) extends Expr {
	def isNumber: Boolean  = true
	def isSum: Boolean = false
	def numValue: Int = n
	def leftOp: Expr = throw new Error("Number.leftOp")
	def rightOp: Expr = throw new Error("Number.rightOp")
}

class Sum(e1: Expr, e2: Expr) extends Expr {
	def isNumber: Boolean = false
	def isSum: Boolean = true
	def numValue: Int = throw new Error("Sum.numValue")
	def leftOp: Expr = e1
	def rightOp: Expr = e2
}
```
You can now write an evaluation function as follow.

```
def eval(e: Expr): Int = {
	if (e.isNumber) e.numValue
	else if (e.isSum) eval(e.leftOp) + eval(e.rightOp)
	else throw new Error("Unkown expression " + e)
}
```
Problem: Writing all these classification and accessor functions quickly becomes 
tedious!

So, what happens if you want to add new expression forms, say
```
class Prod(e1: Expr, e2: Expr) extends Expr
class Var(x: String) extends Expr
```
You need to add methods for classification and access to all classes defined above.

To integrate `Prod` and `Var` into the hierarchy, how many new method definitions do you need?

(including method definition in `Prod` and `Var` themselves, but not counting methods that were already given on the slides)

Possible Answers:
- 9
- 10
- 19
- 25 (Correct answer. This is quite a lot just to add 2 new classes.)
- 35
- 40

A "hacky" solution could use type tests and type casts.

Scala let's you do these using methods defined in class `Any`:

```
def isInstanceOf[T]: Boolean // checks whether this object's type conforms to 'T'
def asInstanceOf[T]: T       // treats this object as an instance of type 'T'
              							 // throws 'ClassCastException' if it isn't.
```

These correspond to Java's type tests and casts

Scala:
- x.isInstanceOf[T]
- x.asInstanceOf[T]

Java:
- x instanceof T
- (T) x

But their use in Scala is discouraged, because there are better alternatives.

Here's a formulation fo the `eval` method using type tests and casts:

```
def eval(e: Expr): Int =
	if (e.isInstanceOf[Number])
		e.asInstanceOf[Number].numValue
	else if (e.isInstanceOf[Sum])
		eval(e.asInstanceOf[Sum].leftOp) + eval(e.asInstanceOf[Sum].rightOp)
	else throw new Error("Unknown expression " + e)
```
Assessment of this solution:

(+) no need for classification methods, access methods only for classes where the value is defined.

(-) low-level and potentially unsafe. In general, we may not not know at runtime whether type cast will succeed.


Alternatively, we could look at *Object-Oriented Decomposition*.
For example, suppose that all you want to do is evaluate expressions.

You could then define:
```
trait Expr {
	def eval: Int
}

class Number(n: Int) extends Expr {
	def eval: Int = n
}

class Sum(e1: Expr, e2: Expr) extends Expr {
	def eval: Int = e1.eval + e2.eval
}

```

But what happens if you'd like to display expressions now?
You have to define new methods in all the subclasses, which may not be do-able in a large codebase.

And what if you want to simplify the expressions, say using the rule:

```
a * b + a * c -> a * (b + c)
```

Problem: This is a non-local simplification. It cannot be encapsulated in the method of a single object.

You are back to square one; you need test and access methods for all the different subclasses.

---

Reminder: The task we are trying to solve is to find a general and convenient way to access objects in a extensible class hierarchy.

Attempts seen previously:
- Classification and access methods: quadratic explosion
- Type tests and casts: unsafe, low-level
- Object-oriented decomposition: does not always work, need to touch all classes to add a new method.

In this session, we will see a new way to decompose objects using pattern matching.

Observation: the sole purpose of test and accessor functions is to reverse the construction process:
- which subclass was used?
- what were the arguments of the constuctor?

This situation is so common that many functional languages, Scala included, automates it.

A *case class* definition is similar to a normal class definition, except that it is preceded by the modifier *case*. For example:

```
trait Expr
case class Number(n: Int) extends Expr
case class Number(e1: Expr, e2: Expr) extends Expr
``` 

Like before, this defines a trait `Expr`, and two concrete subclasses `Number` and 
`Sum`.

It also implicitly defines companion objects with `apply` methods.
```
object Number {
	def apply(n: Int) = new Number(n)
}

object Sum {
	def apply(e1: Expr, e2: Expr) = new Sum(e1, e2)
}

```

so you can write `Number(1)` instead of `new Number(1)`. `Number(1)` is equivalent to 
`Number.apply(2)`.

However, these classes are now empty. So how can we access the members?

*Pattern matching* is a generalization of `switch` from C/Java to class hierarchies. 

It's expressed in Scala using the keyword `match`.

Example:

```
def eval(e: Expr): Int = e match {
	case Number(n) => n
	case Sum(e1, e2) => eval(e1) + eval(e2)
}
```

Rules:
- `match` is followed by a sequence of `cases`, `pat => expr`.
- Each case associates an `expression expr` with a `pattern pat`.
- A `MatchError` exception is thrown if no pattern matches the value of the selector.

Patterns are constructed from:
- constructors, e.g. `Number`, `Sum`
- variables, e.g. `n`, `e1`, `e2`
- wildcard pattern `_`
- constants, e.g. `1`,  `true`, `"abc"`, `N` (if `val N = 2`)

Variables always begin with a lowercase letter.
 
The same variable name can only appear once in a pattern. So, `Sum(x, x)` is not a legal pattern.

Names of constants begin with a capital letter, with the exception of the reserved words `null`, `true`, `false`.

An expression of the form `e match { case p1 => e1 ... case pn => en}`

matches the value of the selector `e` with the pattern `p1 ... pn` in the order in which they are written.

The whole match expression is rewritten to the right-hand side of the first case where the pattern matches the selector `e`.

References to pattern variables are replaced by the corresponding parts in the selector.

What do patterns match?
- A constructor pattern `C(p1, ..., pn)` matches all the values of type `C` (or a subtype) that have been constructed with arguments matching the pattern `p1 ... pn`.
- A variable pattern `x` matches any value, and *binds* the name of the variable to this value.
- A constant pattern `c` matches values that are equal to `c` (in the sense of `==`)

Example.

```
eval(Sum(Number(1), Number(2)))

-> eval(Number(1)) + eval(Number(2))
-> 1 + eval(Number(2))
-> 1 + 2
-> 3 
```

Of course, it's also possible to define the evaluation function as a method of the base trait.

Example.
```
trait Expr {
	def eval: Int = this match {
		case Number(n) => n
		case Sum(e1, e2) => e1.eval + e2.eval
	}
}
```

Now, you may wonder what are the trade-offs of the pattern matching method and the previous object-oriented decomposition.

One criteria might be whether you are more often creating new sub-classes of expressions or are you more often creating new methods. It's a criterion that looks at the future extensibility and possible extensions of your system. If what you do is mostly creating new subclasses, then actually the object oriented decomposition solution has the upper hand. The reason is that it's very easy that requires only a local change to create a new sub-class whereas for the functional solution, you'd have to go back to the trait and change the eval function. 

On the other hand, if what you do will create lots of new methods and the class hierarchy wil be kept relatively stable, then pattern matching solution is just a local change whereas in the object oriented decomposition would require a new incrementation in each subclass, so there will be more parts to change.

The problem of extensibiity in two dimensions, where you might want to add new classes to a hierarchy, or you might want to add new methods, or maybe both, has been named the "expression problem".

Exercise.

Write a function `show` that uses pattern matching to return the representation of a given expression as a `string`.

```
def show(e: Expr): String = e match {
	case: Number(x) => x.toString
	case: Sum(l, r) => show(l) + " + " + show(r)
}
```
---

The `list` is a fundamental data structure in functional programming.

A list having `x1, ..., xn` as elements is written `List(x1, ..., xn)`.

Example.
```
val fruit = List("apples", "oranges", "pears")
val nums = List(1, 2, 3, 4)
val diag3 = List(List(1, 0, 0), List(0, 1, 0), List(0, 0, 1))
val empty = List()
```

There are two important differences between lists and arrays.

- Lists are immutable - the elements of a list cannot be changed.
- Lists are recursive, while arrays are flat.

Like arrays, lists are *homogeneous*: the elements of a list must all have the same type.

The type of a list with elements of type `T` is written `scala.List[T]` or shorter just `List[T]`.

Example.
```
val fruit: List[String] = List("apples", "oranges", "pears")
val nums: List[Int] = List(1, 2, 3, 4)
val diag3: List[List[Int]] = List(List(1,0,0), List(0,1,0), List(0,0,1))
val empty: List[Nothing] = List()
```
All lists are constructed from:
- the empty list `Nil`, and
- the construction operation `::` (pronounced cons):
`x :: xs` gives a new list with the first element `x`, 
followed by the elements of `xs`.

For example.

```
fruit = "apples" :: ("oranges" :: ("pears" :: Nil))
nums = 1 :: (2 :: (3 :: (4 :: Nil)))
empty = Nil
```

Convention: Operators ending with ":" associates to the right.
```
A :: B :: C is interpreted as A :: (B :: C)
```
We can thus omit the parentheses in the definition above.

Example.
```
val nums = 1 :: 2 :: 3 :: 4 :: Nil
```

Operators ending in  "::" are also different in the way that they are seen as method calls of the `right-hand` operand.

So the expression above is equivalent to
```
Nil.::(4).::(3).::(2).::(1) // :: is equivalent to the prepend operation
```

All operations on lists can be expressed in terms of the following three operations:
```
head : the first element of the list
tail: the list composed of all the elements except the first.
isEmpty: 'true' if the list is empty, 'false' otherwise.
```

These operations are defined as methods of objects of type `list`. For example:
```
fruit.head == "apples"
fruit.tail.head == "oranges"
diag3.head == List(1,0,0)
empty.head == throw new NoSuchElementException("head of empty list")
```

It is also possible to decompose lists with pattern matching.
```
Nil                     The Nil constant
p :: ps                 A pattern that matches a list with a head matching p and a tail matching ps
List(p1, ..., pn)       same as p1 :: ... :: pn :: Nil
```

Example.

```
1 :: 2 :: xs            List that start with 1 and then 2
x :: Nil                Lists of length 1
List(x)                 Same as x :: Nil
List()                  The empty list, same as Nil
List(2 :: xs)           A list that contains as only element, another list that starts with 2
```

Exercise.
Consider the pattern `x :: y :: List(xs, ys) :: zs`.

What is the condition that describes most accurately the length `L` of the lists it matches?
- L == 3
- L == 4
- L == 5
- L >= 3 (Correct. zs has 0 or more elements)
- L >= 4
- L >= 5

Suppose we want to sort a list of numbers in ascending order:
- One way to sort the list `List(7, 3, 9, 2)` is to sort the tail `List(3, 9, 2)` to obtain `List(2, 3, 9)`.
- The next step is to insert the head 7 in the right place to obtain the result `List(2, 3, 7, 9)`.

This idea describes Insertion Sort:

```
def isort(xs: List[Int]): List[Int] = xs match {
	case List() => List()
	case y :: ys => insert(y, isort(ys))
}
```

Exercise.

Complete the definition insertion sort by filling in the ???s in the definition below:
```
def insert(x: Int, xs: List[Int]): List[Int] = xs match {
	case List() => ???
	case y :: ys => ??? 
}
```

Solution:
```
def insert(x: Int, xs: List[Int]): List[Int] = xs match {
	case List() => List(x)
	case y :: ys => if (x > y) y :: insert(x, ys) else x :: xs
}
```
What is the worst-case complexity of insertion sort relative to the length of the input list `N`?
- the sort takes constant time
- proportional to N
- proportional to N * log(N)
- proportional to N * N

