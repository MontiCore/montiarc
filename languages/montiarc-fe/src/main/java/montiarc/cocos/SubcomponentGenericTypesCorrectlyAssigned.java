/*
 * Copyright (c)  RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package montiarc.cocos;

import de.monticore.symboltable.types.JTypeSymbol;
import de.monticore.symboltable.types.references.ActualTypeArgument;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTComponent;
import montiarc._cocos.MontiArcASTComponentCoCo;
import montiarc._symboltable.ComponentInstanceSymbol;
import montiarc._symboltable.ComponentSymbol;
import montiarc._symboltable.ComponentSymbolReference;

/**
 * Assures that a subcomponent instance of a component with at least one generic
 * parameter has a type assigned for all of those parameters.
 *
 * @author (last commit) Michael Mutert
 * @version 2018-03-26
 * @implements [Hab16] R9: If a generic component type is instantiated as a
 * subcomponent, all generic parameters have to be assigned.
 */
public class SubcomponentGenericTypesCorrectlyAssigned implements MontiArcASTComponentCoCo {
  
  @Override
  public void check(ASTComponent node) {
    if (!node.getSymbolOpt().isPresent()) {
      Log.error(
          String.format("0xMA010 ASTComponent node \"%s\" has no " +
                            "symbol. Did you forget to run the " +
                            "SymbolTableCreator before checking cocos?",
              node.getName()));
      return;
    }
    final ComponentSymbol componentSymbol = (ComponentSymbol) node.getSymbolOpt().get();
    
    // Get all subcomponent instance symbols and run the check on them
    for (ComponentInstanceSymbol componentInstanceSymbol : componentSymbol.getSubComponents()) {
      final ComponentSymbolReference componentType = componentInstanceSymbol.getComponentType();
      
      if (componentType.getActualTypeArguments().size() != componentType.getReferencedSymbol()
          .getFormalTypeParameters().size()) {
        Log.error(String.format("0xMA085 The number of type parameters " +
            "assigned to the subcomponent %s of " +
            "type %s does not match the amount " +
            "of required parameters.",
            componentInstanceSymbol.getName(),
            componentInstanceSymbol.getComponentType().getFullName()),
            node.get_SourcePositionStart());
      }
      // error case instance uses generic parameter that is not defined in
      // surrounding component type
      else {
        for (ActualTypeArgument actualP : componentType.getActualTypeArguments()) {
          // if not exists referenced symbol it must either be a generic type or
          // a type that is not imported
          if (!actualP.getType().existsReferencedSymbol()) {
            boolean foundGenericType = false;
            
            // try to resolve as generic type
            for (JTypeSymbol genericTypeParam : componentSymbol.getFormalTypeParameters()) {
              if (actualP.getType().getName().equals(genericTypeParam.getName())) {
                foundGenericType = true;
              }
            }
            
            if(!foundGenericType) {
              Log.error(String.format("0xMA096 The type parameter '" + actualP.getType().getName() +
                  "' assigned to the subcomponent %s of " +
                  "type %s could not be found.",
                  componentInstanceSymbol.getName(),
                  componentInstanceSymbol.getComponentType().getFullName()),
                  node.get_SourcePositionStart());
            }
          }
        }
      }
      
    }
  }
}
