package montiarc.cocos;

import java.util.Optional;

import de.monticore.java.symboltable.JavaTypeSymbolReference;
import de.monticore.symboltable.types.JTypeSymbol;
import de.monticore.symboltable.types.references.JTypeReference;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTInterface;
import montiarc._ast.ASTVariable;
import montiarc._cocos.MontiArcASTInterfaceCoCo;
import montiarc._cocos.MontiArcASTVariableCoCo;
import montiarc._symboltable.VariableSymbol;
import montiarc.helper.TypeCompatibilityChecker;

/**
 * Context condition for checking, if the type of a declared local
 * variable fits the type of the value it was assigned.
 *
 * @author (last commit) $Author$
 * @version $Revision$, $Date$
 * @since $Version$
 */
//public class InitialValueDoesNotFit implements
//        MontiArcASTInterfaceCoCo,
//        IOAutomatonASTOutputDeclarationCoCo,
//        MontiArcASTVariableCoCo
//{

//  @Override
//  public void check(ASTInterface node) {
//    for (ASTVariable var : node.getVariables()) {
//      if (var.valuationIsPresent()) {
//        if (!doTypesMatch(var, var.getValuation().get())) {
//          Log.error("0xAA420 The value of input " + var.getName() + " does not fit its type.", var.get_SourcePositionStart());
//        }
//      }
//    }
//  }

//  @Override
//  public void check(ASTVariable node) {
//    for (ASTVariable var : node.get) {
//      if (var.valuationIsPresent()) {
//        if (!doTypesMatch(var, var.getValuation().get())) {
//          Log.error("0xAA421 The value of variable " + var.getName() + " does not fit its type.", var.get_SourcePositionStart());
//        }
//      }
//    }
//  }

//  @Override
//  public void check(ASTOutputDeclaration node) {
//    for (ASTVariable var : node.getVariables()) {
//      if (var.valuationIsPresent()) {
//        if (!doTypesMatch(var, var.getValuation().get())) {
//          Log.error("0xAA422 The value of output " + var.getName() + " does not fit its type.", var.get_SourcePositionStart());
//        }
//      }
//    }
//  }

//  private boolean doTypesMatch(ASTVariable var, ASTValuationExt valuation) {
//    VariableSymbol varSymbol = (VariableSymbol) var.getSymbol().get();
//    JTypeReference<? extends JTypeSymbol> varType = varSymbol.getTypeReference();
//    Optional<? extends JavaTypeSymbolReference> exprType = TypeCompatibilityChecker.getExpressionType(valuation.getExpression());
//    if (!exprType.isPresent()) {
//      Log.error("0xAA423  Could not resolve type of expression for checking the initial value " + var.getName() + ".", var.get_SourcePositionStart());
//      return true;
//    } else {
//      return TypeCompatibilityChecker.doTypesMatch(exprType.get(), varType);
//    }
//  }
//}
