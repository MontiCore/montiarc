/* (c) https://github.com/MontiCore/monticore */
package variablearc._cocos.util;

import arcbasis._ast.ASTComponentType;
import arcbasis._visitor.ArcBasisHandler;
import arcbasis._visitor.ArcBasisTraverser;
import com.google.common.base.Preconditions;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc.VariableArcMill;
import variablearc._ast.ASTArcVarIf;
import variablearc._visitor.VariableArcTraverser;
import variablearc._visitor.VariableArcVisitor2;

import java.util.function.Consumer;

/**
 * Generic class that applies the {@code consumer} on all {@link ASTArcVarIf}
 */
public class ComponentVarIfHandler implements VariableArcVisitor2, ArcBasisHandler {

  final Consumer<ASTArcVarIf> consumer;
  final ASTComponentType componentType;

  private ArcBasisTraverser traverser;

  public ComponentVarIfHandler(ASTComponentType componentType, Consumer<ASTArcVarIf> consumer) {
    this(componentType, consumer, VariableArcMill.traverser());
  }

  public ComponentVarIfHandler(ASTComponentType componentType, Consumer<ASTArcVarIf> consumer, VariableArcTraverser traverser) {
    this.componentType = componentType;
    this.consumer = consumer;
    traverser.setArcBasisHandler(this);
    traverser.add4VariableArc(this);
    this.traverser = traverser;
  }

  @Override
  public ArcBasisTraverser getTraverser() {
    return this.traverser;
  }

  @Override
  public void setTraverser(@NotNull ArcBasisTraverser traverser) {
    Preconditions.checkArgument(traverser != null);
    this.traverser = traverser;
  }

  @Override
  public void handle(ASTComponentType node) {
    if (node == componentType)
      ArcBasisHandler.super.handle(node);
  }

  @Override
  public void visit(ASTArcVarIf node) {
    consumer.accept(node);
  }
}
