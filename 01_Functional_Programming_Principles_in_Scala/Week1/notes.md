Recommended Books:
1. Structure and Interpretation of Computer Programs. Harold Abelson and Gerald J. Sussman. 2nd edition. MIT Press 1996
  - A classic. Many parts of the course and quizzes are based on it, but we changed the language from Scheme to Scala)
2. Programming in Scala. Martin Odersky, Lex Spoon, and Bill Venner. 2nd edition. Artima 2010.
  - Written by the instructor of the course, Martin Odersky.

---

Imperative programming is about:
- modifying mutable variables
- using assignment
- and control structure such as if-then-else, loops, break, continue, return

Functional programming (FP) means programming without mutable variables, assignments, loops, and other imperative control structures.
In a restricted sense, a FP language is one which does not have mutable variables, assignments, or imperative control structures.
In a wider sense, a FP language enables the construction of elegant programs that focus on functions.

In particular, functions in a FP language are first-class citizens. This means:
- they can be defined anywhere, including inside other functions
- like any other value, they can be passed as parameters to functions and returned as results
- as for other values, there exists a set of operators to compose functions

---

A non-primitive expression is evaluated as follows:
1. Take the leftmost operator
2. Evaluate its operands (left before right)
3. Apply the operator to the operands

A name is evaluated by replacing it with the right hand side of its definition. The evaluation process stops once it results in a value.

Function parameters come with their type, which is given after a colon:
```
def power(x: Double, y: Int): Double = ...
```
If a return type is give, it follows the parameter list.

Primitives types are as in Java, but are written capitalized, e.g.:

- `Int` 32-bit integers
- `Double` 64-bit floating point numbers
- `Boolean` boolean values `true` and `false`

Applications of parameterized functions are evalated in a similar way as operators:
1. Evaluate all function arguments, from left to right
2. Replace the function call by the function's right-hand side, and, at the same time
3. Replace the formal parameters of the function by the actual arguments

Example:
```
sumOfSquares(3, 2+2)
sumOfSquares(3,4)
square(3) + square(4)
3 * 3 + square(4)
9 + square(4)
9 + 4 * 4
9 + 16
25
```
This scheme of expression evaluation is called the **substitution model**.

The idea underlying this model is that all evaluation does is reduce an expression to a value.

It can be applied to all expressions, as long as they have no side effects (e.g. updating the value of a variables).

The substitution model is formalized in the λ-calculus, which gives a foundation for FP.

Does every expression reduce to a value (in a finite number of steps)?

No. Counter example:
```
def loop: Int = loop
loop
// loop -> loop -> ...
```

Alternatively, one could apply the function to unreduced arguments.
Example:
```
sumOfSquares(3, 2+2)
square(3) + square(2+2)
3 * 3 + square(2+2)
9 + square(2+2)
9 + (2+2) * (2+2)
9 + 4 * (2+2)
9 + 4 * 4
25
```
The first evaluation strategy is known as **call-by-value**, the second is known as **call-by-name**.

Both strategies reduce to the same final values as long as
- the reduced expression consists of pure functions, and
- both evaluation terminates.

Call-by value has the advantage that it evaluates every function argument only once.

Call-by-name has the advantage that a function argument is not evaluated if the corresponding parameter is unused in the evaluation of the function body.

Question: Say you are given the following function definition:
`def test(x: Int, y: Int) = x * x `

For each of the following function applications, indicate which evaluation strategy is fastest (has the fewest reduction steps)

```
CBV fastest          CBN fastest          same #steps
    0                     0                    T              test(2, 3)
    T                     0                    0              test(3+4, 8)
    0                     T                    0              test(7, 2*4)
    0                     0                    T              test(3+4, 2*4)
```

---

We know from the last module that the call-by-name and call-by-value evaluation strategies reduce an expression to the same value, as long as both evaluation terminate.

But what if termination is not guaranteed?

We have: 
- If CBV evaluation of an expression `e` terminates, then CBN evaluation of `e` terminates, too.
- The other direction is not true.

Let's define `def first(x: Int, y: Int) = x` and consider the expresssion `first(1, loop)`.

Under CBN:
```
first(1, loop)
1
```
Under CBV:
```
first(1, loop)
first(1, loop)
...
```

Scala normallu uses call-by-value. But if the type of a function parameter starts with `=>` it uses
call-by-name.
Example: `def constOne(x: Int, y: => Int) = 1`

---

To express choosing between two alternatives, Scala has a conditional expression `if-else`.
It looks like a `if-esle` in Java, but is used for expressions, not statements.

Example: `def abs(x: Int) = if (x >= 0) x else -x`

Boolean expressions `b` can be composed of
```
true false    // Constants
!b            // Negation
b && b        // Conjunction
b || b        // Disjunction
```
and of the usual comparison operations:
`e <= e, e >= e, e < e, e > e, e == e, e != e`

Here are reduction rules for Boolean expressions (`e` is an arbitrary expresssion)
```
!true        -->  false
!false       --> true
true && e    --> e
false && e   --> false
true || e    --> true
false || e   --> e
```
Note that `&&` and `||` do not always need their right operand to be evaluated. We say, these expresssion uses "short-circuit evaluation".

We have seen that function parameters can be passed by value or be passed by name.

The same distinction applies to definitions. The `def` form is "by-name", its right hand side is evaluated on each use.

There is also a `val` for which is `by-value`. 

Example: 
```
val x = 2
val y = square(x)
```

The right-hand side of a `val` definition is evaluated at the point of the definition itself.
Afterwards, the name refers to the value. In the example above, `y` refers to `4`, not `square(2)`.

---
Task: We will define in this session a function
```
/** Calculates the square root of parameter x */
def sqrt(x: Double): Double = ...
```
The classical way to achieve this is by sucessive approximations using Newton's method.

To compute `sqrt(x)`:
- Start with an intial estimate y (let's pick `y = 1`).
- Repeatedly improve the estimate by taking the mean of `y` and `x/y`.

First, we define a function which computes one iteration step:
```
def sqrtIter(guess: Double, x: Double): Double = 
  if (isGoodEnough(guess, x)) guess
  else sqrtIter(improve(guess, x), x)
```
Note that `sqrtIter` is recursive, its right-hand side calls itself. 
Recursive functions need an explicit return type in Scala. 
For non-recursive functions, the return type is optional.

Solution:
```
def abs(x: Double) = if (x < 0) -x else x

def sqrtIter(guess: Double, x: Double): Double =
  if (isGoodEnough(guess, x)) guess
  else sqrtIter(improve(guess, x), x)
  
def isGoodEnough(guess: Double, x: Double) = abs(guess * guess - x) / x < 0.001

def improve(guess: Double, x: Double) = (guess + x / guess) / 2

def sqrt(x: Double) = sqrtIter(1.0, x)

sqrt(2)
```

---

It's good functional programming style to split up a task into many small functions.

But the names of functions like `sqrtIter`, `improve`, and `isGoodEnough` matter only for the 
implementation of `sqrt`, not for its usage.

Normally we would not like users to access these functions directly.

We can achieve this and at the same time avoid "name-space" pollution by putting the auxiliary 
functions inside `sqrt`.

```
def abs(x: Double) = if (x < 0) -x else x

def sqrt(x: Double) = {
  def sqrtIter(guess: Double, x: Double): Double =
    if (isGoodEnough(guess, x)) guess
    else sqrtIter(improve(guess, x), x)

  def isGoodEnough(guess: Double, x: Double) = abs(guess * guess - x) / x < 0.001

  def improve(guess: Double, x: Double) = (guess + x / guess) / 2

  sqrtIter(1.0, x)
}

sqrt(2)
```

A **block** is delimited by braces `{ ... }`.
```
{ val x = f(3)
  x * x
}
```
It contains a sequence of definitions or expressions. 
The last element of a block is an expression that defines its value. 
This return expression can be preceded by auxiliary definitions.
Blocks are themselves expressions; a block may appear everywhere an expression can.

The definitions inside a block are only visible from within the block. 
The definitions inside a block shadow definitions of the same names outside the block.

Question: What is the value of result in the following program?
```
val x = 0
def f(y: Int) = y + 1
val result = {
  val x = f(3)
  x * x
} + x
```
Answer: 16

Definitions of outer blocks are visible inside a block unless they are shadowed. 
This is known as **lexical scoping**.
Therefore, we can simplify `sqrt` by eliminating redundant occurrences of the `x` parameter,
which means everywhere the same thing:

```
def abs(x: Double) = if (x < 0) -x else x

def sqrt(x: Double) = {
  def sqrtIter(guess: Double): Double =
    if (isGoodEnough(guess)) guess
    else sqrtIter(improve(guess))

  def isGoodEnough(guess: Double) = abs(guess * guess - x) / x < 0.001

  def improve(guess: Double) = (guess + x / guess) / 2

  sqrtIter(1.0)
}

sqrt(2)
```

In Scala, semicolons at the end of lines are in most cases optional. You could write `val x = 1;`, 
but most people would omit the semicolon. On the other hand, if there are more than one statements 
on a line, they need to be separated by semicolons:
`val y = x + 1; y * y`

One issue with Scala's semicolon convention is how to write expressions that span several lines.
For instance,
```
someLongExpression
+ someOtherExpression
```
would be interpreted as two expressions:
```
someLongExpression;
+ someOtherExpression
```
There are two ways to overcome this problem.

You could write the multi-line expression in parentheses, because semicolons are never inserted 
inside (...):
```
(someLongExpression
+ someOtherExpression)
```

Or you could write the operator on the first line, because this 
tells the Scala compiler that the expressison is not yet finished:
```
someLongExpression + 
someOtherExpression
```

---

To evaluate a function application f(e<sub>1</sub>, ..., e<sub>n</sub>):
- evaluate the expressions e<sub>1</sub>, ..., e<sub>n</sub> resulting in the values 
v<sub>1</sub>, ... , v<sub>n</sub>, then
- replace the application with the body of the function f,
- the actual paramteters v<sub>1</sub>, ..., v<sub>n</sub> replace the formal parameters of f.

This can be formalized as a rewriting of the program itself:

def f(x<sub>1</sub>, ..., x<sub>n</sub>) = B; ... f(v<sub>1</sub>, ..., v<sub>n</sub>) ->
def f(x<sub>1</sub>, ..., x<sub>n</sub>) = B; ... [v<sub>1</sub>/x<sub>1</sub>, ..., v<sub>n</sub>/x<sub>n</sub>]B

Here, [v<sub>1</sub>/x<sub>1</sub>, ..., v<sub>n</sub>/x<sub>n</sub>]B means:

The expresssion B in which all occurrences of x<sub>i</sub>, have been replaced by v<sub>i</sub>.
[v<sub>1</sub>/x<sub>1</sub>, ..., v<sub>n</sub>/x<sub>n</sub>] is called a substitution. 

Consider gcd, the function tha computes the greatest comon divisor of two numbers.
Here's an implementation of gcd using Euclid's algorithm
```
def gcd(a: Int, b: Int): Int = if (b==0) a else gcd(b, a % b)
gcd(14, 21)
-> if (21 == 0) 14 else gcd(21, 14 % 21)
-> if (false) 14 else gcd(21, 14 % 21)
-> gcd(21, 14 % 21)
-> gcd(21, 14)
-> if (14 == 0) 21 else gcd(14, 21 % 14)
-> gcd(14, 7)
-> gcd(7, 0)
-> if (0 == 0) 7 else gcd(0, 7 % 0)
-> 7
```
Consider factorial:
```
def factorial(n: Int): Int = if (n==0) 1 else n * factorial(n-1)
factorial(4)
-> if (4 == 0) 1 else 4 * factorial(4 - 1)
-> 4 * factorial(3)
-> 4 * (3 * factorial(2))
-> 4 * (3 * (2 * factorial(1)))
-> 4 * (3 * (2 * (1 * factorial(0)))
-> 4 * (3 * (2 * (1 * 1)))
->
```
One difference between the reduction sequences is that for gcd, the reduction sequence oscillates, 
but for factorial, the reduction sequence gets longer and longer until we reduce it to the final 
value.

Implementation Consideration: If a function calls itself as its last action, the function's stack
frame can be reused. This is called **tail recursion**.
=> Tail recursive functions are iterative processes.
In general, if the last action of a function consists of calling a function (which may be the same),
one stack would be sufficient for both functions. Such calls are called **tail-calls**.

In Scala, only directly recursive calls to the current function are optimized. One can require
that a function is tail-recursive using a `@tailrec` annotation:
```
@tailrec
def gcd(a: Int, b: Int): Int = ...
```
If the annotation is given, and the implementation of gcd were not tail recursive, an error
would be issued.

Question: Should every function be tail recursive. Well, not really. 
The interest of tail recursion is mostly to avoid very deep recursive chains. 
If the input data is such that these deep recursive chains could happen, then yes it's a good idea to reformulate your function to be tail recursive, to run in constant stack frame, so as to avoid stack overflow exceptions. 
On the other hand, if your input data are not susceptible to deep recursive chains then clarity trumps efficiency every time, so write your function the clearest way you can. Which often is not recursive.

> Premature optimisation is the source of all evil - Donald Knuth

Exercise: Design a tail-recursive version of factorial
```
def factorial(n: Int): Int = {
  def loop(acc: Int, n: Int): Int =
    if (n == 0) acc
    else loop(acc * n, n - 1)
  loop(1, n)
  }
}
```
