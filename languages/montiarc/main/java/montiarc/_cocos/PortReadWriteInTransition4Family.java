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
import de.monticore.sctransitions4code._ast.ASTTransitionBody;
import de.se_rwth.commons.logging.Log;
import montiarc.MontiArcMill;
import montiarc._cocos.util.ASTTransitionBodyCollector;
import montiarc._cocos.util.ASTVariableDeclaratorCollector;
import montiarc._cocos.util.PortReadWriteHandler4ExpressionBasis4Family;
import montiarc._cocos.util.PortReadWriteInTransition4FamilyCollector;
import montiarc._visitor.MontiArcTraverser;
import montiarc.util.ArcError;
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
import java.util.Objects;

import static arcbasis._cocos.PortReadWriteHandler4ExpressionsBasis.ContextState;

public class PortReadWriteInTransition4Family implements ArcBasisASTArcComponentTypeCoCo {

  public void check(@NotNull ASTArcComponentType node) {
    Preconditions.checkNotNull(node);

    ExpressionSolver expSolver = ExpressionSolverService.getExpressionSolver();
    Context ctx = ExpressionSolverService.getContext();

    List<String> allFeatures;
    List<ExpressionSet> allConstraints;

    Map<ASTArcPort, BoolExpr> portConditions;
    Map<ASTTransitionBody, BoolExpr> transitionConditions;

    // Reading and processing parts of the Main-Component
    List<String> mainFeatures = node.getBody().getArcElementList().stream().filter(e -> e instanceof ASTArcFeatureDeclaration).map(v -> ((ASTArcFeatureDeclaration) v)).map(ASTArcFeatureDeclaration::getArcFeatureList).flatMap(List::stream).map(e -> node.getSymbol().getFullName() + "." + e.getSymbol().getName()).toList();

    allFeatures = new ArrayList<>(mainFeatures);
    allConstraints = new ArrayList<>();
    portConditions = new HashMap<>();
    transitionConditions = new HashMap<>();

    MontiArcTraverser nodeTraverser = MontiArcMill.traverser();
    ASTTransitionBodyCollector transitionCollector = new ASTTransitionBodyCollector();
    nodeTraverser.add4SCTransitions4Code(transitionCollector);
    node.accept(nodeTraverser);

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
        var statecharts = variationPoint.getArcElements().stream().filter(e -> e instanceof ASTArcStatechart).map(k -> (ASTArcStatechart) k).toList();

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

      List<ASTArcPort> mainPorts = node.getPorts();
      for (ASTArcPort port : mainPorts) {
        portConditions.put(port, ctx.mkTrue());
      }
    }

    // Adding Constraints
    BoolExpr featureConstraints = ctx.mkTrue();
    if (node instanceof ASTVariableArcFullVariantComponentType)
      featureConstraints = VariationConditionHelper.getFeatureConstraints(node, allConstraints.getFirst(), allFeatures, expSolver);

    for (Map.Entry<ASTTransitionBody, BoolExpr> transitionEntry : transitionConditions.entrySet()) {

      // Step 1: Check if action can be active
      List<BoolExpr> transitionEntryExpressionList = new ArrayList<>(List.of(featureConstraints, transitionEntry.getValue()));
      if (ExpressionSolverService.solve(transitionEntryExpressionList) == Status.UNSATISFIABLE)
        continue;

      transitionEntryExpressionList.clear();

      // Step 2: Get all ports form the action
      PortReadWriteInTransition4FamilyCollector portCollector = new PortReadWriteInTransition4FamilyCollector();
      portCollector.check(transitionEntry.getKey());
      List<PortReadWriteHandler4ExpressionBasis4Family.PortWithState> portContext = new ArrayList<>(portCollector.getPortWithContext());

      MontiArcTraverser variableTraverser = MontiArcMill.traverser();
      ASTVariableDeclaratorCollector variableCollector = new ASTVariableDeclaratorCollector();
      variableTraverser.add4MCVarDeclarationStatements(variableCollector);
      transitionEntry.getKey().accept(variableTraverser);

      // Step 3: Check if there is a potential violation
      if (!portContext.isEmpty()) {
        for (PortReadWriteHandler4ExpressionBasis4Family.PortWithState portEntry : portContext) {
          var portName = portEntry.getPortSymbol().getName();
          // Check if there are fields shadowing ports
          var shadowingFields = variableCollector.getDeclarators().stream().filter(e -> e.getDeclarator().getName().equals(portName)).toList();
          var possiblePorts = portConditions.entrySet().stream().filter(e -> e.getKey().getName().equals(portName)).toList();
          var possibleIncomingPorts = possiblePorts.stream().filter(e -> e.getKey().isPresentSymbol() && e.getKey().getSymbol().isIncoming()).toList();
          var possibleOutgoingPorts = possiblePorts.stream().filter(e -> e.getKey().isPresentSymbol() && e.getKey().getSymbol().isOutgoing()).toList();

          for (Map.Entry<ASTArcPort, BoolExpr> incoming : possibleIncomingPorts) {
            transitionEntryExpressionList.addAll(List.of(featureConstraints, incoming.getValue()));
            if (ExpressionSolverService.solve(transitionEntryExpressionList) == Status.SATISFIABLE) {
              if (shadowingFields.isEmpty()) {
                if (portContext.stream().noneMatch(e -> e.getPortSymbol().getName().equals(incoming.getKey().getSymbol().getName())))
                  continue;

                var portWithContext = portContext.stream().filter(Objects::nonNull).filter(e -> e.getPortSymbol().getName().equals(incoming.getKey().getSymbol().getName())).findFirst();

                ContextState context = portWithContext.stream().findFirst().isPresent() ? portWithContext.get().getContextState() : null;
                if (context == null)
                  continue;

                if (context.isInWriteContext()) {
                  Log.error(ArcError.WRITE_TO_INCOMING_PORT.format(incoming.getKey().getName()),
                    transitionEntry.getKey().get_SourcePositionStart(),
                    transitionEntry.getKey().get_SourcePositionEnd()
                  );
                }
              }
            }
            transitionEntryExpressionList.clear();
          }

          for (Map.Entry<ASTArcPort, BoolExpr> outgoing : possibleOutgoingPorts) {

            transitionEntryExpressionList.addAll(List.of(featureConstraints, outgoing.getValue()));
            if (ExpressionSolverService.solve(transitionEntryExpressionList) == Status.SATISFIABLE) {
              if (shadowingFields.isEmpty()) {
                if (portContext.stream().noneMatch(e -> e.getPortSymbol().getName().equals(outgoing.getKey().getSymbol().getName())))
                  continue;

                var portWithContext = portContext.stream().filter(Objects::nonNull).filter(e -> e.getPortSymbol().getName().equals(outgoing.getKey().getSymbol().getName())).findFirst();

                ContextState context = portWithContext.stream().findFirst().isPresent() ? portWithContext.get().getContextState() : null;
                if (context == null)
                  continue;

                if (context.isInReadContext()) {
                  Log.error(ArcError.READ_FROM_OUTGOING_PORT.format(outgoing.getKey().getName()),
                    transitionEntry.getKey().get_SourcePositionStart(),
                    transitionEntry.getKey().get_SourcePositionEnd()
                  );
                }
              }
            }
            transitionEntryExpressionList.clear();
          }
        }
      }
    }
  }
}
