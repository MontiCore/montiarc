/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcbasis._ast.ASTArcArgument;
import arcbasis._ast.ASTArcComponentType;
import arcbasis._ast.ASTArcField;
import arcbasis._ast.ASTComponentInstance;
import arcbasis._ast.ASTComponentInstantiation;
import arcbasis._cocos.ArcBasisASTArcComponentTypeCoCo;
import arcbasis._symboltable.IArcBasisScope;
import com.google.common.base.Preconditions;
import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Status;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.se_rwth.commons.SourcePosition;
import de.se_rwth.commons.logging.Log;
import montiarc.MontiArcMill;
import montiarc._cocos.util.ASTNameCollector;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc._ast.ASTArcFeatureDeclaration;
import variablearc._ast.ASTVariableArcFullVariantComponentType;
import variablearc._cocos.util.DuplicateElementsService;
import variablearc._cocos.util.ExpressionSolverService;
import variablearc._cocos.util.VariationConditionHelper;
import variablearc._symboltable.IVariableArcComponentTypeSymbol;
import variablearc.evaluation.ExpressionSet;
import variablearc.evaluation.ExpressionSolver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static montiarc.util.ArcError.FIELD_REF_IN_STATIC_CONTEXT;

public class NoFieldInSubcomponentArgument4Family implements ArcBasisASTArcComponentTypeCoCo {

  @Override
  public void check(@NotNull ASTArcComponentType node) {
    Preconditions.checkNotNull(node);

    if (DuplicateElementsService.duplicateElementPresent(node)) {
      return;
    }

    ExpressionSolver expSolver = ExpressionSolverService.getExpressionSolver();
    Context ctx = ExpressionSolverService.getContext();

    ExpressionSet constraints;
    Map<ASTArcField, BoolExpr> fieldConditions;
    Map<ASTComponentInstance, BoolExpr> subcomponentConditions;

    List<String> allFeatures = node.getBody().getArcElementList().stream()
      .filter(e -> e instanceof ASTArcFeatureDeclaration)
      .map(v -> (ASTArcFeatureDeclaration) v)
      .map(ASTArcFeatureDeclaration::getArcFeatureList)
      .flatMap(List::stream)
      .map(e -> node.getSymbol().getFullName() + "." + e.getSymbol().getName())
      .toList();

    if (node instanceof ASTVariableArcFullVariantComponentType) {
      ASTVariableArcFullVariantComponentType variant = (ASTVariableArcFullVariantComponentType) node;
      constraints = null;
      if (node.isPresentSymbol()) {
        constraints = ((IVariableArcComponentTypeSymbol) variant.getOriginal().getSymbol()).getConstraints();
      }

      fieldConditions = variant.getFieldConditions();
      subcomponentConditions = variant.getSubcomponentConditions();
    } else {
      constraints = null;
      if (node.isPresentSymbol()) {
        constraints = ((IVariableArcComponentTypeSymbol) node.getSymbol()).getConstraints();
      }

      fieldConditions = new HashMap<>();
      for (ASTArcField field : node.getFields()) {
        fieldConditions.put(field, ctx.mkTrue());
      }

      subcomponentConditions = new HashMap<>();
      List<ASTComponentInstance> mainSubcomponents = node.getBody().getArcElementList().stream()
        .filter(e -> e instanceof ASTComponentInstantiation)
        .map(v -> ((ASTComponentInstantiation) v).getComponentInstanceList())
        .flatMap(List::stream)
        .toList();
      for (ASTComponentInstance component : mainSubcomponents) {
        subcomponentConditions.put(component, ctx.mkTrue());
      }
    }

    BoolExpr featureConstraints = VariationConditionHelper.getFeatureConstraints(node, constraints, allFeatures, expSolver);

    for (Map.Entry<ASTComponentInstance, BoolExpr> componentEntry : subcomponentConditions.entrySet()) {
      if (!componentEntry.getKey().isPresentArcArguments()) {
        continue;
      }

      List<BoolExpr> subcomponentActiveConditions = new ArrayList<>(List.of(featureConstraints, componentEntry.getValue()));
      if (ExpressionSolverService.solve(subcomponentActiveConditions) == Status.UNSATISFIABLE) {
        continue;
      }

      for (ASTArcArgument argument : componentEntry.getKey().getArcArguments().getArcArgumentList()) {
        ASTNameCollector nameCollector = new ASTNameCollector();
        var traverser = MontiArcMill.traverser();
        traverser.add4ExpressionsBasis(nameCollector);
        argument.getExpression().accept(traverser);

        for (ASTNameExpression nameExpression : nameCollector.getExpressions()) {
          String variableName = resolveField(nameExpression)
            .map(VariableSymbol::getName)
            .orElse(nameExpression.getName());
          var possibleFields = fieldConditions.entrySet().stream()
            .filter(e -> e.getKey().getName().equals(variableName))
            .toList();

          for (Map.Entry<ASTArcField, BoolExpr> fieldEntry : possibleFields) {
            List<BoolExpr> fieldAndSubcomponentActiveConditions =
              new ArrayList<>(List.of(featureConstraints, componentEntry.getValue(), fieldEntry.getValue()));
            if (ExpressionSolverService.solve(fieldAndSubcomponentActiveConditions) == Status.SATISFIABLE) {
              SourcePosition sourcePosition = argument.getExpression().get_SourcePositionStart();
              Log.error(
                FIELD_REF_IN_STATIC_CONTEXT.format(fieldEntry.getKey().getName()),
                sourcePosition,
                argument.getExpression().get_SourcePositionEnd()
              );
            }
          }
        }
      }
    }
  }

  protected Optional<VariableSymbol> resolveField(@NotNull ASTNameExpression expr) {
    Preconditions.checkNotNull(expr);

    if (!(expr.getEnclosingScope() instanceof IArcBasisScope)) {
      return Optional.empty();
    }

    String name = expr.getName();
    IArcBasisScope scope = (IArcBasisScope) expr.getEnclosingScope();
    List<VariableSymbol> variables = scope.resolveVariableMany(name, v -> true);

    if (variables.size() != 1 || !isField(variables.get(0))) {
      return Optional.empty();
    }

    return Optional.of(variables.get(0));
  }

  protected boolean isField(@NotNull VariableSymbol symbol) {
    Preconditions.checkNotNull(symbol);
    return symbol.isPresentAstNode()
      && MontiArcMill.typeDispatcher().isArcBasisASTArcField(symbol.getAstNode());
  }
}
