/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcautomaton._ast.ASTArcStatechart;
import arcbasis._ast.ASTArcComponentType;
import arcbasis._ast.ASTArcPort;
import arcbasis._cocos.ArcBasisASTArcComponentTypeCoCo;
import com.google.common.base.Preconditions;
import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Status;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.scactions._ast.ASTSCExitAction;
import de.se_rwth.commons.SourcePosition;
import de.se_rwth.commons.logging.Log;
import montiarc.MontiArcMill;
import montiarc._cocos.util.ASTNameCollector;
import montiarc._cocos.util.ASTSCExitActionCollector;
import montiarc._cocos.util.ASTVariableDeclaratorCollector;
import montiarc._visitor.MontiArcTraverser;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc._ast.ASTArcFeatureDeclaration;
import variablearc._ast.ASTVariableArcFullVariantComponentType;
import variablearc._cocos.util.DuplicateElementsService;
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

import static montiarc.util.ArcError.IN_PORT_REF_IN_INVALID_CONTEXT;

public class NoInputPortInExitAction4Family implements ArcBasisASTArcComponentTypeCoCo {

  public void check(@NotNull ASTArcComponentType node) {
    Preconditions.checkNotNull(node);

    if(DuplicateElementsService.duplicateElementPresent(node))
      return;

    ExpressionSolver expSolver = ExpressionSolverService.getExpressionSolver();
    Context ctx = ExpressionSolverService.getContext();

    List<String> allFeatures;
    List<ExpressionSet> allConstraints;

    Map<ASTArcPort, BoolExpr> portConditions;
    Map<ASTSCExitAction, BoolExpr> actionConditions;

    // Reading and processing parts of the Main-Component
    ArrayList<String> mainFeatures = (ArrayList<String>) node.getBody().getArcElementList().stream().filter(e -> e instanceof ASTArcFeatureDeclaration).map(v -> ((ASTArcFeatureDeclaration) v)).map(ASTArcFeatureDeclaration::getArcFeatureList).flatMap(List::stream).map(e -> node.getSymbol().getFullName() + "." + e.getSymbol().getName()).collect(Collectors.toList());

    allFeatures = new ArrayList<>(mainFeatures);
    allConstraints = new ArrayList<>();
    portConditions = new LinkedHashMap<>();
    actionConditions = new LinkedHashMap<>();

    MontiArcTraverser nodeTraverser = MontiArcMill.traverser();
    ASTSCExitActionCollector exitActionCollector = new ASTSCExitActionCollector();
    nodeTraverser.add4SCActions(exitActionCollector);
    node.accept(nodeTraverser);

    for (ASTSCExitAction action : exitActionCollector.getExpressions()) {
      actionConditions.put(action, ctx.mkTrue());
    }

    if (node instanceof ASTVariableArcFullVariantComponentType) {
      ExpressionSet mainConstraintSet = null;
      if (node.isPresentSymbol())
        mainConstraintSet = ((IVariableArcComponentTypeSymbol) ((ASTVariableArcFullVariantComponentType) node).getOriginal().getSymbol()).getConstraints();
      allConstraints.add(mainConstraintSet);

      portConditions = ((ASTVariableArcFullVariantComponentType) node).getPortConditions();

      List<VariableArcVariationPoint> variationPoints = ((ASTVariableArcFullVariantComponentType) node).getVariationPoints();
      for (VariableArcVariationPoint variationPoint : variationPoints) {
        var varifExprSet = new ExpressionSet(new ArrayList<>(variationPoint.getAllConditions()));
        var expr = expSolver.convert(varifExprSet);

        if (expr.isEmpty())
          continue;

        BoolExpr variationExpr = ctx.mkAnd(expr.get());
        MontiArcTraverser varifTraverser = MontiArcMill.traverser();
        var statecharts = variationPoint.getArcElements().stream().filter(e -> e instanceof ASTArcStatechart).map(k -> (ASTArcStatechart) k).collect(Collectors.toList());

        ASTSCExitActionCollector varifActionCollector = new ASTSCExitActionCollector();
        varifTraverser.add4SCActions(varifActionCollector);

        for (ASTArcStatechart statechart : statecharts) {
          statechart.accept(varifTraverser);
          for (ASTSCExitAction action : varifActionCollector.getExpressions()) {
            actionConditions.put(action, variationExpr);
          }
          varifActionCollector.clearExpressions();
        }
      }
    } else {
      ExpressionSet mainConstraintSet = null;
      if (node.isPresentSymbol())
        mainConstraintSet = ((IVariableArcComponentTypeSymbol) node.getSymbol()).getConstraints();
      allConstraints.add(mainConstraintSet);

      ArrayList<ASTArcPort> mainPorts = (ArrayList<ASTArcPort>) node.getPorts();
      for (ASTArcPort port : mainPorts) {
        portConditions.put(port, ctx.mkTrue());
      }
    }

    // Adding Constraints
    BoolExpr featureConstraints = ctx.mkTrue();
    if (node instanceof ASTVariableArcFullVariantComponentType)
      featureConstraints = VariationConditionHelper.getFeatureConstraints(node, allConstraints, allFeatures, expSolver);

    for (Map.Entry<ASTSCExitAction, BoolExpr> actionEntry : actionConditions.entrySet()) {

      // Step 1: Check if action can be active
      List<BoolExpr> actionEntryExpressionList = new ArrayList<>(List.of(featureConstraints, actionEntry.getValue()));
      if (ExpressionSolverService.solve(actionEntryExpressionList) == Status.UNSATISFIABLE)
        continue;

      actionEntryExpressionList.clear();

      // Step 2: Get all nameExpressions from the SCABody
      MontiArcTraverser nameTraverser = MontiArcMill.traverser();
      ASTNameCollector nameCollector = new ASTNameCollector();
      nameTraverser.add4ExpressionsBasis(nameCollector);
      actionEntry.getKey().getSCABody().accept(nameTraverser);
      List<String> variableNames = new ArrayList<>();

      for (ASTNameExpression nameExpression : nameCollector.getExpressions()) {
        variableNames.add(nameExpression.getName());
      }

      MontiArcTraverser variableTraverser = MontiArcMill.traverser();
      ASTVariableDeclaratorCollector variableCollector = new ASTVariableDeclaratorCollector();
      variableTraverser.add4MCVarDeclarationStatements(variableCollector);
      actionEntry.getKey().getSCABody().accept(variableTraverser);

      // Step 3: Check if there is a potential violation
      if (!variableNames.isEmpty()) {
        for (String variableName : variableNames) {
          // Check if there are fields shadowing ports
          var shadowingFields = variableCollector.getDeclarators().stream().filter(e -> e.getDeclarator().getName().equals(variableName)).collect(Collectors.toList());
          var possiblePorts = portConditions.entrySet().stream().filter(e -> e.getKey().getName().equals(variableName)).collect(Collectors.toList());
          possiblePorts = possiblePorts.stream().filter(e -> e.getKey().isPresentSymbol() && e.getKey().getSymbol().isIncoming()).collect(Collectors.toList());
          for (Map.Entry<ASTArcPort, BoolExpr> entry : possiblePorts) {
            actionEntryExpressionList.clear();
            actionEntryExpressionList.addAll(List.of(featureConstraints, actionEntry.getValue(), entry.getValue()));
            if (ExpressionSolverService.solve(actionEntryExpressionList) == Status.SATISFIABLE) {
              if (shadowingFields.isEmpty()) {
                SourcePosition sourcePosition = actionEntry.getKey().get_SourcePositionStart();
                Log.error(IN_PORT_REF_IN_INVALID_CONTEXT.format(variableName, actionEntry.getKey().getSCABody()), sourcePosition);
              }
            }
          }
        }
      }
    }
  }
}
