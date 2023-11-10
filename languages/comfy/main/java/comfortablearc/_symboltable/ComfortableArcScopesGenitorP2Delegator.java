/* (c) https://github.com/MontiCore/monticore */
package comfortablearc._symboltable;

import arcbasis.ArcBasisMill;
import arcbasis._ast.ASTArcElement;
import arcbasis._symboltable.ArcBasisScopesGenitorP2;
import com.google.common.base.Preconditions;
import comfortablearc.ComfortableArcMill;
import comfortablearc._visitor.ComfortableArcTraverser;
import org.codehaus.commons.nullanalysis.NotNull;

public class ComfortableArcScopesGenitorP2Delegator {

  protected IComfortableArcGlobalScope globalScope;

  protected ComfortableArcTraverser traverser;

  protected ComfortableArcTraverser getTraverser() {
    return this.traverser;
  }

  public ComfortableArcScopesGenitorP2Delegator() {
    this.globalScope = ComfortableArcMill.globalScope();
    this.traverser = ComfortableArcMill.traverser();
    this.init();
  }

  protected void init() {
    this.initArcBasis();
  }

  protected void initArcBasis() {
    ArcBasisScopesGenitorP2 arcBasisScopesGenitorP2 = ArcBasisMill.scopesGenitorP2();
    this.getTraverser().add4ArcBasis(arcBasisScopesGenitorP2);
    this.getTraverser().add4CompSymbols(arcBasisScopesGenitorP2);
    this.getTraverser().setArcBasisHandler(arcBasisScopesGenitorP2);
  }

  public void createFromAST(@NotNull ASTArcElement rootNode) {
    Preconditions.checkNotNull(rootNode);
    rootNode.accept(traverser);
  }
}