/* (c) https://github.com/MontiCore/monticore */
package components.body.automaton.transition.assignments;

/*
 * Invalid model.
 */
component UninitializedVariableAssignment {
  port in int i1;
  int v, w;
  int x, y, z;

  automaton {
    state A1, A2, A3, B, C, D, E;
    initial A1 / {x = y}; // y not initialized
    initial A2 / {y = i1};
    initial A3 / {x = 5, y = z}; // z not initialized

    A1 -> B / {y = 5, z = x};
    A1 -> C / {x = z, z = 5}; // z may not be initialized
    A1 -> D / {z = 5};
    A2 -> B / {v = 1 + y, w = y + 1, x = y + z}; // z may not initialized
    A3 -> D / {y = 5};
    D -> E / {v = z, w = y}; // z, y may not be initialized
  }
}
