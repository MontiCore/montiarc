/* (c) https://github.com/MontiCore/monticore */
package portTypeExists;

/**
 * Invalid model. (Let List<T>, Foo<U>, qual.Generic<W> and int be resolvable, but not Cool<V>, nor Green)
 */
component HasInvalidTypes<T> {
  port
    in Green p1,
    in List<Green> p2,
    in Foo<Green> p3,
    in Cool<int> p4,
    in this.IsNotPresent p5,
    in qual.Generic<Green> p6,
    out Green p7,
    out List<Green> p8,
    out Foo<Green> p9,
    out Cool<int> p10,
    out this.IsNotPresent p11,
    out qual.Generic<Green> p12;
}