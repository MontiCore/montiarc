/* (c) https://github.com/MontiCore/monticore */
package variablearc._cocos;

import arcbasis._ast.ASTArcComponentType;
import arcbasis._ast.ASTArcPort;
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
import java.util.List;
import java.util.Map;

public class ConnectorDirectionsFit4Family implements ArcBasisASTArcComponentTypeCoCo {

  static class PortInfo {

    String name;
    boolean isIncoming;
    boolean isSubComponent;
    BoolExpr condition;

    PortInfo(String name, boolean isIncoming, boolean isSubComponent, BoolExpr condition) {
      this.name = name;
      this.isIncoming = isIncoming;
      this.isSubComponent = isSubComponent;
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
    Map<String, PortInfo> portNameInfos = new HashMap<>();
    Map<ASTComponentInstance, BoolExpr> subcomponentConditions = new HashMap<>();

    // Reading and processing parts of the Main-Component
    List<String> mainFeatures = node.getBody().getArcElementList().stream().filter(e -> e instanceof ASTArcFeatureDeclaration).map(v -> ((ASTArcFeatureDeclaration) v)).map(ASTArcFeatureDeclaration::getArcFeatureList).flatMap(List::stream).map(e -> node.getSymbol().getFullName() + "." + e.getSymbol().getName()).toList();

    // Getting all features, constraints and connectors from the Main-Component
    List<String> allFeatures;
    List<ExpressionSet> allConstraints;
    List<ASTConnector> allConnectors;

    allFeatures = new ArrayList<>(mainFeatures);
    allConstraints = new ArrayList<>();
    allConnectors = new ArrayList<>();

    if (node instanceof ASTVariableArcFullVariantComponentType) {
      ExpressionSet mainConstraintSet = ((IVariableArcComponentTypeSymbol) ((ASTVariableArcFullVariantComponentType) node).getOriginal().getSymbol()).getConstraints();
      allConstraints.add(mainConstraintSet);

      connectorConditions = ((ASTVariableArcFullVariantComponentType) node).getConnectorConditions();
      allConnectors.addAll(connectorConditions.keySet());
      portConditions = ((ASTVariableArcFullVariantComponentType) node).getPortConditions();
      for (Map.Entry<ASTArcPort, BoolExpr> portEntry : portConditions.entrySet()) {
        portNameInfos.put(node.getSymbol().getFullName() + "." + portEntry.getKey().getName(), new PortInfo(node.getSymbol().getFullName() + "." + portEntry.getKey().getName(), portEntry.getKey().getSymbol().isIncoming(), false, ctx.mkTrue()));
      }

      subcomponentConditions = ((ASTVariableArcFullVariantComponentType) node).getSubcomponentConditions();

    } else {
      ExpressionSet mainConstraintSet = ((IVariableArcComponentTypeSymbol) node.getSymbol()).getConstraints();
      allConstraints.add(mainConstraintSet);

      List<ASTArcPort> mainPorts = node.getBody().getArcElementList().stream().filter(e -> e instanceof ASTComponentInterface).map(v -> ((ASTComponentInterface) v).getPortDeclarationList()).flatMap(List::stream).map(ASTPortDeclaration::getArcPortList).flatMap(List::stream).toList();
      for (ASTArcPort mainPort : mainPorts) {
        portConditions.put(mainPort, ctx.mkTrue());
        portNameInfos.put(node.getSymbol().getFullName() + "." + mainPort.getName(), new PortInfo(node.getSymbol().getFullName() + "." + mainPort.getName(), mainPort.getSymbol().isIncoming(), false, ctx.mkTrue()));
      }

      List<ASTConnector> mainConnectors = node.getConnectors();
      allConnectors.addAll(mainConnectors);
      for (ASTConnector connector : mainConnectors) {
        connectorConditions.put(connector, ctx.mkTrue());
      }

      List<ASTComponentInstance> mainSubComps = node.getBody().getArcElementList().stream().filter(e -> e instanceof ASTComponentInstantiation).map(l -> (ASTComponentInstantiation) l).map(ASTComponentInstantiationTOP::getComponentInstanceList).flatMap(List::stream).toList();
      for (ASTComponentInstance mainSubComp : mainSubComps) {
        subcomponentConditions.put(mainSubComp, ctx.mkTrue());
      }
    }

    for (ASTComponentInstance subComp : subcomponentConditions.keySet()) {

      SubcomponentSymbol sub = subComp.getSymbol();

      if(!sub.isTypePresent())
        continue;
      if(!sub.getType().getTypeInfo().isPresentAstNode())
        continue;

      var subVariationPoints = ((IVariableArcComponentTypeSymbol) (sub.getType().getTypeInfo().getAstNode().getSymbol())).getAllVariationPoints();

      var compTypeSymbol = VariableArcMill.typeDispatcher().asArcBasisASTArcComponentType(sub.getAstNode().getSymbol().getType().getTypeInfo().getAstNode());
      var subFeatures = compTypeSymbol.getBody().getArcElementList().stream().filter(e -> e instanceof ASTArcFeatureDeclaration).map(v -> ((ASTArcFeatureDeclaration) v)).map(ASTArcFeatureDeclaration::getArcFeatureList).flatMap(List::stream).map(l -> sub.getFullName() + "." + l.getSymbol().getName()).toList();
      allFeatures.addAll(subFeatures);
      if (sub.isTypePresent()) {
        // Add Ports of the sub-component, that are not declared in variation points
        for (PortSymbol subPort : sub.getType().getTypeInfo().getAllPorts()) {
          portNameInfos.put(node.getSymbol().getFullName() + "." + sub.getName() + "." + subPort.getName(), new PortInfo(node.getSymbol().getFullName() + "." + sub.getName() + "." + subPort.getName(), subPort.isIncoming(), true, ctx.mkTrue()));
        }
      }

      for (VariableArcVariationPoint variationPoint : subVariationPoints) {
        var varifExprSet = VariationConditionHelper.getExpressionSetCopyWithContext(new ExpressionSet(new ArrayList<>(variationPoint.getAllConditions())));
        varifExprSet.getExpressions().forEach(k -> VariationConditionHelper.changeNameExpressionInCondition(k.getAstExpression()));

        var expr = expSolver.convert(varifExprSet);

        if (expr.isEmpty())
          continue;

        BoolExpr variationExpr = ctx.mkOr(expr.get());
        var subVariationExpr = VariationConditionHelper.renamePrefix(ctx, variationExpr, compTypeSymbol.getSymbol().getFullName(), node.getSymbol().getFullName() + "." + sub.getName());

        var subVariationPorts = variationPoint.getArcElements().stream().filter(e -> e instanceof ASTComponentInterface).map(v -> ((ASTComponentInterface) v).getPortDeclarationList()).flatMap(List::stream).map(ASTPortDeclaration::getArcPortList).flatMap((List::stream)).map(ASTArcPort::getSymbol).toList();
        for (PortSymbol subVariationPort : subVariationPorts) {
          portNameInfos.put(node.getSymbol().getFullName() + "." + sub.getName() + "." + subVariationPort.getName(), new PortInfo(node.getSymbol().getFullName() + "." + sub.getName() + "." + subVariationPort.getName(), subVariationPort.isIncoming(), true, (BoolExpr) subVariationExpr));
        }
      }
    }

    // Adding Constraints
    BoolExpr featureConstraints = VariationConditionHelper.getFeatureConstraints(node, allConstraints, allFeatures, expSolver);


    for (ASTConnector connector : allConnectors) {
      var source = connector.getSource();
      BoolExpr condition = connectorConditions.get(connector) != null ? connectorConditions.get(connector) : ctx.mkTrue();

      if (!source.isPresentPortSymbol())
        continue;

      var sourceName = source.isPresentComponent() ? node.getSymbol().getFullName() + "." + source.getComponent() + "." + source.getPort() : node.getSymbol().getFullName() + "." + source.getPort();
      var sourcePortInfo = portNameInfos.get(sourceName);
      if(sourcePortInfo == null)
        continue;

      BoolExpr srcExists = ctx.mkAnd(condition, sourcePortInfo.condition);
      boolean sourceDirectionValid =
        (!sourcePortInfo.isSubComponent && sourcePortInfo.isIncoming) || (sourcePortInfo.isSubComponent && !sourcePortInfo.isIncoming);

      BoolExpr sourceInValid = ctx.mkAnd(
        condition,
        ctx.mkNot(ctx.mkBool(sourceDirectionValid))
      );

      List<BoolExpr> sourceDirectionExpressionList = new ArrayList<>(List.of(featureConstraints, srcExists, sourceInValid));

      if (ExpressionSolverService.solve(sourceDirectionExpressionList) == Status.SATISFIABLE) {
        Log.error(ArcError.SOURCE_DIRECTION_MISMATCH.format(source.getQName()),
          source.get_SourcePositionStart(), source.get_SourcePositionEnd());
      }

      for (ASTPortAccess target : connector.getTargetList()) {

        if (!target.isPresentPortSymbol())
          continue;

        var targetName = target.isPresentComponent() ? node.getSymbol().getFullName() + "." + target.getComponent() + "." + target.getPort() : node.getSymbol().getFullName() + "." + target.getPort();
        var targetPortInfo = portNameInfos.get(targetName);
        if(targetPortInfo == null)
          continue;

        BoolExpr trgtExists = ctx.mkAnd(condition, targetPortInfo.condition);

        boolean targetDirectionInvalid =
          (targetPortInfo.isSubComponent && !targetPortInfo.isIncoming) ||
            (!targetPortInfo.isSubComponent && targetPortInfo.isIncoming);

        BoolExpr targetInValid = ctx.mkAnd(
          condition,
          ctx.mkBool(targetDirectionInvalid));

        List<BoolExpr> targetDirectionExpressionList = new ArrayList<>(List.of(featureConstraints, trgtExists, targetInValid));

        if (ExpressionSolverService.solve(targetDirectionExpressionList) == Status.SATISFIABLE) {
          Log.error(ArcError.TARGET_DIRECTION_MISMATCH.format(target.getQName()),
            target.get_SourcePositionStart(), target.get_SourcePositionEnd()
          );
        }
      }
    }
  }
}
