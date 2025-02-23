/* (c) https://github.com/MontiCore/monticore */
package montiarc.datatypes.generics;

import java.util.List;
import java.util.Optional;

component PrimitiveGenericsForward(List<List<int>> ps) {
  port in Optional<float> i;
  port out List<boolean> o;

  PrimitiveGenerics<boolean> a;

  i -> a.i;
  a.o -> o;
}
