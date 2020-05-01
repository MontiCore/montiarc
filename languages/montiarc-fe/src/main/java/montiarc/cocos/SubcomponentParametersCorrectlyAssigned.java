/* (c) https://github.com/MontiCore/monticore */
package montiarc.cocos;

import de.monticore.java.symboltable.JavaFieldSymbol;
import de.monticore.java.symboltable.JavaTypeSymbolReference;
import de.monticore.mcexpressions._ast.ASTExpression;
import de.monticore.symboltable.types.JFieldSymbol;
import de.monticore.symboltable.types.JTypeSymbol;
import de.monticore.symboltable.types.references.ActualTypeArgument;
import de.monticore.symboltable.types.references.JTypeReference;
import de.monticore.types.TypesPrinter;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTComponent;
import montiarc._ast.ASTParameter;
import montiarc._ast.ASTSubComponent;
import montiarc._cocos.MontiArcASTComponentCoCo;
import montiarc._symboltable.ComponentInstanceSymbol;
import montiarc._symboltable.ComponentSymbol;
import montiarc._symboltable.ComponentSymbolReference;
import montiarc.helper.TypeCompatibilityChecker;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Ensures that the arguments assigned to the subcomponent instance fit the
 * components parameters in terms of type correctness. Furhermore, this coco
 * compares the number of arguments passed to the subcomponent with the
 * parameters of the instantiated type. It considers default parameters that are
 * optional when instantiating a component. Type and Ordering is checked in
 * other cocos.
 * 
 * @implements [Wor16] MR1: Arguments of configuration parameters with default
 * values may be omitted during subcomponent declaration. (p. 58, Lst. 4.11)
 * @implements [Wor16] MR4: All mandatory component configuration parameters
 *  precede the parameters with default values. (p.60 Lst. 4.14)
 * @implements TODO: Klaeren welche CoCo in der Literatur repraesentiert wird.
 */
public class SubcomponentParametersCorrectlyAssigned
    implements MontiArcASTComponentCoCo {
  
  /**
   * @see montiarc._cocos.MontiArcASTComponentCoCo#check(montiarc._ast.ASTComponent)
   */
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
    ComponentSymbol sym = (ComponentSymbol) node.getSymbolOpt().get();
    
    // Check whether the types of the arguments fit the types of the
    // subcomponent's parameters
    for (ComponentInstanceSymbol instance : sym.getSubComponents()) {
      if(!instance.getComponentType().existsReferencedSymbol()){
        continue; // TODO Error handling required or already done by another coco?
      }
      ComponentSymbol instanceType = instance.getComponentType().getReferencedSymbol();
      for (ASTExpression arg : instance.getConfigArguments()) {
        int paramIndex = instance.getConfigArguments().indexOf(arg);
        checkConfigArgument(node, sym, instance, instanceType, paramIndex, arg);
      }
    }

    for (ASTSubComponent sub : node.getSubComponents()) {
      String type = TypesPrinter.printTypeWithoutTypeArgumentsAndDimension(sub.getType());
      Optional<ComponentSymbol> subcompSym = sym.getEnclosingScope().<ComponentSymbol> resolve(type,
          ComponentSymbol.KIND);
      if (subcompSym.isPresent()) {
        ASTComponent subcompType = (ASTComponent) subcompSym.get().getAstNode().get();
        List<ASTParameter> params = subcompType.getHead().getParameterList();
        int numberOfNecessaryConfigParams = params.size() - getNumberOfDefaultParameters(params);
        if (numberOfNecessaryConfigParams > sub.getArgumentsList().size()
            || sub.getArgumentsList().size() > params.size()) {
          Log.error(
              String.format("0xMA082 Subcomponent of type \"%s\" is " +
                                "instantiated with %d arguments but " +
                                "requires %d arguments by type definition.",
                  type, sub.getArgumentsList().size(),
                  numberOfNecessaryConfigParams),
              sub.get_SourcePositionStart());
        }
      }
    }
  }

  /**
   * Performs the correctness checks on a config argument of a subcomponent
   * instance.
   *
   * @param node The node of the component containing the subcomponent with the
   *             config argument to check
   * @param sym The symbol of the component containing the subcomponent with the
   *            config argument to check.
   * @param instance The symbol of the subcomponent instance
   * @param instanceType The component type of the subcomponent instance
   * @param paramIndex The index of the config argument in the list of arguments
   *                   for the subcomponent
   * @param arg The expression of the configuration argument
   */
  private void checkConfigArgument(ASTComponent node,
                                   ComponentSymbol sym,
                                   ComponentInstanceSymbol instance,
                                   ComponentSymbol instanceType,
                                   int paramIndex,
                                   ASTExpression arg) {

    Optional<? extends JavaTypeSymbolReference> typeArgumentTypeOpt
        = TypeCompatibilityChecker.getExpressionType(arg);

    if (typeArgumentTypeOpt.isPresent()) {

      // The type of the config argument was determined
      if (paramIndex < instanceType.getConfigParameters().size()) {

        JFieldSymbol configParam = instanceType.getConfigParameters().get(paramIndex);

        // generic config parameter (e.g. "component <T> B(T t) {
        // subcomponent A(5, t) }")
        Optional<Integer> index = getIndexOfGenericTypeParam(instance, configParam);
        final JavaTypeSymbolReference actualArg = typeArgumentTypeOpt.get();
        if (index.isPresent()) {
          ActualTypeArgument actualTypeArg = instance.getComponentType()
              .getActualTypeArguments().get(index.get());
          final JTypeReference<? extends JTypeSymbol> actualTypeArgType =
              (JTypeReference<? extends JTypeSymbol>) actualTypeArg.getType();

          if(!actualArg.existsReferencedSymbol()){
            Log.error(String.format("0xMA103 Type %s is used but does not exist.",
                actualArg.getName()),
                arg.get_SourcePositionStart());
          }

          if(actualTypeArgType.existsReferencedSymbol()
                 && actualArg.existsReferencedSymbol()) {
            if (!TypeCompatibilityChecker.doTypesMatch(
                actualTypeArgType,
                actualTypeArgType.getReferencedSymbol().getFormalTypeParameters()
                    .stream()
                    .map(p -> (JTypeSymbol) p)
                    .collect(Collectors.toList()),
                (actualTypeArgType)
                    .getActualTypeArguments()
                    .stream()
                    .map(a -> (JavaTypeSymbolReference) a.getType())
                    .collect(Collectors.toList()),
                actualArg,
                actualArg.getReferencedSymbol().getFormalTypeParameters()
                    .stream()
                    .map(p -> (JTypeSymbol) p)
                    .collect(Collectors.toList()),
                actualArg.getActualTypeArguments().stream()
                    .map(a -> (JavaTypeSymbolReference) a.getType())
                    .collect(Collectors.toList()))
                ) {
              Log.error(
                  String.format("0xMA064 Type of argument %d (%s) of " +
                                    "subcomponent %s of component type " +
                                    "'%s' does not fit generic parameter " +
                                    "type %s (instantiated with: %s)",
                      paramIndex,
                      actualArg.getName(),
                      instance.getName(),
                      node.getName(),
                      configParam.getType().getName(),
                      actualTypeArg.getType().getName()),
                  arg.get_SourcePositionStart());
            }
          }
        }
        else {
          List<JTypeSymbol> actualArgTypeParams =
              TypeCompatibilityChecker.toJTypeSymbols(
                  actualArg.getReferencedSymbol().getFormalTypeParameters());

          List<JTypeReference<?>> actualArgActualParams =
              getSubstitutedGenericArguments(
                  TypeCompatibilityChecker.toJTypeReferences(actualArg.getActualTypeArguments()),
                  sym, instance);

          JTypeReference<? extends JTypeSymbol> configParamType =
              configParam.getType();
          List<JTypeSymbol> configParamTypeParams =
              TypeCompatibilityChecker.toJTypeSymbols(
                  configParamType.getReferencedSymbol().getFormalTypeParameters());

          List<JTypeReference<?>> configParamActualParams =
              getSubstitutedGenericArguments(
                  TypeCompatibilityChecker.toJTypeReferences(
                      configParam.getType().getActualTypeArguments()),
                  sym, instance);

          final boolean typesMatch = TypeCompatibilityChecker.doTypesMatch(
              actualArg,
              actualArgTypeParams,
              actualArgActualParams,
              configParamType,
              configParamTypeParams,
              configParamActualParams);

          if (actualArgActualParams.size() == configParamActualParams.size()
              && !typesMatch) {
            Log.error(
                String.format("0xMA064 Type of argument %d (%s) of " +
                                  "subcomponent %s of component type '%s' " +
                                  "does not fit parameter type %s",
                    paramIndex,
                    actualArg.getName(),
                    instance.getName(),
                    node.getName(),
                    configParam.getType().getName()),
                arg.get_SourcePositionStart());
          }
        }
      }
    }
    else {
      Log.error(String.format("0xMA065 Could not find type of argument " +
                                  "no %d of subcomponent %s",
          paramIndex, instance.getName()),
          arg.get_SourcePositionStart());
    }
  }

  private Optional<Integer> getIndexOfGenericTypeParam(ComponentInstanceSymbol instance,
                                                       JFieldSymbol configParam) {
    ComponentSymbol instanceType
        = instance.getComponentType().getReferencedComponent().get();
    List<JTypeSymbol> typeGenericTypeParams = instanceType.getFormalTypeParameters();

    for (JTypeSymbol typeGenericTypeParam : typeGenericTypeParams) {
      if (configParam.getType().getName().equals(typeGenericTypeParam.getName())) {
        return Optional.of(typeGenericTypeParams.indexOf(typeGenericTypeParam));
      }
    }
    return Optional.empty();
  }
  
  private List<JTypeReference<?>> getSubstitutedGenericArguments(
      List<JTypeReference<?>> actualParams, ComponentSymbol surroundComponentSymbol,
      ComponentInstanceSymbol instance) {

    List<JTypeReference<?>> substitutedArgList = new ArrayList<>();

    for (JTypeReference<?> param : actualParams) {
      // if referenced symbol cannot be loaded we assume that it is a
      // generic type param. Whether the type exists is checked by
      // another coco.
      if (!param.existsReferencedSymbol()) {
        
        // we try to resolve its name to a generic parameter in the
        // instance or in the surrounding component type.
        
        // Check component instance type
        final ComponentSymbolReference componentTypeOfInstance = instance.getComponentType();
        for (JTypeSymbol instanceTypeFormalTypeParam : componentTypeOfInstance
            .getFormalTypeParameters()) {
          if (param.getName().equals(instanceTypeFormalTypeParam.getName())) {
            final JavaFieldSymbol configParam
                = new JavaFieldSymbol(param.getName(), JavaFieldSymbol.KIND, (JavaTypeSymbolReference) param);
            Optional<Integer> index = getIndexOfGenericTypeParam(instance, configParam);

            index.ifPresent(i -> substitutedArgList.add(
                (JTypeReference<?>) componentTypeOfInstance.getActualTypeArguments().get(i).getType()));
          }
        }
        
        // Check surrounding component
        for (JTypeSymbol compSymFormalTypeParam :
            surroundComponentSymbol.getFormalTypeParameters()) {

          // if this is the case the parameter is set in the component
          // instantiation as subcomponent instance. This has to be checked
          // there then.
          if (param.getName().equals(compSymFormalTypeParam.getName())) {
            return new ArrayList<>();
          }
        }
      }
      else {
        substitutedArgList.add(param);
      }
    }
    return substitutedArgList;
  }
  
  private int getNumberOfDefaultParameters(List<ASTParameter> params) {
    return params.stream()
               .filter(ASTParameter::isPresentDefaultValue)
               .collect(Collectors.toList())
               .size();
  }
  
}
