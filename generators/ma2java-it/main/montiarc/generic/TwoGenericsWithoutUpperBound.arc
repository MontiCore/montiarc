/* (c) https://github.com/MontiCore/monticore */
package generic;

component TwoGenericsWithoutUpperBound<T, S> {
  port
   in T i1,
   in S i2,
   out T o1,
   out S o2;

  compute {
    o1 = i1;
    o2 = i2;
  }
}
