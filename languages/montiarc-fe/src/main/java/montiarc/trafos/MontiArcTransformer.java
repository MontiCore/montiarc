/* (c) https://github.com/MontiCore/monticore */
package montiarc.trafos;

import arcbasis._ast.ASTArcBasisNode;
import arcbasis._ast.ASTComponentType;
import arcbasis.trafos.ArcBasisNodeTransformer;
import arcbasis.trafos.ComponentTypeTransformer;
import arccore._ast.ASTArcCoreNode;
import arccore.trafos.ArcCoreNodeTransformer;
import comfortablearc._ast.ASTComfortableArcNode;
import comfortablearc.trafos.ComfortableArcNodeTransformer;
import genericarc._ast.ASTGenericArcNode;
import genericarc.trafos.GenericArcNodeTransformer;
import montiarc._ast.ASTMACompilationUnit;
import montiarc._ast.ASTMontiArcNode;
import montiarc._visitor.MontiArcInheritanceVisitor;
import montiarc._visitor.MontiArcVisitor;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;

public class MontiArcTransformer implements MontiArcInheritanceVisitor {

  protected montiarc._visitor.MontiArcVisitor realThis = this;

  protected List<MontiArcTransformer> montiArcTrafos = new ArrayList<>();

  protected Collection<MACompilationUnitTransformer> montiArcASTMACompilationUnitTrafos = new LinkedHashSet<>();

  protected Collection<MontiArcNodeTransformer> montiArcASTMontiArcNodeTrafos = new LinkedHashSet<>();

  protected Collection<GenericArcNodeTransformer> genericArcASTGenericArcNodeTrafos = new LinkedHashSet<>();

  protected Collection<ComfortableArcNodeTransformer> comfortableArcASTComfortableArcNodeTrafos = new LinkedHashSet<>();

  protected Collection<ArcCoreNodeTransformer> arcCoreASTArcCoreNodeTrafos = new LinkedHashSet<>();

  protected Collection<ComponentTypeTransformer> arcBasisASTComponentTypeTrafos = new LinkedHashSet<>();

  protected Collection<ArcBasisNodeTransformer> arcBasisASTArcBasisNodeTrafos = new LinkedHashSet<>();

  public MontiArcTransformer() {
  }

  public MontiArcVisitor getRealThis() {
    return this.realThis;
  }

  public void setRealThis(@NotNull MontiArcVisitor realThis) {
    this.realThis = realThis;
  }

  public void addTrafo(@NotNull MontiArcTransformer trafo) {
    this.montiArcTrafos.add(trafo);
  }

  public void transformAll(@NotNull ASTMontiArcNode node) {
    node.accept(getRealThis());
  }

  public MontiArcTransformer addTrafo(@NotNull MACompilationUnitTransformer trafo) {
    montiArcASTMACompilationUnitTrafos.add(trafo);
    return this;
  }

  public void visit(@NotNull ASTMACompilationUnit node) {
    for (MACompilationUnitTransformer trafo : montiArcASTMACompilationUnitTrafos) {
      trafo.transform(node);
    }
    montiArcTrafos.forEach(trafo -> trafo.visit(node));
  }

  public MontiArcTransformer addTrafo(@NotNull MontiArcNodeTransformer trafo) {
    montiArcASTMontiArcNodeTrafos.add(trafo);
    return this;
  }

  public void visit(@NotNull ASTMontiArcNode node) {
    for (MontiArcNodeTransformer trafo : montiArcASTMontiArcNodeTrafos) {
      trafo.transform(node);
    }
    montiArcTrafos.forEach(trafo -> trafo.visit(node));
  }

  public void transformAll(@NotNull ASTGenericArcNode node) {
    node.accept(getRealThis());
  }

  public MontiArcTransformer addTrafo(GenericArcNodeTransformer trafo) {
    genericArcASTGenericArcNodeTrafos.add(trafo);
    return this;
  }

  public void visit(@NotNull ASTGenericArcNode node) {
    for (GenericArcNodeTransformer trafo : genericArcASTGenericArcNodeTrafos) {
      trafo.transform(node);
    }
    montiArcTrafos.forEach(trafo -> trafo.visit(node));
  }

  public void transformAll(@NotNull ASTComfortableArcNode node) {
    node.accept(getRealThis());
  }

  public MontiArcTransformer addTrafo(
    ComfortableArcNodeTransformer trafo) {
    comfortableArcASTComfortableArcNodeTrafos.add(trafo);
    return this;
  }

  public void visit(@NotNull ASTComfortableArcNode node) {
    for (ComfortableArcNodeTransformer trafo : comfortableArcASTComfortableArcNodeTrafos) {
      trafo.transform(node);
    }
    montiArcTrafos.forEach(trafo -> trafo.visit(node));
  }

  public void transformAll(@NotNull ASTArcCoreNode node) {
    node.accept(getRealThis());
  }

  public MontiArcTransformer addTrafo(@NotNull ArcCoreNodeTransformer trafo) {
    arcCoreASTArcCoreNodeTrafos.add(trafo);
    return this;
  }

  public void visit(@NotNull ASTArcCoreNode node) {
    for (ArcCoreNodeTransformer trafo : arcCoreASTArcCoreNodeTrafos) {
      trafo.transform(node);
    }
    montiArcTrafos.forEach(trafo -> trafo.visit(node));
  }

  public void transformAll(@NotNull ASTArcBasisNode node) {
    node.accept(getRealThis());
  }

  public MontiArcTransformer addTrafo(@NotNull ComponentTypeTransformer trafo) {
    arcBasisASTComponentTypeTrafos.add(trafo);
    return this;
  }

  public void visit(@NotNull ASTComponentType node) {
    for (ComponentTypeTransformer trafo : arcBasisASTComponentTypeTrafos) {
      trafo.transform(node);
    }
    montiArcTrafos.forEach(trafo -> trafo.visit(node));
  }

  public MontiArcTransformer addTrafo(@NotNull ArcBasisNodeTransformer trafo) {
    arcBasisASTArcBasisNodeTrafos.add(trafo);
    return this;
  }

  public void visit(@NotNull ASTArcBasisNode node) {
    for (ArcBasisNodeTransformer trafo : arcBasisASTArcBasisNodeTrafos) {
      trafo.transform(node);
    }
    montiArcTrafos.forEach(trafo -> trafo.visit(node));
  }
}