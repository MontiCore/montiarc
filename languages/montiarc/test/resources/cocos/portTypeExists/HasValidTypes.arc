/* (c) https://github.com/MontiCore/monticore */
package portTypeExists;

/**
 * Valid model. (As long as List<T>, Foo<U>, qual.Typee, qual.Generic, and Person are resolvable)
 */
component HasValidTypes<T> {
  port
    in T p1,
    in int p2,
    in Person p3,
    in List<Person> p4,
    in Foo<Person> p5,
    in qual.Typee p6,
    in qual.Generic<Person> p7,
    out T p8,
    out int p9,
    out Person p10,
    out List<Person> p11,
    out Foo<Person> p12,
    out qual.Generic<Person> p3;
}
