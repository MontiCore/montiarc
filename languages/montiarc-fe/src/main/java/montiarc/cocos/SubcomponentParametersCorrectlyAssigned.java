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
import montiarc.helper.TypeCompatibilityChecker;

/**
 * Ensures that the arguments assigned to the subcomponent instance fit the
 * components parameters in terms of type correctness.
 *
 *
 *
 * Furhermore, this coco compares the number of arguments passed to the subcomponent with
 * the parameters of the instantiated type. It considers default parameters that
 * are optional when instantiating a component. Type and Ordering is checked in
 * other cocos.
 * 
 * @implements [Wor16] MR1: Arguments of configuration parameters with default
 * values may be omitted during subcomponent declaration. (p. 58, Lst. 4.11)
 * 
 *
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
    ComponentSymbol sym = (ComponentSymbol) node.getSymbol().get();
    
    // Check whether the types of the arguments fit the types of the
    // subcomponent's parameters
    for (ComponentInstanceSymbol instance : sym.getSubComponents()) {
      ComponentSymbol instanceType = instance.getComponentType().getReferencedSymbol();
      int paramIndex = 0;
      for (ASTExpression arg : instance.getConfigArguments()) {
        ASTExpression expr = arg;
        Optional<? extends JavaTypeSymbolReference> actualArg = TypeCompatibilityChecker
            .getExpressionType(expr);
        if (actualArg.isPresent()) {
          if (paramIndex < instanceType.getConfigParameters().size()) {
            JFieldSymbol configParam = instanceType.getConfigParameters().get(paramIndex);
            
            // generic config parameter (e.g. "component <T> B(T t) {
            // subcomponent A(5, t) }")
            Optional<Integer> index = getIndexOfGenericTypeParam(instance, configParam);
            if (index.isPresent()) {
              ActualTypeArgument actualTypeArg = instance.getComponentType()
                  .getActualTypeArguments().get(index.get());
              if (!TypeCompatibilityChecker.doTypesMatch(
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
    
    List<ASTSubComponent> subComps = node.getSubComponents();
    for (ASTSubComponent sub : subComps) {
      String type = TypesPrinter.printTypeWithoutTypeArgumentsAndDimension(sub.getType());
      Optional<ComponentSymbol> subcompSym = sym.getEnclosingScope().<ComponentSymbol> resolve(type,
          ComponentSymbol.KIND);
      if (subcompSym.isPresent()) {
        ASTComponent subcompType = (ASTComponent) subcompSym.get().getAstNode().get();
        List<ASTParameter> params = subcompType.getHead().getParameters();
        int numberOfNecessaryConfigParams = params.size() - getNumberOfDefaultParameters(params);
        if (numberOfNecessaryConfigParams > sub.getArguments().size() || sub.getArguments().size() > params.size()) {
          Log.error(String.format("0xMA082 Subcomponent of type \"%s\" is instantiated with "
              + sub.getArguments().size() + " arguments but requires "
              + numberOfNecessaryConfigParams + " arguments by type definition.", type),
              sub.get_SourcePositionStart());
        }
      }
    }
  }
  
  
  private Optional<Integer> getIndexOfGenericTypeParam(ComponentInstanceSymbol instance,
      JFieldSymbol configParam) {
    ComponentSymbol instanceType = instance.getComponentType().getReferencedComponent().get();
    List<JTypeSymbol> typeGenericTypeParams = instanceType.getFormalTypeParameters();
    int index = 0;
    for (JTypeSymbol typeGenericTypeParam : typeGenericTypeParams) {
      if (configParam.getType().getName().equals(typeGenericTypeParam.getName())) {
        return Optional.of(index);
      }
      index++;
    }
    return Optional.empty();
  }
  
  private int getNumberOfDefaultParameters(List<ASTParameter> params) {
    int counter = 0;
    
    for (ASTParameter param : params) {
      if (param.getDefaultValue().isPresent()) {
        counter++;
      }
    }
    return counter;
  }
  
}
