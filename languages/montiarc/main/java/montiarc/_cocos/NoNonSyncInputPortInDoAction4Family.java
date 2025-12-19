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
import de.monticore.scdoactions._ast.ASTSCDoAction;
import de.se_rwth.commons.SourcePosition;
import de.se_rwth.commons.logging.Log;
import montiarc.MontiArcMill;
import montiarc._cocos.util.ASTNameCollector;
import montiarc._cocos.util.ASTSCDoActionCollector;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static de.monticore.symbols.compsymbols._symboltable.Timing.TIMED_SYNC;
import static montiarc.util.ArcError.IN_PORT_REF_IN_INVALID_CONTEXT;

public class NoNonSyncInputPortInDoAction4Family implements ArcBasisASTArcComponentTypeCoCo {
  public void check(@NotNull ASTArcComponentType node) {
    Preconditions.checkNotNull(node);

    if(DuplicateElementsService.duplicateElementPresent(node))
      return;

    ExpressionSolver expSolver = ExpressionSolverService.getExpressionSolver();
    Context ctx = ExpressionSolverService.getContext();

    List<String> allFeatures;
    List<ExpressionSet> allConstraints;

    Map<ASTArcPort, BoolExpr> portConditions;
    Map<ASTSCDoAction, BoolExpr> doConditions;

    // Reading and processing parts of the Main-Component
    List<String> mainFeatures = node.getBody().getArcElementList().stream().filter(e -> e instanceof ASTArcFeatureDeclaration).map(v -> ((ASTArcFeatureDeclaration) v)).map(ASTArcFeatureDeclaration::getArcFeatureList).flatMap(List::stream).map(e -> node.getSymbol().getFullName() + "." + e.getSymbol().getName()).toList();

    allFeatures = new ArrayList<>(mainFeatures);
    allConstraints = new ArrayList<>();
    portConditions = new HashMap<>();
    doConditions = new HashMap<>();

    MontiArcTraverser nodeTraverser = MontiArcMill.traverser();
    ASTSCDoActionCollector doCollector = new ASTSCDoActionCollector();
    nodeTraverser.add4SCDoActions(doCollector);
    node.accept(nodeTraverser);

    for (ASTSCDoAction doAction : doCollector.getExpressions()) {
      doConditions.put(doAction, ctx.mkTrue());
    }

    ExpressionSet mainConstraintSet = null;
    if (node instanceof ASTVariableArcFullVariantComponentType) {
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
        var statecharts = variationPoint.getArcElements().stream().filter(e -> e instanceof ASTArcStatechart).map(k -> (ASTArcStatechart) k).toList();

        ASTSCDoActionCollector varifDoActionCollector = new ASTSCDoActionCollector();
        varifTraverser.add4SCDoActions(varifDoActionCollector);

        for (ASTArcStatechart statechart : statecharts) {
          statechart.accept(varifTraverser);
          for (ASTSCDoAction doAction : varifDoActionCollector.getExpressions()) {
            doConditions.put(doAction, variationExpr);
          }
          varifDoActionCollector.clearExpressions();
        }
      }
    } else {
      if (node.isPresentSymbol())
        mainConstraintSet = ((IVariableArcComponentTypeSymbol) node.getSymbol()).getConstraints();
      allConstraints.add(mainConstraintSet);

      List<ASTArcPort> mainPorts = node.getPorts();
      for (ASTArcPort port : mainPorts) {
        portConditions.put(port, ctx.mkTrue());
      }
    }

    // Adding Constraints
    BoolExpr featureConstraints = ctx.mkTrue();
    if (node instanceof ASTVariableArcFullVariantComponentType)
      featureConstraints = VariationConditionHelper.getFeatureConstraints(node, allConstraints.getFirst(), allFeatures, expSolver);

    for (Map.Entry<ASTSCDoAction, BoolExpr> doEntry : doConditions.entrySet()) {

      // Step 1: Check if action can be active
      List<BoolExpr> doEntryExpressionList = new ArrayList<>(List.of(featureConstraints, doEntry.getValue()));
      if (ExpressionSolverService.solve(doEntryExpressionList) == Status.UNSATISFIABLE)
        continue;

      doEntryExpressionList.clear();

      // Step 2: Get all nameExpressions from the SCABody
      MontiArcTraverser nameTraverser = MontiArcMill.traverser();
      ASTNameCollector nameCollector = new ASTNameCollector();
      nameTraverser.add4ExpressionsBasis(nameCollector);
      doEntry.getKey().getSCABody().accept(nameTraverser);
      List<String> variableNames = new ArrayList<>();

      for (ASTNameExpression nameExpression : nameCollector.getExpressions()) {
        variableNames.add(nameExpression.getName());
      }

      MontiArcTraverser variableTraverser = MontiArcMill.traverser();
      ASTVariableDeclaratorCollector variableCollector = new ASTVariableDeclaratorCollector();
      variableTraverser.add4MCVarDeclarationStatements(variableCollector);
      doEntry.getKey().getSCABody().accept(variableTraverser);

      // Step 3: Check if there is a potential violation
      if (!variableNames.isEmpty()) {
        for (String variableName : variableNames) {
          // Check if there are fields shadowing ports
          var shadowingFields = variableCollector.getDeclarators().stream().filter(e -> e.getDeclarator().getName().equals(variableName)).toList();
          var possiblePorts = portConditions.entrySet().stream().filter(e -> e.getKey().getName().equals(variableName)).collect(Collectors.toList());
          possiblePorts = possiblePorts.stream().filter(e -> e.getKey().isPresentSymbol() && e.getKey().getSymbol().isIncoming() && !e.getKey().getSymbol().getTiming().matches(TIMED_SYNC)).toList();
          for (Map.Entry<ASTArcPort, BoolExpr> entry : possiblePorts) {
            doEntryExpressionList.clear();
            doEntryExpressionList.addAll(List.of(featureConstraints, ctx.mkAnd(doEntry.getValue(), entry.getValue())));
            if (ExpressionSolverService.solve(doEntryExpressionList) == Status.SATISFIABLE) {
              if (shadowingFields.isEmpty()) {
                SourcePosition sourcePosition = doEntry.getKey().get_SourcePositionStart();
                Log.error(IN_PORT_REF_IN_INVALID_CONTEXT.format(variableName, doEntry.getKey().getSCABody()), sourcePosition);
              }
            }
          }
        }
      }
    }
  }
}
