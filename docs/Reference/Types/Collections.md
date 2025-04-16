<!-- (c) https://github.com/MontiCore/monticore -->

# Collections

Collections are a means to _collect_ multiple values into one value.
This is especially useful if their quantity changes over time or is very large.

As an initial example, in the following,
the two numbers `1` and `2` are both inserted
into the list of numbers `numList`.

```montiarc
List<Integer> numList = [1, 2];
```

This `numList` can then be used in further calculations.

## Collection Types

MontiArc supports the four major collection types

* `Set`: an unordered collection of 0 to n values
* `List`: an ordered collection of 0 to n values
* `Optional`: a collection of 0 to 1 values
* `Map`: an unordered collection offering qualified access to its elements,
  using so-called keys.

They are explained in further detail below.

### Type Arguments

Collection types require type arguments;
These arguments state which type of element the collection can hold.
E.g., a list of type `List<Person>` can only hold instances
of the type `Person`, while a list of type `List<Car>` may only hold
instances of `Car`. `Person` and `Car` are the _type arguments_
of the aforementioned lists, they are always written within angle brackets
after the name of the Collection type.

`Map` is a special case in that not only the type of the included elements
has to be specified, the type of the key has to be provided as well;
`Map<Car, Person>` is a qualified collection of `Person`,
with the key having the type `Car`, separated by a comma.
This example could be the mapping of owners to their cars.

In MontiArc [Primitives](Primitives.md) can also be used as type parameters.

## List

An ordered collection of arbitrary size.

Using Lists requires the import

```montiarc
import java.util.List;
```

A new List can be created with `[]`,
one may add initial values separated by a comma.

```montiarc
List<Integer> l1 = []; // empty list
List<Integer> l2 = [1, 2]; // list containing 1 and 2
```

Alternatively, one may import a `List`-implementation from Java, e.g.,

```montiarc
import java.util.List;
import java.util.LinkedList;

component Dummy {
  List<Integer> = LinkedList.LinkedList();
}
```

Lists offer methods to modify and
query information on the contained elements, e.g.,

```montiarc
List<Integer> l = [1]; // l is [1]
l.add(1); // adds 2 at the end, l is [1,1]
l.add(1,7); // adds 7 at the index 1, l is [1,7,1]
l.remove(0); // removes the element at index 0, l is [7,1]
int e = l.get(1); // gets the element at index 1, e is 1
int s = l.size(); // returns the amount of elements, s is 2
boolean b = l.isEmpty(); // returns whether the list is empty, b is false
boolean c = l.contains(7); // returns wether the list contains 7, c is true
```

## Set

An unordered collection of arbitrary size,
each element is contained at most once.

Using Sets requires the import

```montiarc
import java.util.Set;
```

A new Set can be created with `{}`,
one may add initial values separated by a comma.

```montiarc
Set<Integer> s1 = {}; // empty set
Set<Integer> s2 = {1, 2}; // set containing 1 and 2
```

Alternatively, one may import a `Set`-implementation from Java, e.g.,

```montiarc
import java.util.Set;
import java.util.HashSet;

component Dummy {
  Set<Integer> = HashSet.HashSet();
}
```

Sets offer methods to modify and
query information on the contained elements, e.g.,

```montiarc
Set<Integer> s = {1}; // s is {1}
s.add(1); // adds 1, but 1 is already in s, s is {1}
s.add(2); // adds 2, s is {1, 2}
s.remove(1); // removes the element 1, s is {2}
int r = s.size(); // returns the amount of elements, r is 1
boolean b = s.isEmpty(); // returns whether the set is empty, b is false
boolean c = s.contains(7); // returns wether the set contains 7, c is false
```

To iterate over the elements of a set,
a [`for`-loop](../Concepts/Statements.md#for-loop) can be used.

```montiarc
Set<Integer> s = {1,2,3};
int sum = 0;
for(int i : s) {
  sum = sum + i;
}
// here, sum is 6
```

## Optional

A collection of size 0 to 1.
It can be used to represent a value that may be absent.

Using Optionals requires the import

```montiarc
import java.util.Optional;
```

New Optionals are created using the respective methods:
```montiarc
Optional<String> opt1 = Optional.empty(); // empty Optional
Optional<String> opt2 = Optional.of("A Value"); // Optional containing a value
```

Optionals offer methods to query information on the contained elements, e.g.,

```montiarc
Optional<String> o = Optional.of("a"); o contains "a"
boolean p = o.isPresent(); // returns wether an element is contained, p is true
boolean e = o.isEmpty(); // returns the optional is empty, e is false
String v = o.get(); // returns the contained element. v is "a", o must not be empty!
```

## Map

This type represents a mapping from keys to values.

Using Maps requires the import

```montiarc
import java.util.Map;
```

A new Map can be created by importing a `Map`-Implementation from Java, e.g.,

```montiarc
import java.util.Map;
import java.util.HashMap;

component Dummy {
 Map<String, Integer> m = HashMap.HashMap();
}
```

Maps offer methods to modify and
query information on the contained elements, e.g.,

```montiarc
Map<String, Integer> m = HashMap.HashMap();
m.put("Sean", 3); // adds a mapping, m is {"Sean":3}
boolean c = m.containsKey("Blair"); // whether the key "Blair" is contained, c is false
boolean d = m.containsValue(3); // whether the value 3 is contained, d is true
int v = m.get("Sean"); // returns the value corresponding to the key "Sean", v is 3, the key must exist!
```
