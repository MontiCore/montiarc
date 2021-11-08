/* (c) https://github.com/MontiCore/monticore */
package montiarc.check;

import arcbasis.check.ArcTypeCheck;
import de.monticore.expressions.commonexpressions.CommonExpressionsMill;
import de.monticore.expressions.commonexpressions._ast.ASTCallExpression;
import de.monticore.expressions.commonexpressions._visitor.CommonExpressionsTraverser;
import de.monticore.symbols.basicsymbols._symboltable.FunctionSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.types.check.DeriveSymTypeOfCommonExpressions;
import de.monticore.types.check.NameToCallExpressionVisitor;
import de.monticore.types.check.SymTypeExpression;

import java.util.*;

public class ArcDeriveSymTypeOfCommonExpression extends DeriveSymTypeOfCommonExpressions {

  protected List<FunctionSymbol> getFittingMethods(List<FunctionSymbol> methodlist, ASTCallExpression expr) {
    List<FunctionSymbol> fittingMethods = new ArrayList();
    Iterator var5 = methodlist.iterator();

    while (true) {
      FunctionSymbol method;
      do {
        if (!var5.hasNext()) {
          return fittingMethods;
        }

        method = (FunctionSymbol) var5.next();
      } while (expr.getArguments().getExpressionList().size() != method.getParameterList().size());

      boolean success = true;

      for (int i = 0; i < method.getParameterList().size(); ++i) {
        expr.getArguments().getExpression(i).accept(this.getTraverser());
        if (!((VariableSymbol) method.getParameterList().get(i)).getType().deepEquals(this.typeCheckResult.getCurrentResult()) && !ArcTypeCheck.compatible(((VariableSymbol) method.getParameterList().get(i)).getType(), this.typeCheckResult.getCurrentResult())) {
          success = false;
        }
      }

      if (success) {
        fittingMethods.add(method);
      }
    }
  }

  @Override
  public void traverse(ASTCallExpression expr) {
    NameToCallExpressionVisitor visitor = new NameToCallExpressionVisitor();
    CommonExpressionsTraverser traverser = CommonExpressionsMill.traverser();
    traverser.setCommonExpressionsHandler(visitor);
    traverser.add4CommonExpressions(visitor);
    traverser.setExpressionsBasisHandler(visitor);
    traverser.add4ExpressionsBasis(visitor);
    expr.accept(traverser);
    SymTypeExpression innerResult;
    expr.getExpression().accept(getTraverser());
    if (typeCheckResult.isPresentCurrentResult()) {
      innerResult = typeCheckResult.getCurrentResult();
      //resolve methods with name of the inner expression
      List<FunctionSymbol> methodlist = innerResult.getMethodList(expr.getName(), typeCheckResult.isType());
      //count how many methods can be found with the correct arguments and return type
      List<FunctionSymbol> fittingMethods = getFittingMethods(methodlist,expr);
      //if the last result is static then filter for static methods
      if(typeCheckResult.isType()){
        fittingMethods = filterModifiersFunctions(fittingMethods);
      }
      //there can only be one method with the correct arguments and return type
      if (!fittingMethods.isEmpty()) {
        if (fittingMethods.size() > 1) {
          SymTypeExpression returnType = fittingMethods.get(0).getReturnType();
          for (FunctionSymbol method : fittingMethods) {
            if (!returnType.deepEquals(method.getReturnType())) {
              logError("0xA1238", expr.get_SourcePositionStart());
            }
          }
        }
        SymTypeExpression result = fittingMethods.get(0).getReturnType();
        typeCheckResult.setMethod();
        typeCheckResult.setCurrentResult(result);
      } else {
        typeCheckResult.reset();
        logError("0xA1240", expr.get_SourcePositionStart());
      }
    } else {
      Collection<FunctionSymbol> methodcollection = getScope(expr.getEnclosingScope()).resolveFunctionMany(expr.getName());
      List<FunctionSymbol> methodlist = new ArrayList<>(methodcollection);
      //count how many methods can be found with the correct arguments and return type
      List<FunctionSymbol> fittingMethods = getFittingMethods(methodlist,expr);
      //there can only be one method with the correct arguments and return type
      if (fittingMethods.size() == 1) {
        Optional<SymTypeExpression> wholeResult = Optional.of(fittingMethods.get(0).getReturnType());
        typeCheckResult.setMethod();
        typeCheckResult.setCurrentResult(wholeResult.get());
      } else {
        typeCheckResult.reset();
        logError("0xA1241", expr.get_SourcePositionStart());
      }
    }
  }
}