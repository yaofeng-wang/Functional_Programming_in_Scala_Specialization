Consider the task of writing a class for sets of integers with the following operations:

```
abstract class IntSet {
  def incl(x: Int): IntSet
  def contains(x: Int): Boolean
```

`IntSet` is an `abstract` class.

Abstract classes can contain members which are missing an implementation (in our case, `incl` and`contains`).

Consequently, no instances of an abstract class can be created with the operator `new`.

Let's consider implmenting sets as binary sub-trees.

There are two types of possible trees: a tree for the empty set and a tree consisting of an integer and two sub-trees.

```
class NonEmpty(elem: Int, left: IntSet, right: IntSet) extends IntSet {
  def contains(x: Int): Boolean =
    if (x < elem) left contains x
    else if (x > elem) right contains x
    else true

  def incl(x: Int): IntSet =
    if (x < elem) new NonEmpty(elem, left incl x, right)
    else if (x > elem) new NonEmpty(elem, left, right incl x)
    else this

  override def toString = "{" + left + elem + right + "}"
}

class Empty extends IntSet {
  def contains(x: Int): Boolean = false
  def incl(x: Int): IntSet = new NonEmpty(x, new Empty, new Empty)
  override def toString = "."
}
```

`Empty` and `NonEmpty` both `extend` the class `IntSet`.

This implies that the types `Empty` and `NonEmpty` *confroms* to the type `IntSet`
- an object of type `Empty' or `NonEmpty` can be used wherever an object of type `IntSet` is required.

IntSet is called the **superclass** of `Empty` and `NonEmpty`.

`Empty` and `NonEmpty` are **subclasses** of `IntSet`.

In Scala, any user-defined class extends another class. If no superclass is given, the standard class `Object` in the Java package `java.lang` is assumed.

The direct and indirect superclasses of a class C are called **base classes** of C.

So, the base classes of `NonEmpty` are `IntSet` and `Object`.

The definitions of `contains` and `incl` in the classes `Empty` and `NonEmpty` *implement* the abstract functions in the base trait `IntSet`.

It is also possible to *redefine* an existing, non-abstract definition in a subsclass by using *override*.
```
abstract class Base {
  def foo = 1
  def bar: Int
}

class Sub extends Base {
  override def foo = 2 
  def bar = 3
}

```

In the `IntSet` example, one could argue that there is really only a single empty `IntSet`.

So it seems overkill to have the user create many instances of it.

We can express this case better with an object definition:
```
object Empty extends IntSet {
  def contains(x: Int): Boolean = false
  def incl(x: Int): IntSet = new NonEmpty(x, Empty, Empty)
```

This defines a *singleton object* named `Empty`.

No other `Empty` instances can be (or need to be) created.

Singleton objects are values, so Empty evaluates to itself.

So far we have executed all Scala code from the REPL or the worksheet.

But it is also possible to create standalone applications in Scala.

Each such application contains an bject with a `main` method.

For instance, here is the "Hello World!" program in Scala.

```
object Hello {
  def main(args: Array[String]) = println("hello world!")
}
```

Once this program is complied, you can start it from the command line with
```
> scala Hello
```

Exercise:
Write a method `union` for forming the union of two sets. You should implement the following abstract class.

```
abstract class IntSet {
  def incl(x: Int): IntSet
  def contains(x: Int): Boolean
  def union(other: IntSet): IntSet
}
```

Object-oriented languages (including Scala) implement **dynamic method dispatch**.

This means that the code invoked by a method call depends on the runtime type of the object that contains the method
Example
```
Empty contains 1
-> [1/x][Empty/this]false 
= false

```
Dynamic dispatch of methods is analogous to calls to higher-order functions
The similarity is that in both cases the code that get executed on a functional method is not known statically. It's not apparent from the name or the type of thing you called. But it is determined by the runtime value that is passed.

Question:
Can we implement one concept in terms of the other?
- Objects in terms of higher-order functions?
- Higher-order functions in terms of objects?

---

Classes and objects are organized in packages.

To place a class or object inside a package, use a package clause at the top of your source file.

```
package progfun.examples

object Hello { ... }
```
This would place `Hello` in the package `progfun.examples.`

You can then refer to `Hello` by its fully qualified name `progfun.examples.Hello`. For instance, to run the `Hello` program:
`scala progfun.examples.Hello`

Example
```
import week3.Rational

new Rational(1, 2)
```
or 
```
new week3.Rational(1, 2)
```

Imports come in serveral forms:
```
import week3.Rational             // imports just Rational
import week3.{Rational, Hello}    // imports both Rational and Hello
import week3._                    // imports everything in package week3
```

The first two forms are called **named imports**.

The last form is called a **wildcard import**.

You can import from either a package or an object.

Some entities are automatically imported in any Scala program.
These are:
- All members of package `scala`
- All members of package `java.lang`
- All members of the singleton object `scala.Predef`

Here are the fully qualifed names of some types and functions which you have seen so far:
```
Int         scala.Int
Boolean     scala.Boolean
Object      java.lang.Object
require     scala.Predef.require
assert      scala.Predef.assert
```

In Java, as well as in Scala, a class can only have one superclass (Single inheritance language).

But what if a class has several natural supertypes to which it conforms or ffrom which it wants to inherit code?

Here, you could use `traits`.

A trait is declared like an abstract class, just with `trait` instead of `abstract class`.

```
trait Planar {
  def height: Int
  def width: Int
  def surface = height * width
```

Class, objects and traits can inherit from at most one class but arbitrary many traits.
Example:
`class Square extends Shape with Planar with Moveable ...`

Traits resemble interfaces in Java, but are more powerful because they can contain fields
and concrete methods.

On the other hand, `traits` cannot have (value) parameters, only `classes` can.

At the top of the type hierarchy we find:
```
Any             the base type of all types
                Methods: '==', '!=', 'equals', 'hashCode', 'toString'

AnyRef          The base type of all reference types; Alias of 'java.lang.Object'

AnyVal          The base type of all primitive types.
```

The Nothing Type
`Nothing` is at the bottom of Scala's type hierarchy. It is a subtype of every other type.

There is no value of type `Nothing`.

Why is that useful?
- To signal abnormal termination
- As an element type of empty collections

Scala's exception handling is similar to Java's.
The expression `throw Exc` aborts evaluation with the exception `Exc`.The type of this expression is Nothing.

Every reference class type also has `null` as a value.

The type of `null` is `Null`.

`Null` is a subtype of every class tht inherits from `Object`; it is incompatible with subtypes of `AnyVal`.
```
val x = null              // x: Null
val y: String = null      // y: String
val z: Int = null         // error: type mismatch
```

What is the type of 
`if (true) 1 else false`
Answer: AnyVal.
Explanation: The `if` expression has return type `Int` and the `else` expression has return type `Boolean`. So we take the most specific type that is a base type of both `Int` and `Boolean`.

---

A fundamental data structure in many functional languages is the immutable linked list.

It is constructed from two building blocks:

`Nil` the empty list.

`Cons` a cell containing an element and the remainder of the list.

Here's an outline of a class hierarchy that represents lists of integers in this fashion:
```
package week4

trait IntList ...
class Cons(val head: Int, val tail: IntList) extends IntList ...
class Nil extends Int List ...
```

A list is either
- an empty list `new Nil`, or
- a list `new Cons(x, xs)` consisting of a `head` element `x` and a `tail` list `xs`.

Note the abbreviation `(val head: Int, val tail: IntList)` in the definition of `Cons`.

This defines at the same time parameters and fields of a `class`.

It is equivalent to:
```
class Cons(_head: Int, _tail: IntList) extends IntList {
  val head = _head
  val tail = _tail
}

```
where `_head` and `_tail` are otherwise unused names.

It seems too narrow to define only lists with `Int` elements.

We'd need another class hierarchy for `Double` lists and so on, one for each possible element type.

We can generalize the definition using a type parameter:
```
package week4 

trait List[T]
class Cons[T](val head: T, val tail: List[T]) extends List[T]
class Nil[T] extends List[T]

```
Type paramters are written in square brackets, e.g.`[T]`.
```
trait List[T] {
  def isEmpty: Boolean
  def head: T
  def tail: List[T]
}

class Cons[T](val head: T, val tail: List[T]) extends List[T] {
  def isEmpty: Boolean = false
}

class Nil extends List[T] {
  def isEmpty: Boolean = true
  def head: Nothing = throw new NoSuchElementException("Nil.head")
  def tail: Nothing = throw new NoSuchElementException("Nil.tail")
}
```

Like classes, functions can have type parameters.

For instance, here is a function that creates a list consisting of a single element.
```
def singleton[T](elem: T) = new Cons[T](elem, new Nil[T])
```

We can then write:
```
singleton[Int](1)
singleton[Boolean](true)
```

In fact, the Scala compiler can usually deduce the correct type parameters from the value arguments of a function call (Type inference).

So, in most cases, type parameters can be left out. You could also write:
```
singleton(1)
singleton(true)
```

Type parameters do not affect evaluation in Scala.

We can assume that all type parameters and type arguments are removed before evaluating the program.

This is also called **type erasure**. Types are only important for the compiler to verify that the program satisfies certain correctness properties, but they're not relevant for the actual execution.

Languagees that use erasure include Java, Scala, Haskell, ML, OCaml.

Some other languages keep the type parameters around at run time these include C++, C#, F#.

Polymorphism means that a function type comes "in many forms".

In programming it means that:
- the function can be applied to arguments of many types, or
- the type can have instances of many types.

We have seen two principal forms of polymorphism:
- subtyping: instances of a subclass can be passed to a base class.
- generics: instances of a function or class are created by type parameterization.

Exercise:
Write a function `nth` that takes an integer `n` and a list and selects
the n'th element of the list.

Elements are numbered from 0.

If index is outside the range from 0 up to the length of the list minus one, a `IndexOutOfBoundException` should be thrown.





















