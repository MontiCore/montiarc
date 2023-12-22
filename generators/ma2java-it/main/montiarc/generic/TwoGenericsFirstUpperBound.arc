/* (c) https://github.com/MontiCore/monticore */
package generic;

component TwoGenericsFirstUpperBound<T extends java.lang.Number, S> {
  port
   in T i1,
   in S i2,
   out Integer o1,
   out S o2;

  compute {
    o1 = i1.intValue();
    o2 = i2;
  }
}
