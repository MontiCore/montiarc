/* (c) https://github.com/MontiCore/monticore */
package montiarc.datatypes.generics;

import java.lang.Number;
import java.util.List;

component NumberList {
  port in Integer i;
  port out Number o;

  List<Number> numbers = [1.0F, 3.33333, 1.6F];

  automaton {
    initial state S;
    S -> S [i >= 0 && i < numbers.size()] i / {
      o = numbers.get(i);
    }
  }
}
