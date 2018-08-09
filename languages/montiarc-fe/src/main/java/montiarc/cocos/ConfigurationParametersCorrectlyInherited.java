/*
 * Copyright (c)  RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package montiarc.cocos;

import de.monticore.ast.ASTNode;
import de.monticore.java.symboltable.JavaTypeSymbolReference;
import de.monticore.symboltable.Symbol;
import de.monticore.symboltable.types.JFieldSymbol;
import de.monticore.symboltable.types.JTypeSymbol;
import de.monticore.symboltable.types.references.JTypeReference;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTComponent;
import montiarc._cocos.MontiArcASTComponentCoCo;
import montiarc._symboltable.ComponentSymbol;
import montiarc._symboltable.ComponentSymbolReference;
import montiarc.helper.TypeCompatibilityChecker;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * This CoCo checks that a component which extends another component correctly
 * specifies the configuration parameters. Considered valid are components which
 * specify at least as many configuration parameters as the extended component.
 * Furthermore, if `n` specifies the number of parameters of the extended
 * component, the types of the first `n` parameters of the extending component
 * have to match those of it's super component. This means that the names of the
 * parameters do not have to match those of the super component. If the extended
 * component has optional parameters with default values, then all new
 * parameters of the inheriting component have to be optional as well.
 *
 * @author (last commit) Michael Mutert
 * @implements [Hab16] R14: Components that inherit from a parametrized
 * component provide configuration parameters with the same types, but are
 * allowed to provide more parameters. (p.69 Lst. 3.49)
 */
public class ConfigurationParametersCorrectlyInherited implements MontiArcASTComponentCoCo {
  
  @Override
  public void check(ASTComponent component) {
    // Try to resolve the symbol for the component
    final Optional<? extends Symbol> componentSymbolOpt = component.getSymbolOpt();
    if (componentSymbolOpt.isPresent()
        && componentSymbolOpt.get() instanceof ComponentSymbol) {
      ComponentSymbol componentSymbol = (ComponentSymbol) componentSymbolOpt.get();
      
      final List<JFieldSymbol> configParameters = componentSymbol.getConfigParameters();
      
      // Check whether the component has a super component
      // Otherwise, no check is required.
      final Optional<ComponentSymbolReference> superComponentOpt = componentSymbol
          .getSuperComponent();
      
      if (superComponentOpt.isPresent()) {
        final ComponentSymbol referencedSymbol
            = superComponentOpt.get().getReferencedSymbol();
        final List<JFieldSymbol> superConfigParameters
            = referencedSymbol.getConfigParameters();
        
        final int numInheritedParams = superConfigParameters.size();
        if (configParameters.size() < numInheritedParams) {
          Log.error(
              String.format("0xMA084 There are too few configuration " +
                  "parameters! The component requires at " +
                  "least %d configuration parameters",
                  numInheritedParams),
              component.getHead().get_SourcePositionStart());
        }
        
        for (int paramIndex = 0; paramIndex < Math.min(numInheritedParams,
            configParameters.size()); paramIndex++) {
          
          final JTypeReference<? extends JTypeSymbol> superParameterType
              = superConfigParameters.get(paramIndex).getType();
          final JTypeReference<? extends JTypeSymbol> paramType
              = configParameters.get(paramIndex).getType();
          
          // Check type correctness
          if (!TypeCompatibilityChecker.doTypesMatch(superParameterType,
              superParameterType.getReferencedSymbol().getFormalTypeParameters()
                  .stream()
                  .map(p -> (JTypeSymbol) p)
                  .collect(Collectors.toList()),
              superParameterType.getActualTypeArguments().stream()
                  .map(a -> (JavaTypeSymbolReference) a.getType())
                  .collect(Collectors.toList()),
              paramType,
              paramType.getReferencedSymbol().getFormalTypeParameters().stream()
                  .map(p -> (JTypeSymbol) p)
                  .collect(Collectors.toList()),
              paramType.getActualTypeArguments().stream()
                  .map(a -> (JavaTypeSymbolReference) a.getType())
                  .collect(Collectors.toList()))) {
            Log.error(
                String.format("0xMA084 Configuration parameter '%s' at " +
                    "position %d of component %s is of type" +
                    " '%s', but it is required to be of " +
                    "type '%s'.",
                    configParameters.get(paramIndex).getName(),
                    paramIndex + 1,
                    componentSymbol.getFullName(),
                    paramType.getReferencedSymbol().getFullName(),
                    superParameterType.getReferencedSymbol().getFullName()),
                component.getHead().getParameterList()
                    .get(paramIndex).get_SourcePositionStart());
          }
          
          if (hasParameterDefaultValue(superComponentOpt.get(), paramIndex)) {
            if (!hasParameterDefaultValue(componentSymbol, paramIndex)) {
              Log.error(
                  String.format("0xMA084 Configuration parameter '%s' at " +
                      "position %d of component %s is should " +
                      "have a default value but has none.",
                      configParameters.get(paramIndex).getName(),
                      paramIndex + 1,
                      componentSymbol.getFullName()),
                  component.getHead().getParameterList()
                      .get(paramIndex).get_SourcePositionStart());
            }
          }
        }
      }
    }
    else {
      Log.error(
          String.format("0xMA071 ASTComponent node \"%s\" has no symbol.",
              component.getName()));
    }
  }
  
  private boolean hasParameterDefaultValue(ComponentSymbol symbol, int position) {
    final ComponentSymbol actualComponent = symbol.getReferencedComponent().orElse(symbol);
    if (actualComponent.getAstNode().isPresent()) {
      final ASTComponent astNode = (ASTComponent) actualComponent.getAstNode().get();
      if (astNode.getHead().getParameterList()
          .get(position).isPresentDefaultValue()) {
        return true;
      }
    }
    return false;
  }
}
