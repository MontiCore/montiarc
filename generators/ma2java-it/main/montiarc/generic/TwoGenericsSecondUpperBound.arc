/* (c) https://github.com/MontiCore/monticore */
package generic;

component TwoGenericsSecondUpperBound<T, S extends java.lang.String> {
  port
   sync in T i1,
   sync in S i2,
   sync out T o1,
   sync out String o2;

  compute {
    o1 = i1;
    o2 = i2;
  }
}
