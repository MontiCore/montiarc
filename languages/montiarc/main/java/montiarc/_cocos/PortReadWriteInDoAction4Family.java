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
import de.monticore.scdoactions._ast.ASTSCDoAction;
import de.se_rwth.commons.logging.Log;
import montiarc.MontiArcMill;
import montiarc._cocos.util.ASTSCDoActionCollector;
import montiarc._cocos.util.ASTVariableDeclaratorCollector;
import montiarc._cocos.util.PortReadWriteHandler4ExpressionBasis4Family;
import montiarc._cocos.util.PortReadWriteInDoAction4FamilyCollector;
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static arcbasis._cocos.PortReadWriteHandler4ExpressionsBasis.ContextState;

public class PortReadWriteInDoAction4Family implements ArcBasisASTArcComponentTypeCoCo {

  public void check(@NotNull ASTArcComponentType node) {
    Preconditions.checkNotNull(node);

    ExpressionSolver expSolver = ExpressionSolverService.getExpressionSolver();
    Context ctx = ExpressionSolverService.getContext();

    List<String> allFeatures;
    List<ExpressionSet> allConstraints;

    Map<ASTArcPort, BoolExpr> portConditions;
    Map<ASTSCDoAction, BoolExpr> doConditions;

    // Reading and processing parts of the Main-Component
    ArrayList<String> mainFeatures = (ArrayList<String>) node.getBody().getArcElementList().stream().filter(e -> e instanceof ASTArcFeatureDeclaration).map(v -> ((ASTArcFeatureDeclaration) v)).map(ASTArcFeatureDeclaration::getArcFeatureList).flatMap(List::stream).map(e -> node.getSymbol().getFullName() + "." + e.getSymbol().getName()).collect(Collectors.toList());

    allFeatures = new ArrayList<>(mainFeatures);
    allConstraints = new ArrayList<>();
    portConditions = new LinkedHashMap<>();
    doConditions = new LinkedHashMap<>();

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
        var statecharts = variationPoint.getArcElements().stream().filter(e -> e instanceof ASTArcStatechart).map(k -> (ASTArcStatechart) k).collect(Collectors.toList());

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

      ArrayList<ASTArcPort> mainPorts = (ArrayList<ASTArcPort>) node.getPorts();
      for (ASTArcPort port : mainPorts) {
        portConditions.put(port, ctx.mkTrue());
      }
    }

    // Adding Constraints
    BoolExpr featureConstraints = ctx.mkTrue();
    if (node instanceof ASTVariableArcFullVariantComponentType)
      featureConstraints = VariationConditionHelper.getFeatureConstraints(node, allConstraints, allFeatures, expSolver);

    for (Map.Entry<ASTSCDoAction, BoolExpr> doEntry : doConditions.entrySet()) {

      // Step 1: Check if action can be active
      List<BoolExpr> doEntryExpressionList = new ArrayList<>(List.of(featureConstraints, doEntry.getValue()));
      if (ExpressionSolverService.solve(doEntryExpressionList) == Status.UNSATISFIABLE)
        continue;

      doEntryExpressionList.clear();

      // Step 2: Get all ports form the action
      PortReadWriteInDoAction4FamilyCollector portCollector = new PortReadWriteInDoAction4FamilyCollector();
      portCollector.check(doEntry.getKey());
      List<PortReadWriteHandler4ExpressionBasis4Family.PortWithState> portContext = portCollector.getPortWithContext();

      MontiArcTraverser variableTraverser = MontiArcMill.traverser();
      ASTVariableDeclaratorCollector variableCollector = new ASTVariableDeclaratorCollector();
      variableTraverser.add4MCVarDeclarationStatements(variableCollector);
      doEntry.getKey().accept(variableTraverser);

      // Step 3: Check if there is a potential violation
      if (!portContext.isEmpty()) {
        for (PortReadWriteHandler4ExpressionBasis4Family.PortWithState portEntry : portContext) {
          var portName = portEntry.getPortSymbol().getName();
          // Check if there are fields shadowing ports
          var shadowingFields = variableCollector.getDeclarators().stream().filter(e -> e.getDeclarator().getName().equals(portName)).collect(Collectors.toList());
          var possiblePorts = portConditions.entrySet().stream().filter(e -> e.getKey().getName().equals(portName)).collect(Collectors.toList());
          var possibleIncomingPorts = possiblePorts.stream().filter(e -> e.getKey().isPresentSymbol() && e.getKey().getSymbol().isIncoming()).collect(Collectors.toList());
          var possibleOutgoingPorts = possiblePorts.stream().filter(e -> e.getKey().isPresentSymbol() && e.getKey().getSymbol().isOutgoing()).collect(Collectors.toList());

          for (Map.Entry<ASTArcPort, BoolExpr> incoming : possibleIncomingPorts) {

            doEntryExpressionList.addAll(List.of(featureConstraints, incoming.getValue()));
            if (ExpressionSolverService.solve(doEntryExpressionList) == Status.SATISFIABLE) {
              if (shadowingFields.isEmpty()) {
                if (portContext.stream().noneMatch(e -> e.getPortSymbol().getName().equals(incoming.getKey().getSymbol().getName())))
                  continue;
                var portWithContext = portContext.stream().filter(Objects::nonNull).filter(e -> e.getPortSymbol().getName().equals(incoming.getKey().getSymbol().getName())).findFirst();

                ContextState context = portWithContext.stream().findFirst().isPresent() ? portWithContext.get().getContextState() : null;
                if (context == null)
                  continue;

                if (context.isInWriteContext()) {
                  Log.error(ArcError.WRITE_TO_INCOMING_PORT.format(incoming.getKey().getName()),
                    doEntry.getKey().get_SourcePositionStart(),
                    doEntry.getKey().get_SourcePositionEnd()
                  );
                }
              }
            }
            doEntryExpressionList.clear();
          }

          for (Map.Entry<ASTArcPort, BoolExpr> outgoing : possibleOutgoingPorts) {

            doEntryExpressionList.addAll(List.of(featureConstraints, outgoing.getValue()));
            if (ExpressionSolverService.solve(doEntryExpressionList) == Status.SATISFIABLE) {
              if (shadowingFields.isEmpty()) {
                if (portContext.stream().noneMatch(e -> e.getPortSymbol().getName().equals(outgoing.getKey().getSymbol().getName())))
                  continue;

                var portWithContext = portContext.stream().filter(Objects::nonNull).filter(e -> e.getPortSymbol().getName().equals(outgoing.getKey().getSymbol().getName())).findFirst();

                ContextState context = portWithContext.stream().findFirst().isPresent() ? portWithContext.get().getContextState() : null;
                if (context == null)
                  continue;

                if (context.isInReadContext()) {
                  Log.error(ArcError.READ_FROM_OUTGOING_PORT.format(outgoing.getKey().getName()),
                    doEntry.getKey().get_SourcePositionStart(),
                    doEntry.getKey().get_SourcePositionEnd()
                  );
                }
              }
            }
            doEntryExpressionList.clear();
          }
        }
      }
    }
  }
}
