/* (c) https://github.com/MontiCore/monticore */
package arcbasis._symboltable;

import arcbasis.ArcBasisMill;
import arcbasis._ast.ASTArcElement;
import arcbasis._visitor.ArcBasisTraverser;
import com.google.common.base.Preconditions;
import org.codehaus.commons.nullanalysis.NotNull;

public class ArcBasisScopesGenitorP3Delegator {

  protected IArcBasisGlobalScope globalScope;

  protected ArcBasisTraverser traverser;

  protected ArcBasisTraverser getTraverser() {
    return this.traverser;
  }

  public ArcBasisScopesGenitorP3Delegator() {
    this.globalScope = ArcBasisMill.globalScope();
    this.traverser = ArcBasisMill.traverser();
    this.init();
  }

  protected void init() {
    this.initArcBasis();
  }

  protected void initArcBasis() {
    ArcBasisScopesGenitorP3 arcBasisScopesGenitorP3 = ArcBasisMill.scopesGenitorP3();
    this.getTraverser().add4ArcBasis(arcBasisScopesGenitorP3);
  }

  public void createFromAST(@NotNull ASTArcElement rootNode) {
    Preconditions.checkNotNull(rootNode);
    rootNode.accept(traverser);
  }
}
