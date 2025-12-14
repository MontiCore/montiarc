/* (c) https://github.com/MontiCore/monticore */
package variablearc._cocos;

import arcbasis._ast.ASTArcComponentType;
import arcbasis._ast.ASTComponentInstance;
import arcbasis._ast.ASTComponentInstantiation;
import arcbasis._ast.ASTComponentInstantiationTOP;
import arcbasis._ast.ASTConnector;
import arcbasis._ast.ASTPortAccess;
import arcbasis._cocos.ArcBasisASTArcComponentTypeCoCo;
import com.google.common.base.Preconditions;
import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.IntExpr;
import com.microsoft.z3.Model;
import com.microsoft.z3.Status;
import de.monticore.symbols.compsymbols._symboltable.SubcomponentSymbol;
import de.se_rwth.commons.logging.Log;
import montiarc.util.ArcError;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc._ast.ASTArcFeatureDeclaration;
import variablearc._ast.ASTVariableArcFullVariantComponentType;
import variablearc._cocos.util.ExpressionSolverService;
import variablearc._cocos.util.VariationConditionHelper;
import variablearc._symboltable.IVariableArcComponentTypeSymbol;
import variablearc.evaluation.ExpressionSet;
import variablearc.evaluation.ExpressionSolver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Collectors;

public class FeedbackStrongCausality4Family implements ArcBasisASTArcComponentTypeCoCo {

  protected static class ConnectorInfo {

    ASTConnector connector;
    BoolExpr condition;
    Boolean stronglyCausal;

    ConnectorInfo(ASTConnector connector, BoolExpr condition, Boolean stronglyCausal) {
      this.connector = connector;
      this.condition = condition;
      this.stronglyCausal = stronglyCausal;
    }
  }

  @Override
  public void check(@NotNull ASTArcComponentType node) {
    Preconditions.checkNotNull(node);
    Preconditions.checkArgument(node.isPresentSymbol());

    ExpressionSolver expSolver = ExpressionSolverService.getExpressionSolver();
    Context ctx = ExpressionSolverService.getContext();

    List<ConnectorInfo> connectorInfos = new ArrayList<>();
    Map<ASTConnector, BoolExpr> connectorConditions;
    Map<ASTComponentInstance, BoolExpr> subcomponentConditions = new HashMap<>();
    Map<SubcomponentSymbol, Integer> subcomponentToInt = new HashMap<>();

    // Reading and processing parts of the Main-Component
    List<String> mainFeatures = node.getBody().getArcElementList().stream().filter(e -> e instanceof ASTArcFeatureDeclaration).map(v -> ((ASTArcFeatureDeclaration) v)).map(ASTArcFeatureDeclaration::getArcFeatureList).flatMap(List::stream).map(e -> node.getSymbol().getFullName() + "." + e.getSymbol().getName()).toList();

    // Managing all features, variations, constraints and connectors
    List<String> allFeatures;
    List<ExpressionSet> allConstraints;
    List<SubcomponentSymbol> allSubComponents;

    // Getting all features, ports and variations from the Main-Component
    allFeatures = new ArrayList<>(mainFeatures);
    allConstraints = new ArrayList<>();
    allSubComponents = new ArrayList<>();

    if (node instanceof ASTVariableArcFullVariantComponentType) {
      ExpressionSet mainConstraintSet = ((IVariableArcComponentTypeSymbol) (((ASTVariableArcFullVariantComponentType) node).getOriginal()).getSymbol()).getConstraints();
      allConstraints.add(mainConstraintSet);
      connectorConditions = ((ASTVariableArcFullVariantComponentType) node).getConnectorConditions();
      subcomponentConditions = ((ASTVariableArcFullVariantComponentType) node).getSubcomponentConditions();
      allSubComponents.addAll(subcomponentConditions.keySet().stream().map(ASTComponentInstance::getSymbol).toList());
      for (Map.Entry<ASTConnector, BoolExpr> connectorEntry : connectorConditions.entrySet()) {
        Boolean sourceStronglyCausal = connectorEntry.getKey().getSource().isPresentPortSymbol() ? connectorEntry.getKey().getSource().getPortSymbol().getStronglyCausal() : false;
        var newConnector = new ConnectorInfo(connectorEntry.getKey(), connectorEntry.getValue(), sourceStronglyCausal);
        connectorInfos.add(newConnector);
      }

    } else {
      ExpressionSet mainConstraintSet = ((IVariableArcComponentTypeSymbol) node.getSymbol()).getConstraints();
      allConstraints.add(mainConstraintSet);
      List<ASTConnector> mainConnectors = node.getConnectors();

      List<ASTComponentInstance> mainSubComps = node.getBody().getArcElementList().stream().filter(e -> e instanceof ASTComponentInstantiation).map(l -> (ASTComponentInstantiation) l).map(ASTComponentInstantiationTOP::getComponentInstanceList).flatMap(List::stream).toList();
      for (ASTComponentInstance mainSubComp : mainSubComps) {
        subcomponentConditions.put(mainSubComp, ctx.mkTrue());
      }
      allSubComponents.addAll(mainSubComps.stream().map(ASTComponentInstance::getSymbol).toList());
      for (ASTConnector connector : mainConnectors) {
        Boolean sourceStronglyCausal = connector.getSource().isPresentPortSymbol() ? connector.getSource().getPortSymbol().getStronglyCausal() : false;
        var newConnector = new ConnectorInfo(connector, ctx.mkTrue(), sourceStronglyCausal);
        connectorInfos.add(newConnector);
      }
    }

    // Adding Constraints
    BoolExpr featureConstraints = VariationConditionHelper.getFeatureConstraints(node, allConstraints, allFeatures, expSolver);

    // Indexing subcomponents, to later access them in the edge-matrix
    int subIndex = 1;
    for (SubcomponentSymbol subcomp : allSubComponents) {
      subcomponentToInt.put(subcomp, subIndex++);
    }

    // Creating the edge-matrix, to manage all conenctions between subcomponents
    // The matrix defines which connectors are active and under which conditions
    int componentCount = allSubComponents.size() + 1;
    BoolExpr[][] edge = new BoolExpr[componentCount][componentCount];
    for (int i = 0; i < componentCount; i++) {
      for (int j = 0; j < componentCount; j++) {
        edge[i][j] = ctx.mkFalse();
      }
    }

    // Saving all connectors, that are active, when a feedback-loop exists
    List<BoolExpr> activeConnectorExprs = new ArrayList<>();
    // Mapping each connector with an id
    Map<Integer, ConnectorInfo> indexToConnector = new HashMap<>();
    int connectorIndex = 0;
    for (ConnectorInfo connectorInfo : connectorInfos) {
      for (ASTPortAccess target : connectorInfo.connector.getTargetList()) {
        // If the source-port of the connector is not strongly-causal, add the connector to the edge-matrix
        if (!connectorInfo.stronglyCausal) {
          BoolExpr isActive = connectorInfo.condition;
          int sourceIndex = connectorInfo.connector.getSource().isPresentComponentSymbol()
            ? subcomponentToInt.get(connectorInfo.connector.getSource().getComponentSymbol())
            : 0;
          int targetIndex = target.isPresentComponentSymbol()
            ? subcomponentToInt.get(target.getComponentSymbol())
            : 0;
          edge[sourceIndex][targetIndex] = ctx.mkOr(edge[sourceIndex][targetIndex], isActive);
          activeConnectorExprs.add(isActive);
          indexToConnector.put(connectorIndex++, connectorInfo);
        }
      }
    }

    BoolExpr cycleExists = ctx.mkFalse();
    // try to identify if cycle of length cycleLen exists
    for (int cycleLen = 2; cycleLen <= componentCount; cycleLen++) {
      IntExpr[] path = new IntExpr[cycleLen + 1];
      for (int i = 0; i <= cycleLen; i++) {
        path[i] = ctx.mkIntConst("n" + i);
      }

      List<BoolExpr> constraints = new ArrayList<>();

      // Check that each index is within bounds
      for (IntExpr pathNode : path) {
        constraints.add(ctx.mkAnd(ctx.mkLe(ctx.mkInt(0), pathNode), ctx.mkLt(pathNode, ctx.mkInt(componentCount))));
      }

      // The starting node has to be equal to the ending node, to form a cycle
      constraints.add(ctx.mkEq(path[0], path[cycleLen]));

      // If the cycle is of lenght > 2, then each intermediate node should be distinct
      if (cycleLen > 2) {
        for (int i = 0; i < cycleLen; i++) {
          for (int j = i + 1; j < cycleLen; j++) {
            constraints.add(ctx.mkNot(ctx.mkEq(path[i], path[j])));
          }
        }
      }

      // Add conditions that need to hold, for edges (connections) to exist
      for (int i = 0; i < cycleLen; i++) {
        IntExpr from = path[i];
        IntExpr to = path[i + 1];
        BoolExpr edgeExists = ctx.mkFalse();

        for (int src = 0; src < componentCount; src++) {
          for (int target = 0; target < componentCount; target++) {
            if (!edge[src][target].isFalse()) {
              BoolExpr match = ctx.mkAnd(ctx.mkEq(from, ctx.mkInt(src)), ctx.mkEq(to, ctx.mkInt(target)));
              edgeExists = ctx.mkOr(edgeExists, ctx.mkAnd(match, edge[src][target]));
            }
          }
        }
        constraints.add(edgeExists);
      }
      // Comnbine all constraints, to ensure that a cycle exists
      BoolExpr cycleConstraints = ctx.mkAnd(constraints.toArray(new BoolExpr[0]));
      cycleExists = ctx.mkOr(cycleExists, cycleConstraints);
    }

    List<BoolExpr> cycleExistsExpressionList = new ArrayList<>(List.of(cycleExists, featureConstraints));

    if (ExpressionSolverService.solve(cycleExistsExpressionList) == Status.SATISFIABLE) {
      Model model = ExpressionSolverService.getModel();

      List<ASTConnector> involvedConnectors = new ArrayList<>();
      for (ConnectorInfo connector : connectorInfos) {
        if (!connector.stronglyCausal) {
          BoolExpr isActive = connector.condition;
          if (model.evaluate(isActive, false).isTrue()) {
            involvedConnectors.add(connector.connector);
          }
        }
      }

      Set<SubcomponentSymbol> visited = new HashSet<>();

      for (SubcomponentSymbol vertex : allSubComponents) {
        if (!visited.contains(vertex)) {
          this.check(involvedConnectors, vertex, new Stack<>(), visited);
        }
      }

    }

  }

  protected void check(@NotNull List<ASTConnector> validConnectors,
                       @NotNull SubcomponentSymbol next,
                       @NotNull Stack<SubcomponentSymbol> path,
                       @NotNull Set<SubcomponentSymbol> visited) {
    Preconditions.checkNotNull(validConnectors);
    Preconditions.checkNotNull(next);
    Preconditions.checkNotNull(path);
    Preconditions.checkNotNull(visited);

    path.push(next);
    for (ASTConnector connector : getConnectorsFromSubcomponent(validConnectors, next)) {
      this.check(validConnectors, connector, path, visited);
    }
    visited.add(path.pop());
  }

  protected void check(@NotNull List<ASTConnector> validConnectors,
                       @NotNull ASTConnector next,
                       @NotNull Stack<SubcomponentSymbol> path,
                       @NotNull Set<SubcomponentSymbol> visited) {
    Preconditions.checkNotNull(validConnectors);
    Preconditions.checkNotNull(next);
    Preconditions.checkNotNull(path);
    Preconditions.checkNotNull(visited);

    if (next.getSource().isPresentPortSymbol() && !next.getSource().getPortSymbol().getStronglyCausal()) {
      for (ASTPortAccess target : next.getTargetList()) {
        this.check(validConnectors, target, path, visited);
      }
    }

  }

  protected void check(@NotNull List<ASTConnector> validConnectors,
                       @NotNull ASTPortAccess next,
                       @NotNull Stack<SubcomponentSymbol> path,
                       @NotNull Set<SubcomponentSymbol> visited) {
    Preconditions.checkNotNull(validConnectors);
    Preconditions.checkNotNull(next);
    Preconditions.checkNotNull(path);
    Preconditions.checkNotNull(visited);

    if (!next.isPresentComponent() || !next.isPresentComponentSymbol()) {
      return;
    }

    if (path.contains(next.getComponentSymbol())) {
      Log.error(ArcError.FEEDBACK_CAUSALITY.toString(),
        next.get_SourcePositionStart(), next.get_SourcePositionEnd()
      );
    } else {
      this.check(validConnectors, next.getComponentSymbol(), path, visited);
    }
  }

  protected List<ASTConnector> getConnectorsFromSubcomponent(@NotNull List<ASTConnector> validConnectors, @NotNull SubcomponentSymbol subcomponent) {
    Preconditions.checkNotNull(subcomponent);
    return validConnectors.stream().filter(connector -> connector.getSource().isPresentComponent()
        && connector.getSource().isPresentComponentSymbol()
        && connector.getSource().getComponentSymbol().equals(subcomponent))
      .collect(Collectors.toList());
  }

}
