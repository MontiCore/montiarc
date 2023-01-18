/* (c) https://github.com/MontiCore/monticore */
package monticore.varDeclarationInitializationHasCorrectType;

import instanceArgsOmitPortReferences.toInstantiate.*;

/**
 * Valid model.
 */
component ValidMultiVarDeclaration {
  automaton {
    initial { int a = 10, b, c = 12; } state Init;
  }
}
