/* (c) https://github.com/MontiCore/monticore */
package arccore._symboltable;

import arcbasis.ArcBasisMill;
import arcbasis._ast.ASTArcElement;
import arcbasis._symboltable.ArcBasisSymbolTableCompleter;
import arccore.ArcCoreMill;
import arccore._visitor.ArcCoreTraverser;
import com.google.common.base.Preconditions;
import genericarc.GenericArcMill;
import genericarc._symboltable.GenericArcSymbolTableCompleter;
import org.codehaus.commons.nullanalysis.NotNull;

public class ArcCoreSymbolTableCompleterDelegator {

  protected IArcCoreGlobalScope globalScope;

  protected ArcCoreTraverser traverser;

  protected ArcCoreTraverser getTraverser() {
    return this.traverser;
  }

  public ArcCoreSymbolTableCompleterDelegator() {
    this.globalScope = ArcCoreMill.globalScope();
    this.traverser = ArcCoreMill.traverser();
    this.init();
  }

  protected void init() {
    this.initArcBasis();
    this.initGenericArc();
  }

  protected void initArcBasis() {
    ArcBasisSymbolTableCompleter arcBasisSymbolTableCompleter = ArcBasisMill.symbolTableCompleter();
    this.getTraverser().add4ArcBasis(arcBasisSymbolTableCompleter);
    this.getTraverser().setArcBasisHandler(arcBasisSymbolTableCompleter);
  }

  protected void initGenericArc() {
    GenericArcSymbolTableCompleter genericArcSymbolTableCompleter = GenericArcMill.symbolTableCompleter();
    this.getTraverser().add4GenericArc(genericArcSymbolTableCompleter);
    this.getTraverser().setGenericArcHandler(genericArcSymbolTableCompleter);
  }

  public void createFromAST(@NotNull ASTArcElement rootNode) {
    Preconditions.checkNotNull(rootNode);
    rootNode.accept(traverser);
  }
}