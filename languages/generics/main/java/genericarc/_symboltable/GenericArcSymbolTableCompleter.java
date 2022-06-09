/* (c) https://github.com/MontiCore/monticore */
package genericarc._symboltable;

import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis._visitor.IFullPrettyPrinter;
import arcbasis.check.ArcBasisSynthesizeComponent;
import arcbasis.check.CompTypeExpression;
import arcbasis.check.IArcTypeCalculator;
import arcbasis.check.ISynthesizeComponent;
import com.google.common.base.Preconditions;
import de.monticore.symbols.basicsymbols._symboltable.TypeVarSymbol;
import de.monticore.symboltable.resolving.ResolvedSeveralEntriesForSymbolException;
import de.monticore.types.check.TypeCheckResult;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.se_rwth.commons.logging.Log;
import genericarc.GenericArcMill;
import genericarc._ast.ASTArcTypeParameter;
import genericarc._ast.ASTGenericComponentHead;
import genericarc._visitor.GenericArcHandler;
import genericarc._visitor.GenericArcTraverser;
import genericarc._visitor.GenericArcVisitor2;
import genericarc.check.GenericArcTypeCalculator;
import montiarc.util.ArcError;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.Optional;

public class GenericArcSymbolTableCompleter implements GenericArcVisitor2, GenericArcHandler {

  protected GenericArcTraverser traverser;
  protected IFullPrettyPrinter typePrinter;
  protected ISynthesizeComponent componentSynthesizer;
  protected IArcTypeCalculator typeCalculator;

  public GenericArcSymbolTableCompleter() {
    this(GenericArcMill.fullPrettyPrinter());
  }

  public GenericArcSymbolTableCompleter(@NotNull IFullPrettyPrinter typePrinter) {
    this(typePrinter, new ArcBasisSynthesizeComponent(), new GenericArcTypeCalculator());
  }

  public GenericArcSymbolTableCompleter(@NotNull IFullPrettyPrinter typePrinter,
                                        @NotNull ISynthesizeComponent componentSynthesizer,
                                        @NotNull IArcTypeCalculator typeCalculator) {
    this.typePrinter = Preconditions.checkNotNull(typePrinter);
    this.componentSynthesizer = Preconditions.checkNotNull(componentSynthesizer);
    this.typeCalculator = Preconditions.checkNotNull(typeCalculator);
  }

  @Override
  public GenericArcTraverser getTraverser() {
    return this.traverser;
  }

  @Override
  public void setTraverser(@NotNull GenericArcTraverser traverser) {
    Preconditions.checkNotNull(traverser);
    this.traverser = traverser;
  }

  public IFullPrettyPrinter getTypePrinter() {
    return this.typePrinter;
  }

  protected void setTypePrinter(@NotNull IFullPrettyPrinter typesPrinter) {
    Preconditions.checkNotNull(typesPrinter);
    this.typePrinter = typesPrinter;
  }

  public ISynthesizeComponent getComponentSynthesizer() {
    return this.componentSynthesizer;
  }

  public void setComponentSynthesizer(@NotNull ISynthesizeComponent componentSynthesizer) {
    Preconditions.checkNotNull(componentSynthesizer);
    this.componentSynthesizer = componentSynthesizer;
  }

  public IArcTypeCalculator getTypeCalculator() {
    return this.typeCalculator;
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
          this.getTypePrinter().prettyprint(node.getParent())), node.get_SourcePositionStart()
        );
      }
    }
  }

  @Override
  public void visit(@NotNull ASTArcTypeParameter typeParam) {
    Preconditions.checkNotNull(typeParam);
    Preconditions.checkArgument(typeParam.isPresentSymbol());

    TypeVarSymbol typeParamSym = typeParam.getSymbol();

    for (ASTMCType upperBound : typeParam.getUpperBoundList()) {
      try {
        TypeCheckResult boundExpr = this.getTypeCalculator().synthesizeType(upperBound);
        if (boundExpr.isPresentResult()) {
          typeParamSym.addSuperTypes(boundExpr.getResult());
        } else {
          Log.error(String.format("Could not create a SymTypeExpression from '%s'",
            this.getTypePrinter().prettyprint(upperBound)), upperBound.get_SourcePositionStart()
          );
        }
      }  catch (ResolvedSeveralEntriesForSymbolException e) {
        Log.error(ArcError.SYMBOL_TOO_MANY_FOUND.format(this.getTypePrinter().prettyprint(upperBound)), upperBound.get_SourcePositionStart());
      }
    }
  }
}