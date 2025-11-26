/* (c) https://github.com/MontiCore/monticore */
package de.monticore.sd2arc.trafo;

import arcbasis._ast.ASTArcComponentType;
import arcbasis._ast.ASTComponentInstantiation;
import de.monticore.lang.sd4components.SD4ComponentsMill;
import de.monticore.lang.sd4components._visitor.SD4ComponentsTraverser;
import de.monticore.lang.sdbasis._ast.ASTSDArtifact;
import de.monticore.lang.sdbasis._ast.ASTSDObject;
import de.monticore.lang.sdbasis._ast.ASTSequenceDiagram;
import de.monticore.lang.sdbasis._visitor.SDBasisVisitor2;
import de.se_rwth.commons.logging.Log;
import montiarc.MontiArcMill;
import montiarc._ast.ASTMACompilationUnit;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Optional;

public class EmbeddingComponent implements SDBasisVisitor2 {

  final static LinkedHashMap<ASTSequenceDiagram, ASTArcComponentType> tmp = new LinkedHashMap<>();

  protected static String STEREOTYPE_KEY = "montiarc";

  public static ASTSDArtifact inject(ASTSDArtifact node) {
    SD4ComponentsTraverser traverser = SD4ComponentsMill.inheritanceTraverser();
    EmbeddingComponent injector = new EmbeddingComponent();
    traverser.add4SDBasis(injector);
    node.accept(traverser);
    return node;
  }

  public static ASTArcComponentType get(ASTSequenceDiagram diagram) {
    return tmp.get(diagram);
  }

  public static boolean isEmbedded(ASTSequenceDiagram diagram) {
    return tmp.containsKey(diagram);
  }

  @Override
  public void visit(ASTSDArtifact artifact) {
    ASTSequenceDiagram node = artifact.getSequenceDiagram();
    if (!node.isPresentStereotype() || !node.getStereotype().contains(STEREOTYPE_KEY))
      return;

    String path = node.getStereotype().getValue(STEREOTYPE_KEY);

    ASTMACompilationUnit compilationUnit = null;
    try {
      Optional<ASTMACompilationUnit> cu = MontiArcMill.parser().parse(path);
      if (cu.isEmpty()) throw new IOException();

      compilationUnit = cu.get();
      tmp.put(node, cu.get().getArcComponentType());

      // -- remove superfluous arc elements --
      // remove interface
      compilationUnit.getArcComponentType().getBody().getArcElementList()
        .removeIf(e -> MontiArcMill.typeDispatcher().isArcBasisASTComponentInterface(e));
      // remove connectors from incoming interface ports
      compilationUnit.getArcComponentType().getBody().getArcElementList()
        .removeIf(e -> MontiArcMill.typeDispatcher().isArcBasisASTConnector(e) && !MontiArcMill.typeDispatcher().asArcBasisASTConnector(e).getSource().isPresentComponent());
      // remove connectors from outgoing interface ports
      compilationUnit.getArcComponentType().getBody().getArcElementList().removeIf(e -> {
        if (MontiArcMill.typeDispatcher().isArcBasisASTConnector(e)) {
          MontiArcMill.typeDispatcher().asArcBasisASTConnector(e).getTargetList().removeIf(t -> !t.isPresentComponent());
          return MontiArcMill.typeDispatcher().asArcBasisASTConnector(e).getTargetList().isEmpty();
        }
        return false;
      });

    } catch (Exception e) {
      Log.error(String.format("Couldn't parse MontiArc component: %S", path), node.getStereotype().get_SourcePositionStart(), node.getStereotype().get_SourcePositionEnd());
    }

    if (compilationUnit != null) {
      // Inject imports
      artifact.getMCImportStatementList().addAll(compilationUnit.getImportStatementList());
      artifact.getMCImportStatementList().add(SD4ComponentsMill.mCImportStatementBuilder().setMCQualifiedName(compilationUnit.getPackage()).setStar(true).build());

      // Inject component type
      for (ASTSDObject o : node.getSDObjectList()) {
        if (SD4ComponentsMill.typeDispatcher().isSD4ComponentsASTSDComponent(o) && !o.isPresentMCObjectType()) {
          for (ASTComponentInstantiation instantiation : compilationUnit.getArcComponentType().getSubComponentInstantiations()) {
            if (instantiation.getInstancesNames().contains(o.getName()) && SD4ComponentsMill.typeDispatcher().isMCBasicTypesASTMCObjectType(instantiation.getMCType())) {
              SD4ComponentsMill.typeDispatcher().asSD4ComponentsASTSDComponent(o).setMCObjectType(SD4ComponentsMill.typeDispatcher().asMCBasicTypesASTMCObjectType(instantiation.getMCType()));
              break;
            }
          }
        }
      }
    }
  }
}
