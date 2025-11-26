/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcbasis._ast.ASTArcComponentType;
import arcbasis._ast.ASTArcPort;
import arcbasis._cocos.ArcBasisASTArcComponentTypeCoCo;
import arccompute._ast.ASTArcCompute;
import com.google.common.base.Preconditions;
import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Status;
import de.se_rwth.commons.logging.Log;
import montiarc.MontiArcMill;
import montiarc._cocos.util.ASTArcComputeCollector;
import montiarc._cocos.util.ASTVariableDeclaratorCollector;
import montiarc._cocos.util.PortReadWriteHandler4ExpressionBasis4Family;
import montiarc._cocos.util.PortReadWriteInCompute4FamilyCollector;
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

public class PortReadWriteInCompute4Family implements ArcBasisASTArcComponentTypeCoCo {

  public void check(@NotNull ASTArcComponentType node) {
    Preconditions.checkNotNull(node);

    ExpressionSolver expSolver = ExpressionSolverService.getExpressionSolver();
    Context ctx = ExpressionSolverService.getContext();

    List<String> allFeatures;
    List<ExpressionSet> allConstraints;

    Map<ASTArcPort, BoolExpr> portConditions;
    Map<ASTArcCompute, BoolExpr> computeConditions;

    // Reading and processing parts of the Main-Component
    ArrayList<String> mainFeatures = (ArrayList<String>) node.getBody().getArcElementList().stream().filter(e -> e instanceof ASTArcFeatureDeclaration).map(v -> ((ASTArcFeatureDeclaration) v)).map(ASTArcFeatureDeclaration::getArcFeatureList).flatMap(List::stream).map(e -> node.getSymbol().getFullName() + "." + e.getSymbol().getName()).collect(Collectors.toList());

    allFeatures = new ArrayList<>(mainFeatures);
    allConstraints = new ArrayList<>();
    portConditions = new LinkedHashMap<>();
    computeConditions = new LinkedHashMap<>();

    MontiArcTraverser nodeTraverser = MontiArcMill.traverser();
    ASTArcComputeCollector computeCollector = new ASTArcComputeCollector();
    nodeTraverser.add4ArcCompute(computeCollector);
    node.accept(nodeTraverser);

    for (ASTArcCompute compute : computeCollector.getExpressions()) {
      computeConditions.put(compute, ctx.mkTrue());
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
        var arcInits = variationPoint.getArcElements().stream().filter(e -> e instanceof ASTArcCompute).map(k -> (ASTArcCompute) k).collect(Collectors.toList());

        for (ASTArcCompute arcCompute : arcInits) {
          computeConditions.put(arcCompute, variationExpr);
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

    for (Map.Entry<ASTArcCompute, BoolExpr> computeEntry : computeConditions.entrySet()) {

      // Step 1: Check if action can be active
      List<BoolExpr> computeEntryExpressionList = new ArrayList<>(List.of(featureConstraints, computeEntry.getValue()));
      if (ExpressionSolverService.solve(computeEntryExpressionList) == Status.UNSATISFIABLE)
        continue;

      computeEntryExpressionList.clear();

      // Step 2: Get all ports form the action
      PortReadWriteInCompute4FamilyCollector portCollector = new PortReadWriteInCompute4FamilyCollector();
      portCollector.check(computeEntry.getKey());
      List<PortReadWriteHandler4ExpressionBasis4Family.PortWithState> portContext = portCollector.getPortWithContext();

      MontiArcTraverser variableTraverser = MontiArcMill.traverser();
      ASTVariableDeclaratorCollector variableCollector = new ASTVariableDeclaratorCollector();
      variableTraverser.add4MCVarDeclarationStatements(variableCollector);
      computeEntry.getKey().accept(variableTraverser);

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
            computeEntryExpressionList.addAll(List.of(featureConstraints, incoming.getValue()));
            if (ExpressionSolverService.solve(computeEntryExpressionList) == Status.SATISFIABLE) {
              if (shadowingFields.isEmpty()) {
                if (portContext.stream().noneMatch(e -> e.getPortSymbol().getName().equals(incoming.getKey().getSymbol().getName())))
                  continue;

                var portWithContext = portContext.stream().filter(Objects::nonNull).filter(e -> e.getPortSymbol().getName().equals(incoming.getKey().getSymbol().getName())).findFirst();

                ContextState context = portWithContext.stream().findFirst().isPresent() ? portWithContext.get().getContextState() : null;
                if (context == null)
                  continue;

                if (context.isInWriteContext()) {
                  Log.error(ArcError.WRITE_TO_INCOMING_PORT.format(incoming.getKey().getName()),
                    computeEntry.getKey().get_SourcePositionStart(),
                    computeEntry.getKey().get_SourcePositionEnd()
                  );
                }
              }
            }
            computeEntryExpressionList.clear();
          }

          for (Map.Entry<ASTArcPort, BoolExpr> outgoing : possibleOutgoingPorts) {

            computeEntryExpressionList.addAll(List.of(featureConstraints, outgoing.getValue()));
            if (ExpressionSolverService.solve(computeEntryExpressionList) == Status.SATISFIABLE) {
              if (shadowingFields.isEmpty()) {
                if (portContext.stream().noneMatch(e -> e.getPortSymbol().getName().equals(outgoing.getKey().getSymbol().getName())))
                  continue;

                var portWithContext = portContext.stream().filter(Objects::nonNull).filter(e -> e.getPortSymbol().getName().equals(outgoing.getKey().getSymbol().getName())).findFirst();

                ContextState context = portWithContext.stream().findFirst().isPresent() ? portWithContext.get().getContextState() : null;
                if (context == null)
                  continue;

                if (context.isInReadContext()) {
                  Log.error(ArcError.READ_FROM_OUTGOING_PORT.format(outgoing.getKey().getName()),
                    computeEntry.getKey().get_SourcePositionStart(),
                    computeEntry.getKey().get_SourcePositionEnd()
                  );
                }
              }
            }
            computeEntryExpressionList.clear();
          }
        }
      }
    }
  }
}
