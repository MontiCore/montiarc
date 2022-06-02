/* (c) https://github.com/MontiCore/monticore */
package parameterTypeExists;

/**
 * Invalid model. (Let List<T>, Foo<U>, qual.Generic<W> and int be resolvable, but not Cool<V>, nor Green. Permit null)
 */
component HasInvalidTypes<T> (
  Green p1,
  List<Green> p2,
  Foo<Green> p3,
  Cool<int> p4,
  this.IsNotPresent p5 = null,
  qual.Generic<Green> p6 = null
) { }