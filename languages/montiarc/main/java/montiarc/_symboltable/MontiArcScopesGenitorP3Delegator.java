/* (c) https://github.com/MontiCore/monticore */
package montiarc._symboltable;

import arcbasis.ArcBasisMill;
import com.google.common.base.Preconditions;
import montiarc.MontiArcMill;
import montiarc._ast.ASTMACompilationUnit;
import montiarc._visitor.MontiArcTraverser;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.Collection;

public class MontiArcScopesGenitorP3Delegator {

  protected IMontiArcGlobalScope globalScope;

  protected MontiArcTraverser traverser;

  protected MontiArcTraverser getTraverser() {
    return this.traverser;
  }

  public MontiArcScopesGenitorP3Delegator() {
    this.globalScope = MontiArcMill.globalScope();
    this.traverser = MontiArcMill.traverser();
    this.init();
  }

  protected void init() {
    this.initArcBasis();
  }

  protected void initArcBasis() {
    this.getTraverser().add4ArcBasis(ArcBasisMill.scopesGenitorP3());
  }

  public void createFromAST(@NotNull ASTMACompilationUnit rootNode) {
    Preconditions.checkNotNull(rootNode);
    rootNode.accept(traverser);
  }

  public void createFromAST(@NotNull Collection<ASTMACompilationUnit> rootNodes) {
    Preconditions.checkNotNull(rootNodes);
    for (ASTMACompilationUnit rootNode : rootNodes) {
      this.createFromAST(rootNode);
    }
  }
}
