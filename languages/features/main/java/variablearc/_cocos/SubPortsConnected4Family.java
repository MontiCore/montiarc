/* (c) https://github.com/MontiCore/monticore */
package variablearc._cocos;

import arcbasis._ast.ASTArcComponentType;
import arcbasis._ast.ASTComponentInstance;
import arcbasis._ast.ASTComponentInstantiation;
import arcbasis._ast.ASTComponentInstantiationTOP;
import arcbasis._ast.ASTComponentInterface;
import arcbasis._ast.ASTConnector;
import arcbasis._ast.ASTPortAccess;
import arcbasis._ast.ASTPortDeclaration;
import arcbasis._cocos.ArcBasisASTArcComponentTypeCoCo;
import com.google.common.base.Preconditions;
import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Status;
import de.monticore.symbols.compsymbols._symboltable.PortSymbol;
import de.monticore.symbols.compsymbols._symboltable.SubcomponentSymbol;
import de.se_rwth.commons.logging.Log;
import montiarc.util.ArcError;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc.VariableArcMill;
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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class SubPortsConnected4Family implements ArcBasisASTArcComponentTypeCoCo {

  @Override
  public void check(@NotNull ASTArcComponentType node) {
    Preconditions.checkNotNull(node);

    ExpressionSolver expSolver = ExpressionSolverService.getExpressionSolver();
    Context ctx = ExpressionSolverService.getContext();

    if (node.getSymbol().isAtomic()) {
      return;
    }

    // Reading and processing parts of the Main-Component
    List<String> mainFeatures = node.getBody().getArcElementList().stream().filter(e -> e instanceof ASTArcFeatureDeclaration).map(v -> ((ASTArcFeatureDeclaration) v)).map(ASTArcFeatureDeclaration::getArcFeatureList).flatMap(List::stream).map(e -> node.getSymbol().getFullName() + "." + e.getSymbol().getName()).toList();

    // Managing all features, variations, constraints and connectors
    List<String> allFeatures;
    ExpressionSet constraints;
    List<ASTConnector> allConnectors;

    // Getting all features, ports and variations from the Main-Component
    allFeatures = new ArrayList<>(mainFeatures);
    allConnectors = new ArrayList<>();

    Map<ASTConnector, BoolExpr> connectorConditions = new HashMap<>();
    Map<ASTComponentInstance, BoolExpr> subcomponentConditions = new HashMap<>();

    Map<String, BoolExpr> ports = new HashMap<>();
    Map<String, PortSymbol> portSymbols = new HashMap<>();
    Map<String, BoolExpr> portNameConditions = new HashMap<>();
    Map<String, ASTComponentInstance> portToSubcomponent = new HashMap<>();
    Map<String, BoolExpr> portConnected = new HashMap<>();
    Map<String, Set<ASTConnector>> subCompNameToConnectors = new HashMap<>();

    if (node instanceof ASTVariableArcFullVariantComponentType) {
      constraints = ((IVariableArcComponentTypeSymbol) (((ASTVariableArcFullVariantComponentType) node).getOriginal()).getSymbol()).getConstraints();

      connectorConditions = ((ASTVariableArcFullVariantComponentType) node).getConnectorConditions();
      for (ASTConnector connector : connectorConditions.keySet()) {
        allConnectors.add(connector);
        for (ASTPortAccess target : connector.getTargetList()) {
          if (target.isPresentComponent())
            subCompNameToConnectors.computeIfAbsent(node.getSymbol().getFullName() + "." + target.getComponent(), k -> new HashSet<>()).add(connector);
        }
      }
      subcomponentConditions = ((ASTVariableArcFullVariantComponentType) node).getSubcomponentConditions();

    } else {
      constraints = ((IVariableArcComponentTypeSymbol) node.getSymbol()).getConstraints();

      List<ASTConnector> mainConnectors = node.getConnectors();
      allConnectors.addAll(mainConnectors);

      for (ASTConnector connector : mainConnectors) {
        connectorConditions.put(connector, ctx.mkTrue());
        for (ASTPortAccess target : connector.getTargetList()) {
          if (target.isPresentComponent())
            subCompNameToConnectors.computeIfAbsent(node.getSymbol().getFullName() + "." + target.getComponent(), k -> new HashSet<>()).add(connector);
        }
      }
      List<ASTComponentInstance> mainSubComps = node.getBody().getArcElementList().stream().filter(e -> e instanceof ASTComponentInstantiation).map(l -> (ASTComponentInstantiation) l).map(ASTComponentInstantiationTOP::getComponentInstanceList).flatMap(List::stream).toList();
      for (ASTComponentInstance mainSubComp : mainSubComps) {
        subcomponentConditions.put(mainSubComp, ctx.mkTrue());
      }
    }

    // Adding Constraints
    BoolExpr featureConstraints = VariationConditionHelper.getFeatureConstraints(node, constraints, allFeatures, expSolver);

    for (ASTComponentInstance subComp : subcomponentConditions.keySet()) {

      SubcomponentSymbol sub = subComp.getSymbol();

      if (!sub.isTypePresent())
        continue;

      List<VariableArcVariationPoint> subVariationPoints;
      ASTArcComponentType compTypeSymbol = null;
      List<String> subPorts;

      if (sub.getType().isGenericComponentType()) {
        subVariationPoints = ((IVariableArcComponentTypeSymbol) sub.getType().asGenericComponentType().getTypeInfo()).getAllVariationPoints();
        var portList = ((IVariableArcComponentTypeSymbol) sub.getType().asGenericComponentType().getTypeInfo()).getTypeInfo().getPorts();
        subPorts = portList.stream().map(l -> sub.getFullName() + "." + l.getName()).collect(Collectors.toList());

        for (PortSymbol port : portList) {
          String portName = sub.getFullName() + "." + port.getName();
          ports.put(portName, ctx.mkBoolConst(portName));
          portSymbols.put(portName, port);
          portToSubcomponent.put(portName, subComp);
        }
      } else {
        subVariationPoints = ((IVariableArcComponentTypeSymbol) sub.getType().asComponentType().getTypeInfo()).getAllVariationPoints();
        if (sub.isPresentAstNode() && sub.getAstNode().isPresentSymbol() && sub.getAstNode().getSymbol().getType().getTypeInfo().isPresentAstNode()) {
          compTypeSymbol = VariableArcMill.typeDispatcher().asArcBasisASTArcComponentType(sub.getAstNode().getSymbol().getType().getTypeInfo().getAstNode());
          var subFeatures = compTypeSymbol.getBody().getArcElementList().stream().filter(e -> e instanceof ASTArcFeatureDeclaration).map(v -> ((ASTArcFeatureDeclaration) v)).map(ASTArcFeatureDeclaration::getArcFeatureList).flatMap(List::stream).map(l -> sub.getFullName() + "." + l.getSymbol().getName()).toList();
          allFeatures.addAll(subFeatures);
        }
        var portList = ((IVariableArcComponentTypeSymbol) sub.getType().asComponentType().getTypeInfo()).getTypeInfo().getPorts();
        subPorts = portList.stream().map(l -> sub.getFullName() + "." + l.getName()).collect(Collectors.toList());
        for (PortSymbol port : portList) {
          String portName = sub.getFullName() + "." + port.getName();
          ports.put(portName, ctx.mkBoolConst(portName));
          portSymbols.put(portName, port);
          portToSubcomponent.put(portName, subComp);
        }
      }

      for (String port : subPorts) {
        portNameConditions.merge(
          port,
          subcomponentConditions.get(subComp),
          ctx::mkOr
        );
      }

      for (VariableArcVariationPoint varif : subVariationPoints) {
        var varifExprSet = VariationConditionHelper.getExpressionSetCopyWithContext(new ExpressionSet(new ArrayList<>(varif.getAllConditions())));
        varifExprSet.getExpressions().forEach(k -> VariationConditionHelper.changeNameExpressionInCondition(k.getAstExpression()));

        var expr = expSolver.convert(varifExprSet);

        if (expr.isEmpty())
          continue;

        BoolExpr variationExpr = ctx.mkAnd(expr.get());
        assert compTypeSymbol != null;
        var subVariationExpr = VariationConditionHelper.renamePrefix(ctx, variationExpr, compTypeSymbol.getSymbol().getFullName(), node.getSymbol().getFullName() + "." + sub.getName());

        var variationPorts = varif.getArcElements().stream().filter(e -> e instanceof ASTComponentInterface).map(v -> ((ASTComponentInterface) v).getPortDeclarationList()).flatMap(List::stream).map(ASTPortDeclaration::getArcPortList).flatMap((List::stream)).map(l -> node.getSymbol().getFullName() + "." + sub.getName() + "." + l.getName()).toList();
        for (String variationPort : variationPorts) {
          var variationPortConst = ctx.mkBoolConst(variationPort + "_active");
          BoolExpr portActive = ctx.mkEq(variationPortConst, ctx.mkAnd(subVariationExpr, subcomponentConditions.get(subComp)));
          portNameConditions.put(variationPort, ctx.mkAnd(portActive, variationPortConst));

          BoolExpr portConnectors = ctx.mkFalse();

          if (subCompNameToConnectors.get(node.getSymbol().getFullName() + "." + sub.getName()) == null)
            continue;

          for (ASTConnector portConnector : subCompNameToConnectors.get(node.getSymbol().getFullName() + "." + sub.getName())) {
            for (var target : portConnector.getTargetsNames()) {
              if ((node.getSymbol().getFullName() + "." + sub.getName() + "." + target).equals(variationPort)) {
                portConnectors = ctx.mkOr(portConnectors, ctx.mkEq(ctx.mkBoolConst("From_" + portConnector.getSource().getPort() + "_To_" + variationPort), connectorConditions.get(portConnector)));
              }
            }
          }
          portConnected.put(variationPort, portConnectors);
        }
      }
    }

    // Define connectivity with implications
    for (ASTConnector connector : allConnectors) {
      if (connector.getSource().isPresentComponentSymbol()) {
        if (!connector.getSource().getComponentSymbol().isTypePresent())
          continue;
        String sourceName = connector.getSource().getComponentSymbol().getFullName() + "." + connector.getSource().getPort();
        if (portNameConditions.get(sourceName) == null || connectorConditions.get(connector) == null)
          continue;
        portConnected.merge(sourceName, ctx.mkImplies(portNameConditions.get(sourceName), connectorConditions.get(connector)), ctx::mkOr);
      }
      for (ASTPortAccess target : connector.getTargetList()) {
        if (target.isPresentComponentSymbol()) {
          if (!target.getComponentSymbol().isTypePresent())
            continue;
          String targetName = target.getComponentSymbol().getFullName() + "." + target.getPort();
          if (portNameConditions.get(targetName) == null || connectorConditions.get(connector) == null)
            continue;
          portConnected.merge(targetName, ctx.mkImplies(portNameConditions.get(targetName), connectorConditions.get(connector)), ctx::mkOr);
        }
      }
    }

    for (String port : ports.keySet()) {

      BoolExpr portisPresent = portNameConditions.get(port);
      if (portisPresent == null)
        portisPresent = ctx.mkFalse();
      BoolExpr portisConnected = portConnected.get(port) != null ? portConnected.get(port) : ctx.mkFalse();
      BoolExpr portUnConnected = ctx.mkNot(portisConnected);
      BoolExpr portConnectsTo = !portisConnected.equals(ctx.mkFalse()) ? ctx.mkImplies(ctx.mkBoolConst(port + "_active"), portisConnected) : ctx.mkTrue();

      List<BoolExpr> expressionList = new ArrayList<>(List.of(portisPresent, featureConstraints, portUnConnected, portConnectsTo));

      if (ExpressionSolverService.solve(expressionList) == Status.SATISFIABLE) {
        PortSymbol portSymbol = portSymbols.get(port);
        ASTComponentInstance subComponent = portToSubcomponent.get(port);
        String portIdentifier = subComponent.getName() + "." + port.substring(subComponent.getSymbol().getFullName().length() + 1);
        if (portSymbol.isIncoming()) {
          Log.error(ArcError.IN_PORT_NOT_CONNECTED.format(portIdentifier), subComponent.getSymbol().getAstNode().get_SourcePositionStart(), subComponent.getSymbol().getAstNode().get_SourcePositionEnd());
        } else {
          Log.warn(ArcError.OUT_PORT_NOT_CONNECTED.format(portIdentifier), subComponent.getSymbol().getAstNode().get_SourcePositionStart(), subComponent.getSymbol().getAstNode().get_SourcePositionEnd());
        }
      }
    }
  }
}
