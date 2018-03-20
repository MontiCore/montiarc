/*
 * Copyright (c) 2016 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package montiarc.cocos;

import java.util.List;
import java.util.Optional;

import de.monticore.java.javadsl._ast.ASTExpression;
import de.monticore.java.symboltable.JavaTypeSymbolReference;
import de.monticore.symboltable.types.JFieldSymbol;
import de.monticore.symboltable.types.JTypeSymbol;
import de.monticore.symboltable.types.TypeSymbol;
import de.monticore.symboltable.types.references.JTypeReference;
import de.monticore.symboltable.types.references.TypeReference;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTComponent;
import montiarc._cocos.MontiArcASTComponentCoCo;
import montiarc._symboltable.ComponentInstanceSymbol;
import montiarc._symboltable.ComponentSymbol;
import montiarc._symboltable.ValueSymbol;
import montiarc.helper.TypeCompatibilityChecker;

/**
 * TODO JP Ensures that parameters in the component's head are defined in the
 * right order. It is not allowed to define a normal parameter after a
 * declaration of a default parameter. E.g.: Wrong: A[int x = 5, int y] Right:
 * B[int x, int y = 5]
 *
 * @implements TODO: Klaeren welche CoCo in der Literatur repraesentiert wird.
 * @author Andreas Wortmann
 */
public class SubcomponentParametersCorrectlyAssigned
    implements MontiArcASTComponentCoCo {
  
  /**
   * @see montiarc._cocos.MontiArcASTComponentCoCo#check(montiarc._ast.ASTComponent)
   */
  @Override
  public void check(ASTComponent node) {
    ComponentSymbol symb = (ComponentSymbol) node.getSymbol().get();
    for (ComponentInstanceSymbol instance : symb.getSubComponents()) {
      ComponentSymbol instanceType = instance.getComponentType().getReferencedSymbol();
      int paramIndex = 0;
      for (ValueSymbol<TypeReference<TypeSymbol>> arg : instance.getConfigArguments()) {
        ASTExpression expr = arg.getValue();
        Optional<? extends JavaTypeSymbolReference> argType = TypeCompatibilityChecker
            .getExpressionType(expr);
        if (argType.isPresent()) {
          if (paramIndex < instanceType.getConfigParameters().size()) {
            JFieldSymbol configParam = instanceType.getConfigParameters().get(paramIndex);
            // TODO: If configParam.getType() is a generic type (e.g., <T>),
            // then look up how it was
            // instantiated (e.g., <java.lang.String>), resolve the latter and
            // compare this to
            // argType.get())
            if (isGenericConfigParameter(instance, configParam.getType())) {
              return;
            }
            
            if (!TypeCompatibilityChecker.doTypesMatch(
                configParam.getType(),
                argType.get())) {
              Log.error("0xMA064 Type of argument " + paramIndex + " (" + argType.get().getName()
                  + ") of subcomponent " + instance.getName() + " of component type '"
                  + node.getName() + "' does not fit parameter type "
                  + configParam.getType().getName(), expr.get_SourcePositionStart());
            }
          }
        }
        else {
          Log.error("0xMA065 Could not find type of argument no " + paramIndex + " of subcomponent"
              + instance.getName(), expr.get_SourcePositionStart());
        }
        paramIndex++;
      }
    }
  }
  
  public boolean isGenericConfigParameter(ComponentInstanceSymbol instance,
      JTypeReference<?> configParam) {
    ComponentSymbol instanceType = instance.getComponentType().getReferencedComponent().get();
    List<JTypeSymbol> typeGenericTypeParams = instanceType.getFormalTypeParameters();
    for (JTypeSymbol typeGenericTypeParam : typeGenericTypeParams) {
      if (configParam.getName().equals(typeGenericTypeParam.getName())) {
        return true;
      }
    }
    return false;
  }
}
