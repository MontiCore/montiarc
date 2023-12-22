/* (c) https://github.com/MontiCore/monticore */
package generic;

import java.lang.Integer;

component OneGenericWithUpperBound<T extends java.lang.Number> {
  port
   in T i,
   out Integer o;

  compute {
    Integer v = i.intValue();
    o = v;
  }
}
