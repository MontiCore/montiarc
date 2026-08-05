<!-- (c) https://github.com/MontiCore/monticore -->
# Stream Expressions

The **Stream Expressions** are domain-specific operations for stream processing.
Based on the theoretical foundations of FOCUS[^1], this allow modelers to succinctly declare, manipulate, and evaluate streams.

[^1]: Manfred Broy and Ketil Stølen. *Specification and Development of Interactive Systems. Focus on Streams, Interfaces and Refinement*. Springer Verlag Heidelberg, 2001.

## Stream Types and Timing

MontiArc provides several distinct type constructors to handle different stream [timing](../Timing.md) paradigms:

* `Stream<T>`: The base stream type.
* `UntimedStream<T>`: A standard stream of elements with no timing semantics.
* `EventStream<T>`: A timed stream that can include `Tick` elements to denote the end of a time slice. (This is the default timing if none is specified).
* `SyncStream<T>`: A synchronous stream where time slices are strictly aligned.
* `ToptStream<T>`: A timed optional stream that allows for the explicit absence of values.

---

## Stream Syntax and Operators

The grammar introduces specialized syntax for constructing and combining streams natively within expressions.

### Stream Constructors

You can construct streams using angle brackets `< ... >`. Constructors support optional timing modifiers and explicit type arguments.

**Format:** `[Timing]? ["<" Type ">"]? "<" Elements ">"`

**Elements** can be standard expressions, `Tick`, or `~` (which denotes the absence of a value, shorthand for `Abs`).

| Syntax Example            | Equivalent To                 | Valid In      |
| ------------------------- | ----------------------------- | ------------- |
| `<>`                      | Empty stream                  | All streams   |
| `<a>`                     | `a:<>`                        | All streams   |
| `<a, b>`                  | `a:b:<>`                      | All streams   |
| `<Tick, a>`               | `Tick:a:<>`                   | `EventStream` |
| `<a, ~>`                  | `a:Abs:<>`                    | `ToptStream`  |
| `Event<Integer><1, Tick>` | Explicitly typed event stream | `EventStream` |

### Stream Operators

Stream expressions support custom infix operators for common manipulations. Note that concatenation and append operations are **right-associative** (e.g., `a:b:c` evaluates as `a:(b:c)`).

| Operator | Name              | Example              | Description                                                 |
| -------- | ----------------- | -------------------- | ----------------------------------------------------------- |
| `:`      | **Append**        | `a : stream`         | Appends a single element `a` to the front of a stream.      |
| `Abs:`   | **Append Absent** | `Abs : stream`       | Appends the absence of an element (used in `Topt` streams). |
| `Tick:`  | **Append Tick**   | `Tick : stream`      | Appends a time slice boundary (used in `Event` streams).    |
| `^^`     | **Concatenate**   | `streamA ^^ streamB` | Concatenates two streams together.                          |
| `#`      | **Length**        | `#stream`            | Returns the length of the stream.                           |

---

## `Stream<T>` Standard API

The `Stream<T>` interface provides a rich library of methods for querying, slicing, and transforming streams.

### Stream Generation (Static Methods)

| Method Signature            | Description                                                                                                     |
| --------------------------- | --------------------------------------------------------------------------------------------------------------- |
| `repeat(S elem, long n)`    | Generates a stream of type `S` by repeating `elem` exactly `n` times.                                           |
| `iterate(S -> S f, S elem)` | Generates an infinite stream by repeatedly applying function `f` to the previous element, starting with `elem`. |

### Properties and State

| Method Signature           | Description                                                        |
| -------------------------- | ------------------------------------------------------------------ |
| `long len()`               | Returns the number of elements in the stream.                      |
| `boolean hasInfiniteLen()` | Returns `true` if the stream is infinite.                          |
| `boolean isEmpty()`        | Returns `true` if the stream contains zero elements.               |
| `Set<T> values()`          | Collects and returns all unique elements of the stream as a `Set`. |

### Slicing and Repetition

| Method Signature                 | Description                                                                             |
| -------------------------------- | --------------------------------------------------------------------------------------- |
| `Stream<T> dropFirst()`          | Returns a new stream with the first element removed.                                    |
| `Stream<T> dropMultiple(long n)` | Returns a new stream with the first `n` elements removed.                               |
| `Stream<T> take(long n)`         | Returns a new stream containing only the first `n` elements.                            |
| `Stream<T> times(long n)`        | Repeats the entire stream sequence `n` times (e.g., `<2>.times(3)` yields `<2, 2, 2>`). |
| `Stream<T> infTimes()`           | Repeats the stream sequence infinitely.                                                 |
| `Stream<T> rcDups()`             | Returns a stream with consecutive duplicate elements removed.                           |

### Higher-Order Functions

| Method Signature                            | Description                                                                                 |
| ------------------------------------------- | ------------------------------------------------------------------------------------------- |
| `<U> Stream<U> map(T -> U f)`               | Transforms the stream by applying the function `f` to each element.                         |
| `Stream<T> filter(T -> boolean p)`          | Keeps only the elements that satisfy the predicate `p`.                                     |
| `Stream<T> takeWhile(T -> boolean p)`       | Yields elements from the beginning of the stream as long as predicate `p` is true.          |
| `Stream<T> dropWhile(T -> boolean p)`       | Bypasses elements as long as predicate `p` is true, then yields the remaining stream.       |
| `<U> Stream<U> scanl(U -> T -> U f, U acc)` | Folds the stream from the left, yielding a new stream of the successive accumulated values. |

### Tuple and Projection Operations

| Method Signature                     | Description                                                                      |
| ------------------------------------ | -------------------------------------------------------------------------------- |
| `<U> Stream<(T,U)> zip(Stream<U> s)` | Combines this stream with stream `s` into a single stream of pairs/tuples.       |
| `static projFst(Stream<(S,U)> s)`    | Extracts a stream containing only the **first** elements of a stream of tuples.  |
| `static projSnd(Stream<(S,U)> s)`    | Extracts a stream containing only the **second** elements of a stream of tuples. |
