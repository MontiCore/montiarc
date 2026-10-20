/* (c) https://github.com/MontiCore/monticore */
package montiarc.datatypes.structural;

import montiarc.datatypes.primitive.composition.IntForward;

// Connectors widen a member type to a union type
component UnionForward {
  port sync in int member;
  port sync out int | boolean widened;

  IntForward forward;

  member -> forward.pIn;
  forward.pOut -> widened;
}
