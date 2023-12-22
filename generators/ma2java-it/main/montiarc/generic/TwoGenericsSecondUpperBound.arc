/* (c) https://github.com/MontiCore/monticore */
package generic;

component TwoGenericsSecondUpperBound<T, S extends java.lang.String> {
  port
   in T i1,
   in S i2,
   out T o1,
   out String o2;

  compute {
    o1 = i1;
    o2 = i2;
  }
}
