/* (c) https://github.com/MontiCore/monticore */
package de.monticore.sd2arc.codegen;

import arcbasis._ast.ASTArcElement;
import arcbasis._ast.ASTConnectorTOP;
import arcbasis._ast.ASTPortAccess;
import de.monticore.lang.sd4components.SD4ComponentsMill;
import de.monticore.lang.sd4components._ast.ASTSDCondition;
import de.monticore.lang.sd4components._ast.ASTSDIncompleteAction;
import de.monticore.lang.sd4components._ast.ASTSDComponent;
import de.monticore.lang.sd4components._ast.ASTSDMessage;
import de.monticore.lang.sd4components._ast.ASTSDPort;
import de.monticore.lang.sd4components._ast.ASTSDTick;
import de.monticore.lang.sd4components._ast.ASTSDVariableDeclaration;
import de.monticore.lang.sd4components._symboltable.SD4ComponentsScope;
import de.monticore.lang.sdbasis._ast.ASTSDElement;
import de.monticore.lang.sdbasis._ast.ASTSDSendMessage;
import de.monticore.lang.sdbasis._ast.ASTSDCompleteModifier;
import de.monticore.lang.sdbasis._ast.ASTSDVisibleModifier;
import de.monticore.lang.sdbasis._ast.ASTSequenceDiagram;
import de.monticore.sd2arc._ast.ASTSDHiddenFreeModifier;
import de.monticore.sd2arc.trafo.EmbeddingComponent;
import de.monticore.sd2arc.util.SDInteractions;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.compsymbols._symboltable.PortSymbol;
import de.monticore.symbols.compsymbols._symboltable.Timing;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.umlstereotype._ast.ASTStereoValue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class SDHelper {

  public static long defaultTicks = 9999;

  /**
   * Stereotype values that are passed to the generated test as expressions. All other values are passed as strings.
   */
  protected static final Set<String> EXPRESSION_STEREOTYPES = Set.of("ticks", "simulatedTickLength", "exception");

  public boolean isSendMessage(Object element) {
    return element instanceof ASTSDSendMessage;
  }

  public boolean isMessage(Object action) {
    return action instanceof ASTSDMessage;
  }

  public boolean isIncompleteAction(Object action) {
    return action instanceof ASTSDIncompleteAction;
  }

  public boolean isTick(Object element) {
    return element instanceof ASTSDTick;
  }

  public boolean isCondition(Object element) {
    return element instanceof ASTSDCondition;
  }

  public boolean isVariableDeclaration(Object element) {
    return element instanceof ASTSDVariableDeclaration;
  }

  public List<ASTSDComponent> getVisibleComponents(ASTSequenceDiagram diagram) {
    return diagram.streamSDObjects()
      .filter(ASTSDComponent.class::isInstance)
      .map(ASTSDComponent.class::cast)
      .collect(Collectors.toList());
  }

  public Map<String, String> getImpliedConnectors(ASTSequenceDiagram diagram) {
    Map<String, String> targetSource = new HashMap<>();
    for (ASTSDSendMessage connector : SDInteractions.streamSendMessages(diagram)
      .toList()) {
      if (connector.isPresentSDTarget() && connector.getSDTarget() instanceof ASTSDPort astTarget
        && connector.isPresentSDSource() && connector.getSDSource() instanceof ASTSDPort astSource) {
        String source = astSource.getName() + "." + astSource.getPort();
        String target = astTarget.getName() + "." + astTarget.getPort();

        targetSource.put(target, source);
      }
    }
    return targetSource;
  }

  public List<PortSymbol> getComponentPorts(ASTSDComponent component) {
    return ((SD4ComponentsScope) component.getSymbol().getType().getTypeInfo().getSpannedScope()).getLocalPortSymbols();
  }

  public List<ASTSDVariableDeclaration> getVariableDeclarations(ASTSequenceDiagram diagram) {
    return diagram.getSDBody().streamSDElements()
      .filter(ASTSDVariableDeclaration.class::isInstance)
      .map(ASTSDVariableDeclaration.class::cast)
      .collect(Collectors.toList());
  }

  public boolean connectedToWorld(ASTSequenceDiagram diagram, String component, PortSymbol portSymbol) {
    String qname = component + "." + portSymbol.getName();
    return getPortSource(diagram, qname) == null;
  }

  public String getPortSource(ASTSequenceDiagram diagram, String qName) {
    if (!isEmbedded(diagram)) return getImpliedConnectors(diagram).get(qName);

    return EmbeddingComponent.get(diagram)
      .getConnectors().stream()
      .filter(c -> c.getTargetList().stream().anyMatch(a -> a.getQName().equals(qName)))
      .findAny()
      .map(ASTConnectorTOP::getSource)
      .map(ASTPortAccess::getQName)
      .orElse(null);
  }

  public String qNameToVarName(String qname) {
    return qname.replaceFirst("\\.", "_");
  }

  public String getNullLikeValue(SymTypeExpression type) {
    if (type.isPrimitive()) {
      if (BasicSymbolsMill.BOOLEAN.equals(type.asPrimitive().getPrimitiveName()))
        return "false";
      else return "0";
    } else return "null";
  }

  public boolean isSyncPort(PortSymbol port) {
    return port.getTiming().matches(Timing.TIMED_SYNC);
  }

  public boolean isSyncTarget(ASTSequenceDiagram diagram, ASTSDSendMessage message) {
    return message.isPresentSDTarget() && message.getSDTarget() instanceof ASTSDPort target
      && getComponentByName(diagram, target.getName()).stream()
      .flatMap(c -> getComponentPorts(c).stream())
      .anyMatch(p -> p.getName().equals(target.getPort()) && isSyncPort(p));
  }

  /**
   * An incoming synchronous port that is driven by the test.
   *
   * @param varName  the variable holding the last value sent to the port
   * @param nullable whether the variable is null until the first trigger
   */
  public record SyncInput(String varName, boolean nullable) { }

  /**
   * @return the incoming synchronous ports of the components that are driven by the test
   */
  public List<SyncInput> getSyncInputs(ASTSequenceDiagram diagram) {
    return getVisibleComponents(diagram).stream()
      .flatMap(c -> getComponentPorts(c).stream()
        .filter(p -> p.isIncoming() && isSyncPort(p) && connectedToWorld(diagram, c.getName(), p))
        .map(p -> new SyncInput(c.getName() + "_" + p.getName(), !p.getType().isPrimitive())))
      .collect(Collectors.toList());
  }

  /**
   * As synchronous ports carry a message in every tick, a tick that does not match the expected tick block violates
   * the diagram if one of these ports may not be omitted (match complete or visible).
   */
  public boolean hasConstrainedSyncPorts(ASTSequenceDiagram diagram) {
    return getVisibleComponents(diagram).stream()
      .anyMatch(c -> getComponentPorts(c).stream()
        .anyMatch(p -> p.isOutgoing() && isSyncPort(p) && !isFree(diagram, c, p)));
  }

  public String printPortTiming(PortSymbol port) {
    return isSyncPort(port) ? "sync " : "";
  }

  public List<ASTSDSendMessage> getTickMessages(ASTSDTick tick) {
    return tick.getSDSendMessageList().stream()
      .filter(ASTSDSendMessage::isPresentSDSource)
      .filter(msg -> !(msg.getSDAction() instanceof ASTSDMessage action && action.isTrigger()))
      .collect(Collectors.toList());
  }

  public List<ASTSDSendMessage> getTickTriggers(ASTSDTick tick) {
    return tick.getSDSendMessageList().stream()
      .filter(ASTSDSendMessage::isPresentSDTarget)
      .filter(msg -> msg.getSDAction() instanceof ASTSDMessage action && action.isTrigger())
      .collect(Collectors.toList());
  }

  /**
   * The match restrictions (complete, visible) only hold between the first and the last interaction. If the diagram
   * starts with a trigger, the trigger is the first interaction and the restrictions already hold before the first
   * observed interaction.
   */
  public boolean isRestrictedFromStart(ASTSequenceDiagram diagram) {
    for (ASTSDElement element : diagram.getSDBody().getSDElementList()) {
      if (element instanceof ASTSDTick tick) {
        return !getTickTriggers(tick).isEmpty();
      } else if (element instanceof ASTSDSendMessage msg && msg.getSDAction() instanceof ASTSDMessage action && action.isTrigger()) {
        return true;
      } else if (element instanceof ASTSDSendMessage msg && msg.isPresentSDSource()) {
        return false;
      }
    }
    return false;
  }

  public boolean isPortSourceAtMessageIndex(int index, ASTSequenceDiagram diagram, ASTSDComponent component, PortSymbol port) {
    ASTSDElement element = diagram.getSDBody().getSDElementList().stream()
      .filter(e -> e instanceof ASTSDTick
        || (e instanceof ASTSDSendMessage msg
          && !(msg.getSDAction() instanceof ASTSDMessage action && action.isTrigger())))
      .toList().get(index);

    List<ASTSDSendMessage> messages = element instanceof ASTSDTick tick
      ? getTickMessages(tick)
      : List.of((ASTSDSendMessage) element);

    return messages.stream()
      .filter(ASTSDSendMessage::isPresentSDSource)
      .map(ASTSDSendMessage::getSDSource)
      .filter(ASTSDPort.class::isInstance)
      .map(ASTSDPort.class::cast)
      .anyMatch(src -> Objects.equals(src.getName(), component.getName())
        && Objects.equals(src.getPort(), port.getName()));
  }

  public boolean isFree(ASTSequenceDiagram diagram, ASTSDSendMessage interaction) {
    if (!interaction.isPresentSDSource() || !(interaction.getSDSource() instanceof ASTSDPort source))
      return true;
    Optional<ASTSDComponent> sourceComp = getComponentByName(diagram, source.getName());
    return sourceComp.map(component -> isFree(diagram, component, source.getPortSymbol())).orElse(true);
  }

  public boolean isFree(ASTSequenceDiagram diagram, ASTSDComponent component, PortSymbol port) {
    //    c is match-complete.
    if (component.getSDModifierList().stream().anyMatch(ASTSDCompleteModifier.class::isInstance))
      return false;
    List<ASTSDComponent> targetComponents = getTargetComponents(diagram, component.getName() + "." + port.getName()).stream()
      .filter(c -> c.streamSDModifiers().noneMatch(m -> m instanceof ASTSDHiddenFreeModifier))
      .toList();
    if (targetComponents.isEmpty()) return true;
    //    c is match-visible, and p is connected to a visible component.
    if (component.getSDModifierList().stream().anyMatch(ASTSDVisibleModifier.class::isInstance))
      return false;
    //    p is connected to a match-complete component.
    if (targetComponents.stream().flatMap(ASTSDComponent::streamSDModifiers).anyMatch(ASTSDCompleteModifier.class::isInstance))
      return false;
    //    c is not hidden and p is connected to a match-visible component.
    if (component.getSDModifierList().stream().noneMatch(ASTSDHiddenFreeModifier.class::isInstance) && targetComponents.stream().flatMap(ASTSDComponent::streamSDModifiers).anyMatch(ASTSDVisibleModifier.class::isInstance))
      return false;
    return true;
  }

  public Optional<ASTSDComponent> getComponentByName(ASTSequenceDiagram diagram, String name) {
    return getVisibleComponents(diagram).stream()
      .filter(c -> Objects.equals(c.getName(), name))
      .findFirst();
  }

  protected List<ASTSDComponent> getTargetComponents(ASTSequenceDiagram diagram, String source) {
    if (!isEmbedded(diagram))
      return getImpliedConnectors(diagram).entrySet().stream()
        .filter(e -> e.getValue().equals(source))
        .map(Map.Entry::getKey)
        .map(s -> s.split("\\.", 2)[0])
        .collect(Collectors.toSet()).stream()
        .map(t -> getComponentByName(diagram, t))
        .filter(Optional::isPresent)
        .map(Optional::get)
        .collect(Collectors.toList());

    return EmbeddingComponent.get(diagram)
      .getConnectors().stream()
      .filter(c -> c.getSource().getQName().equals(source))
      .flatMap(ASTConnectorTOP::streamTarget)
      .map(t -> getComponentByName(diagram, t.getComponent()))
      .filter(Optional::isPresent)
      .map(Optional::get)
      .collect(Collectors.toList());
  }

  public boolean isEmbedded(ASTSequenceDiagram diagram) {
    return EmbeddingComponent.isEmbedded(diagram);
  }

  public List<ASTArcElement> getEmbeddingBody(ASTSequenceDiagram diagram) {
    return EmbeddingComponent.get(diagram).getBody().getArcElementList();
  }

  public Map<String, String> getStereotypes(ASTSequenceDiagram diagram) {
    Map<String, String> stereotypes = new HashMap<>();
    stereotypes.put("ticks", String.valueOf(defaultTicks));
    stereotypes.put("test", null);
    stereotypes.put("oracle", "\"first\"");

    if (!diagram.isPresentStereotype()) return stereotypes;
    for (ASTStereoValue value : diagram.getStereotype().getValuesList()) {
      stereotypes.put(value.getName(), printStereoValue(value));
    }

    return stereotypes;
  }

  /**
   * Prints the value of a stereotype for the generated test. Values are given unquoted, e.g., {@code ticks=5}. The
   * quoted form, e.g., {@code ticks="5"}, is supported as well.
   *
   * @return the value as expression for {@link #EXPRESSION_STEREOTYPES}, otherwise as string literal, or null if the
   * stereotype has no value
   */
  protected String printStereoValue(ASTStereoValue value) {
    if (!value.isPresentExpression()) return null;

    String printed = value.isPresentText() ? value.getValue() : SD4ComponentsMill.prettyPrint(value.getExpression(), false);
    if (!EXPRESSION_STEREOTYPES.contains(value.getName())) return "\"" + printed + "\"";
    // SDs have no list expressions, but MaUnit expects the expected exceptions as list
    if (value.getName().equals("exception") && !printed.startsWith("[")) return "[" + printed + "]";
    return printed;
  }

  public String tickLength(ASTSequenceDiagram diagram) {
    String ticks = getStereotypes(diagram).get("ticks");
    return ticks != null ? ticks : String.valueOf(defaultTicks);
  }
}
