/* (c) https://github.com/MontiCore/monticore */
package montiarc._symboltable;

import arcbasis.ArcBasisMill;
import arcbasis._symboltable.ArcBasisSymbolTableCompleter;
import com.google.common.base.Preconditions;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.prettyprint.MCSimpleGenericTypesFullPrettyPrinter;
import montiarc.MontiArcMill;
import montiarc._ast.ASTMACompilationUnit;
import montiarc._visitor.MontiArcTraverser;
import montiarc.check.MontiArcSynthesizeComponent;
import org.codehaus.commons.nullanalysis.NotNull;

public class MontiArcSymbolTableCompleterDelegator {

  protected IMontiArcGlobalScope globalScope;

  protected MontiArcTraverser traverser;

  protected MontiArcTraverser getTraverser() {
    return this.traverser;
  }

  public MontiArcSymbolTableCompleterDelegator() {
    this.globalScope = MontiArcMill.globalScope();
    this.traverser = MontiArcMill.traverser();
    this.init();
  }

  protected void init() {
    this.initArcBasis();
  }

  protected void initArcBasis() {
    ArcBasisSymbolTableCompleter arcBasisSymbolTableCompleter = ArcBasisMill.symbolTableCompleter();
    arcBasisSymbolTableCompleter.setTypePrinter(new MCSimpleGenericTypesFullPrettyPrinter(new IndentPrinter()));
    arcBasisSymbolTableCompleter.setCompTypeExpressionSynth(new MontiArcSynthesizeComponent());

    this.getTraverser().add4ArcBasis(arcBasisSymbolTableCompleter);
    this.getTraverser().setArcBasisHandler(arcBasisSymbolTableCompleter);
  }

  public void createFromAST(@NotNull ASTMACompilationUnit rootNode) {
    Preconditions.checkNotNull(rootNode);
    rootNode.accept(traverser);
  }
}