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
import de.monticore.java.types.HCJavaDSLTypeResolver;
import de.monticore.symboltable.types.JTypeSymbol;
import de.monticore.symboltable.types.references.JTypeReference;
import de.monticore.types.TypesHelper;
import de.monticore.types.TypesPrinter;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTComponent;
import montiarc._ast.ASTParameter;
import montiarc._cocos.MontiArcASTComponentCoCo;
import montiarc._symboltable.ComponentSymbol;
import montiarc.helper.TypeCompatibilityChecker;

/**
 * Ensures that default values of parameters in the component's head are correctly assigned.
 *
 * @implements [Wor16] MT7: Default values of parameters conform to their type. (p. 64, Lst. 4.22)
 * @author Jerome Pfeiffer
 */
public class DefaultParametersCorrectlyAssigned
    implements MontiArcASTComponentCoCo {
  
  /**
   * @see montiarc._cocos.MontiArcASTComponentCoCo#check(montiarc._ast.ASTComponent)
   */
  @Override
  public void check(ASTComponent node) {
    List<ASTParameter> params = node.getHead().getParameters();
    ComponentSymbol comp = (ComponentSymbol) node.getSymbol().get();
    for (ASTParameter param : params) {
      
      if (param.getDefaultValue().isPresent()) {
        int dimension = TypesHelper.getArrayDimensionIfArrayOrZero(param.getType());
        JTypeReference<? extends JTypeSymbol> paramTypeSymbol = new JavaTypeSymbolReference(
            TypesPrinter.printTypeWithoutTypeArgumentsAndDimension(param
                .getType()),
            comp.getSpannedScope(), dimension);
        
        HCJavaDSLTypeResolver javaTypeResolver = new HCJavaDSLTypeResolver();
        ASTExpression expression = param.getDefaultValue().get().getExpression();
        // param.getDefaultValue().get().getValue().accept(javaTypeResolver);
        expression.accept(javaTypeResolver);
        Optional<JavaTypeSymbolReference> result = javaTypeResolver.getResult();
        if (!result.isPresent()) {
          Log.error(
              "0xMA068 Could not resolve type of default parameter value for comparing it with the referenced parameter type.",
              param.getDefaultValue().get().get_SourcePositionStart());
        }
        else if (!TypeCompatibilityChecker.doTypesMatch(result.get(), paramTypeSymbol)) {
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
