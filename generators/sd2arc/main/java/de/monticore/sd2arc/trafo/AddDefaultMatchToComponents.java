/* (c) https://github.com/MontiCore/monticore */
package de.monticore.sd2arc.trafo;

import de.monticore.lang.sd4components.SD4ComponentsMill;
import de.monticore.lang.sd4components._visitor.SD4ComponentsTraverser;
import de.monticore.lang.sdbasis._ast.ASTSDArtifact;
import de.monticore.lang.sdbasis._ast.ASTSDModifier;
import de.monticore.lang.sdbasis._ast.ASTSDObject;
import de.monticore.lang.sdbasis._ast.ASTSequenceDiagram;
import de.monticore.lang.sdbasis._visitor.SDBasisVisitor2;

import java.util.List;

/**
 * Adds all adjacent subcomponents (from the embedding component) into the sequence diagram
 */
public class AddDefaultMatchToComponents implements SDBasisVisitor2 {

  List<ASTSDModifier> defaultModifiers;

  public static ASTSDArtifact transform(ASTSDArtifact node) {
    SD4ComponentsTraverser traverser = SD4ComponentsMill.inheritanceTraverser();
    AddDefaultMatchToComponents trafo = new AddDefaultMatchToComponents();
    traverser.add4SDBasis(trafo);
    node.accept(traverser);
    return node;
  }

  @Override
  public void visit(ASTSequenceDiagram node) {
    defaultModifiers = node.getSDModifierList();
  }

  @Override
  public void endVisit(ASTSequenceDiagram node) {
    defaultModifiers = null;
  }

  @Override
  public void visit(ASTSDObject node) {
    if (defaultModifiers != null && node.getSDModifierList().isEmpty())
      node.getSDModifierList().addAll(defaultModifiers);
  }
}
