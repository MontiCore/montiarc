<!-- (c) https://github.com/MontiCore/monticore -->
# Set Expressions

The **Set Expressions** are domain-specific operations for sets and lists, commonly found in OCL (Object Constraint Language) and specifications.
These include literal collections, comprehensions, and set algebra (union, intersect, difference).

> **Java Compatibility:** When the [Java Integration (Class2MC)](../../Types/JavaIntegration.md) feature is enabled, these expressions are fully compatible with Java `List` and `Set` collections.

## Defining Collections (Set Enumerations)

Set Enumerations allow you to explicitly define sets or lists of values. You can define collections using individual values, ranges, or a combination of both.

* **Sets** are defined using curly braces `{ ... }` or explicitly prefixed with `Set{ ... }`.
* **Lists** are defined using square brackets `[ ... ]`.

### Elements and Ranges

A collection can contain single expressions or value ranges defined with the `..` operator.

| Syntax Example  | Type | Description                                                    |
| --------------- | ---- | -------------------------------------------------------------- |
| `{1, 2, 3}`     | Set  | A set containing the values 1, 2, and 3.                       |
| `Set{a, b}`     | Set  | Explicit set definition.                                       |
| `[1, 2, 3]`     | List | A list containing the values 1, 2, and 3.                      |
| `{1..3}`        | Set  | A set of values from 1 to 3.                                   |
| `[x+1..10, 21]` | List | A list containing a range from `x+1` to 10, plus the value 21. |

## Set Comprehensions

Set Comprehensions allow you to dynamically build sets or lists by generating elements from an existing collection, filtering them, and mapping them.

**Syntax:**

* `{ expression | item1, item2, ... }`
* `[ expression | item1, item2, ... ]`

The left side of the pipe `|` defines the elements stored in the collection. The right side is a comma-separated list of items that characterize the comprehension.

### Comprehension Items

A comprehension item on the right side of the `|` can be one of three things:

1. **Generator:** Introduces a new variable and lets it range over an existing set.
* *Format:* `[Type]? Name in Expression`
* *Example:* `x in S`, `int y in {3..10}`


2. **Filter (Expression):** A boolean expression that filters which elements are included.
* *Example:* `x < 10`


3. **Variable Declaration:** Introduces an intermediate local variable.
* *Format:* `[Type]? Name = Expression`
* *Example:* `int y = x * 2`



**Examples:**

```montiarc
{ x * x | x in S, x < 10 }          // Set of squares of elements in S that are less than 10
[ y | x in {1..5}, int y = 2 * x ]  // List [2, 4, 6, 8, 10]

```

## Set Operations

A variety of infix and prefix operators to interact with and combine sets are available.

### Membership Operators

| Operator | Example           | Description                                                 |
| -------- | ----------------- | ----------------------------------------------------------- |
| `isin`   | `elem isin setA`  | Evaluates to `true` if `elem` exists within `setA`.         |
| `notin`  | `elem notin setA` | Evaluates to `true` if `elem` does not exist within `setA`. |

### Binary Set Algebra

| Operator    | Example               | Description                                                        |
| ----------- | --------------------- | ------------------------------------------------------------------ |
| `union`     | `setA union setB`     | Combines elements from both sets into a single set.                |
| `intersect` | `setA intersect setB` | Returns a set containing only the elements present in both sets.   |
| `\`         | `setA \ setB`         | Set difference; returns elements in `setA` that are not in `setB`. |

### Unary Collection Operations (Flattening)

These operations are applied to a single set containing other sets (sets of sets).

| Operator    | Example               | Description                                                           |
| ----------- | --------------------- | --------------------------------------------------------------------- |
| `union`     | `union setOfSets`     | Flattens the collection by taking the union of all contained sets.    |
| `intersect` | `intersect setOfSets` | Returns the intersection of all sets contained within the collection. |

### Logical Set Operations

These prefix operators apply logical aggregation over a set of boolean values.

| Operator | Example             | Description                                                                                 |
| -------- | ------------------- | ------------------------------------------------------------------------------------------- |
| `setand` | `setand setOfBools` | Returns `true` if **all** elements in the set evaluate to `true` (logical FORALL).          |
| `setor`  | `setor setOfBools`  | Returns `true` if **at least one** element in the set evaluates to `true` (logical EXISTS). |