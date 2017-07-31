package de.monticore.lang.montiarc.cocos.automaton.correctness;

import java.util.Optional;

import de.monticore.java.javadsl._ast.ASTExpression;
import de.monticore.java.symboltable.JavaTypeSymbolReference;
import de.monticore.lang.montiarc.helper.TypeCompatibilityChecker;
import de.monticore.lang.montiarc.montiarc._ast.ASTComponentVariable;
import de.monticore.lang.montiarc.montiarc._ast.ASTComponentVariableDeclaration;
import de.monticore.lang.montiarc.montiarc._ast.ASTValuation;
import de.monticore.lang.montiarc.montiarc._cocos.MontiArcASTComponentVariableDeclarationCoCo;
import de.monticore.lang.montiarc.montiarc._symboltable.VariableSymbol;
import de.monticore.symboltable.types.JTypeSymbol;
import de.monticore.symboltable.types.references.JTypeReference;
import de.se_rwth.commons.logging.Log;

/**
 * Context condition for checking, if the type of a declared local
 * variable fits the type of the value it was assigned.
 * 
 * @author (last commit) $Author$
 * @version $Revision$, $Date$
 * @since $Version$
 */
public class InitialValueDoesNotFit implements MontiArcASTComponentVariableDeclarationCoCo {

  
  @Override
  public void check(ASTComponentVariableDeclaration node) {
    for (ASTComponentVariable var : node.getComponentVariables()) {
      if (var.valuationIsPresent()) {
        if (!doTypesMatch(var, var.getValuation().get())) {
          Log.error("0xAA421 The value of variable " + var.getName() + " does not fit its type.", var.get_SourcePositionStart());
        }
      }
    }
  }
  
  private boolean doTypesMatch(ASTComponentVariable var, ASTValuation valuation) {
    VariableSymbol varSymbol = (VariableSymbol) var.getSymbol().get();
    JTypeReference<? extends JTypeSymbol> varType = varSymbol.getTypeReference();
    Optional<? extends JavaTypeSymbolReference> exprType = TypeCompatibilityChecker.getExpressionType((ASTExpression) valuation);
    if (!exprType.isPresent()) {
      Log.error("0xAA423  Could not resolve type of expression for checking the initial value " + var.getName() + ".", var.get_SourcePositionStart());
      return true;
    } else {
      return TypeCompatibilityChecker.doTypesMatch(exprType.get(), varType);
    }
  }
}
