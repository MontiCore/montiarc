/*
 * Copyright (c) 2016 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package montiarc.cocos;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import de.monticore.java.symboltable.JavaTypeSymbolReference;
import de.monticore.mcexpressions._ast.ASTExpression;
import de.monticore.symboltable.types.JTypeSymbol;
import de.monticore.symboltable.types.references.JTypeReference;
import de.monticore.types.TypesHelper;
import de.monticore.types.TypesPrinter;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTComponent;
import montiarc._ast.ASTParameter;
import montiarc._cocos.MontiArcASTComponentCoCo;
import montiarc._symboltable.ComponentSymbol;
import montiarc.helper.MontiArcHCJavaDSLTypeResolver;
import montiarc.helper.TypeCompatibilityChecker;

/**
 * Ensures that default values of parameters in the component's head are
 * correctly assigned.
 *
 * @implements [Wor16] MT7: Default values of parameters conform to their type.
 * (p. 64, Lst. 4.22)
 * @implements [Wor16] MR3: No default values for configuration parameters of
 * purely generic types. (p. 59, Lst. 4.13)
 * @author Jerome Pfeiffer
 */
public class DefaultParametersCorrectlyAssigned
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
    ComponentSymbol comp = (ComponentSymbol) node.getSymbolOpt().get();
    List<ASTParameter> params = node.getHead().getParameterList();

    for (ASTParameter param : params) {
      if (param.isPresentDefaultValue()) {
        int dimension = TypesHelper.getArrayDimensionIfArrayOrZero(param.getType());
        JTypeReference<? extends JTypeSymbol> paramTypeSymbol = new JavaTypeSymbolReference(
            TypesPrinter.printTypeWithoutTypeArgumentsAndDimension(param
                .getType()),
            comp.getSpannedScope(), dimension);
        
        MontiArcHCJavaDSLTypeResolver javaTypeResolver = new MontiArcHCJavaDSLTypeResolver();
        ASTExpression expression = param.getDefaultValue().getExpression();
        // param.getDefaultValue().get().getValue().accept(javaTypeResolver);
        expression.accept(javaTypeResolver);
        Optional<JavaTypeSymbolReference> result = javaTypeResolver.getResult();
        if (!result.isPresent()) {
          Log.error(
              "0xMA068 Could not resolve type of default parameter value for comparing it with the referenced parameter type.",
              param.getDefaultValue().get_SourcePositionStart());
        }
        else if (!TypeCompatibilityChecker.doTypesMatch(result.get(),
            result.get().getReferencedSymbol().getFormalTypeParameters().stream()
                .map(p -> (JTypeSymbol) p).collect(Collectors.toList()),
            result.get().getActualTypeArguments().stream()
                .map(a -> (JavaTypeSymbolReference) a.getType())
                .collect(Collectors.toList()),
            paramTypeSymbol,
            paramTypeSymbol.getReferencedSymbol().getFormalTypeParameters().stream()
                .map(p -> (JTypeSymbol) p).collect(Collectors.toList()),
            paramTypeSymbol.getActualTypeArguments().stream()
                .map(a -> (JavaTypeSymbolReference) a.getType())
                .collect(Collectors.toList()))) {
          Log.error("0xMA062 Type of parameter " + param.getName()
              + " in the parameter declaration does not match the type of its assigned value. Type "
              +
              paramTypeSymbol.getName() + " can not cast to type " + result.get().getName() + ".",
              param.get_SourcePositionStart());
        }
      }
    }
    
  }
  
}
