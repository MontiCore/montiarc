/* (c) https://github.com/MontiCore/monticore */
package genericarc._symboltable;

import arcbasis.ArcBasisMill;
import arcbasis._ast.ASTArcElement;
import arcbasis._symboltable.ArcBasisSymbolTableCompleter;
import com.google.common.base.Preconditions;
import genericarc.GenericArcMill;
import genericarc._visitor.GenericArcTraverser;
import org.codehaus.commons.nullanalysis.NotNull;

public class GenericArcSymbolTableCompleterDelegator {

  protected IGenericArcGlobalScope globalScope;

  protected GenericArcTraverser traverser;

  protected GenericArcTraverser getTraverser() {
    return this.traverser;
  }

  public GenericArcSymbolTableCompleterDelegator() {
    this.globalScope = GenericArcMill.globalScope();
    this.traverser = GenericArcMill.traverser();
    this.init();
  }

  protected void init() {
    this.initArcBasis();
  }

  protected void initArcBasis() {
    ArcBasisSymbolTableCompleter arcBasisSymbolTableCompleter = ArcBasisMill.symbolTableCompleter();
    this.getTraverser().add4ArcBasis(arcBasisSymbolTableCompleter);
    this.getTraverser().setArcBasisHandler(arcBasisSymbolTableCompleter);
  }

  public void createFromAST(@NotNull ASTArcElement rootNode) {
    Preconditions.checkNotNull(rootNode);
    rootNode.accept(traverser);
  }
}