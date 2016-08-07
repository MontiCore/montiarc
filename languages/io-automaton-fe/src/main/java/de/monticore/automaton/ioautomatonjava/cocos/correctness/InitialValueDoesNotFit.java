package de.monticore.automaton.ioautomatonjava.cocos.correctness;

import de.monticore.automaton.ioautomaton.TypeCompatibilityChecker;
import de.monticore.automaton.ioautomaton._ast.ASTInputDeclaration;
import de.monticore.automaton.ioautomaton._ast.ASTOutputDeclaration;
import de.monticore.automaton.ioautomaton._ast.ASTValuationExt;
import de.monticore.automaton.ioautomaton._ast.ASTVariable;
import de.monticore.automaton.ioautomaton._ast.ASTVariableDeclaration;
import de.monticore.automaton.ioautomaton._cocos.IOAutomatonASTInputDeclarationCoCo;
import de.monticore.automaton.ioautomaton._cocos.IOAutomatonASTOutputDeclarationCoCo;
import de.monticore.automaton.ioautomaton._cocos.IOAutomatonASTVariableDeclarationCoCo;
import de.monticore.automaton.ioautomaton._symboltable.VariableSymbol;
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
public class InitialValueDoesNotFit implements IOAutomatonASTInputDeclarationCoCo, IOAutomatonASTVariableDeclarationCoCo ,IOAutomatonASTOutputDeclarationCoCo {

  @Override
  public void check(ASTInputDeclaration node) {
    for (ASTVariable var : node.getVariables()) {
      if (var.valuationIsPresent()) {
        if (!doTypesMatch(var, var.getValuation().get())) {
          Log.error("0xAA420 The value of input " + var.getName() + " does not fit its type.", var.get_SourcePositionStart());
        }
      }
    }
  }
  
  @Override
  public void check(ASTVariableDeclaration node) {
    for (ASTVariable var : node.getVariables()) {
      if (var.valuationIsPresent()) {
        if (!doTypesMatch(var, var.getValuation().get())) {
          Log.error("0xAA421 The value of variable " + var.getName() + " does not fit its type.", var.get_SourcePositionStart());
        }
      }
    }
  }
  
  @Override
  public void check(ASTOutputDeclaration node) {
    for (ASTVariable var : node.getVariables()) {
      if (var.valuationIsPresent()) {
        if (!doTypesMatch(var, var.getValuation().get())) {
          Log.error("0xAA422 The value of output " + var.getName() + " does not fit its type.", var.get_SourcePositionStart());
        }
      }
    }
  }
  
  private boolean doTypesMatch(ASTVariable var, ASTValuationExt valuation) {
    VariableSymbol varSymbol = (VariableSymbol) var.getSymbol().get();
    JTypeReference<? extends JTypeSymbol> varType = varSymbol.getTypeReference();
    
    return TypeCompatibilityChecker.doTypesMatch(valuation.getExpression(), varType);
  }
}
