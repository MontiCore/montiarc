/* (c) https://github.com/MontiCore/monticore */
package variablearc._cocos;

import arcbasis._ast.ASTArcComponentType;
import arcbasis._ast.ASTArcPort;
import arcbasis._ast.ASTComponentInterface;
import arcbasis._ast.ASTConnector;
import arcbasis._ast.ASTPortAccess;
import arcbasis._ast.ASTPortDeclaration;
import arcbasis._cocos.ArcBasisASTArcComponentTypeCoCo;
import com.google.common.base.Preconditions;
import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Status;
import de.monticore.symbols.compsymbols._ast.ASTPort;
import de.monticore.symbols.compsymbols._symboltable.ComponentTypeSymbol;
import de.monticore.symbols.compsymbols._symboltable.PortSymbol;
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
import java.util.List;
import java.util.Map;

public class PortsConnected4Family implements ArcBasisASTArcComponentTypeCoCo {

  @Override
  public void check(@NotNull ASTArcComponentType node) {
    Preconditions.checkNotNull(node);
    Preconditions.checkNotNull(node.getSymbol());

    ExpressionSolver expSolver = ExpressionSolverService.getExpressionSolver();
    Context ctx = ExpressionSolverService.getContext();

    ComponentTypeSymbol symbol = node.getSymbol();

    if (symbol.isAtomic()) {
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
    Map<ASTArcPort, BoolExpr> portConditions = new HashMap<>();

    Map<String, BoolExpr> portNameConditions = new HashMap<>();
    Map<String, BoolExpr> portConnected = new HashMap<>();

    if (node instanceof ASTVariableArcFullVariantComponentType) {
      constraints = ((IVariableArcComponentTypeSymbol) ((ASTVariableArcFullVariantComponentType) node).getOriginal().getSymbol()).getConstraints();

      connectorConditions = ((ASTVariableArcFullVariantComponentType) node).getConnectorConditions();
      allConnectors.addAll(connectorConditions.keySet());
      portConditions = ((ASTVariableArcFullVariantComponentType) node).getPortConditions();
      for (Map.Entry<ASTArcPort, BoolExpr> portEntry : portConditions.entrySet()) {
        String portName = node.getSymbol().getFullName() + "." + portEntry.getKey().getName();
        portNameConditions.put(portName, portEntry.getValue());
      }
    } else {
      constraints = ((IVariableArcComponentTypeSymbol) node.getSymbol()).getConstraints();

      List<ASTArcPort> mainPorts = node.getBody().getArcElementList().stream().filter(e -> e instanceof ASTComponentInterface).map(v -> ((ASTComponentInterface) v).getPortDeclarationList()).flatMap(List::stream).map(ASTPortDeclaration::getArcPortList).flatMap(List::stream).toList();
      for (ASTArcPort mainPort : mainPorts) {
        String portName = node.getSymbol().getFullName() + "." + mainPort.getName();
        portConditions.put(mainPort, ctx.mkTrue());
        portNameConditions.put(portName, ctx.mkTrue());
      }

      List<ASTConnector> mainConnectors = node.getConnectors();
      allConnectors.addAll(mainConnectors);
      for (ASTConnector connector : mainConnectors) {
        connectorConditions.put(connector, ctx.mkTrue());
      }
    }

    // Adding Constraints
    BoolExpr featureConstraints = VariationConditionHelper.getFeatureConstraints(node, constraints, allFeatures, expSolver);

    // Define connectivity with implications
    for (ASTConnector connector : allConnectors) {
      if (!connector.getSource().isPresentComponentSymbol()) {
        String sourceName = connector.getSource().isPresentPortSymbol() ? connector.getSource().getPortSymbol().getFullName() : node.getSymbol().getFullName() + "." + connector.getSource().getPort();
        if (portConnected.containsKey(sourceName)) {
          var existingCondition = portConnected.get(sourceName);
          portConnected.put(sourceName, ctx.mkOr(existingCondition, ctx.mkImplies(portNameConditions.get(sourceName), connectorConditions.get(connector))));
        } else {
          if (portNameConditions.containsKey(sourceName) && connectorConditions.containsKey(connector))
            portConnected.put(sourceName, ctx.mkImplies(portNameConditions.get(sourceName), connectorConditions.get(connector)));
        }
      }
      for (ASTPortAccess target : connector.getTargetList()) {
        if (target.isPresentComponentSymbol())
          continue;
        String targetName = target.isPresentPortSymbol() ? target.getPortSymbol().getFullName() : node.getSymbol().getFullName() + "." + target.getPort();
        if (portConnected.containsKey(targetName)) {
          var existingCondition = portConnected.get(targetName);
          portConnected.put(targetName, ctx.mkOr(existingCondition, ctx.mkImplies(portNameConditions.get(targetName), connectorConditions.get(connector))));
        } else {
          if (portNameConditions.containsKey(targetName) && connectorConditions.containsKey(connector))
            portConnected.put(targetName, ctx.mkImplies(portNameConditions.get(targetName), connectorConditions.get(connector)));
        }
      }
    }

    for (ASTPort port : portConditions.keySet()) {
      PortSymbol portSymbol = port.getSymbol();
      BoolExpr portisPresent = portNameConditions.get(portSymbol.getFullName());
      BoolExpr portisConnected = portConnected.get(portSymbol.getFullName()) != null ? portConnected.get(portSymbol.getFullName()) : ctx.mkFalse();

      BoolExpr portUnConnected = ctx.mkAnd(featureConstraints, portisPresent, ctx.mkNot(portisConnected));

      List<BoolExpr> expressionList = new ArrayList<>();
      expressionList.add(portUnConnected);

      if (ExpressionSolverService.solve(expressionList) == Status.SATISFIABLE) {
        if (portSymbol.isIncoming()) {
          Log.warn(ArcError.IN_PORT_UNUSED.format(portSymbol.getName()),
            portSymbol.getAstNode().get_SourcePositionStart(), portSymbol.getAstNode().get_SourcePositionEnd()
          );
        } else {
          Log.warn(ArcError.OUT_PORT_UNUSED.format(portSymbol.getName()),
            portSymbol.getAstNode().get_SourcePositionStart(), portSymbol.getAstNode().get_SourcePositionEnd());
        }
      }
      ExpressionSolverService.getSolver().reset();
    }
  }
}
