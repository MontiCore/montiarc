/* (c) https://github.com/MontiCore/monticore */
package variablearc._cocos.util;

import arcbasis._ast.ASTComponentType;
import arcbasis._visitor.ArcBasisHandler;
import arcbasis._visitor.ArcBasisTraverser;
import com.google.common.base.Preconditions;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc.VariableArcMill;
import variablearc._ast.ASTArcIfStatement;
import variablearc._visitor.VariableArcTraverser;
import variablearc._visitor.VariableArcVisitor2;

import java.util.function.Consumer;

/**
 * Generic class that applies the {@code consumer} on all {@link ASTArcIfStatement}
 */
public class ComponentIfStatementHandler implements VariableArcVisitor2, ArcBasisHandler {

  final Consumer<ASTArcIfStatement> consumer;
  final ASTComponentType componentType;

  private ArcBasisTraverser traverser;

  public ComponentIfStatementHandler(ASTComponentType componentType, Consumer<ASTArcIfStatement> consumer) {
    this(componentType, consumer, VariableArcMill.traverser());
  }

  public ComponentIfStatementHandler(ASTComponentType componentType, Consumer<ASTArcIfStatement> consumer, VariableArcTraverser traverser) {
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
  public void visit(ASTArcIfStatement node) {
    consumer.accept(node);
  }
}
