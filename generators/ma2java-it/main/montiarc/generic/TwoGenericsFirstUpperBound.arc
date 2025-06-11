/* (c) https://github.com/MontiCore/monticore */
package generic;

component TwoGenericsFirstUpperBound<T extends java.lang.Number, S> {
  port
   sync in T i1,
   sync in S i2,
   sync out Integer o1,
   sync out S o2;

  compute {
    o1 = i1.intValue();
    o2 = i2;
  }
}
