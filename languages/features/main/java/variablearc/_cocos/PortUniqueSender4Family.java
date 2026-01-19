/* (c) https://github.com/MontiCore/monticore */
package variablearc._cocos;

import arcbasis._ast.*;

import arcbasis._cocos.ArcBasisASTArcComponentTypeCoCo;
import com.google.common.base.Preconditions;
import com.microsoft.z3.*;
import de.monticore.symbols.compsymbols._symboltable.SubcomponentSymbol;
import montiarc.util.ArcError;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc.VariableArcMill;
import variablearc._ast.ASTArcFeatureDeclaration;
import variablearc._ast.ASTVariableArcFullVariantComponentType;
import variablearc._cocos.util.ExpressionSolverService;
import variablearc._symboltable.IVariableArcComponentTypeSymbol;
import variablearc._symboltable.VariableArcVariationPoint;
import variablearc.evaluation.ExpressionSet;
import variablearc.evaluation.ExpressionSolver;
import variablearc._cocos.util.VariationConditionHelper;
import de.se_rwth.commons.logging.Log;

import java.util.*;

public class PortUniqueSender4Family implements ArcBasisASTArcComponentTypeCoCo {

  private static List<String> getConnectorNamesForTargets(ASTConnector connector, String mainComponentName) {
    List<String> connectorNames = new ArrayList<>();
    var targets = connector.getTargetList();
    for (ASTPortAccess target : targets) {
      String connectorName;
      if (!target.isPresentComponentSymbol()) {
        connectorName = "connectorTo_" + mainComponentName + "." + target.getPort();
      } else {
        connectorName = "connectorTo_" + mainComponentName + "." + ((!target.isPresentComponentSymbol()) ? target.getPort() : target.getComponentSymbol().getName() + "." + target.getPort());
      }
      connectorNames.add(connectorName);
    }
    return connectorNames;
  }

  @Override
  public void check(@NotNull ASTArcComponentType node) {
    Preconditions.checkNotNull(node);

    ExpressionSolver expSolver = ExpressionSolverService.getExpressionSolver();
    Context ctx = ExpressionSolverService.getContext();

    // Data Structures for ports, connectors and subcomponents.
    Map<String, Set<ASTConnector>> portNameConnectorMap = new HashMap<>();
    Map<String, BoolExpr> portNameConditions = new HashMap<>();
    Map<SubcomponentSymbol, BoolExpr> subcomponentSymbolCondition = new HashMap<>();
    Map<ASTConnector, BoolExpr> connectorConditions = new HashMap<>();
    Map<ASTArcPort, BoolExpr> portConditions = new HashMap<>();
    Map<ASTComponentInstance, BoolExpr> subcomponentConditions;

    List<String> allFeatures;
    ExpressionSet constraints;
    List<ASTConnector> allConnectors;
    List<SubcomponentSymbol> allSubComponents;


    // Reading and processing parts of the Main-Component
    List<String> mainFeatures = node.getBody().getArcElementList().stream().filter(e -> e instanceof ASTArcFeatureDeclaration).map(v -> ((ASTArcFeatureDeclaration) v)).map(ASTArcFeatureDeclaration::getArcFeatureList).flatMap(List::stream).map(e -> node.getSymbol().getFullName() + "." + e.getSymbol().getName()).toList();

    allFeatures = new ArrayList<>(mainFeatures);
    allConnectors = new ArrayList<>();
    allSubComponents = new ArrayList<>();

    constraints = null;
    if(node instanceof ASTVariableArcFullVariantComponentType){
      if (node.isPresentSymbol())
        constraints = ((IVariableArcComponentTypeSymbol)(((ASTVariableArcFullVariantComponentType) node).getOriginal()).getSymbol()).getConstraints();

      connectorConditions = ((ASTVariableArcFullVariantComponentType) node).getConnectorConditions();
      allConnectors.addAll(connectorConditions.keySet());

      for(Map.Entry<ASTArcPort, BoolExpr> entry : ((ASTVariableArcFullVariantComponentType) node).getPortConditions().entrySet()){
        portConditions.put(entry.getKey(), entry.getValue());
      }
      for(Map.Entry<ASTArcPort, BoolExpr> portEntry : portConditions.entrySet()){
        portNameConditions.merge(node.getSymbol().getFullName() + "." + portEntry.getKey().getSymbol().getName(),portEntry.getValue(),ctx::mkOr);
      }
      subcomponentConditions = ((ASTVariableArcFullVariantComponentType) node).getSubcomponentConditions();
      for(Map.Entry<ASTComponentInstance, BoolExpr> subCompEntry: subcomponentConditions.entrySet()){
        if(subCompEntry.getKey().isPresentSymbol())
          subcomponentSymbolCondition.put(subCompEntry.getKey().getSymbol(), subCompEntry.getValue());
      }
    } else {
      if (node.isPresentSymbol())
        constraints = ((IVariableArcComponentTypeSymbol) node.getSymbol()).getConstraints();

      List<ASTConnector> mainConnectors = node.getConnectors();
      allConnectors.addAll(mainConnectors);

      List<ASTArcPort> mainPorts = node.getBody().getArcElementList().stream().filter(e -> e instanceof ASTComponentInterface).map(v -> ((ASTComponentInterface) v).getPortDeclarationList()).flatMap(List::stream).map(ASTPortDeclaration::getArcPortList).flatMap((List::stream)).toList();
      for (ASTArcPort port : mainPorts) {
        portConditions.put(port, ctx.mkTrue());
        portNameConditions.merge(node.getSymbol().getFullName() + "." + port.getSymbol().getName(),ctx.mkTrue(),ctx::mkOr);
      }

      List<SubcomponentSymbol> mainSubComps = node.getBody().getArcElementList().stream().filter(e -> e instanceof ASTComponentInstantiation).map(l -> (ASTComponentInstantiation) l).map(ASTComponentInstantiationTOP::getComponentInstanceList).flatMap(List::stream).map(ASTComponentInstance::getSymbol).toList();
      allSubComponents.addAll(mainSubComps);

      for (SubcomponentSymbol subComp : allSubComponents) {
        subcomponentSymbolCondition.put(subComp, ctx.mkTrue());
      }

      for (ASTConnector mainConnector : mainConnectors)
        connectorConditions.put(mainConnector, ctx.mkTrue());
    }

    // Adding Constraints
    BoolExpr featureConstraints = VariationConditionHelper.getFeatureConstraints(node, constraints, allFeatures, expSolver);

    // Processing Sub-Components, that are defined within the component, but not in a variation block
    for (SubcomponentSymbol sub : node.getBody().getArcElementList().stream().filter(e -> e instanceof ASTComponentInstantiation).map(l -> (ASTComponentInstantiation) l).map(ASTComponentInstantiationTOP::getComponentInstanceList).flatMap(List::stream).map(ASTComponentInstance::getSymbol).toList()) {
      if(!sub.isPresentAstNode() ||!sub.getAstNode().getSymbol().isTypePresent() || !sub.getAstNode().getSymbol().getType().getTypeInfo().isPresentAstNode() )
        continue;
      var subComp = VariableArcMill.typeDispatcher().asArcBasisASTArcComponentType(sub.getAstNode().getSymbol().getType().getTypeInfo().getAstNode());
      var subFeatures = subComp.getBody().getArcElementList().stream().filter(e -> e instanceof ASTArcFeatureDeclaration).map(v -> ((ASTArcFeatureDeclaration) v)).map(ASTArcFeatureDeclaration::getArcFeatureList).flatMap(List::stream).map(l -> sub.getFullName() + "." + l.getSymbol().getName()).toList();
      allFeatures.addAll(subFeatures);

      var subPorts = subComp.getBody().getArcElementList().stream().filter(e -> e instanceof ASTComponentInterface).map(v -> ((ASTComponentInterface) v).getPortDeclarationList()).flatMap(List::stream).map(ASTPortDeclaration::getArcPortList).flatMap((List::stream)).toList();
      for (ASTArcPort port : subPorts) {
        portConditions.put(port, subcomponentSymbolCondition.get(sub));
        portNameConditions.merge(sub.getFullName() + "." + port.getSymbol().getName(),subcomponentSymbolCondition.get(sub),ctx::mkOr);
      }

      var subVariationPoints = ((IVariableArcComponentTypeSymbol)(sub.getType().getTypeInfo().getAstNode().getSymbol())).getAllVariationPoints();
      for (VariableArcVariationPoint variationPoint : subVariationPoints) {
        var varifExprSet = VariationConditionHelper.getExpressionSetCopyWithContext(new ExpressionSet(variationPoint.getAllConditions()));

        varifExprSet.getExpressions().forEach(k -> VariationConditionHelper.changeNameExpressionInCondition(k.getAstExpression()));

        var expr = expSolver.convert(varifExprSet);

        if (expr.isEmpty())
          continue;

        BoolExpr variationExpr = ctx.mkOr(expr.get());
        var subVariationExpr = VariationConditionHelper.renamePrefix(ctx, variationExpr, subComp.getName(), node.getSymbol().getFullName() + "." + sub.getName());

        var variationPorts = variationPoint.getArcElements().stream().filter(e -> e instanceof ASTComponentInterface).map(v -> ((ASTComponentInterface) v).getPortDeclarationList()).flatMap(List::stream).map(ASTPortDeclaration::getArcPortList).flatMap((List::stream)).toList();
        for (ASTArcPort variationPort : variationPorts) {
          BoolExpr portActive = ctx.mkEq(ctx.mkBoolConst(variationPort + "_active"), subVariationExpr);
          portConditions.put(variationPort, portActive);
          portNameConditions.merge(sub.getFullName() + "." + variationPort.getSymbol().getName(),subcomponentSymbolCondition.get(sub),ctx::mkOr);
        }
      }
    }

    for (ASTConnector connector : allConnectors) {
      List<String> connectorNames = getConnectorNamesForTargets(connector, node.isPresentSymbol() ? node.getSymbol().getFullName() : node.getName());

      for (String connectorName : connectorNames) {
        String portName = connectorName.substring("connectorTo_".length());
        portNameConnectorMap.computeIfAbsent(portName, k -> new HashSet<>()).add(connector);
      }
    }

    // Checking if ports are connected multiple times
    for (Map.Entry<String, Set<ASTConnector>> entry : portNameConnectorMap.entrySet()) {
      String portName = entry.getKey();

      BoolExpr portisPresent = portNameConditions.get(portName);
      if (portisPresent == null)
        continue;

      Set<ASTConnector> connectors = entry.getValue();

      List<BoolExpr> expressionList = new ArrayList<>();

      // Counting the number of active connections
      ArithExpr<IntSort> count = ctx.mkInt(0);
      int connectorCount = 1;
      List<BoolExpr> connectorActive = new ArrayList<>();
      for (ASTConnector conn : connectors) {
        List<String> connectorNames = getConnectorNamesForTargets(conn, node.isPresentSymbol() ? node.getSymbol().getFullName() : node.getName());
        for (String connectorName : connectorNames) {
          String conPortName = connectorName.substring("connectorTo_".length());
          if (!conPortName.equals(portName))
            continue;
          BoolExpr connectorExpr = ctx.mkBoolConst(connectorName + "_" + connectorCount);
          BoolExpr connActive = ctx.mkIff(connectorConditions.get(conn), connectorExpr);
          connectorActive.add(connActive);
          count = ctx.mkAdd(count, ctx.mkITE(connectorExpr, ctx.mkInt(1), ctx.mkInt(0)));
          connectorCount++;
        }
      }

      BoolExpr multipleTimes = ctx.mkGt(count, ctx.mkInt(1));
      expressionList.addAll(connectorActive);
      expressionList.addAll(List.of(featureConstraints, multipleTimes, portisPresent));

      if (ExpressionSolverService.solve(expressionList) == Status.SATISFIABLE) {
        Model model = ExpressionSolverService.getModel();
        int violationsCount = (Integer.parseInt(model.eval(count, false).toString()) - 1);
        for (int k = 0; k < violationsCount; k++) {
          ASTConnector violatingConnector = connectors.stream().findFirst().isPresent() ? connectors.stream().findFirst().get() : null;
          if(violatingConnector != null) {
            Log.error(ArcError.PORT_MULTIPLE_SENDER.format(portName), violatingConnector.get_SourcePositionStart(), violatingConnector.get_SourcePositionEnd());
          }else{
            Log.error(ArcError.PORT_MULTIPLE_SENDER.format(portName),null,null);
          }
          }
      }
    }
  }
}
