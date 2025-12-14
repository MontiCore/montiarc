/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcautomaton._ast.ASTArcStatechart;
import arcbasis._ast.ASTArcComponentType;
import arcbasis._ast.ASTArcField;
import arcbasis._ast.ASTArcParameter;
import arcbasis._ast.ASTArcPort;
import arcbasis._cocos.ArcBasisASTArcComponentTypeCoCo;
import arccompute._ast.ASTArcCompute;
import arccompute._ast.ASTArcInit;
import com.google.common.base.Preconditions;
import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Status;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.statements.mccommonstatements._ast.ASTIfStatement;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symbols.compsymbols._symboltable.PortSymbol;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types3.SymTypeRelations;
import de.monticore.types3.TypeCheck3;
import de.se_rwth.commons.logging.Log;
import montiarc.MontiArcMill;
import montiarc._cocos.util.ASTIfStatementCollector;
import montiarc._cocos.util.ExpressionBuildHelper;
import montiarc._visitor.MontiArcTraverser;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc._ast.ASTArcFeatureDeclaration;
import variablearc._ast.ASTVariableArcFullVariantComponentType;
import variablearc._cocos.util.ExpressionSolverService;
import variablearc._cocos.util.VariationConditionHelper;
import variablearc._symboltable.IVariableArcComponentTypeSymbol;
import variablearc._symboltable.VariableArcVariationPoint;
import variablearc.evaluation.ExpressionSet;
import variablearc.evaluation.ExpressionSolver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class IfConditionHasBooleanType4Family implements ArcBasisASTArcComponentTypeCoCo {

  public static final String ERROR_CODE = "0xA0909";

  public static final String ERROR_MSG_FORMAT = " Condition in if-statement must be a boolean expression.";

  public void check(@NotNull ASTArcComponentType node) {
    Preconditions.checkNotNull(node);
    Preconditions.checkNotNull(node.getEnclosingScope());

    ExpressionSolver expSolver = ExpressionSolverService.getExpressionSolver();
    Context ctx = ExpressionSolverService.getContext();

    List<String> allFeatures;
    List<ExpressionSet> allConstraints;
    List<VariableSymbol> createdVariableSymbols;
    List<PortSymbol> createdPortSymbols;
    List<ASTArcParameter> allParameters;

    Map<ASTIfStatement, BoolExpr> ifStatementConditions;
    Map<ASTArcField, BoolExpr> fieldConditions;
    Map<ASTArcPort, BoolExpr> portConditions;
    Map<String, BoolExpr> createdVariablesConditions;
    Map<String, List<String>> fieldNameVariations;

    // Reading and processing parts of the Main-Component
    List<String> mainFeatures = node.getBody().getArcElementList().stream().filter(e -> e instanceof ASTArcFeatureDeclaration).map(v -> ((ASTArcFeatureDeclaration) v)).map(ASTArcFeatureDeclaration::getArcFeatureList).flatMap(List::stream).map(e -> node.getSymbol().getFullName() + "." + e.getSymbol().getName()).toList();
    List<ASTArcParameter> mainParameters = node.getHead().getArcParameterList();

    allFeatures = new ArrayList<>(mainFeatures);
    allConstraints = new ArrayList<>();
    ifStatementConditions = new HashMap<>();
    createdVariableSymbols = new ArrayList<>();
    createdPortSymbols = new ArrayList<>();
    fieldConditions = new HashMap<>();
    portConditions = new HashMap<>();
    createdVariablesConditions = new HashMap<>();
    fieldNameVariations = new HashMap<>();
    allParameters = new ArrayList<>();
    if (!mainParameters.isEmpty())
      allParameters = new ArrayList<>(mainParameters);

    MontiArcTraverser nodeTraverser = MontiArcMill.traverser();
    ASTIfStatementCollector ifStatementCollector = new ASTIfStatementCollector();
    nodeTraverser.add4MCCommonStatements(ifStatementCollector);
    node.accept(nodeTraverser);

    for (ASTIfStatement forStatement : ifStatementCollector.getExpressions()) {
      ifStatementConditions.put(forStatement, ctx.mkTrue());
    }

    ExpressionSet mainConstraintSet = null;
    if (node instanceof ASTVariableArcFullVariantComponentType) {
      if (node.isPresentSymbol())
        mainConstraintSet = ((IVariableArcComponentTypeSymbol) ((ASTVariableArcFullVariantComponentType) node).getOriginal().getSymbol()).getConstraints();
      allConstraints.add(mainConstraintSet);

      portConditions = ((ASTVariableArcFullVariantComponentType) node).getPortConditions();
      fieldConditions = ((ASTVariableArcFullVariantComponentType) node).getFieldConditions();

      List<VariableArcVariationPoint> variationPoints = ((ASTVariableArcFullVariantComponentType) node).getVariationPoints();
      for (VariableArcVariationPoint variationPoint : variationPoints) {
        var varifExprSet = new ExpressionSet(new ArrayList<>(variationPoint.getAllConditions()));
        var expr = expSolver.convert(varifExprSet);

        if (expr.isEmpty())
          continue;

        BoolExpr variationExpr = ctx.mkAnd(expr.get());
        MontiArcTraverser varifTraverser = MontiArcMill.traverser();
        var statecharts = variationPoint.getArcElements().stream().filter(e -> e instanceof ASTArcStatechart).map(k -> (ASTArcStatechart) k).toList();
        var arcInits = variationPoint.getArcElements().stream().filter(e -> e instanceof ASTArcInit).map(k -> (ASTArcInit) k).toList();
        var arcComputes = variationPoint.getArcElements().stream().filter(e -> e instanceof ASTArcCompute).map(k -> (ASTArcCompute) k).toList();

        ASTIfStatementCollector varifIfStatementCollector = new ASTIfStatementCollector();
        varifTraverser.add4MCCommonStatements(varifIfStatementCollector);

        for (ASTArcStatechart statechart : statecharts) {
          statechart.accept(varifTraverser);
        }
        for (ASTArcInit arcInit : arcInits) {
          arcInit.accept(varifTraverser);
        }
        for (ASTArcCompute arcCompute : arcComputes) {
          arcCompute.accept(varifTraverser);
        }

        for (ASTIfStatement ifStatement : varifIfStatementCollector.getExpressions()) {
          ifStatementConditions.put(ifStatement, variationExpr);
        }
        varifIfStatementCollector.clearExpressions();
      }
    } else {
      if (node.isPresentSymbol())
        mainConstraintSet = ((IVariableArcComponentTypeSymbol) node.getSymbol()).getConstraints();
      allConstraints.add(mainConstraintSet);

      List<ASTArcPort> mainPorts = node.getPorts();
      for (ASTArcPort port : mainPorts) {
        portConditions.put(port, ctx.mkTrue());
      }

      List<ASTArcField> mainFields = node.getFields();
      for (ASTArcField field : mainFields) {
        fieldConditions.put(field, ctx.mkTrue());
      }
    }

    // Adding Constraints
    BoolExpr featureConstraints = ctx.mkTrue();
    if (node instanceof ASTVariableArcFullVariantComponentType)
      featureConstraints = VariationConditionHelper.getFeatureConstraints(node, allConstraints, allFeatures, expSolver);

    for (Map.Entry<ASTIfStatement, BoolExpr> ifStatementEntry : ifStatementConditions.entrySet()) {

      List<ASTExpression> possibleExpressions = new ArrayList<>();
      if(node instanceof ASTVariableArcFullVariantComponentType) {
        // Step 1: Check if Expression can be active
        List<BoolExpr> ifStatementEntryExpressionList = new ArrayList<>(List.of(featureConstraints, ifStatementEntry.getValue()));
        var expressionSatisfied = ExpressionSolverService.solve(ifStatementEntryExpressionList);
        if (expressionSatisfied == Status.UNSATISFIABLE)
          continue;

        ifStatementEntryExpressionList.clear();

        // Step 2: Check if individual variables and ports are possible and create all combinations
        var variableNames = ExpressionBuildHelper.getVariableNames(ifStatementEntry.getKey().getCondition());
        List<ASTArcField> expressionFields = new ArrayList<>();
        List<ASTArcPort> expressionPorts = new ArrayList<>();

        if (!variableNames.isEmpty()) {
          for (String variableName : variableNames) {
            var possibleFields = fieldConditions.entrySet().stream().filter(e -> e.getKey().getName().equals(variableName)).toList();
            for (Map.Entry<ASTArcField, BoolExpr> entry : possibleFields) {
              ifStatementEntryExpressionList.addAll(List.of(featureConstraints, entry.getValue()));
              if (ExpressionSolverService.solve(ifStatementEntryExpressionList) == Status.SATISFIABLE) {
                var field = entry.getKey();
                expressionFields.add(entry.getKey());
                createdVariablesConditions.put(field.getName() + "_" + (field.getSymbol().getType().print().hashCode() & 0x7fffffff), entry.getValue());
              }
              ifStatementEntryExpressionList.clear();
            }

            var possiblePorts = portConditions.entrySet().stream().filter(e -> e.getKey().getName().equals(variableName)).toList();
            for (Map.Entry<ASTArcPort, BoolExpr> entry : possiblePorts) {
              ifStatementEntryExpressionList.addAll(List.of(featureConstraints, entry.getValue()));
              if (ExpressionSolverService.solve(ifStatementEntryExpressionList) == Status.SATISFIABLE) {
                var port = entry.getKey();
                expressionPorts.add(port);
                createdVariablesConditions.put(port.getName() + "_" + (port.getSymbol().getType().print().hashCode() & 0x7fffffff), entry.getValue());
              }
              ifStatementEntryExpressionList.clear();
            }
          }

          if (expressionFields.isEmpty() && expressionPorts.isEmpty()) {
            possibleExpressions.add(ifStatementEntry.getKey().getCondition());
            break;
          } else {
            ExpressionBuildHelper.setScope(expressionFields.isEmpty() ? expressionPorts.get(0).getEnclosingScope() : expressionFields.get(0).getEnclosingScope());
            for (ASTArcField expressionField : expressionFields) {
              Optional.ofNullable(ExpressionBuildHelper.createVariableSymbol(expressionField.getSymbol(), fieldNameVariations)).ifPresent(createdVariableSymbols::add);
            }
            for (ASTArcPort expressionPort : expressionPorts) {
              Optional.ofNullable(ExpressionBuildHelper.createPortSymbol(expressionPort.getSymbol(), fieldNameVariations)).ifPresent(createdPortSymbols::add);
            }
          }

          if (!allParameters.isEmpty()) {
            for (ASTArcParameter parameter : allParameters) {
              Optional.ofNullable(ExpressionBuildHelper.createParameterSymbol(parameter, fieldNameVariations)).ifPresent(createdVariableSymbols::add);
            }
          }

          var potentialExpressions = ExpressionBuildHelper.createPossibleGuardExpressions(ifStatementEntry.getKey().getCondition(), fieldNameVariations);

          // Step 3: Check if created combinations are possible
          for (ASTExpression potentialExpression : potentialExpressions) {
            List<String> variablesInExpr = ExpressionBuildHelper.getVariableNames(potentialExpression);
            ifStatementEntryExpressionList.add(featureConstraints);
            for (String variableName : variablesInExpr) {
              BoolExpr variableCondition = createdVariablesConditions.get(variableName);
              if (variableCondition != null)
                ifStatementEntryExpressionList.add(variableCondition);
            }
            if (ExpressionSolverService.solve(ifStatementEntryExpressionList) == Status.SATISFIABLE)
              possibleExpressions.add(potentialExpression);
            ifStatementEntryExpressionList.clear();
          }
          fieldNameVariations.clear();
        } else {
          possibleExpressions.add(ifStatementEntry.getKey().getCondition());
        }
      }else{
        possibleExpressions.add(ifStatementEntry.getKey().getCondition());
      }
      for (ASTExpression possibleExpression : possibleExpressions) {
        SymTypeExpression result = TypeCheck3.typeOf(possibleExpression);
        if (!SymTypeRelations.isBoolean(result)) {
          Log.error(ERROR_CODE + ERROR_MSG_FORMAT, ifStatementEntry.getKey().get_SourcePositionStart());
        }
      }
    }
    // Remove created variable-symbols
    for (VariableSymbol variableSymbol : createdVariableSymbols) {
        ExpressionBuildHelper.getScope().remove(variableSymbol);
    }

    for (PortSymbol portSymbol : createdPortSymbols) {
        ExpressionBuildHelper.getScope().remove(portSymbol);
    }
  }
}
