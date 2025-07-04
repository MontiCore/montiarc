/* (c) https://github.com/MontiCore/monticore */
package de.monticore.sd2arc.trafo;

import arcbasis._ast.ASTArcComponentType;
import arcbasis._ast.ASTComponentInstantiation;
import arcbasis._ast.ASTPortAccess;
import de.monticore.lang.sd4components.SD4ComponentsMill;
import de.monticore.lang.sd4components._ast.ASTSDComponent;
import de.monticore.lang.sd4components._visitor.SD4ComponentsTraverser;
import de.monticore.lang.sdbasis._ast.ASTSDArtifact;
import de.monticore.lang.sdbasis._ast.ASTSDObject;
import de.monticore.lang.sdbasis._ast.ASTSequenceDiagram;
import de.monticore.lang.sdbasis._visitor.SDBasisVisitor2;
import de.monticore.sd2arc._ast.ASTSDHiddenFreeModifier;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Adds all adjacent subcomponents (from the embedding component) into the sequence diagram
 */
public class AddAdjacentSubcomponents implements SDBasisVisitor2 {

  public static ASTSDArtifact transform(ASTSDArtifact node) {
    SD4ComponentsTraverser traverser = SD4ComponentsMill.inheritanceTraverser();
    AddAdjacentSubcomponents trafo = new AddAdjacentSubcomponents();
    traverser.add4SDBasis(trafo);
    node.accept(traverser);
    return node;
  }

  @Override
  public void visit(ASTSequenceDiagram node) {
    if (!EmbeddingComponent.isEmbedded(node)) return;

    for (ASTSDComponent component : getComponents(node)) {
      for (ASTSDObject adjacent : getAdjacentComponents(EmbeddingComponent.get(node), component.getName())) {
        if (node.streamSDObjects().noneMatch(o -> o.getName().equals(adjacent.getName()))) {
          node.getSDObjectList().add(adjacent);
        }
      }
    }
  }

  protected List<ASTSDComponent> getComponents(ASTSequenceDiagram node) {
    return node.streamSDObjects()
      .filter(o -> SD4ComponentsMill.typeDispatcher().isSD4ComponentsASTSDComponent(o))
      .map(o -> SD4ComponentsMill.typeDispatcher().asSD4ComponentsASTSDComponent(o))
      .collect(Collectors.toList());
  }

  protected List<ASTSDComponent> getAdjacentComponents(ASTArcComponentType componentType, String subcomponentName) {
    return componentType.getConnectors().stream()
      .filter(c -> c.getSource().getComponent().equals(subcomponentName) || c.getTargetList().stream().anyMatch(p -> p.getComponent().equals(subcomponentName)))
      .flatMap(c -> c.getSource().getComponent().equals(subcomponentName) ? c.getTargetList().stream().map(ASTPortAccess::getComponent) : Stream.of(c.getSource().getComponent()))
      .map(c -> toSDComponent(componentType, c))
      .filter(Optional::isPresent)
      .map(Optional::get)
      .collect(Collectors.toList());
  }

  protected Optional<ASTSDComponent> toSDComponent(ASTArcComponentType componentType, String subcomponentName) {
    for (ASTComponentInstantiation instantiation : componentType.getSubComponentInstantiations()) {
      if (instantiation.getInstancesNames().contains(subcomponentName) && SD4ComponentsMill.typeDispatcher().isMCBasicTypesASTMCObjectType(instantiation.getMCType())) {
        return Optional.of(SD4ComponentsMill.sDComponentBuilder()
          .setName(subcomponentName)
          .setMCObjectType(SD4ComponentsMill.typeDispatcher().asMCBasicTypesASTMCObjectType(instantiation.getMCType()))
          .addSDModifier(new ASTSDHiddenFreeModifier())
          .build());
      }
    }
    return Optional.empty();
  }
}
