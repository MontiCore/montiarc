<!-- (c) https://github.com/MontiCore/monticore -->

# Generic Components

To facilitate reuse, _generic components_ can be parametrized with _type parameters_.
The type parameters can then be used as types for ports, parameters, or other entities in the behavior definition of a component.
For defining multiple type parameters, one separates them using commas:
```montiarc
component Foo<K, V> { /*...*/}
```

The following example shows a generic component type definition:

```montiarc
/* Merges streams by selecting the element of one of them at a time */
component Selector<T>(SelectionRule<T, T, Boolean> rule) {
  port in T elementA,
       in T elementB,
       out T selectedElement;

}
```
The type variable `T` can be used anywhere where a non-generic type could have been used.

## Instantiation
When components with generic type parameters are used during [decomposition](./Decomposition.md), types have to be bound.

In the following, we bind the type parameter `<T>` of our previous example to `WindowButtonMoveEvent`.
```montiarc
component HumanMachineInterface {
  port out WindowButtonMoveEvent buttonBackLeftEvent;
  
  // ...

  Selector<WindowButtonMoveEvent> signalSelector(/*...*/);
  signalSelector.selectedElement -> buttonBackLeftEvent;  
  
  // ...
}
```
Instead of binding a direct type, one can also bind subcomponent type variables with newly defined type parameters in the enclosing component:
```montiarc
component Oracle<T> {
  port out T prediction;
  
  Database<T> database;  // Used for predicting values
  // Other sub components ...
}
```

## Inheritance
When [extending](./Inheritance.md) from a generic component, all its type parameters must be bound.
Using newly defined type parameters for this is also allowed.
```montiarc
component Selector<T>(/*...*/) extends StreamMerger<T> {
  // ...
}
```

## Upper bounds
```montiarc
component AgeFilter<T extends Person>(int minAge) {
  port in T unfiltered;
  port out T filtered;

  compute {
    // Using information of the upper bound to access the age of the incoming element. 
    if (unfiltered.age >= minAge) {
      filtered = unfiltered;
    }
  }
}
```
One can limit the allowed types for binding type parameters by using upper bounds.
As the upper bound introduces additional information about the type that the type parameter
represents, one can access methods and fields of that type's elements within the behavior of the component.
One can also define multiple upper bounds for the same type parameter by separating the upper bounds with `&`s:
```montiarc
component Foo<K extends Worker & Student, V> { /*...*/ }
```