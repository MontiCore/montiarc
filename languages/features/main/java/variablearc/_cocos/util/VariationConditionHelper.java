/* (c) https://github.com/MontiCore/monticore */
package variablearc._cocos.util;

import arcbasis._ast.ASTArcComponentType;
import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Expr;
import de.monticore.expressions.commonexpressions._ast.ASTBooleanAndOpExpression;
import de.monticore.expressions.commonexpressions._ast.ASTBooleanOrOpExpression;
import de.monticore.expressions.commonexpressions._ast.ASTEqualsExpression;
import de.monticore.expressions.commonexpressions._ast.ASTFieldAccessExpression;
import de.monticore.expressions.commonexpressions._ast.ASTLogicalNotExpression;
import de.monticore.expressions.commonexpressions._symboltable.ICommonExpressionsScope;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import variablearc.VariableArcMill;
import variablearc._visitor.VariableArcTraverser;
import variablearc.evaluation.ExpressionSet;
import variablearc.evaluation.ExpressionSolver;
import variablearc.evaluation.expressions.Expression;
import variablearc.evaluation.expressions.NegatedExpression;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VariationConditionHelper {

  private static Map<String, ICommonExpressionsScope> fieldAccessScopes = new HashMap<>();

  public static ExpressionSet getExpressionSetCopyWithContext(ExpressionSet exprSet) {

    List<Expression> expressionCopies = new ArrayList<>();

    ArrayList<Expression> originalExpressions = new ArrayList<>(exprSet.getExpressions());
    ASTNameExpressionChangeContext changeContextNameExpression = new ASTNameExpressionChangeContext();

    VariableArcTraverser traverser = VariableArcMill.traverser();
    traverser.add4ExpressionsBasis(changeContextNameExpression);

    for (Expression expr : originalExpressions) {
      saveFieldAccessScopesFromExpression(expr.getAstExpression());
      Expression copiedExpression;
      if (expr instanceof NegatedExpression) {
        copiedExpression = new NegatedExpression(expr.getAstExpression().deepClone());
      } else {
        copiedExpression = new Expression(expr.getAstExpression().deepClone());
      }
      expr.getAstExpression().accept(traverser);
      copiedExpression.getAstExpression().accept(traverser);
      expressionCopies.add(copiedExpression);
    }
    changeContextNameExpression.clearScopeMap();

    return new ExpressionSet(expressionCopies);
  }

  private static void saveFieldAccessScopesFromExpression(ASTExpression expr) {
    if (expr instanceof ASTFieldAccessExpression fieldAccess) {
      fieldAccessScopes.put(fieldAccess.getName(), fieldAccess.getEnclosingScope());
    }

    if (expr instanceof ASTBooleanAndOpExpression andOp) {
      saveFieldAccessScopesFromExpression(andOp.getLeft());
      saveFieldAccessScopesFromExpression(andOp.getRight());
    }
    if (expr instanceof ASTBooleanOrOpExpression orOp) {
      saveFieldAccessScopesFromExpression(orOp.getLeft());
      saveFieldAccessScopesFromExpression(orOp.getRight());
    }

    if (expr instanceof ASTEqualsExpression eq) {
      saveFieldAccessScopesFromExpression(eq.getLeft());
      saveFieldAccessScopesFromExpression(eq.getRight());
    }

    if (expr instanceof ASTLogicalNotExpression notOp)
      saveFieldAccessScopesFromExpression(notOp.getExpression());
  }

  public static ASTExpression changeNameExpressionInCondition(ASTExpression expr) {

    if (expr instanceof ASTNameExpression nameExpr) {

      if ((nameExpr.getName().contains(expr.getEnclosingScope().getSpanningSymbol().getPackageName()) && !expr.getEnclosingScope().getSpanningSymbol().getPackageName().isEmpty()) || nameExpr.getName().contains(expr.getEnclosingScope().getSpanningSymbol().getFullName() + "."))
        return expr;

      nameExpr.setName(expr.getEnclosingScope().getSpanningSymbol().getFullName() + "." + nameExpr.getName());
      return expr;
    } else if (expr instanceof ASTFieldAccessExpression fieldAccess) {
      expr.setEnclosingScope(fieldAccessScopes.get(fieldAccess.getName()));
    }

    if (expr instanceof ASTBooleanAndOpExpression andOp) {
      changeNameExpressionInCondition(andOp.getLeft());
      changeNameExpressionInCondition(andOp.getRight());
    }
    if (expr instanceof ASTBooleanOrOpExpression orOp) {
      changeNameExpressionInCondition(orOp.getLeft());
      changeNameExpressionInCondition(orOp.getRight());
    }

    if (expr instanceof ASTEqualsExpression eq) {
      changeNameExpressionInCondition(eq.getLeft());
      changeNameExpressionInCondition(eq.getRight());
    }

    if (expr instanceof ASTLogicalNotExpression notOp)
      return changeNameExpressionInCondition(notOp.getExpression());

    return expr;
  }

  public static Expr renamePrefix(Context ctx, Expr expr, String oldPrefix, String newPrefix) {
    if (expr.getNumArgs() == 0 && expr.isApp()) {
      String name = expr.getFuncDecl().getName().toString();
      if (name.startsWith(oldPrefix)) {
        return ctx.mkBoolConst(newPrefix + name.substring(oldPrefix.length()));
      }
      return expr;
    } else if (expr.isApp()) {
      Expr[] args = new Expr[expr.getNumArgs()];
      for (int i = 0; i < args.length; i++) {
        args[i] = renamePrefix(ctx, expr.getArgs()[i], oldPrefix, newPrefix);
      }
      return expr.getFuncDecl().apply(args);
    }
    return expr;
  }

  public static BoolExpr getFeatureConstraints(ASTArcComponentType node, ExpressionSet allConstraints, List<String> allFeatures, ExpressionSolver expSolver) {
    Context ctx = expSolver.getContext();
    BoolExpr featureConstraints = ctx.mkTrue();

    if (allConstraints.isEmpty() || !node.isPresentSymbol()) return featureConstraints;

    for (Expression constraintExpr : allConstraints.getExpressions()) {
      boolean prefixEmpty = constraintExpr.getPrefix().isEmpty();
      ExpressionSet constraintExprSet;
      if (!prefixEmpty) {
        var tempPrefix = allFeatures.stream().filter(e -> e.contains((constraintExpr.getPrefix().get().contains(".") ? constraintExpr.getPrefix().get().substring(constraintExpr.getPrefix().get().lastIndexOf(".")) : constraintExpr.getPrefix().get()))).toList();
        if (!tempPrefix.isEmpty()) {
          String newPrefix = tempPrefix.get(0).substring(0, tempPrefix.get(0).lastIndexOf("."));
          constraintExprSet = new ExpressionSet(new ArrayList<>() {{
            add(constraintExpr.copyWithPrefix(newPrefix));
          }});
        } else {

          constraintExprSet = new ExpressionSet(new ArrayList<>() {{
            add(constraintExpr.copyWithPrefix(node.getSymbol().getFullName() + (constraintExpr.getPrefix().isEmpty() ? "" : "." + constraintExpr.getPrefix().get())));
          }});
        }
      } else {
        constraintExprSet = new ExpressionSet(new ArrayList<>() {{
          add(constraintExpr.copyWithPrefix(node.getSymbol().getFullName() + (constraintExpr.getPrefix().isEmpty() ? "" : "." + constraintExpr.getPrefix().get())));
        }});
      }
      var varIfSolver = expSolver.getSolver(constraintExprSet);
      if (varIfSolver.isPresent()) {
        for (BoolExpr expr : varIfSolver.get().getAssertions()) {
          featureConstraints = ctx.mkAnd(featureConstraints, ctx.mkEq(expr, ctx.mkTrue()));
        }
      }
    }
    return featureConstraints;
  }

  public static BoolExpr convertNotToEqualsFalse(BoolExpr e) {
    if (e.isNot()) {
      Context ctx = ExpressionSolverService.getContext();
      Expr arg = e.getArgs()[0];
      if (arg instanceof BoolExpr boolExpr) {
        return ctx.mkEq(boolExpr, ctx.mkFalse());
      }
    }
    return e;
  }

}
