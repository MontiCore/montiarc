/* (c) https://github.com/MontiCore/monticore */
package monticore.varDeclarationInitializationHasCorrectType;

import instanceArgsOmitPortReferences.toInstantiate.*;

/**
 * Invalid model. For testing purposes let MyType be a resolvable type.
 */
component InvalidMultiVarDeclarationWithTypeReference {
  automaton {                        // v is defined as integer, bus assignment ist string. Illegal!
    initial { int a = 3, b, c = MyType, d = "no no no"; } state Init;
                             // ^ We must not assign Type references like the type "MyType" to fields.
  }
}
