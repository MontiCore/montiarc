/* (c) https://github.com/MontiCore/monticore */
package de.monticore.sd2arc.codegen;

import arcbasis._ast.ASTArcElement;
import arcbasis._ast.ASTConnectorTOP;
import arcbasis._ast.ASTPortAccess;
import de.monticore.lang.sd4components.SD4ComponentsMill;
import de.monticore.lang.sd4components._ast.ASTSDComponent;
import de.monticore.lang.sd4components._ast.ASTSDPort;
import de.monticore.lang.sd4components._ast.ASTSDVariableDeclaration;
import de.monticore.lang.sd4components._symboltable.SD4ComponentsScope;
import de.monticore.lang.sdbasis._ast.ASTSDSendMessage;
import de.monticore.lang.sdbasis._ast.ASTSequenceDiagram;
import de.monticore.sd2arc._ast.ASTSDHiddenFreeModifier;
import de.monticore.sd2arc.trafo.EmbeddingComponent;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.compsymbols._symboltable.PortSymbol;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.umlstereotype._ast.ASTStereoValue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class SDHelper {

  public static long defaultTicks = 9999;

  public List<ASTSDComponent> getVisibleComponents(ASTSequenceDiagram diagram) {
    return diagram.streamSDObjects()
      .filter(SD4ComponentsMill.typeDispatcher()::isSD4ComponentsASTSDComponent)
      .map(SD4ComponentsMill.typeDispatcher()::asSD4ComponentsASTSDComponent)
      .collect(Collectors.toList());
  }

  public Map<String, String> getImpliedConnectors(ASTSequenceDiagram diagram) {
    Map<String, String> targetSource = new HashMap<>();
    for (ASTSDSendMessage connector : diagram.getSDBody().streamSDElements()
      .filter(SD4ComponentsMill.typeDispatcher()::isSDBasisASTSDSendMessage)
      .map(SD4ComponentsMill.typeDispatcher()::asSDBasisASTSDSendMessage)
      .toList()) {
      if (connector.isPresentSDTarget()
        && SD4ComponentsMill.typeDispatcher().isSD4ComponentsASTSDPort(connector.getSDTarget())
        && connector.isPresentSDSource()
        && SD4ComponentsMill.typeDispatcher().isSD4ComponentsASTSDPort(connector.getSDSource())) {
        ASTSDPort astSource = SD4ComponentsMill.typeDispatcher().asSD4ComponentsASTSDPort(connector.getSDSource());
        ASTSDPort astTarget = SD4ComponentsMill.typeDispatcher().asSD4ComponentsASTSDPort(connector.getSDTarget());
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
      .filter(SD4ComponentsMill.typeDispatcher()::isSD4ComponentsASTSDVariableDeclaration)
      .map(SD4ComponentsMill.typeDispatcher()::asSD4ComponentsASTSDVariableDeclaration)
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

  public boolean isPortSourceAtObserveInteractionIndex(int index, ASTSequenceDiagram diagram, ASTSDComponent component, PortSymbol port) {
    ASTSDSendMessage element = diagram.getSDBody().streamSDElements()
      .filter(SD4ComponentsMill.typeDispatcher()::isSDBasisASTSDSendMessage)
      .map(SD4ComponentsMill.typeDispatcher()::asSDBasisASTSDSendMessage)
      .filter(e -> !SD4ComponentsMill.typeDispatcher().isSD4ComponentsASTSDMessage(e.getSDAction()) || !SD4ComponentsMill.typeDispatcher().asSD4ComponentsASTSDMessage(e.getSDAction()).isTrigger())
      .toList().get(index);

    return element.isPresentSDSource()
      && SD4ComponentsMill.typeDispatcher().isSD4ComponentsASTSDPort(element.getSDSource())
      && Objects.equals(SD4ComponentsMill.typeDispatcher().asSD4ComponentsASTSDPort(element.getSDSource()).getName(), component.getName())
      && Objects.equals(SD4ComponentsMill.typeDispatcher().asSD4ComponentsASTSDPort(element.getSDSource()).getPort(), port.getName());
  }

  public boolean isFree(ASTSequenceDiagram diagram, ASTSDSendMessage interaction) {
    if (!interaction.isPresentSDSource() || !SD4ComponentsMill.typeDispatcher().isSD4ComponentsASTSDPort(interaction.getSDSource()))
      return true;
    ASTSDPort source = SD4ComponentsMill.typeDispatcher().asSD4ComponentsASTSDPort(interaction.getSDSource());
    Optional<ASTSDComponent> sourceComp = getComponentByName(diagram, source.getName());
    return sourceComp.map(component -> isFree(diagram, component, source.getPortSymbol())).orElse(true);
  }

  public boolean isFree(ASTSequenceDiagram diagram, ASTSDComponent component, PortSymbol port) {
    //    c is match-complete.
    if (component.getSDModifierList().stream().anyMatch(SD4ComponentsMill.typeDispatcher()::isSDBasisASTSDCompleteModifier))
      return false;
    List<ASTSDComponent> targetComponents = getTargetComponents(diagram, component.getName() + "." + port.getName()).stream()
      .filter(c -> c.streamSDModifiers().noneMatch(m -> m instanceof ASTSDHiddenFreeModifier))
      .toList();
    if (targetComponents.isEmpty()) return true;
    //    c is match-visible, and p is connected to a visible component.
    if (component.getSDModifierList().stream().anyMatch(SD4ComponentsMill.typeDispatcher()::isSDBasisASTSDVisibleModifier))
      return false;
    //    p is connected to a match-complete component.
    if (targetComponents.stream().flatMap(ASTSDComponent::streamSDModifiers).anyMatch(SD4ComponentsMill.typeDispatcher()::isSDBasisASTSDCompleteModifier))
      return false;
    //    c is not hidden and p is connected to a match-visible component.
    if (component.getSDModifierList().stream().noneMatch(m -> m instanceof ASTSDHiddenFreeModifier) && targetComponents.stream().flatMap(ASTSDComponent::streamSDModifiers).anyMatch(SD4ComponentsMill.typeDispatcher()::isSDBasisASTSDVisibleModifier))
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
      // Convert simple stereotypes (key="StringLiteral") to complex stereotype if necessary
      if (!value.isPresentText()) {
        stereotypes.put(value.getName(), null);
      } else if (value.getName().equals("ticks")) {
        stereotypes.put(value.getName(), value.getValue());
      } else {
        stereotypes.put(value.getName(), "\"" + value.getValue() + "\"");
      }
    }

    return stereotypes;
  }

  public String tickLength(ASTSequenceDiagram diagram) {
    return getStereotypes(diagram).get("ticks");
  }
}
