/*
 * Copyright (c)  RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package montiarc.cocos;

import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTComponent;
import montiarc._cocos.MontiArcASTComponentCoCo;
import montiarc._symboltable.ComponentInstanceSymbol;
import montiarc._symboltable.ComponentSymbol;
import montiarc._symboltable.ComponentSymbolReference;

/**
 * Assures that a subcomponent instance of a component with at least one
 * generic parameter has a type assigned for all of those parameters.
 *
 * @author (last commit) Michael Mutert
 * @version 2018-03-26
 * @implements [Hab16] R9: If a generic component type is instantiated as a
 * subcomponent, all generic parameters have to be assigned.
 */
public class SubcomponentGenericTypesCorrectlyAssigned implements MontiArcASTComponentCoCo {

  @Override
  public void check(ASTComponent node) {
    final ComponentSymbol componentSymbol
        = (ComponentSymbol) node.getSymbolOpt().get();

    // Get all subcomponent instance symbols and run the check on them
    for (ComponentInstanceSymbol componentInstanceSymbol :
        componentSymbol.getSubComponents()) {
      final ComponentSymbolReference componentType
          = componentInstanceSymbol.getComponentType();

      if (componentType.getActualTypeArguments().size() !=
              componentType.getReferencedSymbol()
                  .getFormalTypeParameters().size()) {
        Log.error(String.format("0xMA085 The number of type parameters " +
                                    "assigned to the subcomponent %s of " +
                                    "type %s does not match the amount " +
                                    "of required parameters.",
            componentInstanceSymbol.getName(),
            componentInstanceSymbol.getComponentType().getFullName()),
            node.get_SourcePositionStart());
      }
    }
  }
}
