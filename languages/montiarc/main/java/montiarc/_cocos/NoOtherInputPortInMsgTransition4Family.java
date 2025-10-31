/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcautomaton.ArcAutomatonMill;
import arcautomaton._ast.ASTArcStatechart;
import arcautomaton._visitor.ArcAutomatonTraverser;
import arcbasis._ast.ASTArcComponentType;
import arcbasis._ast.ASTArcPort;
import arcbasis._cocos.ArcBasisASTArcComponentTypeCoCo;
import com.google.common.base.Preconditions;
import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Status;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.sctransitions4code._ast.ASTTransitionBody;
import de.se_rwth.commons.SourcePosition;
import de.se_rwth.commons.logging.Log;
import montiarc.MontiArcMill;
import montiarc._cocos.util.ASTNameCollector;
import montiarc._cocos.util.ASTTransitionBodyCollector;
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

import static arcautomaton.ArcAutomatonMill.typeDispatcher;
import static montiarc.util.ArcError.IN_PORT_REF_IN_INVALID_CONTEXT;

public class NoOtherInputPortInMsgTransition4Family implements ArcBasisASTArcComponentTypeCoCo {

  public void check(@NotNull ASTArcComponentType node) {
    Preconditions.checkNotNull(node);

    if(DuplicateElementsService.duplicateElementPresent(node))
      return;

    ExpressionSolver expSolver = ExpressionSolverService.getExpressionSolver();
    Context ctx = ExpressionSolverService.getContext();

    List<String> allFeatures;
    List<ExpressionSet> allConstraints;

    Map<ASTArcPort, BoolExpr> portConditions;
    Map<ASTTransitionBody, BoolExpr> transitionConditions;

    // Reading and processing parts of the Main-Component
    ArrayList<String> mainFeatures = (ArrayList<String>) node.getBody().getArcElementList().stream().filter(e -> e instanceof ASTArcFeatureDeclaration).map(v -> ((ASTArcFeatureDeclaration) v)).map(ASTArcFeatureDeclaration::getArcFeatureList).flatMap(List::stream).map(e -> node.getSymbol().getFullName() + "." + e.getSymbol().getName()).collect(Collectors.toList());

    allFeatures = new ArrayList<>(mainFeatures);
    allConstraints = new ArrayList<>();
    portConditions = new HashMap<>();
    transitionConditions = new HashMap<>();

    ArcAutomatonTraverser automatonTraverser = ArcAutomatonMill.traverser();
    ASTTransitionBodyCollector transitionCollector = new ASTTransitionBodyCollector();
    automatonTraverser.add4SCTransitions4Code(transitionCollector);
    node.accept(automatonTraverser);

    for (ASTTransitionBody transitionBody : transitionCollector.getExpressions()) {
      transitionConditions.put(transitionBody, ctx.mkTrue());
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
        var statecharts = variationPoint.getArcElements().stream().filter(e -> e instanceof ASTArcStatechart).map(k -> (ASTArcStatechart) k).collect(Collectors.toList());

        ASTTransitionBodyCollector varifTransitionCollector = new ASTTransitionBodyCollector();
        varifTraverser.add4SCTransitions4Code(varifTransitionCollector);

        for (ASTArcStatechart statechart : statecharts) {
          statechart.accept(varifTraverser);
          for (ASTTransitionBody transition : varifTransitionCollector.getExpressions()) {
            transitionConditions.put(transition, variationExpr);
          }
          varifTransitionCollector.clearExpressions();
        }
      }
    } else {
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

    for (Map.Entry<ASTTransitionBody, BoolExpr> transitionEntry : transitionConditions.entrySet()) {

      // Step 1: Check if transition can be active
      List<BoolExpr> transitionEntryExpressionList = new ArrayList<>(List.of(featureConstraints, transitionEntry.getValue()));
      if (ExpressionSolverService.solve(transitionEntryExpressionList) == Status.UNSATISFIABLE)
        continue;

      transitionEntryExpressionList.clear();

      if (!(transitionEntry.getKey().isPresentSCEvent()
        && typeDispatcher().isArcAutomatonASTMsgEvent(transitionEntry.getKey().getSCEvent()))) {
        continue;
      }

      // Step 2: Get all nameExpressions from the transition
      MontiArcTraverser nameTraverser = MontiArcMill.traverser();
      ASTNameCollector nameCollector = new ASTNameCollector();
      nameTraverser.add4ExpressionsBasis(nameCollector);
      transitionEntry.getKey().accept(nameTraverser);
      List<String> variableNames = new ArrayList<>();

      for (ASTNameExpression nameExpression : nameCollector.getExpressions()) {
        variableNames.add(nameExpression.getName());
      }

      MontiArcTraverser variableTraverser = MontiArcMill.traverser();
      ASTVariableDeclaratorCollector variableCollector = new ASTVariableDeclaratorCollector();
      variableTraverser.add4MCVarDeclarationStatements(variableCollector);
      transitionEntry.getKey().accept(variableTraverser);

      // event of the transition
      String event = "";
      if (transitionEntry.getKey().isPresentSCEvent())
        event = typeDispatcher().asArcAutomatonASTMsgEvent(transitionEntry.getKey().getSCEvent()).getName();

      // Step 3: Check if there is a potential violation
      if (!variableNames.isEmpty()) {
        for (String variableName : variableNames) {
          // Check if there are fields shadowing ports
          var shadowingFields = variableCollector.getDeclarators().stream().filter(e -> e.getDeclarator().getName().equals(variableName)).collect(Collectors.toList());
          var possiblePorts = portConditions.entrySet().stream().filter(e -> e.getKey().getName().equals(variableName)).collect(Collectors.toList());
          possiblePorts = possiblePorts.stream().filter(e -> e.getKey().isPresentSymbol() && e.getKey().getSymbol().isIncoming()).collect(Collectors.toList());
          for (Map.Entry<ASTArcPort, BoolExpr> entry : possiblePorts) {
            // Check if the port-name is the same as the event
            if (entry.getKey().getName().equals(event)) {
              continue;
            }
            transitionEntryExpressionList.clear();
            transitionEntryExpressionList.addAll(List.of(featureConstraints, transitionEntry.getValue(), entry.getValue()));
            if (ExpressionSolverService.solve(transitionEntryExpressionList) == Status.SATISFIABLE) {
              if (shadowingFields.isEmpty()) {
                SourcePosition sourcePosition = transitionEntry.getKey().get_SourcePositionStart();
                Log.error(IN_PORT_REF_IN_INVALID_CONTEXT.format(variableName, transitionEntry.getKey()), sourcePosition);
              }
            }
          }
        }
      }
    }
  }
}
