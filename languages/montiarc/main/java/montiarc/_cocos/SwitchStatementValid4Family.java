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
import de.monticore.statements.mccommonstatements._ast.ASTSwitchStatement;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symbols.compsymbols._symboltable.PortSymbol;
import de.monticore.symbols.oosymbols.OOSymbolsMill;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types3.SymTypeRelations;
import de.monticore.types3.TypeCheck3;
import de.se_rwth.commons.logging.Log;
import montiarc.MontiArcMill;
import montiarc._cocos.util.ASTSwitchStatementCollector;
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
import java.util.stream.Collectors;

public class SwitchStatementValid4Family implements ArcBasisASTArcComponentTypeCoCo {

  public static final String ERROR_CODE = "0xA0917";

  public static final String ERROR_MSG_FORMAT =
    "Switch expression in the switch-statement must be " +
      "char, byte, short, int, Character, Byte, Short, " +
      "Integer, or an enum type.";

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

    Map<ASTSwitchStatement, BoolExpr> switchStatementConditions;
    Map<ASTArcField, BoolExpr> fieldConditions;
    Map<ASTArcPort, BoolExpr> portConditions;
    Map<String, BoolExpr> createdVariablesConditions;
    Map<String, List<String>> fieldNameVariations;

    // Reading and processing parts of the Main-Component
    ArrayList<String> mainFeatures = (ArrayList<String>) node.getBody().getArcElementList().stream().filter(e -> e instanceof ASTArcFeatureDeclaration).map(v -> ((ASTArcFeatureDeclaration) v)).map(ASTArcFeatureDeclaration::getArcFeatureList).flatMap(List::stream).map(e -> node.getSymbol().getFullName() + "." + e.getSymbol().getName()).collect(Collectors.toList());
    ArrayList<ASTArcParameter> mainParameters = (ArrayList<ASTArcParameter>) node.getHead().getArcParameterList();

    allFeatures = new ArrayList<>(mainFeatures);
    allConstraints = new ArrayList<>();
    switchStatementConditions = new HashMap<>();
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
    ASTSwitchStatementCollector switchStatementCollector = new ASTSwitchStatementCollector();
    nodeTraverser.add4MCCommonStatements(switchStatementCollector);
    node.accept(nodeTraverser);

    for (ASTSwitchStatement switchStatement : switchStatementCollector.getExpressions()) {
      switchStatementConditions.put(switchStatement, ctx.mkTrue());
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

        ASTSwitchStatementCollector varifSwitchStatementCollector = new ASTSwitchStatementCollector();
        varifTraverser.add4MCCommonStatements(varifSwitchStatementCollector);

        for (ASTArcStatechart statechart : statecharts) {
          statechart.accept(varifTraverser);
        }
        for (ASTArcInit arcInit : arcInits) {
          arcInit.accept(varifTraverser);
        }
        for (ASTArcCompute arcCompute : arcComputes) {
          arcCompute.accept(varifTraverser);
        }

        for (ASTSwitchStatement switchStatement : varifSwitchStatementCollector.getExpressions()) {
          switchStatementConditions.put(switchStatement, variationExpr);
        }
        varifSwitchStatementCollector.clearExpressions();
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
    if(node instanceof ASTVariableArcFullVariantComponentType)
        featureConstraints = VariationConditionHelper.getFeatureConstraints(node, allConstraints, allFeatures, expSolver);

    for (Map.Entry<ASTSwitchStatement, BoolExpr> switchStatementEntry : switchStatementConditions.entrySet()) {

      List<ASTExpression> possibleExpressions = new ArrayList<>();
      if(node instanceof ASTVariableArcFullVariantComponentType) {
        // Step 1: Check if Expression can be active
        List<BoolExpr> switchStatementEntryExpressionList = new ArrayList<>(List.of(featureConstraints, switchStatementEntry.getValue()));
        var expressionSatisfied = ExpressionSolverService.solve(switchStatementEntryExpressionList);
        if (expressionSatisfied == Status.UNSATISFIABLE)
          continue;

        switchStatementEntryExpressionList.clear();

        // Step 2: Check if individual variables and ports are possible and create all combinations
        var variableNames = ExpressionBuildHelper.getVariableNames(switchStatementEntry.getKey().getExpression());
        List<ASTArcField> expressionFields = new ArrayList<>();
        List<ASTArcPort> expressionPorts = new ArrayList<>();

        if (!variableNames.isEmpty()) {
          for (String variableName : variableNames) {
            var possibleFields = fieldConditions.entrySet().stream().filter(e -> e.getKey().getName().equals(variableName)).collect(Collectors.toList());
            for (Map.Entry<ASTArcField, BoolExpr> entry : possibleFields) {
              switchStatementEntryExpressionList.addAll(List.of(featureConstraints, entry.getValue()));
              if (ExpressionSolverService.solve(switchStatementEntryExpressionList) == Status.SATISFIABLE) {
                var field = entry.getKey();
                expressionFields.add(entry.getKey());
                createdVariablesConditions.put(field.getName() + "_" + (field.getSymbol().getType().print().hashCode() & 0x7fffffff), entry.getValue());
              }
              switchStatementEntryExpressionList.clear();
            }

            var possiblePorts = portConditions.entrySet().stream().filter(e -> e.getKey().getName().equals(variableName)).collect(Collectors.toList());
            for (Map.Entry<ASTArcPort, BoolExpr> entry : possiblePorts) {
              switchStatementEntryExpressionList.addAll(List.of(featureConstraints, entry.getValue()));
              if (ExpressionSolverService.solve(switchStatementEntryExpressionList) == Status.SATISFIABLE) {
                var port = entry.getKey();
                expressionPorts.add(port);
                createdVariablesConditions.put(port.getName() + "_" + (port.getSymbol().getType().print().hashCode() & 0x7fffffff), entry.getValue());
              }
              switchStatementEntryExpressionList.clear();
            }
          }

          if (expressionFields.isEmpty() && expressionPorts.isEmpty()) {
            possibleExpressions.add(switchStatementEntry.getKey().getExpression());
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

          var potentialExpressions = ExpressionBuildHelper.createPossibleGuardExpressions(switchStatementEntry.getKey().getExpression(), fieldNameVariations);

          // Step 3: Check if created combinations are possible
          for (ASTExpression potentialExpression : potentialExpressions) {
            List<String> variablesInExpr = ExpressionBuildHelper.getVariableNames(potentialExpression);
            switchStatementEntryExpressionList.add(featureConstraints);
            for (String variableName : variablesInExpr) {
              BoolExpr variableCondition = createdVariablesConditions.get(variableName);
              if (variableCondition != null)
                switchStatementEntryExpressionList.add(variableCondition);
            }
            if (ExpressionSolverService.solve(switchStatementEntryExpressionList) == Status.SATISFIABLE)
              possibleExpressions.add(potentialExpression);
            switchStatementEntryExpressionList.clear();
          }
          fieldNameVariations.clear();
        } else {
          possibleExpressions.add(switchStatementEntry.getKey().getExpression());
        }
      }else{
        possibleExpressions.add(switchStatementEntry.getKey().getExpression());
      }
      for (ASTExpression possibleExpression : possibleExpressions) {
        SymTypeExpression result = TypeCheck3.typeOf(possibleExpression);
        if (!(SymTypeRelations.isChar(result) || SymTypeRelations.isByte(result)
          || SymTypeRelations.isShort(result) || SymTypeRelations.isInt(result)
          || isEnumMember(result))) {
          Log.error(ERROR_CODE + ERROR_MSG_FORMAT, switchStatementEntry.getKey().get_SourcePositionStart());
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

  public boolean isEnumMember(SymTypeExpression ste) {
    if (ste.hasTypeInfo()) {
      if (OOSymbolsMill.typeDispatcher().isOOSymbolsOOType(ste.getTypeInfo())) {
        return OOSymbolsMill.typeDispatcher().asOOSymbolsOOType(ste.getTypeInfo()).isIsEnum();
      }
    }
    return false;
  }
}
