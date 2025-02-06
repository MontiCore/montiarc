/* (c) https://github.com/MontiCore/monticore */
package arccore._symboltable;

import arcbasis.ArcBasisMill;
import arcbasis._ast.ASTArcElement;
import arcbasis._symboltable.ArcBasisScopesGenitorP2;
import arccore.ArcCoreMill;
import arccore._visitor.ArcCoreTraverser;
import com.google.common.base.Preconditions;
import org.codehaus.commons.nullanalysis.NotNull;

public class ArcCoreScopesGenitorP2Delegator {

  protected IArcCoreGlobalScope globalScope;

  protected ArcCoreTraverser traverser;

  protected ArcCoreTraverser getTraverser() {
    return this.traverser;
  }

  public ArcCoreScopesGenitorP2Delegator() {
    this.globalScope = ArcCoreMill.globalScope();
    this.traverser = ArcCoreMill.traverser();
    this.init();
  }

  protected void init() {
    this.initArcBasis();
  }

  protected void initArcBasis() {
    ArcBasisScopesGenitorP2 arcBasisScopesGenitorP2 = ArcBasisMill.scopesGenitorP2();
    this.getTraverser().add4ArcBasis(arcBasisScopesGenitorP2);
    this.getTraverser().add4CompSymbols(arcBasisScopesGenitorP2);
    this.getTraverser().add4TypeParameters(arcBasisScopesGenitorP2);
    this.getTraverser().setArcBasisHandler(arcBasisScopesGenitorP2);
  }

  public void createFromAST(@NotNull ASTArcElement rootNode) {
    Preconditions.checkNotNull(rootNode);
    rootNode.accept(traverser);
  }
}