/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcbasis._ast.ASTArcComponentType;
import arcbasis._ast.ASTArcPort;
import arcbasis._ast.ASTComponentInstance;
import arcbasis._ast.ASTComponentInstantiation;
import arcbasis._ast.ASTComponentInstantiationTOP;
import arcbasis._ast.ASTComponentInterface;
import arcbasis._ast.ASTConnector;
import arcbasis._ast.ASTPortAccess;
import arcbasis._ast.ASTPortDeclarationTOP;
import arcbasis._cocos.ArcBasisASTArcComponentTypeCoCo;
import arcbasis._cocos.ConnectorDirectionsFit;
import arcbasis._symboltable.IArcBasisScope;
import com.google.common.base.Preconditions;
import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Status;
import de.monticore.symbols.compsymbols._symboltable.ComponentTypeSymbol;
import de.monticore.symbols.compsymbols._symboltable.PortSymbol;
import de.monticore.symboltable.IScopeSpanningSymbol;
import de.monticore.symboltable.resolving.ResolvedSeveralEntriesForSymbolException;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeObscure;
import de.monticore.types3.SymTypeRelations;
import de.se_rwth.commons.logging.Log;
import montiarc._cocos.util.ExpressionBuildHelper;
import montiarc._cocos.util.ExpressionBuildHelper.PortInformation;
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
import java.util.Optional;
import java.util.stream.Collectors;

public class ConnectorTypesFit4Family implements ArcBasisASTArcComponentTypeCoCo {

  protected static void logInfoThatCoCoIsNotChecked4TargetPort(@NotNull ASTPortAccess targetPort) {
    Preconditions.checkNotNull(targetPort);
    Log.debug(() -> String.format("Will not check CoCo on port '%s' at '%s' a its symbol does not " +
        "seem to exist or the type of the port does not seem to be set.", targetPort.getQName(),
      targetPort.get_SourcePositionStart()), ConnectorDirectionsFit.class.getSimpleName()
    );
  }

  public void check(@NotNull ASTArcComponentType node) {
    Preconditions.checkNotNull(node);


    ExpressionSolver expSolver = ExpressionSolverService.getExpressionSolver();
    Context ctx = ExpressionSolverService.getContext();

    List<String> allFeatures;
    List<ExpressionSet> allConstraints;
    List<PortSymbol> createdPortSymbols;
    Map<PortInformation, BoolExpr> portInfoConditions;
    Map<ASTConnector, BoolExpr> connectorConditions;
    Map<String, List<String>> portNameVariations;
    Map<ASTComponentInstance, BoolExpr> subcomponentCondition = new HashMap<>();

    // Reading and processing parts of the Main-Component
    List<String> mainFeatures = node.getBody().getArcElementList().stream().filter(e -> e instanceof ASTArcFeatureDeclaration).map(v -> ((ASTArcFeatureDeclaration) v)).map(ASTArcFeatureDeclaration::getArcFeatureList).flatMap(List::stream).map(e -> node.getSymbol().getFullName() + "." + e.getSymbol().getName()).collect(Collectors.toList());

    // Getting all features, constraints, ports and connectors from the Main-Component
    allFeatures = new ArrayList<>(mainFeatures);
    allConstraints = new ArrayList<>();
    createdPortSymbols = new ArrayList<>();
    portInfoConditions = new HashMap<>();
    connectorConditions = new HashMap<>();
    portNameVariations = new HashMap<>();

    if (node instanceof ASTVariableArcFullVariantComponentType) {
      ExpressionSet mainConstraintSet = ((IVariableArcComponentTypeSymbol) ((ASTVariableArcFullVariantComponentType) node).getOriginal().getSymbol()).getConstraints();
      allConstraints.add(mainConstraintSet);

      Map<ASTArcPort, BoolExpr> portConditions = ((ASTVariableArcFullVariantComponentType) node).getPortConditions();
      for (Map.Entry<ASTArcPort, BoolExpr> portEntry : portConditions.entrySet()) {
        portInfoConditions.put(new PortInformation(portEntry.getKey().getName(), portEntry.getKey().getSymbol()), portEntry.getValue());
      }
      connectorConditions = ((ASTVariableArcFullVariantComponentType) node).getConnectorConditions();
      subcomponentCondition = ((ASTVariableArcFullVariantComponentType) node).getSubcomponentConditions();
    } else {
      ExpressionSet mainConstraintSet = ((IVariableArcComponentTypeSymbol) node.getSymbol()).getConstraints();
      allConstraints.add(mainConstraintSet);
      List<ASTArcPort> mainPorts = node.getBody().getArcElementList().stream().filter(e -> e instanceof ASTComponentInterface).map(v -> ((ASTComponentInterface) v).getPortDeclarationList()).flatMap(List::stream).map(ASTPortDeclarationTOP::getArcPortList).flatMap(List::stream).collect(Collectors.toList());
      List<ASTConnector> mainConnectors = node.getBody().getArcElementList().stream().filter(e -> e instanceof ASTConnector).map(v -> ((ASTConnector) v)).collect(Collectors.toList());

      List<ASTComponentInstance> mainSubComps = node.getBody().getArcElementList().stream().filter(e -> e instanceof ASTComponentInstantiation).map(l -> (ASTComponentInstantiation) l).map(ASTComponentInstantiationTOP::getComponentInstanceList).flatMap(List::stream).collect(Collectors.toList());

      for (ASTComponentInstance subComp : mainSubComps) {
        subcomponentCondition.put(subComp, ctx.mkTrue());
      }

      for (ASTArcPort port : mainPorts) {
        portInfoConditions.put(new PortInformation(port.getName(), port.getSymbol()), ctx.mkTrue());
      }

      for (ASTConnector connector : mainConnectors) {
        connectorConditions.put(connector, ctx.mkTrue());
      }
    }

    // Adding Constraints
    BoolExpr featureConstraints = VariationConditionHelper.getFeatureConstraints(node, allConstraints, allFeatures, expSolver);

    for (Map.Entry<ASTComponentInstance, BoolExpr> subEntry : subcomponentCondition.entrySet()) {

      if(!subEntry.getKey().getSymbol().isTypePresent())
        continue;

      var compSymbol = (ComponentTypeSymbol)subEntry.getKey().getSymbol().getType().getTypeInfo();
      var subPorts = compSymbol.getPorts();

      for (PortSymbol port : subPorts) {

        portInfoConditions.put(new PortInformation(subEntry.getKey().getSymbol().getName() + "." + port.getName(), port,subEntry.getKey().getSymbol()), subcomponentCondition.get(subEntry.getKey()));
      }
    }

    for (Map.Entry<ASTConnector, BoolExpr> connectorEntry : connectorConditions.entrySet()) {


      // Step 1: Check if connector can be active
      List<BoolExpr> connectorActiveExpressionList = new ArrayList<>();
      connectorActiveExpressionList.add(featureConstraints);
      connectorActiveExpressionList.add(connectorEntry.getValue());
      var connectorSatisfied = ExpressionSolverService.solve(connectorActiveExpressionList);
      if (connectorSatisfied == Status.UNSATISFIABLE)
        continue;

      // Step 2: Check which port-combination is possible
      var portNames = ExpressionBuildHelper.getPortNames(connectorEntry.getKey());
      Map<String, Long> portCount = portInfoConditions.entrySet().stream().filter(e -> portNames.contains(e.getKey().getPortName())).collect(Collectors.groupingBy(port -> port.getKey().getPortName(), Collectors.counting()));
      boolean multiplePortsExist = portCount.entrySet().stream().anyMatch(e -> e.getValue() > 1);
      boolean genericPorts = !portInfoConditions.entrySet().stream().filter(e -> portNames.contains(e.getKey().getPortName())).filter(e -> e.getKey().getSubcomponentSymbol() != null ?  e.getKey().getSubcomponentSymbol().getType().isGenericComponentType() : false).map(k -> k).collect(Collectors.toList()).isEmpty();
      List<ASTConnector> connectorsToCheck = new ArrayList<>();
      List<PortInformation> connectorPorts = new ArrayList<>();


      // If multiple ports with the same name exist or generic ports, then check and create possible connectors
       if (multiplePortsExist||genericPorts) {
        List<Map.Entry<ExpressionBuildHelper.PortInformation, BoolExpr>> possiblePorts;
        for (String portName : portNames) {
          possiblePorts = portInfoConditions.entrySet().stream().filter(e -> e.getKey().getPortName().equals(portName)).collect(Collectors.toList());
          for (Map.Entry<PortInformation, BoolExpr> entry : possiblePorts) {
            List<BoolExpr> possibleConnectorExpressionList = new ArrayList<>(List.of(featureConstraints, connectorEntry.getValue(), entry.getValue()));

            if (ExpressionSolverService.solve(possibleConnectorExpressionList) == Status.SATISFIABLE) {
              connectorPorts.add(entry.getKey());
            }
          }
        }

         ExpressionBuildHelper.setScope((IArcBasisScope) connectorPorts.get(0).getArcPort().getEnclosingScope());
          for (PortInformation connectorPort : connectorPorts) {
            Optional.ofNullable(ExpressionBuildHelper.createPortSymbolForName(connectorPort, portNameVariations)).ifPresent( createdPortSymbols::add);
          }
          connectorsToCheck.addAll(ExpressionBuildHelper.createPossibleConnectors(connectorEntry.getKey(), portNameVariations));

      } else {
        connectorsToCheck.add(connectorEntry.getKey());
      }

      for (ASTConnector connector : connectorsToCheck) {
        performTypeCheck(connector, createdPortSymbols);
      }

      // Remove created Portsymbols from the scope of the connector
      for (PortSymbol portSymbol : createdPortSymbols) {
          connectorPorts.stream().filter(e -> e.getArcPort().getFullName().equals(portSymbol.getFullName())).forEach(l -> l.getArcPort().getEnclosingScope().remove(portSymbol));
          ExpressionBuildHelper.getScope().remove(portSymbol);
          connectorEntry.getKey().getEnclosingScope().remove(portSymbol);
      }

      createdPortSymbols = new  ArrayList<>();
      portNameVariations = new HashMap<>();
      ExpressionBuildHelper.setScope(null);
    }
  }

  private void performTypeCheck(ASTConnector conn, List<PortSymbol> createdSymbols) {
    Optional<SymTypeExpression> symTypeOfSource = getTypeOfPortIfPresent(conn.getSource(), createdSymbols);
    if (symTypeOfSource.isEmpty()) {
      Log.debug(() -> String.format("Skip coco check, cannot resolve source port '%s'", conn.getSource().getQName()),
        this.getClass().getCanonicalName());
      return;
    }

    for (ASTPortAccess target : conn.getTargetList()) {
      Optional<SymTypeExpression> symTypeOfTarget = getTypeOfPortIfPresent(target, createdSymbols);
      if (symTypeOfTarget.isPresent()) {
        SymTypeExpression sourceType = symTypeOfSource.get();
        SymTypeExpression targetType = symTypeOfTarget.get();

        // Skip type checking if either source or target is Obscure
        if (sourceType instanceof SymTypeObscure || targetType instanceof SymTypeObscure) {
          Log.debug("Skip type check: One or both port types are 'Obscure'.", this.getClass().getSimpleName());
          continue;
        }

        // Perform type check
        try {
          if (!SymTypeRelations.isCompatible(targetType, sourceType)) {

            Log.error(
              ArcError.CONNECTOR_TYPE_MISMATCH.format(
                targetType.print(), sourceType.print()),
              target.get_SourcePositionStart(),
              target.get_SourcePositionEnd()
            );
          }
        } catch (ResolvedSeveralEntriesForSymbolException e) {
          Log.error(
            ArcError.CONNECTOR_TYPE_MISMATCH.format(
              targetType.print(), sourceType.print()),
            target.get_SourcePositionStart(),
            target.get_SourcePositionEnd()
          );
        }
      } else {
        logInfoThatCoCoIsNotChecked4TargetPort(target);
      }
    }
  }

  protected Optional<SymTypeExpression> getTypeOfPortIfPresent(@NotNull ASTPortAccess astPort, List<PortSymbol> createdSymbols) {
    Preconditions.checkNotNull(astPort);
    if (astPort.getEnclosingScope() == null)
      astPort.setEnclosingScope(ExpressionBuildHelper.getScope());

    if (astPort.isPresentPortSymbol()) {
      if (createdSymbols.contains(astPort.getPortSymbol())) {
        return Optional.of(astPort.getPortSymbol().getType());
      }
    }
    if (astPort.isPresentComponent()) {
      if (astPort.isPresentComponentSymbol() && astPort.getComponentSymbol().isTypePresent()) {
        return astPort.getComponentSymbol().getType().getTypeOfPort(astPort.getPort());
      }
    } else if (getEnclosingComponent(astPort).isPresent()) {
      return getEnclosingComponent(astPort).get().getPort(astPort.getPort()).map(PortSymbol::getType);
    } else if (astPort.isPresentPortSymbol() && astPort.getPortSymbol().isTypePresent()) {
      return Optional.ofNullable(astPort.getPortSymbol().getType());
    }
    return Optional.empty();
  }

  /**
   * @return an {@code Optional} of the component type this portAccess belongs to. The {@code Optional} is empty if the access
   * does not belong to a component type.
   */
  protected Optional<ComponentTypeSymbol> getEnclosingComponent(@NotNull ASTPortAccess portAccess) {
    Preconditions.checkNotNull(portAccess);
    if (portAccess.getEnclosingScope() == null) {
      return Optional.empty();
    }
    if (!portAccess.getEnclosingScope().isPresentSpanningSymbol()) {
      return Optional.empty();
    }
    IScopeSpanningSymbol symbol = portAccess.getEnclosingScope().getSpanningSymbol();
    if (symbol instanceof ComponentTypeSymbol) {
      return Optional.of((ComponentTypeSymbol) symbol);
    } else {
      return Optional.empty();
    }
  }
}
