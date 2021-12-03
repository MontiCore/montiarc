/* (c) https://github.com/MontiCore/monticore */
package genericarc._symboltable;

import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis.check.ArcBasisSynthesizeComponent;
import arcbasis.check.CompTypeExpression;
import arcbasis.check.ISynthesizeComponent;
import com.google.common.base.Preconditions;
import de.monticore.types.mcbasictypes.MCBasicTypesMill;
import de.monticore.types.prettyprint.MCBasicTypesFullPrettyPrinter;
import de.se_rwth.commons.logging.Log;
import genericarc._ast.ASTGenericComponentHead;
import genericarc._visitor.GenericArcHandler;
import genericarc._visitor.GenericArcTraverser;
import genericarc._visitor.GenericArcVisitor2;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.Optional;

public class GenericArcSymbolTableCompleter implements GenericArcVisitor2, GenericArcHandler {

  protected GenericArcTraverser traverser ;

  @Override
  public void setTraverser(@NotNull GenericArcTraverser traverser) {
    Preconditions.checkNotNull(traverser);
    this.traverser = traverser;
  }

  @Override
  public GenericArcTraverser getTraverser() {
    return this.traverser;
  }

  protected MCBasicTypesFullPrettyPrinter typePrinter;

  public MCBasicTypesFullPrettyPrinter getTypePrinter() {
    return this.typePrinter;
  }

  protected void setTypePrinter(@NotNull MCBasicTypesFullPrettyPrinter typesPrinter) {
    Preconditions.checkNotNull(typesPrinter);
    this.typePrinter = typesPrinter;
  }

  protected ISynthesizeComponent componentSynthesizer;

  public void setComponentSynthesizer(@NotNull ISynthesizeComponent componentSynthesizer) {
    Preconditions.checkNotNull(componentSynthesizer);
    this.componentSynthesizer = componentSynthesizer;
  }

  public ISynthesizeComponent getComponentSynthesizer() {
    return this.componentSynthesizer;
  }

  public GenericArcSymbolTableCompleter() {
    this(MCBasicTypesMill.mcBasicTypesPrettyPrinter());
  }

  public GenericArcSymbolTableCompleter(@NotNull MCBasicTypesFullPrettyPrinter typePrinter) {
    this(typePrinter, new ArcBasisSynthesizeComponent());
  }

  public GenericArcSymbolTableCompleter(@NotNull MCBasicTypesFullPrettyPrinter typePrinter,
                                      @NotNull ISynthesizeComponent componentSynthesizer) {
    this.typePrinter = Preconditions.checkNotNull(typePrinter);
    this.componentSynthesizer = Preconditions.checkNotNull(componentSynthesizer);
  }

  @Override
  public void visit(@NotNull ASTGenericComponentHead node) {
    Preconditions.checkNotNull(node);
    Preconditions.checkNotNull(node.getEnclosingScope());
    Preconditions.checkArgument(node.getEnclosingScope().isPresentSpanningSymbol());
    Preconditions.checkArgument(node.getEnclosingScope().getSpanningSymbol() instanceof ComponentTypeSymbol);

    if (node.isPresentParent()) {
      Optional<CompTypeExpression> parentTypeExpr = this.getComponentSynthesizer().synthesizeFrom(node.getParent());
      if (parentTypeExpr.isPresent()) {
        ComponentTypeSymbol sym = (ComponentTypeSymbol) node.getEnclosingScope().getSpanningSymbol();
        sym.setParent(parentTypeExpr.get());
      } else {
        Log.error(String.format("Could not create a component type expression from '%s'",
          node.getParent().printType(this.getTypePrinter())), node.get_SourcePositionStart()
        );
      }
    }
  }
}