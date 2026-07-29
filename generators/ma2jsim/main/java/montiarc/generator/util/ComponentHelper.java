/* (c) https://github.com/MontiCore/monticore */
package montiarc.generator.util;

import arcbasis._ast.ASTArcArgument;
import arcbasis._ast.ASTArcComponentType;
import arcbasis._ast.ASTComponentBody;
import arcbasis._ast.ASTComponentInstance;
import arcbasis._ast.ASTConnector;
import arcbasis._ast.ASTPortAccess;
import arcbasis._visitor.NameCollectorVisitor;
import com.google.common.base.Preconditions;
import de.monticore.expressions.expressionsbasis.ExpressionsBasisMill;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisTraverser;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbolTOP;
import de.monticore.symbols.compsymbols._symboltable.ComponentTypeSymbol;
import de.monticore.symbols.compsymbols._symboltable.PortSymbol;
import de.monticore.symbols.compsymbols._symboltable.Timing;
import de.monticore.symboltable.IScopeSpanningSymbol;
import de.monticore.types.check.SymTypeExpression;
import modes._ast.ASTArcMode;
import modes._ast.ASTModeAutomaton;
import montiarc.MontiArcMill;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc._ast.ASTArcConstraintDeclaration;
import variablearc._ast.ASTArcFeatureDeclaration;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class ComponentHelper {

  public boolean isSync(PortSymbol portSymbol) {
    return portSymbol.getTiming().matches(Timing.TIMED_SYNC);
  }

  public boolean isMsgEventPort(PortSymbol portSymbol) {
    return portSymbol.getTiming().matches(Timing.TIMED)
      || portSymbol.getTiming().matches(Timing.UNTIMED);
  }

  public List<PortSymbol> getSyncedInPortsOf(ComponentTypeSymbol comp) {
    return comp.getAllIncomingPorts().stream()
      .filter(this::isSync)
      .collect(Collectors.toList());
  }

  public List<PortSymbol> getMsgEventInPortsOf(ComponentTypeSymbol comp) {
    return comp.getAllIncomingPorts().stream()
      .filter(this::isMsgEventPort)
      .collect(Collectors.toList());
  }

  public Optional<ASTModeAutomaton> getModeAutomaton(ASTArcComponentType ast) {
    return getModeAutomaton(ast.getBody());
  }

  public Optional<ASTModeAutomaton> getModeAutomaton(ASTComponentBody ast) {
    return ast.streamArcElementsOfType(ASTModeAutomaton.class).findFirst();
  }

  public List<PortSymbol> getUnconnectedOutPortsWithoutModes(ComponentTypeSymbol comp) {
    Set<String> targets = ((ASTArcComponentType) comp.getAstNode()).getConnectors().stream()
      .map(ASTConnector::getTargetsNames)
      .flatMap(Collection::stream)
      .collect(Collectors.toSet());

    return comp.getAllOutgoingPorts().stream()
      .filter(p -> !targets.contains(p.getName()))
      .collect(Collectors.toList());
  }

  public List<PortSymbol> getUnconnectedOutPortsIncludingMode(ComponentTypeSymbol comp, ASTArcMode mode) {
    Set<String> classicalTargets = ((ASTArcComponentType) comp.getAstNode()).getConnectors().stream()
      .map(ASTConnector::getTargetsNames)
      .flatMap(Collection::stream)
      .collect(Collectors.toSet());

    Set<String> modeConnectorTargets = mode.getBody().streamArcElementsOfType(ASTConnector.class)
      .map(ASTConnector::getTargetsNames)
      .flatMap(Collection::stream)
      .collect(Collectors.toSet());

    return comp.getAllOutgoingPorts().stream()
      .filter(p -> !classicalTargets.contains(p.getName()) && !modeConnectorTargets.contains(p.getName()))
      .collect(Collectors.toList());
  }

  public boolean isComponentInputTimeAware(ASTArcComponentType component) {
    return component.getSymbol()
      .getAllIncomingPorts().stream().findAny()
      .map(p -> p.getTiming() != Timing.UNTIMED).orElse(false);
  }

  public boolean isComponentOutputTimeAware(ASTArcComponentType component) {
    return component.getSymbol()
      .getAllOutgoingPorts().stream().findAny()
      .map(p -> p.getTiming() != Timing.UNTIMED).orElse(false);
  }

  public Map<String, ASTExpression> getArgNamesMappedToExpressions(ASTComponentInstance instance) {
    if (!instance.isPresentArcArguments()) return new HashMap<>();

    ComponentTypeSymbol type = instance.getSymbol().getType().getTypeInfo();

    List<String> unsetParams = type.getParameterList().stream()
      .map(VariableSymbolTOP::getName).collect(Collectors.toList());

    Map<String, ASTExpression> result = new HashMap<>();
    instance.getArcArguments().streamArcArguments()
      .filter(ASTArcArgument::isPresentName)
      .forEach(arg -> {
        unsetParams.remove(arg.getName());
        result.put(arg.getName(), arg.getExpression());
      });
    instance.getArcArguments().forEachArcArguments(arg -> {
      if (!arg.isPresentName()) {
        result.put(unsetParams.remove(0), arg.getExpression());
      }
    });
    return result;
  }

  public List<?> getFeatures(@NotNull ASTArcComponentType ast) {
    Preconditions.checkNotNull(ast);

    return ast.getBody()
      .streamArcElementsOfType(ASTArcFeatureDeclaration.class)
      .flatMap(ASTArcFeatureDeclaration::streamArcFeatures)
      .collect(Collectors.toList());
  }

  public List<?> getConstraintExpressions(@NotNull ASTArcComponentType ast) {
    Preconditions.checkNotNull(ast);

    return ast.getBody()
      .streamArcElementsOfType(ASTArcConstraintDeclaration.class)
      .map(ASTArcConstraintDeclaration::getExpression)
      .collect(Collectors.toList());
  }

  public List<?> getNonPrimitiveParameters(@NotNull ASTArcComponentType ast) {
    return ast.getHead().streamArcParameters().filter(param -> !param.getSymbol().getType().isPrimitive()).collect(Collectors.toList());
  }

  public boolean isGenericComponent(ASTArcComponentType astComponentType) {
    return astComponentType.getHead().isPresentTypeParameters();
  }

  public ASTExpression getInitialForVariable(VariableSymbol variableSymbol) {
    if (variableSymbol.isPresentAstNode() && MontiArcMill.typeDispatcher().isArcBasisASTArcField(variableSymbol.getAstNode()))
      return MontiArcMill.typeDispatcher().asArcBasisASTArcField(variableSymbol.getAstNode()).getInitial();
    return MontiArcMill.literalExpressionBuilder().setLiteral(MontiArcMill.nullLiteralBuilder().build()).build();
  }

  /**
   * When we can find a port symbol that fits the given port access and we when its type has already been set then we
   * return the type. When we can not find the port symbol or when we do not find the component instance of the port
   * access or the type of that instance or if we do not find the type of the port then we return an empty Optional.
   */
  public Optional<SymTypeExpression> getTypeOfPortIfPresent(@NotNull ASTPortAccess astPort) {
    Preconditions.checkNotNull(astPort);

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

  /**
   * Returns the list of fields in an order such that every field’s initializer
   * only refers to fields that have already been initialized.
   * <p>
   * We build a directed dependency graph from each field to the other fields
   * it references in its initializer, then perform a topological sort (Kahn’s
   * algorithm).  Any fields involved in cycles are simply appended in their
   * original declaration order at the end.
   *
   * @param ast the component AST whose fields we want to order
   * @return a List of VariableSymbol in an order respecting dependencies
   */
  public List<VariableSymbol> getFieldsInDependencyOrder(ASTArcComponentType ast) {
    List<VariableSymbol> fields = ast.getSymbol().getFields();
    Map<VariableSymbol, Set<VariableSymbol>> deps = new LinkedHashMap<>();

    for (VariableSymbol f : fields) {
      ASTExpression initExpr = getInitialForVariable(f);
      ExpressionsBasisTraverser traverser = ExpressionsBasisMill.traverser();
      NameCollectorVisitor nameCollector = new NameCollectorVisitor();
      traverser.add4ExpressionsBasis(nameCollector);
      initExpr.accept(traverser);

      Set<String> usedNames = nameCollector.getNames();
      Set<VariableSymbol> usedFields = fields.stream()
        .filter(g -> !g.equals(f) && usedNames.contains(g.getName()))
        .collect(Collectors.toSet());
      deps.put(f, usedFields);
    }

    Map<VariableSymbol, List<VariableSymbol>> rev = new HashMap<>();
    for (VariableSymbol f : fields) {
      rev.put(f, new ArrayList<>());
    }
    for (Map.Entry<VariableSymbol, Set<VariableSymbol>> e : deps.entrySet()) {
      for (VariableSymbol g : e.getValue()) {
        rev.get(g).add(e.getKey());
      }
    }

    Map<VariableSymbol, Integer> inDeg = new HashMap<>();
    for (Map.Entry<VariableSymbol, Set<VariableSymbol>> e : deps.entrySet()) {
      inDeg.put(e.getKey(), e.getValue().size());
    }

    Deque<VariableSymbol> queue = new ArrayDeque<>();
    for (Map.Entry<VariableSymbol, Integer> e : inDeg.entrySet()) {
      if (e.getValue() == 0) {
        queue.add(e.getKey());
      }
    }

    List<VariableSymbol> sorted = new ArrayList<>();
    while (!queue.isEmpty()) {
      VariableSymbol u = queue.remove();
      sorted.add(u);
      for (VariableSymbol child : rev.get(u)) {
        int d2 = inDeg.get(child) - 1;
        inDeg.put(child, d2);
        if (d2 == 0) {
          queue.add(child);
        }
      }
    }

    for (VariableSymbol f : fields) {
      if (!sorted.contains(f)) {
        sorted.add(f);
      }
    }

    return sorted;
  }

  public String getOracleFactory(ASTArcComponentType ast) {
    if (ast.isPresentStereotype() && ast.getStereotype().contains("oracle")) {
      switch (ast.getStereotype().getValue("oracle")) {
        case "first":
          return "montiarc.rte.oracle.OracleFactory.preferFirst()";
        case "last":
          return "montiarc.rte.oracle.OracleFactory.preferLast()";
        case "lowestHash":
          return "montiarc.rte.oracle.OracleFactory.lowestHash()";
        case "random":
          return "montiarc.rte.oracle.OracleFactory.random()";
        case "leastUsed":
          return "montiarc.rte.oracle.OracleFactory.preferLeastUsed(montiarc.rte.oracle.OracleFactory.preferFirst())";
        case "unexplored":
          return "montiarc.rte.oracle.OracleFactory.preferUnexplored(montiarc.rte.oracle.OracleFactory.preferFirst())";
      }
    }
    return "montiarc.rte.oracle.OracleFactory.lowestHash()";
  }
}
