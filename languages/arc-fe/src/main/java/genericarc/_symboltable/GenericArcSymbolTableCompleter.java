/* (c) https://github.com/MontiCore/monticore */
package genericarc._symboltable;

import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis.check.ArcBasisSynthesizeComponent;
import arcbasis.check.ArcBasisSynthesizeType;
import arcbasis.check.CompTypeExpression;
import arcbasis.check.ISynthesizeComponent;
import com.google.common.base.Preconditions;
import de.monticore.symbols.basicsymbols._symboltable.TypeVarSymbol;
import de.monticore.types.check.ISynthesize;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.mcbasictypes.MCBasicTypesMill;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.prettyprint.MCBasicTypesFullPrettyPrinter;
import de.se_rwth.commons.logging.Log;
import genericarc._ast.ASTArcTypeParameter;
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

  protected ISynthesize typeSynthesizer;

  protected void setTypeSynthesizer(@NotNull ISynthesize typeSynthesizer) {
    Preconditions.checkNotNull(typeSynthesizer);
    this.typeSynthesizer = typeSynthesizer;
  }

  public ISynthesize getTypeSynthesizer() {
    return this.typeSynthesizer;
  }

  public Optional<SymTypeExpression> synthSymTypeFrom(@NotNull ASTMCType astType) {
    Preconditions.checkNotNull(astType);
    Preconditions.checkState(this.getTypeSynthesizer() != null);

    this.getTypeSynthesizer().init();
    astType.accept(this.getTypeSynthesizer().getTraverser());
    return this.getTypeSynthesizer().getResult();
  }

  public GenericArcSymbolTableCompleter() {
    this(MCBasicTypesMill.mcBasicTypesPrettyPrinter());
  }

  public GenericArcSymbolTableCompleter(@NotNull MCBasicTypesFullPrettyPrinter typePrinter) {
    this(typePrinter, new ArcBasisSynthesizeComponent(), new ArcBasisSynthesizeType());
  }

  public GenericArcSymbolTableCompleter(@NotNull MCBasicTypesFullPrettyPrinter typePrinter,
                                        @NotNull ISynthesizeComponent componentSynthesizer,
                                        @NotNull ISynthesize typeSynthesizer) {
    this.typePrinter = Preconditions.checkNotNull(typePrinter);
    this.componentSynthesizer = Preconditions.checkNotNull(componentSynthesizer);
    this.typeSynthesizer = Preconditions.checkNotNull(typeSynthesizer);
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

  @Override
  public void visit(@NotNull ASTArcTypeParameter typeParam) {
    Preconditions.checkNotNull(typeParam);
    Preconditions.checkArgument(typeParam.isPresentSymbol());

    TypeVarSymbol typeParamSym = typeParam.getSymbol();

    for(ASTMCType upperBound : typeParam.getUpperBoundList()) {
      Optional<SymTypeExpression> boundExpr = this.synthSymTypeFrom(upperBound);
      if(boundExpr.isPresent()) {
        typeParamSym.addSuperTypes(boundExpr.get());
      } else {
        Log.error(String.format("Could not create a SymTypeExpression from '%s'",
          upperBound.printType(this.getTypePrinter())), upperBound.get_SourcePositionStart()
        );
      }
    }
  }
}