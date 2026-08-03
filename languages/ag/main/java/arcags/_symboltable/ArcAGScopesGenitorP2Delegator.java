/* (c) https://github.com/MontiCore/monticore */
package arcags._symboltable;

import arcags.ArcAGsMill;
import arcags._visitor.ArcAGsTraverser;
import arcbasis.ArcBasisMill;
import arcbasis._ast.ASTArcElement;
import arcbasis._symboltable.ArcBasisScopesGenitorP2;
import com.google.common.base.Preconditions;
import org.codehaus.commons.nullanalysis.NotNull;

public class ArcAGScopesGenitorP2Delegator {

  protected IArcAGsGlobalScope globalScope;

  protected ArcAGsTraverser traverser;

  public ArcAGScopesGenitorP2Delegator() {
    this.globalScope = ArcAGsMill.globalScope();
    this.traverser = ArcAGsMill.traverser();
    this.init();
  }

  protected ArcAGsTraverser getTraverser() {
    return this.traverser;
  }

  protected void init() {
    this.initArcBasis();
    this.initArcAGs();
  }

  protected void initArcAGs() {
    ArcAGScopesGenitorP2 arcAGsScopesGenitorP2 = ArcAGsMill.scopesGenitorP2();
    this.getTraverser().add4ArcBasis(arcAGsScopesGenitorP2);
    this.getTraverser().add4ArcAGs(arcAGsScopesGenitorP2);
  }

  protected void initArcBasis() {
    ArcBasisScopesGenitorP2 scopesGenP2 = ArcBasisMill.scopesGenitorP2();
    this.getTraverser().add4ArcBasis(scopesGenP2);
    this.getTraverser().add4CompSymbols(scopesGenP2);
    this.getTraverser().add4TypeParameters(scopesGenP2);
    this.getTraverser().setArcBasisHandler(scopesGenP2);
  }

  public void createFromAST(@NotNull ASTArcElement rootNode) {
    Preconditions.checkNotNull(rootNode);
    rootNode.accept(traverser);
  }
}
