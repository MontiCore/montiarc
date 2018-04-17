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
import de.monticore.symboltable.types.references.ActualTypeArgument;
import de.monticore.symboltable.types.references.JTypeReference;
import de.monticore.symboltable.types.references.TypeReference;
import de.monticore.types.TypesPrinter;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTComponent;
import montiarc._ast.ASTParameter;
import montiarc._ast.ASTSubComponent;
import montiarc._cocos.MontiArcASTComponentCoCo;
import montiarc._symboltable.ComponentInstanceSymbol;
import montiarc._symboltable.ComponentSymbol;
import montiarc._symboltable.ValueSymbol;
import montiarc.helper.TypeCompatibilityChecker;

/**
 * Ensures that the arguments assigned to the subcomponent instance fit the
 * components parameters.
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
    
    // Check whether the types of the arguments fit the types of the
    // subcomponent's parameters
    for (ComponentInstanceSymbol instance : symb.getSubComponents()) {
      ComponentSymbol instanceType = instance.getComponentType().getReferencedSymbol();
      int paramIndex = 0;
      for (ValueSymbol<TypeReference<TypeSymbol>> arg : instance.getConfigArguments()) {
        ASTExpression expr = arg.getValue();
        Optional<? extends JavaTypeSymbolReference> actualArg = TypeCompatibilityChecker
            .getExpressionType(expr);
        if (actualArg.isPresent()) {
          if (paramIndex < instanceType.getConfigParameters().size()) {
            JFieldSymbol configParam = instanceType.getConfigParameters().get(paramIndex);
            Optional<JTypeSymbol> genericConfigParameter = getGenericConfigParameter(instance,
                configParam);
            
            // generic config parameter (e.g. "component <T> B(T t) {
            // subcomponent A(5, t) }")
            if (genericConfigParameter.isPresent()) {
              String configParamName = genericConfigParameter.get().getName();
              int index = getIndexOfGenericTypeParam(instance, configParam);
              
              ActualTypeArgument actualTypeArg = instance.getComponentType()
                  .getActualTypeArguments().get(index);
              if (TypeCompatibilityChecker.doTypesMatch(
                  (JTypeReference<? extends JTypeSymbol>) actualTypeArg.getType(),
                  actualArg.get())) {
                Log.error(
                    "0xMA064 Type of argument " + paramIndex + " (" + actualArg.get().getName()
                        + ") of subcomponent " + instance.getName() + " of component type '"
                        + node.getName() + "' does not fit generic parameter type "
                        + configParam.getType().getName() +" (instantiated with: " + actualTypeArg.getType().getName() + ")",
                    expr.get_SourcePositionStart());
              }
            }
            else if (!TypeCompatibilityChecker.doTypesMatch(
                configParam.getType(),
                actualArg.get())) {
              Log.error("0xMA064 Type of argument " + paramIndex + " (" + actualArg.get().getName()
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
  
  private Optional<JTypeSymbol> getGenericConfigParameter(ComponentInstanceSymbol instance,
      JFieldSymbol configParam) {
    ComponentSymbol instanceType = instance.getComponentType().getReferencedComponent().get();
    List<JTypeSymbol> typeGenericTypeParams = instanceType.getFormalTypeParameters();
    for (JTypeSymbol typeGenericTypeParam : typeGenericTypeParams) {
      if (configParam.getType().getName().equals(typeGenericTypeParam.getName())) {
        return Optional.of(typeGenericTypeParam);
      }
    }
    return Optional.empty();
  }
  
  private int getIndexOfGenericTypeParam(ComponentInstanceSymbol instance,
      JFieldSymbol configParam) {
    ComponentSymbol instanceType = instance.getComponentType().getReferencedComponent().get();
    List<JTypeSymbol> typeGenericTypeParams = instanceType.getFormalTypeParameters();
    int index = 0;
    for (JTypeSymbol typeGenericTypeParam : typeGenericTypeParams) {
      if (configParam.getType().getName().equals(typeGenericTypeParam.getName())) {
        return index;
      }
      index++;
    }
    return index;
  }
  
}
