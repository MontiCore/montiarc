/* (c) https://github.com/MontiCore/monticore */
package montiarc.generator.util;

import arcautomaton.ArcAutomatonMill;
import arcautomaton._ast.ASTArcStatechart;
import arcautomaton._ast.ASTMsgEvent;
import arcautomaton._ast.ASTMsgEventTOP;
import arcbasis._ast.ASTArcArgument;
import arcbasis._ast.ASTComponentInstance;
import arcbasis._ast.ASTComponentInstantiation;
import arcbasis._ast.ASTComponentInstantiationTOP;
import arcbasis._ast.ASTComponentType;
import arcbasis._ast.ASTConnector;
import arcbasis._ast.ASTPortAccess;
import arccompute._ast.ASTArcCompute;
import arccompute._ast.ASTArcInit;
import com.google.common.base.Preconditions;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.scbasis.SCBasisMill;
import de.monticore.scbasis._ast.ASTSCEmptyBody;
import de.monticore.scbasis._ast.ASTSCState;
import de.monticore.scbasis._ast.ASTSCStateElement;
import de.monticore.scbasis._ast.ASTSCTransition;
import de.monticore.scstatehierarchy._ast.ASTSCHierarchyBody;
import de.monticore.sctransitions4code._ast.ASTAnteAction;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
  public Map<String, List<ASTSCTransition>> getTransitionsForPortEvents(ASTArcStatechart sc) {
    return getTransitionsMappedToPortTriggers(sc.streamTransitions());
  }

  public Map<String, List<ASTSCTransition>> getTransitionsForPortEvents(ASTModeAutomaton modeAutomaton) {
    return getTransitionsMappedToPortTriggers(getTransitions(modeAutomaton).stream());
  }

  protected Map<String, List<ASTSCTransition>> getTransitionsMappedToPortTriggers(Stream<ASTSCTransition> transitions) {
    Map<String, List<ASTSCTransition>> result = new HashMap<>();
    transitions.forEach(tr -> {
      Optional<ASTTransitionBody> body = getASTTransitionBody(tr);
      if (body.isEmpty()) return;
      Optional<String> trigger = getTriggeringPortName(body.get());
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

  public Optional<String> getTriggeringPortName(ASTTransitionBody body) {
    return Optional.of(body)
      .filter(ASTTransitionBody::isPresentSCEvent)
      .filter(bdy -> bdy.getSCEvent() instanceof ASTMsgEvent)
      .map(bdy -> (ASTMsgEvent) bdy.getSCEvent())
      .filter(event -> !ArcAutomatonMill.TICK.equals(event.getName()))
      .map(ASTMsgEventTOP::getName);
  }

  public List<PortSymbol> getInPortsNotTriggeringAnyTransition(ASTArcStatechart sc, ASTComponentType comp) {
    List<String> triggeringPorts = getTransitionsForPortEvents(sc).keySet().stream()
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

  public List<ASTMCBlockStatement> getAutomatonInitAction(ASTArcStatechart ast) {
    return ast.streamInitialStates()
      .filter(init -> init.getSCSAnte() instanceof ASTAnteAction)
      .map(init -> (ASTAnteAction) init.getSCSAnte())
      .filter(anteAction -> anteAction.sizeMCBlockStatements() > 0)
      .findFirst()
      .map(ASTAnteAction::getMCBlockStatementList)
      .orElse(new ArrayList<>());
  }

  public Optional<ASTModeAutomaton> getModeAutomaton(ASTComponentType ast) {
    return ast.getBody().streamArcElementsOfType(ASTModeAutomaton.class).findFirst();
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

  public Map<ASTArcMode, List<ASTComponentInstance>> getInstancesFromModes(ASTModeAutomaton ast) {
    return getModes(ast).stream().collect(
      Collectors.toMap(
        Function.identity(),
        mode -> mode.getBody()
          .getElementsOfType(ASTComponentInstantiation.class).stream()
          .flatMap(ASTComponentInstantiationTOP::streamComponentInstances)
          .collect(Collectors.toList())
      )
    );
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

  public List<ASTComponentInstance> getSubcomponentsWithoutInPorts(@NotNull ASTComponentType ast) {
    return ast.getSubComponents().stream()
      .filter(ASTComponentInstance::isPresentSymbol)
      .filter(inst -> inst.getSymbol().isTypePresent())
      .filter(inst -> inst.getSymbol().getType().getTypeInfo().getAllIncomingPorts().isEmpty())
      .collect(Collectors.toList());

  }

  public boolean isGenericComponent(ASTComponentType astComponentType) {
    return astComponentType.getHead() instanceof ASTGenericComponentHead;
  }

  public ASTExpression getInitialForVariable(VariableSymbol variableSymbol) {
    if (variableSymbol.isPresentAstNode() && MontiArcMill.typeDispatcher().isASTArcField(variableSymbol.getAstNode()))
      return MontiArcMill.typeDispatcher().asASTArcField(variableSymbol.getAstNode()).getInitial();
    return MontiArcMill.literalExpressionBuilder().setLiteral(MontiArcMill.nullLiteralBuilder().build()).build();
  }

  public String getNullLikeValue(SymTypeExpression type) {
    if (type.isPrimitive()) {
      if (BasicSymbolsMill.BOOLEAN.equals(type.asPrimitive().getPrimitiveName()))
        return "false";
      else return "0";
    } else return "null";

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
    return vp.getArcElements().stream().filter(e -> MontiArcMill.typeDispatcher().isASTConnector(e)).map(e -> MontiArcMill.typeDispatcher().asASTConnector(e)).collect(Collectors.toList());
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

  public List<ASTSCStateElement> returnSubstates(ASTSCState State){
    SCBasisMill.init();
    if(SCBasisMill.typeDispatcher().isASTSCEmptyBody(((ASTSCState)State).getSCSBody()))
      return new ArrayList<ASTSCStateElement>();
    List<ASTSCStateElement> list = new ArrayList<>();
    for(ASTSCStateElement s: (((ASTSCHierarchyBody)((ASTSCState)State).getSCSBody()).getSCStateElementList())){
      if(SCBasisMill.typeDispatcher().isASTSCState(s))
        list.add(s);
    }
    Collections.reverse(list);
    return list;
  }
  public List<ASTSCState> getStates(ASTComponentType component) {
    Preconditions.checkNotNull(component);

    return this.getAutomatonBehavior(component).get().getStates();
  }
}