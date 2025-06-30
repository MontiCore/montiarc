/* (c) https://github.com/MontiCore/monticore */
package montiarc.datatypes.generics;

import java.util.List;
import java.util.Optional;

component PrimitiveGenerics<T> {
  port in Optional<float> i;
  port out List<T> o;

  List<int> v = [1, 2, 3];
  Optional<int> oInt = Optional.empty();

  automaton {
    initial state S;
    S -> S / {
      List<int> x = v;
      Optional<int> oIntLocalVar = oInt;
    };
  }
}
