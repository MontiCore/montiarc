/* (c) https://github.com/MontiCore/monticore */
package generic;

import java.lang.Integer;

component TwoGenericsBothUpperBound<T extends java.lang.Number, S extends java.lang.String> {
  port
   sync in T i1,
   sync in S i2,
   sync out Integer o1,
   sync out String o2;

  compute {
    o1 = i1.intValue();
    o2 = i2;
  }
}
