/* (c) https://github.com/MontiCore/monticore */
package monticore.varDeclarationInitializationHasCorrectType;

import instanceArgsOmitPortReferences.toInstantiate.*;

/**
 * Invalid model.
 */
component InvalidMultiVarDeclaration {
  automaton {
    initial { int a = "Oh no", b = 10, c, d = "no no no"; } state Init;
  }
}
