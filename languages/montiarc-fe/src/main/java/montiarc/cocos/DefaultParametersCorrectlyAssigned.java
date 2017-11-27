/*
 * Copyright (c) 2016 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package montiarc.cocos;

import java.util.List;
import java.util.Optional;

import de.monticore.java.symboltable.JavaTypeSymbolReference;
import de.monticore.literals.literals._ast.ASTSignedLiteral;
import de.monticore.symboltable.types.JTypeSymbol;
import de.monticore.symboltable.types.references.JTypeReference;
import de.monticore.types.TypesHelper;
import de.monticore.types.TypesPrinter;
import montiarc._ast.ASTComponent;
import montiarc._ast.ASTLiteralValue;
import montiarc._ast.ASTParameter;
import montiarc._cocos.MontiArcASTComponentCoCo;
import montiarc._symboltable.ComponentSymbol;
import montiarc.helper.TypeCompatibilityChecker;

/**
 * TODO JP Ensures that parameters in the component's head are defined in the
 * right order. It is not allowed to define a normal parameter after a
 * declaration of a default parameter. E.g.: Wrong: A[int x = 5, int y] Right:
 * B[int x, int y = 5]
 *
 * @author Andreas Wortmann
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
        //Scope fehlt??
        Optional<? extends JavaTypeSymbolReference> defaultValType = TypeCompatibilityChecker
            .getExpressionType(param.getDefaultValue().get().getExpression());
        if (!defaultValType.isPresent()) {
          
        }
        else if (!TypeCompatibilityChecker.doTypesMatch(defaultValType.get(), paramTypeSymbol)) {
          
        }
      }
    }
    
  }
  
}
