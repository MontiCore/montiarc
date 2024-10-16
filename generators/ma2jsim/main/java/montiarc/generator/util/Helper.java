/* (c) https://github.com/MontiCore/monticore */
package montiarc.generator.util;

import arcautomaton.ArcAutomatonMill;
import arcautomaton._ast.ASTArcStatechart;
import arcautomaton._ast.ASTMsgEvent;
import arcautomaton._symboltable.Port2EventDefAdapter;
import arcbasis._ast.ASTArcArgument;
import arcbasis._ast.ASTComponentBody;
import arcbasis._ast.ASTComponentInstance;
import arcbasis._ast.ASTComponentInstantiation;
import arcbasis._ast.ASTComponentInstantiationTOP;
import arcbasis._ast.ASTComponentType;
import arcbasis._ast.ASTConnector;
import arcbasis._ast.ASTPortAccess;
import arcbasis._symboltable.ComponentTypeSymbol;
import arccompute._ast.ASTArcCompute;
import arccompute._ast.ASTArcInit;
import com.google.common.base.Preconditions;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.scactions._ast.ASTSCABody;
import de.monticore.scbasis._ast.ASTSCState;
import de.monticore.scbasis._ast.ASTSCStateElement;
import de.monticore.scbasis._ast.ASTSCTransition;
import de.monticore.scstatehierarchy._ast.ASTSCHierarchyBody;
import de.monticore.sctransitions4code._ast.ASTTransitionAction;
import de.monticore.sctransitions4code._ast.ASTTransitionBody;
import de.monticore.statements.mcstatementsbasis._ast.ASTMCBlockStatement;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbolTOP;
import de.monticore.symbols.compsymbols._symboltable.ComponentSymbol;
import de.monticore.symbols.compsymbols._symboltable.PortSymbol;
import de.monticore.symbols.compsymbols._symboltable.SubcomponentSymbol;
import de.monticore.symbols.compsymbols._symboltable.Timing;
import de.monticore.symboltable.ISymbol;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypePrimitive;
import genericarc._ast.ASTGenericComponentHead;
import modes._ast.ASTArcMode;
import modes._ast.ASTModeAutomaton;
import montiarc.MontiArcMill;
import montiarc._symboltable.MontiArcComponentTypeSymbol;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc._ast.ASTArcConstraintDeclaration;
import variablearc._ast.ASTArcFeatureDeclaration;
import variablearc._symboltable.ArcFeatureSymbol;
import variablearc._symboltable.IVariableArcComponentTypeSymbol;
import variablearc._symboltable.VariableArcVariantComponentTypeSymbol;
import variablearc._symboltable.VariableArcVariationPoint;
import variablearc._symboltable.VariantPortSymbol;
import variablearc._symboltable.VariantSubcomponentSymbol;
import variablearc.evaluation.expressions.Expression;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SuppressWarnings("unused")
public class Helper {

  public List<Object> asList(Object... args) {
    return List.of(args);
  }

  public <T> List<T> streamToList(Stream<T> stream) {
    return stream.collect(Collectors.toList());
  }

  public Optional<ASTArcStatechart> getAutomatonBehavior(ASTComponentType component) {
    Preconditions.checkNotNull(component);

    return component.getBody().streamArcElementsOfType(ASTArcStatechart.class).findFirst();
  }

  public Optional<ASTArcCompute> getComputeBehavior(ASTComponentType component) {
    Preconditions.checkNotNull(component);

    return component.getBody().streamArcElementsOfType(ASTArcCompute.class).findFirst();
  }

  public Optional<ASTArcInit> getComputeInit(ASTComponentType component) {
    Preconditions.checkNotNull(component);

    return component.getBody().streamArcElementsOfType(ASTArcInit.class).findFirst();
  }

  /**
   * Get all transitions from the given statechart that are triggered by a message stimulus.
   * Messages are grouped by triggering port event name.
   *
   * @param sc the statechart from which transitions should be extracted
   * @return event-triggered transitions grouped by triggering port
   */
  public Map<PortSymbol, List<ASTSCTransition>> getTransitionsForPortEvents(ASTArcStatechart sc) {
    return getTransitionsMappedToPortTriggers(sc.streamTransitions());
  }

  public Map<PortSymbol, List<ASTSCTransition>> getTransitionsForPortEvents(ASTModeAutomaton modeAutomaton) {
    return getTransitionsMappedToPortTriggers(getTransitions(modeAutomaton).stream());
  }

  protected Map<PortSymbol, List<ASTSCTransition>> getTransitionsMappedToPortTriggers(Stream<ASTSCTransition> transitions) {
    Map<PortSymbol, List<ASTSCTransition>> result = new HashMap<>();
    transitions.forEach(tr -> {
      Optional<ASTTransitionBody> body = getASTTransitionBody(tr);
      if (body.isEmpty()) return;
      Optional<PortSymbol> trigger = getTriggeringPortName(body.get());
      if (trigger.isEmpty()) return;
      if (result.containsKey(trigger.get())) result.get(trigger.get()).add(tr);
      else {
        ArrayList<ASTSCTransition> l = new ArrayList<>();
        l.add(tr);
        result.put(trigger.get(), l);
      }
    });
    return result;
  }

  public List<ASTSCTransition> filterTransitionsForSourceMode(List<ASTSCTransition> transitions, String srcMode) {
    return transitions.stream()
      .filter(tr -> tr.getSourceName().equals(srcMode))
      .collect(Collectors.toList());
  }

  /**
   * Get all transitions from the given statechart that are triggered by a tick event.
   *
   * @param sc the statechart from which transitions should be extracted
   * @return all transitions triggered by a tick event
   */
  public List<ASTSCTransition> getTransitionsForTickEvent(ASTArcStatechart sc) {
    return filterTransitionsForTickTrigger(sc.streamTransitions());
  }

  public List<ASTSCTransition> getTransitionsForTickEvent(ASTModeAutomaton modeAutomaton) {
    return filterTransitionsForTickTrigger(getTransitions(modeAutomaton).stream());
  }

  public List<ASTSCTransition> filterTransitionsForTickTrigger(Stream<ASTSCTransition> transitions) {
    ArrayList<ASTSCTransition> result = new ArrayList<>();
    transitions.forEach(tr -> {
      Optional<ASTTransitionBody> body = getASTTransitionBody(tr);
      if (body.isEmpty() || !body.get().isPresentSCEvent()) return;
      if (!(body.get().getSCEvent() instanceof ASTMsgEvent)) return;
      if (ArcAutomatonMill.TICK.equals(((ASTMsgEvent) body.get().getSCEvent()).getName()))
        result.add(tr);
    });
    return result;
  }

  public List<ASTSCTransition> getTransitionsForTickEventFromState(ASTModeAutomaton modeAutomaton, ASTArcMode srcMode) {
    Preconditions.checkArgument(srcMode.isPresentSymbol());
    return getTransitionsForTickEvent(modeAutomaton).stream()
      .filter(tr -> tr.getSourceNameSymbol().equals(srcMode.getSymbol()))
      .collect(Collectors.toList());
  }

  public Optional<ASTExpression> getGuard(ASTSCTransition transition) {
    if (transition.getSCTBody() instanceof ASTTransitionBody) {
      ASTTransitionBody body = (ASTTransitionBody) transition.getSCTBody();
      return body.isPresentPre() ? Optional.of(body.getPre()) : Optional.empty();
    } else {
      return Optional.empty();
    }
  }

  /**
   * Get all transitions from the given statechart that are not triggered by any event.
   *
   * @param sc the statechart from which transitions should be extracted
   * @return all transitions without triggers
   */
  public List<ASTSCTransition> getTransitionsWithoutEvent(ASTArcStatechart sc) {
    return filterTransitionsWithoutTrigger(sc.streamTransitions());
  }

  public List<ASTSCTransition> getTransitionsWithoutEvent(ASTModeAutomaton modeAutomaton) {
    return filterTransitionsWithoutTrigger(getTransitions(modeAutomaton).stream());
  }

  public List<ASTSCTransition> filterTransitionsWithoutTrigger(Stream<ASTSCTransition> transitions) {
    ArrayList<ASTSCTransition> result = new ArrayList<>();
    transitions.forEach(tr -> {
      Optional<ASTTransitionBody> body = getASTTransitionBody(tr);
      if (body.isEmpty() || !body.get().isPresentSCEvent()) {
        result.add(tr);
      }
    });
    return result;
  }

  public Optional<ASTTransitionBody> getASTTransitionBody(ASTSCTransition transition) {
    if (transition.getSCTBody() instanceof ASTTransitionBody) {
      return Optional.of((ASTTransitionBody) transition.getSCTBody());
    }
    return Optional.empty();
  }

  public Optional<PortSymbol> getTriggeringPortName(ASTTransitionBody body) {
    return Optional.of(body)
      .filter(ASTTransitionBody::isPresentSCEvent)
      .filter(bdy -> bdy.getSCEvent() instanceof ASTMsgEvent)
      .map(bdy -> (ASTMsgEvent) bdy.getSCEvent())
      .filter(event -> !ArcAutomatonMill.TICK.equals(event.getName()))
      .map(ASTMsgEvent::getEventSymbol)
      .filter(sym -> sym instanceof Port2EventDefAdapter)
      .map(sym -> ((Port2EventDefAdapter) sym).getAdaptee());
  }

  public List<PortSymbol> getInPortsNotTriggeringAnyTransition(ASTArcStatechart sc, ASTComponentType comp) {
    List<String> triggeringPorts = getTransitionsForPortEvents(sc).keySet().stream()
      .map(ISymbol::getName)
      .map(String::toLowerCase).collect(Collectors.toList());
    return comp.getSymbol().getAllIncomingPorts().stream()
      .filter(p -> !triggeringPorts.contains(p.getName().toLowerCase()))
      .collect(Collectors.toList());
  }

  public List<PortSymbol> getInPortsNotTriggeringAnyTransition(ASTModeAutomaton sc, ASTComponentType comp) {
    List<String> triggeringPorts = getTransitionsForPortEvents(sc).keySet().stream()
      .map(ISymbol::getName)
      .map(String::toLowerCase).collect(Collectors.toList());
    return comp.getSymbol().getAllIncomingPorts().stream()
      .filter(p -> !triggeringPorts.contains(p.getName().toLowerCase()))
      .collect(Collectors.toList());
  }

  public boolean isEventBased(ASTArcStatechart sc) {
    return !sc.getTiming().matches(Timing.TIMED_SYNC);
  }

  public boolean isEventBased(ASTModeAutomaton modeAutomaton) {
    return !determineTiming(modeAutomaton).matches(Timing.TIMED_SYNC);
  }

  protected Timing determineTiming(ASTModeAutomaton modeAutomaton) {
    if (!modeAutomaton.isPresentStereotype()) return Timing.DEFAULT;
    return modeAutomaton.getStereotype().streamValues()
      .map(v -> Timing.of(v.getName()))
      .filter(Optional::isPresent)
      .map(Optional::get)
      .findFirst().orElse(Timing.DEFAULT);
  }

  public Optional<ASTModeAutomaton> getModeAutomaton(ASTComponentType ast) {
    return getModeAutomaton(ast.getBody());
  }

  public Optional<ASTModeAutomaton> getModeAutomaton(ASTComponentBody ast) {
    return ast.streamArcElementsOfType(ASTModeAutomaton.class).findFirst();
  }

  public List<ASTArcMode> getModes(ASTModeAutomaton ast) {
    return ast.getSCStatechartElementList().stream()
      .filter(e -> e instanceof ASTArcMode)
      .map(e -> (ASTArcMode) e)
      .collect(Collectors.toList());
  }

  public List<ASTArcMode> getInitialModes(ASTModeAutomaton ast) {
    return getModes(ast).stream().filter(m -> m.getSCModifier().isInitial()).collect(Collectors.toList());
  }

  public List<ASTSCTransition> getTransitions(ASTModeAutomaton ast) {
    return ast.getSCStatechartElementList().stream()
      .filter(e -> e instanceof ASTSCTransition)
      .map(e -> (ASTSCTransition) e)
      .collect(Collectors.toList());
  }

  public List<ASTComponentInstance> getInstancesFromMode(ASTArcMode ast) {
    return ast.getBody().getElementsOfType(ASTComponentInstantiation.class).stream()
      .flatMap(ASTComponentInstantiationTOP::streamComponentInstances)
      .collect(Collectors.toList());
  }

  public List<SubcomponentSymbol> getInstanceSymbolsFromMode(ASTArcMode ast) {
    return getInstancesFromMode(ast).stream()
      .map(ASTComponentInstance::getSymbol)
      .collect(Collectors.toList());
  }

  public Map<ASTArcMode, List<ASTComponentInstance>> getInstancesFromModes(ASTModeAutomaton ast) {
    return getModes(ast).stream().collect(
      Collectors.toMap(
        Function.identity(),
        this::getInstancesFromMode
      )
    );
  }

  public List<PortSymbol> getUnconnectedOutPortsWithoutModes(ComponentTypeSymbol comp) {
    Set<String> targets = comp.getAstNode().getConnectors().stream()
      .map(ASTConnector::getTargetsNames)
      .flatMap(Collection::stream)
      .collect(Collectors.toSet());

    return comp.getAllOutgoingPorts().stream()
      .filter(p -> !targets.contains(p.getName()))
      .collect(Collectors.toList());
  }

  public List<PortSymbol> getUnconnectedOutPortsIncludingMode(ComponentTypeSymbol comp, ASTArcMode mode) {
    Set<String> classicalTargets = comp.getAstNode().getConnectors().stream()
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

  public List<ASTConnector> getConnectors(ASTArcMode ast) {
    return ast.getBody().getElementsOfType(ASTConnector.class);
  }

  /**
   * Check if the component instance referenced in the given port access is defined within the given mode.
   */
  public boolean instanceInMode(ASTPortAccess portAccess, ASTArcMode mode) {
    if (!portAccess.isPresentComponentSymbol()) return false;
    return portAccess.getComponentSymbol().getEnclosingScope() == mode.getSpannedScope();
  }

  public boolean isComponentInputTimeAware(ASTComponentType component) {
    return component.getSymbol()
      .getAllIncomingPorts().stream().findAny()
      .map(p -> p.getTiming() != Timing.UNTIMED).orElse(false);
  }

  public boolean isComponentOutputTimeAware(ASTComponentType component) {
    return component.getSymbol()
      .getAllOutgoingPorts().stream().findAny()
      .map(p -> p.getTiming() != Timing.UNTIMED).orElse(false);
  }

  public Map<String, ASTExpression> getArgNamesMappedToExpressions(ASTComponentInstance instance) {
    if (!instance.isPresentArcArguments()) return new HashMap<>();

    ComponentSymbol type = instance.getSymbol().getType().getTypeInfo();

    List<String> unsetParams = type.getParameters().stream()
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

  public List<?> getFeatures(@NotNull ASTComponentType ast) {
    Preconditions.checkNotNull(ast);

    return ast.getBody()
      .streamArcElementsOfType(ASTArcFeatureDeclaration.class)
      .flatMap(ASTArcFeatureDeclaration::streamArcFeatures)
      .collect(Collectors.toList());
  }

  public List<?> getConstraintExpressions(@NotNull ASTComponentType ast) {
    Preconditions.checkNotNull(ast);

    return ast.getBody()
      .streamArcElementsOfType(ASTArcConstraintDeclaration.class)
      .map(ASTArcConstraintDeclaration::getExpression)
      .collect(Collectors.toList());
  }

  public List<?> getNonPrimitiveParameters(@NotNull ASTComponentType ast) {
    return ast.getHead().streamArcParameters().filter(param -> !param.getSymbol().getType().isPrimitive()).collect(Collectors.toList());
  }

  public boolean isGenericComponent(ASTComponentType astComponentType) {
    return astComponentType.getHead() instanceof ASTGenericComponentHead;
  }

  public ASTExpression getInitialForVariable(VariableSymbol variableSymbol) {
    if (variableSymbol.isPresentAstNode() && MontiArcMill.typeDispatcher().isArcBasisASTArcField(variableSymbol.getAstNode()))
      return MontiArcMill.typeDispatcher().asArcBasisASTArcField(variableSymbol.getAstNode()).getInitial();
    return MontiArcMill.literalExpressionBuilder().setLiteral(MontiArcMill.nullLiteralBuilder().build()).build();
  }

  public String getNullLikeValue(SymTypeExpression type) {
    if (type.isPrimitive()) {
      if (BasicSymbolsMill.BOOLEAN.equals(type.asPrimitive().getPrimitiveName()))
        return "false";
      if (BasicSymbolsMill.CHAR.equals(type.asPrimitive().getPrimitiveName())) {
        return "(char) 0";
      }
      else return "0";
    } else return "null";
  }

  /**
   * Same as {@link this#getNullLikeValue(SymTypeExpression)}, but also explicitly casts 0's to bytes and shorts
   * (i.e.: narrowing operations).
   */
  public String getNarrowedNullLikeValue(SymTypeExpression type) {
    if (type.isPrimitive()) {
      if (BasicSymbolsMill.BYTE.equals(type.asPrimitive().getPrimitiveName())) {
        return "(byte) 0";
      } else if (BasicSymbolsMill.SHORT.equals(type.asPrimitive().getPrimitiveName())) {
        return "(short) 0";
      } else if (BasicSymbolsMill.CHAR.equals(type.asPrimitive().getPrimitiveName())) {
        return "(char) 0";
      } else if (BasicSymbolsMill.BOOLEAN.equals(type.asPrimitive().getPrimitiveName())) {
        return "false";
      } else return "0";
    } else return "null";
  }

  public boolean isUnboxedBoolean(SymTypeExpression type) {
    return type.isPrimitive() && BasicSymbolsMill.BOOLEAN.equals(type.asPrimitive().getPrimitiveName());
  }

  public boolean isUnboxedChar(SymTypeExpression type) {
    return type.isPrimitive() && BasicSymbolsMill.CHAR.equals(type.asPrimitive().getPrimitiveName());
  }

  public boolean isUnboxedByte(SymTypeExpression type) {
    return type.isPrimitive() && BasicSymbolsMill.BYTE.equals(type.asPrimitive().getPrimitiveName());
  }

  public boolean isUnboxedShort(SymTypeExpression type) {
    return type.isPrimitive() && BasicSymbolsMill.SHORT.equals(type.asPrimitive().getPrimitiveName());
  }

  public boolean isUnboxedInt(SymTypeExpression type) {
    return type.isPrimitive() && BasicSymbolsMill.INT.equals(type.asPrimitive().getPrimitiveName());
  }

  public boolean isUnboxedLong(SymTypeExpression type) {
    return type.isPrimitive() && BasicSymbolsMill.LONG.equals(type.asPrimitive().getPrimitiveName());
  }

  public boolean isUnboxedFloat(SymTypeExpression type) {
    return type.isPrimitive() && BasicSymbolsMill.FLOAT.equals(type.asPrimitive().getPrimitiveName());
  }

  public boolean isUnboxedDouble(SymTypeExpression type) {
    return type.isPrimitive() && BasicSymbolsMill.DOUBLE.equals(type.asPrimitive().getPrimitiveName());
  }

  /**
   * Determines whether the type is a numeric primitive, but nor {@code char}.
   * <br>
   * I.e. whether the type is a {@code byte}, {@code short}, {@code int}, {@code long}, {@code float}, or {@code double}
   */
  public boolean isNumber(SymTypePrimitive type) {
    return type.isNumericType() && !this.isUnboxedChar(type);
  }

  public List<Expression> getExistenceCondition(@NotNull ASTComponentType ast, @NotNull ISymbol symbol) {
    if (ast.getSymbol() instanceof IVariableArcComponentTypeSymbol) {
      return ((IVariableArcComponentTypeSymbol) ast.getSymbol()).getAllVariationPoints()
        .stream()
        .filter(vp -> vp.containsSymbol(symbol))
        .findAny()
        .map(VariableArcVariationPoint::getAllConditions)
        .orElseGet(Collections::emptyList);
    }
    return Collections.emptyList();
  }

  public List<ASTConnector> getVariationPointConnectors(@NotNull VariableArcVariationPoint vp) {
    return vp.getArcElements().stream().filter(e -> MontiArcMill.typeDispatcher().isArcBasisASTConnector(e)).map(e -> MontiArcMill.typeDispatcher().asArcBasisASTConnector(e)).collect(Collectors.toList());
  }

  public List<VariableArcVariantComponentTypeSymbol> getVariants(@NotNull ASTComponentType ast) {
    if (ast.getSymbol() instanceof MontiArcComponentTypeSymbol) {
      return ((MontiArcComponentTypeSymbol) ast.getSymbol()).getVariableArcVariants();
    }
    return Collections.emptyList();
  }

  public Map<ArcFeatureSymbol, Boolean> getFeaturesMappedToBool(VariantSubcomponentSymbol subcomponent) {
    return ((VariableArcVariantComponentTypeSymbol) subcomponent.getType().getTypeInfo()).getFeatureSymbolBooleanMap();
  }

  public String variantSuffix(VariableArcVariantComponentTypeSymbol variant) {
    if (variant.getTypeSymbol().getVariableArcVariants().size() == 1) return "";
    return Integer.toString(variant.hashCode());
  }

  public String subcomponentVariantSuffix(ASTComponentType comp, SubcomponentSymbol subcomponent) {
    if (subcomponent instanceof VariantSubcomponentSymbol) {
      subcomponent = ((VariantSubcomponentSymbol) subcomponent).getOriginal();
    }
    List<SubcomponentSymbol> subs = ISymbol.sortSymbolsByPosition(comp.getSpannedScope().resolveSubcomponentMany(subcomponent.getName()));
    return subs.size() <= 1 ? "" : Integer.toString(subs.indexOf(subcomponent));
  }

  public String portVariantSuffix(ASTComponentType comp, PortSymbol port) {
    if (port instanceof VariantPortSymbol) {
      port = ((VariantPortSymbol) port).getOriginal();
    }
    List<PortSymbol> ports = ISymbol.sortSymbolsByPosition(comp.getSpannedScope().resolvePortMany(port.getName()));
    return ports.size() <= 1 ? "" : Integer.toString(ports.indexOf(port));
  }

  public String portVariantSuffix(SubcomponentSymbol sub, PortSymbol port) {
    if (port instanceof VariantPortSymbol) {
      port = ((VariantPortSymbol) port).getOriginal();
    }
    List<PortSymbol> ports = ISymbol.sortSymbolsByPosition(sub.getType().getTypeInfo().getSpannedScope().resolvePortMany(port.getName()));
    return ports.size() <= 1 ? "" : Integer.toString(ports.indexOf(port));
  }

  public String fieldVariantSuffix(ASTComponentType comp, VariableSymbol field) {
    List<VariableSymbol> fields = ISymbol.sortSymbolsByPosition(comp.getSpannedScope().resolveVariableMany(field.getName()));
    return fields.size() <= 1 ? "" : Integer.toString(fields.indexOf(field));
  }

  public List<VariableArcVariantComponentTypeSymbol> getVariantsWithPort(ASTComponentType comp, PortSymbol port) {
    List<VariableArcVariantComponentTypeSymbol> variants = getVariants(comp);
    List<VariableArcVariantComponentTypeSymbol> varsWithPort = new ArrayList<>(variants.size());

    for (VariableArcVariantComponentTypeSymbol variant : variants) {
      Collection<PortSymbol> allVariantPorts = new HashSet<>();
      allVariantPorts.addAll(variant.getAllIncomingPorts());
      allVariantPorts.addAll(variant.getAllOutgoingPorts());

      for (PortSymbol variantPort : allVariantPorts) {
        if (variantPort == port ||
          (variantPort instanceof VariantPortSymbol && ((VariantPortSymbol) variantPort).getOriginal() == port)
        ) {
          varsWithPort.add(variant);
        }
      }
    }

    return varsWithPort;
  }

  public List<VariableArcVariantComponentTypeSymbol> getVariantsWithSubcomponent(ASTComponentType comp, SubcomponentSymbol sub) {
    List<VariableArcVariantComponentTypeSymbol> variants = getVariants(comp);
    List<VariableArcVariantComponentTypeSymbol> varsWithSub = new ArrayList<>(variants.size());

    for (VariableArcVariantComponentTypeSymbol variant : variants) {
      for (SubcomponentSymbol variantSub : variant.getSubcomponents()) {
        if (variantSub == sub ||
          (variantSub instanceof VariantSubcomponentSymbol && ((VariantSubcomponentSymbol) variantSub).getOriginal() == sub)
        ) {
          varsWithSub.add(variant);
        }
      }
    }

    return varsWithSub;
  }

  public List<ASTSCState> getSubstates(ASTSCState state) {
    if (MontiArcMill.typeDispatcher().isSCBasisASTSCEmptyBody(state.getSCSBody()))
      return new ArrayList<>();

    List<ASTSCState> list = new ArrayList<>();
    for (ASTSCStateElement s : (((ASTSCHierarchyBody) state.getSCSBody()).getSCStateElementList())) {
      if (MontiArcMill.typeDispatcher().isSCBasisASTSCState(s))
        list.add(MontiArcMill.typeDispatcher().asSCBasisASTSCState(s));
    }
    return list;
  }

  public List<ASTSCStateElement> getInitialSubstates(ASTSCState state) {
    return getSubstates(state).stream().filter(s -> s.getSCModifier().isInitial()).collect(Collectors.toList());
  }

  public List<ASTSCState> getStates(ASTComponentType component) {
    Preconditions.checkNotNull(component);

    return this.getAutomatonBehavior(component).get().getStates();
  }

  public Optional<ASTMCBlockStatement> getEntryAction(ASTSCState state) {
    for (ASTSCStateElement s : getHierarchyElementsOf(state)) {
      if (MontiArcMill.typeDispatcher().isSCActionsASTSCEntryAction(s)) {
        ASTSCABody actionBody = MontiArcMill.typeDispatcher()
          .asSCActionsASTSCEntryAction(s)
          .getSCABody();
        return getBlockStatementOfAction(actionBody);
      }
    }

    return Optional.empty();
  }

  public Optional<ASTMCBlockStatement> getExitAction(ASTSCState state) {
    for (ASTSCStateElement s : getHierarchyElementsOf(state)) {
      if (MontiArcMill.typeDispatcher().isSCActionsASTSCExitAction(s)) {
        ASTSCABody actionBody = MontiArcMill.typeDispatcher()
          .asSCActionsASTSCExitAction(s)
          .getSCABody();
        return getBlockStatementOfAction(actionBody);
      }
    }

    return Optional.empty();
  }

  public List<ASTMCBlockStatement> getInitAction(ASTSCState state) {
    if (!MontiArcMill.typeDispatcher().isSCTransitions4CodeASTAnteAction(state.getSCSAnte())
      || MontiArcMill.typeDispatcher().asSCTransitions4CodeASTAnteAction(state.getSCSAnte()).isEmptyMCBlockStatements())
      return Collections.emptyList();

    return MontiArcMill.typeDispatcher().asSCTransitions4CodeASTAnteAction(state.getSCSAnte()).getMCBlockStatementList();
  }

  public Optional<ASTMCBlockStatement> getDoAction(ASTSCState state) {
    for (ASTSCStateElement s : getHierarchyElementsOf(state)) {
      if (MontiArcMill.typeDispatcher().isSCDoActionsASTSCDoAction(s)) {
        ASTSCABody actionBody = MontiArcMill.typeDispatcher()
          .asSCDoActionsASTSCDoAction(s)
          .getSCABody();
        return getBlockStatementOfAction(actionBody);
      }
    }

    return Optional.empty();
  }

  /**
   * Hierarchy elements of the state are returned if it is an hierarchical state.
   * Else, the list is empty
   */
  private List<ASTSCStateElement> getHierarchyElementsOf(ASTSCState state) {
    return this.getBodyOfHierarchicalState(state)
      .map(ASTSCHierarchyBody::getSCStateElementList)
      .orElse(Collections.emptyList());
  }

  /** Return the body of a hierarchical state, given that the state body is an {@link ASTSCHierarchyBody}. */
  private Optional<ASTSCHierarchyBody> getBodyOfHierarchicalState(ASTSCState state) {
    if (MontiArcMill.typeDispatcher().isSCStateHierarchyASTSCHierarchyBody(state.getSCSBody())) {
      return Optional.of(MontiArcMill.typeDispatcher().asSCStateHierarchyASTSCHierarchyBody(state.getSCSBody()));
    } else {
      return Optional.empty();
    }
  }

  /** Return the block statement, given that the action body is an {@link ASTTransitionAction}. */
  private Optional<ASTMCBlockStatement> getBlockStatementOfAction(ASTSCABody actionBody) {
    if (MontiArcMill.typeDispatcher().isSCTransitions4CodeASTTransitionAction(actionBody)) {
      ASTTransitionAction actualAction = MontiArcMill.typeDispatcher().asSCTransitions4CodeASTTransitionAction(actionBody);
      return Optional.of(actualAction.getMCBlockStatement());
    } else {
      return Optional.empty();
    }
  }
}