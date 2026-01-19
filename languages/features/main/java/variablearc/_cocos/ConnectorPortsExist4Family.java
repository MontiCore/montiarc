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
import de.se_rwth.commons.logging.Log;
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
import java.util.Optional;

public class ConnectorPortsExist4Family implements ArcBasisASTArcComponentTypeCoCo {

  @Override
  public void check(@NotNull ASTArcComponentType node) {
    Preconditions.checkNotNull(node);

    ExpressionSolver expSolver = ExpressionSolverService.getExpressionSolver();
    Context ctx = ExpressionSolverService.getContext();

    // Data Structures for components, features and ports
    Map<String, Map<String, BoolExpr>> portDeclared = new HashMap<>();
    Map<ASTArcPort, BoolExpr> portConditions = new HashMap<>();
    Map<ASTConnector, BoolExpr> connectorConditions = new HashMap<>();
    Map<ASTComponentInstance, BoolExpr> subcomponentConditions = new HashMap<>();

    List<String> allFeatures;
    ExpressionSet constraints;
    List<ASTConnector> allConnectors;

    // Reading and processing parts of the Main-Component
    List<String> mainFeatures = node.getBody().getArcElementList().stream().filter(e -> e instanceof ASTArcFeatureDeclaration).map(v -> ((ASTArcFeatureDeclaration) v)).map(ASTArcFeatureDeclaration::getArcFeatureList).flatMap(List::stream).map(e -> node.getSymbol().getFullName() + "." + e.getSymbol().getName()).toList();

    // Getting all features, ports and variations from the Main-Component
    allFeatures = new ArrayList<>(mainFeatures);
    allConnectors = new ArrayList<>();

    // Initialize Z3 variables
    Map<String, BoolExpr> portExistsVars = new HashMap<>();
    Map<String, BoolExpr> componentExistsVars = new HashMap<>();

    if (node instanceof ASTVariableArcFullVariantComponentType) {
      constraints = ((IVariableArcComponentTypeSymbol) (((ASTVariableArcFullVariantComponentType) node).getOriginal()).getSymbol()).getConstraints();
      connectorConditions = ((ASTVariableArcFullVariantComponentType) node).getConnectorConditions();
      allConnectors.addAll(connectorConditions.keySet());

      portConditions = ((ASTVariableArcFullVariantComponentType) node).getPortConditions();

      Map<String, BoolExpr> mainPortsDefined = new HashMap<>();
      for (Map.Entry<ASTArcPort, BoolExpr> portEntry : portConditions.entrySet()) {
        String portName = (node.getSymbol().getFullName() + "." + portEntry.getKey().getSymbol().getName());
        mainPortsDefined.merge(portName, portEntry.getValue(), (oldVal, newVal) -> ctx.mkOr(oldVal, newVal));
        portExistsVars.merge(portName, portEntry.getValue(), (oldVal, newVal) -> ctx.mkOr(oldVal, newVal));
      }
      portDeclared.put(node.getSymbol().getFullName(), mainPortsDefined);
      subcomponentConditions = ((ASTVariableArcFullVariantComponentType) node).getSubcomponentConditions();

    } else {
      constraints = ((IVariableArcComponentTypeSymbol) node.getSymbol()).getConstraints();

      List<ASTConnector> mainConnectors = node.getConnectors();
      allConnectors.addAll(mainConnectors);

      for (ASTConnector connector : mainConnectors) {
        connectorConditions.put(connector, ctx.mkTrue());
      }

      List<ASTComponentInstance> mainSubComps = node.getBody().getArcElementList().stream().filter(e -> e instanceof ASTComponentInstantiation).map(l -> (ASTComponentInstantiation) l).map(ASTComponentInstantiationTOP::getComponentInstanceList).flatMap(List::stream).toList();
      for (ASTComponentInstance mainSubComp : mainSubComps) {
        subcomponentConditions.put(mainSubComp, ctx.mkTrue());
      }

      List<String> mainPorts = node.getBody().getArcElementList().stream().filter(e -> e instanceof ASTComponentInterface).map(v -> ((ASTComponentInterface) v).getPortDeclarationList()).flatMap(List::stream).map(ASTPortDeclaration::getArcPortList).flatMap((List::stream)).map(l -> node.getSymbol().getFullName() + "." + l.getSymbol().getName()).toList();

      Map<String, BoolExpr> mainPortsDefined = new HashMap<>();
      for (String port : mainPorts) {
        mainPortsDefined.put(port, ctx.mkTrue());
        portExistsVars.put(port, ctx.mkBool(true));
      }
      portDeclared.put(node.getSymbol().getFullName(), mainPortsDefined);
    }

    Map<String, BoolExpr> subcomponentportsDefined = new HashMap<>();
    for (Map.Entry<ASTComponentInstance, BoolExpr> entry : subcomponentConditions.entrySet()) {

      var sub = entry.getKey().getSymbol();
      if (!sub.isTypePresent())
        continue;

      var compSymbol = sub.getType().getTypeInfo();
      BoolExpr subComponentExpr = entry.getValue();
      //Objects.requireNonNull(Objects.requireNonNull(subcomponentConditions.entrySet().stream().filter(e -> e.getKey().getSymbol().getFullName().equals(sub.getFullName())).findFirst().orElse(null))).getValue();

      if (sub.isTypePresent()) {
        Map<String, BoolExpr> varsubcomponentportsDefined = new HashMap<>();
        for (PortSymbol port : sub.getType().getTypeInfo().getAllPorts()) {
          varsubcomponentportsDefined.put(node.getSymbol().getFullName() + "." + sub.getName() + "." + port.getName(), subComponentExpr);
        }

        // Ensure the inner map exists
        Map<String, BoolExpr> innerMap = portDeclared.computeIfAbsent(sub.getFullName(), k -> new HashMap<>());
        if (innerMap == null) {
          innerMap = new HashMap<>();
          portDeclared.put(sub.getFullName(), innerMap);
        }
        // Merge all entries from flatMap
        for (Map.Entry<String, BoolExpr> portEntry : varsubcomponentportsDefined.entrySet()) {
          innerMap.merge(portEntry.getKey(), portEntry.getValue(), (oldVal, newVal) -> ctx.mkOr(oldVal, newVal));
        }
      }

      componentExistsVars.merge(sub.getFullName(), subComponentExpr, ctx::mkOr);

      // Check elements of subcomponent variation, that are relevant
      if (!sub.getType().getTypeInfo().isPresentAstNode())
        continue;
      var subVariationPoints = ((IVariableArcComponentTypeSymbol) (sub.getType().getTypeInfo().getAstNode()).getSymbol()).getAllVariationPoints();
      for (VariableArcVariationPoint varitationPoint : subVariationPoints) {
        var varifExprSet = VariationConditionHelper.getExpressionSetCopyWithContext(new ExpressionSet(varitationPoint.getAllConditions()));
        varifExprSet.getExpressions().forEach(k -> VariationConditionHelper.changeNameExpressionInCondition(k.getAstExpression()));

        var expr = expSolver.convert(varifExprSet);

        if (expr.isEmpty())
          continue;

        BoolExpr variationExpr = ctx.mkOr(expr.get());
        var subVariationExpr = VariationConditionHelper.renamePrefix(ctx, variationExpr, compSymbol.getName(), node.getSymbol().getFullName() + "." + sub.getName());

        // Add conditions that have to hold, for the variation-ports to exist
        var variationPorts = varitationPoint.getArcElements().stream().filter(e -> e instanceof ASTComponentInterface).map(v -> ((ASTComponentInterface) v).getPortDeclarationList()).flatMap(List::stream).map(ASTPortDeclaration::getArcPortList).flatMap((List::stream)).map(l -> node.getSymbol().getFullName() + "." + sub.getName() + "." + l.getName()).toList();
        for (String variationPort : variationPorts) {
          subcomponentportsDefined.put(variationPort, (BoolExpr) subVariationExpr);
        }
      }

    }

    // Adding Constraints
    BoolExpr featureConstraints = VariationConditionHelper.getFeatureConstraints(node, constraints, allFeatures, expSolver);

    // Check for each connector, if ports or components are missing
    for (ASTConnector connector : allConnectors) {
      var source = connector.getSource();
      if (source.isPresentComponentSymbol() && !source.getComponentSymbol().isTypePresent())
        continue;
      BoolExpr condition = connectorConditions.get(connector) != null ? connectorConditions.get(connector) : ctx.mkTrue();
      var sourceName = ((Optional.ofNullable(source.getComponentSymbol()).isPresent() ? node.getSymbol().getFullName() + "." + source.getComponent() + "." : source.isPresentComponent() ? "" : node.getSymbol().getFullName() + ".") + source.getPort());

      BoolExpr sourcePortDefined = portDeclared.getOrDefault((source.isPresentComponentSymbol() ? source.getComponentSymbol().getFullName() : source.isPresentComponent() ? "" : node.getSymbol().getFullName()), Map.of()).getOrDefault(sourceName, ctx.mkFalse());
      BoolExpr srcExists;
      if (source.isPresentComponentSymbol()) {
        sourceName = source.getComponentSymbol().getFullName();
        srcExists = componentExistsVars.getOrDefault(sourceName, ctx.mkFalse());
      } else {
        srcExists = portExistsVars.getOrDefault(sourceName, ctx.mkFalse());
      }

      // Checking for the source port
      List<BoolExpr> sourcePortExpressionList = new ArrayList<>();
      sourcePortExpressionList.add(ctx.mkAnd(condition, featureConstraints, ctx.mkNot(srcExists)));

      boolean missingSourceElementDetected = false;

      if (ExpressionSolverService.solve(sourcePortExpressionList) == Status.SATISFIABLE) {
        if (!source.isPresentComponent()) {
          Log.error(ArcError.MISSING_PORT.format(source.getQName()),
            source.get_SourcePositionStart(), source.get_SourcePositionEnd()
          );

        } else {
          Log.error(ArcError.MISSING_SUBCOMPONENT.format(source.getComponent()),
            source.get_SourcePositionStart(), source.get_SourcePositionEnd());
        }
        missingSourceElementDetected = true;
      }

      if (!missingSourceElementDetected) {
        sourcePortExpressionList.clear();
        sourcePortExpressionList.add(ctx.mkAnd(condition, featureConstraints, srcExists, ctx.mkNot(sourcePortDefined)));

        if (ExpressionSolverService.solve(sourcePortExpressionList) == Status.SATISFIABLE) {
          Log.error(ArcError.MISSING_PORT.format(source.getQName()),
            source.get_SourcePositionStart(), source.get_SourcePositionEnd());
        }
      }

      for (ASTPortAccess target : connector.getTargetList()) {
        if (target.isPresentComponentSymbol() && !target.getComponentSymbol().isTypePresent())
          continue;
        var targetName = ((Optional.ofNullable(target.getComponentSymbol()).isPresent() ? node.getSymbol().getFullName() + "." + target.getComponent() + "." : target.isPresentComponent() ? "" : node.getSymbol().getFullName() + ".") + target.getPort());

        BoolExpr targetPortDefined = portDeclared.getOrDefault((target.isPresentComponentSymbol() ? target.getComponentSymbol().getFullName() : target.isPresentComponent() ? "" : node.getSymbol().getFullName()), Map.of()).getOrDefault(targetName, ctx.mkFalse());

        BoolExpr targetExists;
        if (target.isPresentComponentSymbol()) {
          targetName = target.getComponentSymbol().getFullName();
          targetExists = componentExistsVars.getOrDefault(targetName, ctx.mkFalse());
        } else {
          targetExists = portExistsVars.getOrDefault(targetName, ctx.mkFalse());
        }

        // Checking for the target port
        List<BoolExpr> targetPortExpressionList = new ArrayList<>();
        targetPortExpressionList.add(ctx.mkAnd(condition, featureConstraints, ctx.mkNot(targetExists)));
        boolean missingTargetElementDetected = false;

        if (ExpressionSolverService.solve(targetPortExpressionList) == Status.SATISFIABLE) {
          if (!target.isPresentComponent()) {
            Log.error(ArcError.MISSING_PORT.format(target.getQName()),
              target.get_SourcePositionStart(), target.get_SourcePositionEnd()
            );
          } else {
            Log.error(ArcError.MISSING_SUBCOMPONENT.format(target.getComponent()),
              target.get_SourcePositionStart(), target.get_SourcePositionEnd());
          }
          missingTargetElementDetected = true;
        }

        if (missingTargetElementDetected)
          continue;

        targetPortExpressionList.clear();
        targetPortExpressionList.add(ctx.mkAnd(condition, featureConstraints, srcExists, ctx.mkNot(targetPortDefined)));

        if (ExpressionSolverService.solve(targetPortExpressionList) == Status.SATISFIABLE) {

          Log.error(ArcError.MISSING_PORT.format(target.getQName()),
            target.get_SourcePositionStart(), target.get_SourcePositionEnd());
        }
      }
    }

  }
}
