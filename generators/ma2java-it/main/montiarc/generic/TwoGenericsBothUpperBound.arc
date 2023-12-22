/* (c) https://github.com/MontiCore/monticore */
package generic;

import java.lang.Integer;

component TwoGenericsBothUpperBound<T extends java.lang.Number, S extends java.lang.String> {
  port
   in T i1,
   in S i2,
   out Integer o1,
   out String o2;

  compute {
    o1 = i1.intValue();
    o2 = i2;
  }
}
