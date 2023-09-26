/* (c) https://github.com/MontiCore/monticore */
package comfortablearc._symboltable;

import arcbasis._ast.ASTComponentInstance;
import arcbasis._ast.ASTComponentInstantiation;
import com.google.common.base.Preconditions;
import comfortablearc._ast.ASTConnectedComponentInstance;
import comfortablearc._ast.ASTFullyConnectedComponentInstantiation;
import comfortablearc._visitor.ComfortableArcHandler;
import comfortablearc._visitor.ComfortableArcTraverser;
import comfortablearc._visitor.ComfortableArcVisitor2;
import org.codehaus.commons.nullanalysis.NotNull;

public class ComfortableArcScopesGenitorP2 implements ComfortableArcVisitor2, ComfortableArcHandler {

  protected ComfortableArcTraverser traverser;

  @Override
  public void setTraverser(@NotNull ComfortableArcTraverser traverser) {
    Preconditions.checkNotNull(traverser);
    this.traverser = traverser;
  }

  @Override
  public ComfortableArcTraverser getTraverser() {
    return this.traverser;
  }

  @Override
  public void visit(@NotNull ASTFullyConnectedComponentInstantiation node) {
    Preconditions.checkNotNull(node);
    getTraverser().visit((ASTComponentInstantiation) node);
  }

  @Override
  public void endVisit(@NotNull ASTFullyConnectedComponentInstantiation node) {
    Preconditions.checkNotNull(node);
    getTraverser().endVisit((ASTComponentInstantiation) node);
  }

  @Override
  public void visit(@NotNull ASTConnectedComponentInstance node) {
    Preconditions.checkNotNull(node);
    getTraverser().visit((ASTComponentInstance) node);
  }

  @Override
  public void endVisit(@NotNull ASTConnectedComponentInstance node) {
    Preconditions.checkNotNull(node);
    getTraverser().endVisit((ASTComponentInstance) node);
  }
}