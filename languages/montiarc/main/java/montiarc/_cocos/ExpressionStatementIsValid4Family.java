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
import de.monticore.statements.mccommonstatements._ast.ASTExpressionStatement;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symbols.compsymbols._symboltable.PortSymbol;
import de.monticore.types3.TypeCheck3;
import montiarc.MontiArcMill;
import montiarc._cocos.util.ExpressionBuildHelper;
import montiarc._cocos.util.ExpressionStatementCollector;
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ExpressionStatementIsValid4Family implements ArcBasisASTArcComponentTypeCoCo {

  public void check(@NotNull ASTArcComponentType node) {
    Preconditions.checkNotNull(node);

    ExpressionSolver expSolver = ExpressionSolverService.getExpressionSolver();
    Context ctx = ExpressionSolverService.getContext();

    List<String> allFeatures;
    List<ExpressionSet> allConstraints;
    List<VariableSymbol> createdVariableSymbols;
    List<PortSymbol> createdPortSymbols;
    List<ASTArcParameter> allParameters;

    Map<ASTExpressionStatement, BoolExpr> expressionStatementConditions;
    Map<ASTArcField, BoolExpr> fieldConditions;
    Map<ASTArcPort, BoolExpr> portConditions;
    Map<String, BoolExpr> createdVariablesConditions;
    Map<String, List<String>> fieldNameVariations;

    // Reading and processing parts of the Main-Component
    ArrayList<String> mainFeatures = (ArrayList<String>) node.getBody().getArcElementList().stream().filter(e -> e instanceof ASTArcFeatureDeclaration).map(v -> ((ASTArcFeatureDeclaration) v)).map(ASTArcFeatureDeclaration::getArcFeatureList).flatMap(List::stream).map(e -> node.getSymbol().getFullName() + "." + e.getSymbol().getName()).collect(Collectors.toList());
    ArrayList<ASTArcParameter> mainParameters = (ArrayList<ASTArcParameter>) node.getHead().getArcParameterList();

    allFeatures = new ArrayList<>(mainFeatures);
    allConstraints = new ArrayList<>();
    expressionStatementConditions = new LinkedHashMap<>();
    createdVariableSymbols = new ArrayList<>();
    createdPortSymbols = new ArrayList<>();
    fieldConditions = new LinkedHashMap<>();
    portConditions = new LinkedHashMap<>();
    createdVariablesConditions = new LinkedHashMap<>();
    fieldNameVariations = new LinkedHashMap<>();
    allParameters = new ArrayList<>();
    if (!mainParameters.isEmpty())
      allParameters = new ArrayList<>(mainParameters);

    MontiArcTraverser nodeTraverser = MontiArcMill.traverser();
    ExpressionStatementCollector nodeExpressionCollector = new ExpressionStatementCollector();
    nodeTraverser.add4MCCommonStatements(nodeExpressionCollector);
    node.accept(nodeTraverser);

    for (ASTExpressionStatement mainExpression : nodeExpressionCollector.getExpressions()) {
      expressionStatementConditions.put(mainExpression, ctx.mkTrue());
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
        var statecharts = variationPoint.getArcElements().stream().filter(e -> e instanceof ASTArcStatechart).map(k -> (ASTArcStatechart) k).collect(Collectors.toList());
        var arcInits = variationPoint.getArcElements().stream().filter(e -> e instanceof ASTArcInit).map(k -> (ASTArcInit) k).collect(Collectors.toList());
        var arcComputes = variationPoint.getArcElements().stream().filter(e -> e instanceof ASTArcCompute).map(k -> (ASTArcCompute) k).collect(Collectors.toList());

        ExpressionStatementCollector varifExpressionStatementCollector = new ExpressionStatementCollector();
        varifTraverser.add4MCCommonStatements(varifExpressionStatementCollector);

        for (ASTArcStatechart statechart : statecharts) {
          statechart.accept(varifTraverser);
        }
        for (ASTArcInit arcInit : arcInits) {
          arcInit.accept(varifTraverser);
        }
        for (ASTArcCompute arcCompute : arcComputes) {
          arcCompute.accept(varifTraverser);
        }

        for (ASTExpressionStatement statement : varifExpressionStatementCollector.getExpressions()) {
          expressionStatementConditions.put(statement, variationExpr);
        }
        varifExpressionStatementCollector.clearExpressions();
      }
    } else {
      if (node.isPresentSymbol())
        mainConstraintSet = ((IVariableArcComponentTypeSymbol) node.getSymbol()).getConstraints();
      allConstraints.add(mainConstraintSet);

      ArrayList<ASTArcPort> mainPorts = (ArrayList<ASTArcPort>) node.getPorts();
      for (ASTArcPort port : mainPorts) {
        portConditions.put(port, ctx.mkTrue());
      }

      ArrayList<ASTArcField> mainFields = (ArrayList<ASTArcField>) node.getFields();
      for (ASTArcField field : mainFields) {
        fieldConditions.put(field, ctx.mkTrue());
      }
    }

    // Adding Constraints
    BoolExpr featureConstraints = ctx.mkTrue();
    if (node instanceof ASTVariableArcFullVariantComponentType)
      featureConstraints = VariationConditionHelper.getFeatureConstraints(node, allConstraints, allFeatures, expSolver);

    List<ASTExpression> possibleExpressions = new ArrayList<>();
    for (Map.Entry<ASTExpressionStatement, BoolExpr> expressionEntry : expressionStatementConditions.entrySet()) {

        if(node instanceof ASTVariableArcFullVariantComponentType) {
          // Step 1: Check if Expression can be active
          List<BoolExpr> expressionEntryExpressionList = new ArrayList<>(List.of(featureConstraints, expressionEntry.getValue()));
          var expressionSatisfied = ExpressionSolverService.solve(expressionEntryExpressionList);
          if (expressionSatisfied == Status.UNSATISFIABLE)
            continue;

          expressionEntryExpressionList.clear();

          // Step 2: Check if individual variables and ports are possible and create all combinations
          var variableNames = ExpressionBuildHelper.getVariableNames(expressionEntry.getKey().getExpression());
          List<ASTArcField> expressionFields = new ArrayList<>();
          List<ASTArcPort> expressionPorts = new ArrayList<>();
          if (!variableNames.isEmpty()) {
            for (String variableName : variableNames) {
              var possibleFields = fieldConditions.entrySet().stream().filter(e -> e.getKey().getName().equals(variableName)).collect(Collectors.toList());
              for (Map.Entry<ASTArcField, BoolExpr> entry : possibleFields) {

                expressionEntryExpressionList.addAll(List.of(featureConstraints, entry.getValue()));
                if (ExpressionSolverService.solve(expressionEntryExpressionList) == Status.SATISFIABLE) {
                  var field = entry.getKey();
                  expressionFields.add(entry.getKey());
                  createdVariablesConditions.put(field.getName() + "_" + (field.getSymbol().getType().print().hashCode() & 0x7fffffff), entry.getValue());
                }
                expressionEntryExpressionList.clear();
              }

              var possiblePorts = portConditions.entrySet().stream().filter(e -> e.getKey().getName().equals(variableName)).collect(Collectors.toList());
              for (Map.Entry<ASTArcPort, BoolExpr> entry : possiblePorts) {

                expressionEntryExpressionList.addAll(List.of(featureConstraints, entry.getValue()));
                if (ExpressionSolverService.solve(expressionEntryExpressionList) == Status.SATISFIABLE) {
                  var port = entry.getKey();
                  expressionPorts.add(port);
                  createdVariablesConditions.put(port.getName() + "_" + (port.getSymbol().getType().print().hashCode() & 0x7fffffff), entry.getValue());
                }
                expressionEntryExpressionList.clear();
              }
            }

            if (expressionFields.isEmpty() && expressionPorts.isEmpty()) {
              possibleExpressions.add(expressionEntry.getKey().getExpression());
              break;
            } else {
              ExpressionBuildHelper.setScope(expressionFields.isEmpty() ? expressionPorts.get(0).getEnclosingScope() : expressionFields.get(0).getEnclosingScope());
              for (ASTArcField expressionField : expressionFields) {
                createdVariableSymbols.add(ExpressionBuildHelper.createVariableSymbol(expressionField.getSymbol(), fieldNameVariations));
              }
              for (ASTArcPort expressionPort : expressionPorts) {
                createdPortSymbols.add(ExpressionBuildHelper.createPortSymbol(expressionPort.getSymbol(), fieldNameVariations));
              }
            }

            if (!allParameters.isEmpty()) {
              for (ASTArcParameter parameter : allParameters) {
                createdVariableSymbols.add(ExpressionBuildHelper.createParameterSymbol(parameter, fieldNameVariations));
              }
            }

            var potentialExpressions = ExpressionBuildHelper.createPossibleGuardExpressions(expressionEntry.getKey().getExpression(), fieldNameVariations);

            // Step 3: Check if created combinations are possible
            for (ASTExpression potentialExpression : potentialExpressions) {
              List<String> variablesInExpr = ExpressionBuildHelper.getVariableNames(potentialExpression);

              expressionEntryExpressionList.add(featureConstraints);
              for (String variableName : variablesInExpr) {
                BoolExpr variableCondition = createdVariablesConditions.get(variableName);
                if (variableCondition != null)
                  expressionEntryExpressionList.add(variableCondition);
              }
              if (ExpressionSolverService.solve(expressionEntryExpressionList) == Status.SATISFIABLE)
                possibleExpressions.add(potentialExpression);
              expressionEntryExpressionList.clear();
            }
            fieldNameVariations.clear();
          } else {
            possibleExpressions.add(expressionEntry.getKey().getExpression());
          }
        }else{
          possibleExpressions.add(expressionEntry.getKey().getExpression());
        }
        // Step 4: TypeCheck possible Expressions
        for (ASTExpression expression : possibleExpressions) {
          var typeCheckResult = TypeCheck3.typeOf(expression);
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
}
