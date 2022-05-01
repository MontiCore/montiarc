/* (c) https://github.com/MontiCore/monticore */
package fieldTypeExists;

/**
 * Invalid model. (Let List<T>, Foo<U>, qual.Generic<W> and int be resolvable, but not Cool<V>, nor Green. Permit null)
 */
component HasInvalidTypes<T> {
  Green p1 = null;
  List<Green> p2 = null;
  Foo<Green> p3 = null;
  Cool<int> p4 = null;
  this.IsNotPresent p5 = null;
  qual.Generic<Green> p6 = null;
}