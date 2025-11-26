/* (c) https://github.com/MontiCore/monticore */
package de.monticore.sd2arc.trafo;

import arcbasis._ast.ASTConnectorTOP;
import arcbasis._ast.ASTPortAccess;
import de.monticore.lang.sd4components.SD4ComponentsMill;
import de.monticore.lang.sd4components._ast.ASTSDPort;
import de.monticore.lang.sd4components._visitor.SD4ComponentsTraverser;
import de.monticore.lang.sdbasis._ast.ASTSDArtifact;
import de.monticore.lang.sdbasis._ast.ASTSDSendMessage;
import de.monticore.lang.sdbasis._ast.ASTSequenceDiagram;
import de.monticore.lang.sdbasis._visitor.SDBasisVisitor2;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static de.monticore.sd2arc.trafo.EmbeddingComponent.isEmbedded;

/**
 * Autocompletes missing sources or targets in interactions
 */
public class CompleteInteractionsTrafo implements SDBasisVisitor2 {

  protected ASTSequenceDiagram currentDiagram;

  public static ASTSDArtifact transform(ASTSDArtifact node) {
    SD4ComponentsTraverser traverser = SD4ComponentsMill.inheritanceTraverser();
    CompleteInteractionsTrafo trafo = new CompleteInteractionsTrafo();
    traverser.add4SDBasis(trafo);
    node.accept(traverser);
    return node;
  }

  @Override
  public void visit(ASTSequenceDiagram node) {
    currentDiagram = node;
  }

  @Override
  public void visit(ASTSDSendMessage node) {
    if (node.isPresentSDSource() && !node.isPresentSDTarget()) {
      ASTSDPort target = qNameToPort(getTarget(SD4ComponentsMill.prettyPrint(node.getSDSource(), false)));
      if (target != null)
        node.setSDTarget(target);
    }
    if (!node.isPresentSDSource() && node.isPresentSDTarget()) {
      ASTSDPort source = qNameToPort(getSource(SD4ComponentsMill.prettyPrint(node.getSDTarget(), false)));
      if (source != null)
        node.setSDSource(source);
    }
  }

  protected String getSource(String target) {
    if (!isEmbedded(currentDiagram)) return getImpliedConnectors().get(target);

    return EmbeddingComponent.get(currentDiagram)
      .getConnectors().stream()
      .filter(c -> c.getTargetList().stream().anyMatch(a -> a.getQName().equals(target)))
      .findAny()
      .map(ASTConnectorTOP::getSource)
      .map(ASTPortAccess::getQName)
      .orElse(null);
  }

  protected String getTarget(String source) {
    if (!isEmbedded(currentDiagram))
      return getImpliedConnectors().entrySet().stream().filter(e -> e.getValue().equals(source)).map(Map.Entry::getKey).findAny().orElse(null);

    return EmbeddingComponent.get(currentDiagram)
      .getConnectors().stream()
      .filter(c -> c.getSource().getQName().equals(source))
      .findAny()
      .map(ASTConnectorTOP::getTargetList)
      .map(s -> s.get(0))
      .map(ASTPortAccess::getQName)
      .orElse(null);
  }

  protected ASTSDPort qNameToPort(String qName) {
    if (qName == null) return null;

    String[] s = qName.split("\\.", 2);
    return SD4ComponentsMill.sDPortBuilder().setName(s[0]).setPort(s[1]).build();
  }

  protected Map<String, String> getImpliedConnectors() {
    Map<String, String> targetSource = new LinkedHashMap<>();
    for (ASTSDSendMessage connector : currentDiagram.getSDBody().streamSDElements()
      .filter(SD4ComponentsMill.typeDispatcher()::isSDBasisASTSDSendMessage)
      .map(SD4ComponentsMill.typeDispatcher()::asSDBasisASTSDSendMessage)
      .collect(Collectors.toList())) {
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
}
