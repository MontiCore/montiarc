/* (c) https://github.com/MontiCore/monticore */
package variablearc._cocos;

import arcbasis._ast.*;
import arcbasis._cocos.ArcBasisASTArcComponentTypeCoCo;
import com.google.common.base.Preconditions;
import com.microsoft.z3.*;
import de.monticore.symbols.compsymbols._symboltable.PortSymbol;
import de.monticore.symbols.compsymbols._symboltable.Timing;
import de.se_rwth.commons.logging.Log;
import montiarc.util.ArcError;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc._ast.ASTArcFeatureDeclaration;
import variablearc._ast.ASTVariableArcFullVariantComponentType;
import variablearc._cocos.util.ExpressionSolverService;
import variablearc._symboltable.IVariableArcComponentTypeSymbol;
import variablearc.evaluation.ExpressionSet;
import variablearc.evaluation.ExpressionSolver;
import variablearc._cocos.util.VariationConditionHelper;

import java.util.*;

public class ConnectorTimingsFit4Family implements ArcBasisASTArcComponentTypeCoCo {

  static class PortInfo {

    String name;
    Timing timing;
    Boolean isIncoming;
    BoolExpr condition;

    PortInfo(String name, boolean isIncoming, Timing timing, BoolExpr condition) {
      this.name = name;
      this.isIncoming = isIncoming;
      this.timing = timing;
      this.condition = condition;
    }
  }

  @Override
  public void check(@NotNull ASTArcComponentType node) {
    Preconditions.checkNotNull(node);

    ExpressionSolver expSolver = ExpressionSolverService.getExpressionSolver();
    Context ctx = ExpressionSolverService.getContext();

    Map<ASTConnector, BoolExpr> connectorConditions = new HashMap<>();
    Map<ASTArcPort, BoolExpr> portConditions = new HashMap<>();
    Map<ASTComponentInstance, BoolExpr> subcomponentConditions = new HashMap<>();
    Map<String, PortInfo> portNameInfos = new HashMap<>();

    // Reading and processing parts of the Main-Component
    List<String> mainFeatures = node.getBody().getArcElementList().stream().filter(e -> e instanceof ASTArcFeatureDeclaration).map(v -> ((ASTArcFeatureDeclaration) v)).map(ASTArcFeatureDeclaration::getArcFeatureList).flatMap(List::stream).map(e -> node.getSymbol().getFullName() + "." + e.getSymbol().getName()).toList();

    // Managing all features, variations, constraints and connectors
    List<String> allFeatures;
    ExpressionSet constraints;
    List<ASTConnector> allConnectors;

    // Getting all features, ports and variations from the Main-Component
    allFeatures = new ArrayList<>(mainFeatures);
    allConnectors = new ArrayList<>();

    if (node instanceof ASTVariableArcFullVariantComponentType) {
      constraints = ((IVariableArcComponentTypeSymbol) ((ASTVariableArcFullVariantComponentType) node).getOriginal().getSymbol()).getConstraints();

      connectorConditions = ((ASTVariableArcFullVariantComponentType) node).getConnectorConditions();
      allConnectors.addAll(connectorConditions.keySet());
      portConditions = ((ASTVariableArcFullVariantComponentType) node).getPortConditions();
      for (Map.Entry<ASTArcPort, BoolExpr> portEntry : portConditions.entrySet()) {
        portNameInfos.put(portEntry.getKey().getSymbol().getFullName(), new PortInfo(portEntry.getKey().getSymbol().getFullName(), portEntry.getKey().getSymbol().isIncoming(), portEntry.getKey().getSymbol().getTiming(), portEntry.getValue()));
      }

      subcomponentConditions = ((ASTVariableArcFullVariantComponentType) node).getSubcomponentConditions();

    } else {
      constraints = ((IVariableArcComponentTypeSymbol) node.getSymbol()).getConstraints();

      List<ASTArcPort> mainPorts = node.getBody().getArcElementList().stream().filter(e -> e instanceof ASTComponentInterface).map(v -> ((ASTComponentInterface) v).getPortDeclarationList()).flatMap(List::stream).map(ASTPortDeclaration::getArcPortList).flatMap(List::stream).toList();
      for (ASTArcPort mainPort : mainPorts) {
        portConditions.put(mainPort, ctx.mkTrue());
        portNameInfos.put(mainPort.getSymbol().getFullName(), new PortInfo(mainPort.getSymbol().getFullName(), mainPort.getSymbol().isIncoming(), mainPort.getSymbol().getTiming(), ctx.mkTrue()));
      }

      List<ASTConnector> mainConnectors = node.getConnectors();
      allConnectors.addAll(mainConnectors);
      for (ASTConnector connector : mainConnectors) {
        connectorConditions.put(connector, ctx.mkTrue());
      }

      for (PortSymbol port : ((IVariableArcComponentTypeSymbol) node.getSymbol()).getTypeInfo().getPorts()) {
        portNameInfos.put(port.getFullName(), new PortInfo(port.getFullName(), port.isIncoming(), port.getTiming(), ctx.mkTrue()));
      }

      List<ASTComponentInstance> mainSubComps = node.getBody().getArcElementList().stream().filter(e -> e instanceof ASTComponentInstantiation).map(l -> (ASTComponentInstantiation) l).map(ASTComponentInstantiationTOP::getComponentInstanceList).flatMap(List::stream).toList();
      for (ASTComponentInstance mainSubComp : mainSubComps) {
        subcomponentConditions.put(mainSubComp, ctx.mkTrue());
      }
    }

    for (Map.Entry<ASTComponentInstance, BoolExpr> subEntry : subcomponentConditions.entrySet()) {
      if (!subEntry.getKey().getSymbol().isTypePresent())
        continue;
      for (PortSymbol subPort : subEntry.getKey().getSymbol().getType().getTypeInfo().getAllPorts()) {
        portNameInfos.put(subPort.getFullName(), new PortInfo(subPort.getFullName(), subPort.isIncoming(), subPort.getTiming(), subEntry.getValue()));
      }
    }

    // Adding Constraints
    BoolExpr featureConstraints = VariationConditionHelper.getFeatureConstraints(node, constraints, allFeatures, expSolver);

    for (ASTConnector connector : allConnectors) {
      var source = connector.getSource();
      BoolExpr condition = connectorConditions.get(connector) != null ? connectorConditions.get(connector) : ctx.mkTrue();

      if (!source.isPresentPortSymbol())
        continue;

      var sourcePortInfo = portNameInfos.get(source.getPortSymbol().getFullName());

      for (ASTPortAccess target : connector.getTargetList()) {

        if (!target.isPresentPortSymbol())
          continue;

        var targetPortInfo = portNameInfos.get(target.getPortSymbol().getFullName());

        BoolExpr isActive = ctx.mkAnd(condition, sourcePortInfo.condition, targetPortInfo.condition);
        SeqExpr<CharSort> srcTiming = ctx.mkString(sourcePortInfo.timing.toString());
        SeqExpr<CharSort> targetTiming = ctx.mkString(targetPortInfo.timing.toString());
        BoolExpr compatible = ctx.mkOr(ctx.mkEq(srcTiming, targetTiming),
          ctx.mkAnd(ctx.mkEq(srcTiming, ctx.mkString("TIMED_SYNC")),
            ctx.mkOr(
              ctx.mkEq(targetTiming, ctx.mkString("TIMED")),
              ctx.mkEq(targetTiming, ctx.mkString("UNTIMED"))
            ))
        );
        BoolExpr timingMismatch = ctx.mkAnd(isActive, ctx.mkNot(compatible));

        List<BoolExpr> timingMismatchExpressionList = new ArrayList<>(List.of(featureConstraints, timingMismatch));

        if (ExpressionSolverService.solve(timingMismatchExpressionList) == Status.SATISFIABLE) {
          Log.error(ArcError.CONNECTOR_TIMING_MISMATCH.format(
              target.getPortSymbol().getTiming().getName(),
              source.getPortSymbol().getTiming().getName()
            ),
            target.get_SourcePositionStart(), target.get_SourcePositionEnd()
          );
        }
      }
    }
  }
}
