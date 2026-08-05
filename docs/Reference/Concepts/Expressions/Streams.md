<!-- (c) https://github.com/MontiCore/monticore -->

# Streams 

According to the Focus theory developed by Professor Manfred Broy A 
stream is a temporarily ordered sequence of messages (events, 
signals), which may be finite or infinite. The FOCUS[^1] theory is a 
semantically very well-founded mathematical construction to ensure 
that distributed and asynchronously or synchronously communicating 
components can be specified in a compositional style, and repeated 
refinements and decompositions are possible. MontiArc What's created 
as a practical language in the spirit of FOCUS[^1]. 

See literature on Focus below (TODO).

Technically Streams can be handled like any other kind of expression 
types. They provide an API for their manipulation, and some 
syntactically special operators, like concatenation (infix: "^^") Or 
the explicit listing of a stream with its values "<a,b,c>".   
Semantically a stream denotes the temporal order of a sequence of 
messages which maybe infinite. Therefore streams can be very well 
used in behavioral descriptions of components, for example in 
Assumption/Guarantee style, but cannot be used as parameters or 
attributes inside a component. 

How to specify with streams is explained in the literature (TODO). 

Here we explain the syntactic appearance of streams and its different 
variants. 

# Stream Expressions 

**Stream Expressions** extend the Expression languages with specific 
operations for stream processing. This allow modelers to succinctly 
declare, manipulate, and evaluate streams. 

[^1]: Manfred Broy and Ketil Stølen. *Specification and Development of Interactive Systems. Focus on Streams, Interfaces and Refinement*. Springer Verlag Heidelberg, 2001.

## Stream Types and Timing

MontiArc provides several distinct type constructors to handle 
different stream [timing](../Timing.md) paradigms. This is necessary 
because, dependent on the level of abstraction, sometimes timing is 
irrelevant and sometimes timing constraints are very relevant. To be 
able to specify timing constraints, time needs to be made explicit in 
the stream of elements. FOCUS does that in different forms, (1) 
untimed, (2) restricting streams to provide exactly or at most one 
message per time slice, or (3) using Ticks as a form of 
pseudo-message that explicitly models the progress of time Into the 
next time slice.  

 * `Stream<T>`: The base stream type (a kind of supertype for all streams).
 * `UntimedStream<T>`: A standard stream of elements with no explicit 
    timing information.
 * `EventStream<T>`: A timed stream that can include 
      `Tick` elements to denote the end of a time slice.
      In a time slice there are finitely many messages allowed.  
      (This is the default timing if none is specified).
 * `SyncStream<T>`: A synchronous stream where a time slice contains exactly one
      message. This is why the stream is denoted By the list of messages and 
      the ticks are omitted. Please note that semantically `SyncStream`
      is a subset of `EventStream` even though the syntactic representation 
      is different. 
 * `ToptStream<T>`: A relaxation of the synchronous stream where 
       the message may be optional (so at most one message per time slice). 

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
