/* (c) https://github.com/MontiCore/monticore */
package genericarc._symboltable;

import arcbasis.ArcBasisMill;
import arcbasis._ast.ASTArcElement;
import arcbasis._symboltable.ArcBasisScopesGenitorP2;
import com.google.common.base.Preconditions;
import genericarc.GenericArcMill;
import genericarc._visitor.GenericArcTraverser;
import org.codehaus.commons.nullanalysis.NotNull;

public class GenericArcScopesGenitorP2Delegator {

  protected IGenericArcGlobalScope globalScope;

  protected GenericArcTraverser traverser;

  protected GenericArcTraverser getTraverser() {
    return this.traverser;
  }

  public GenericArcScopesGenitorP2Delegator() {
    this.globalScope = GenericArcMill.globalScope();
    this.traverser = GenericArcMill.traverser();
    this.init();
  }

  protected void init() {
    this.initArcBasis();
    this.initGenericArc();
  }

  protected void initArcBasis() {
    ArcBasisScopesGenitorP2 arcBasisScopesGenitorP2 = ArcBasisMill.scopesGenitorP2();
    this.getTraverser().add4ArcBasis(arcBasisScopesGenitorP2);
    this.getTraverser().setArcBasisHandler(arcBasisScopesGenitorP2);
  }

  protected void initGenericArc() {
    GenericArcScopesGenitorP2 genericArcScopesGenitorP2 = GenericArcMill.scopesGenitorP2();
    this.getTraverser().add4GenericArc(genericArcScopesGenitorP2);
    this.getTraverser().setGenericArcHandler(genericArcScopesGenitorP2);
  }

  public void createFromAST(@NotNull ASTArcElement rootNode) {
    Preconditions.checkNotNull(rootNode);
    rootNode.accept(traverser);
  }
}