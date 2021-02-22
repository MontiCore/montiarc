/* (c) https://github.com/MontiCore/monticore */
package genericarc._symboltable;


import arcbasis._symboltable.ArcBasisScopesGenitor;
import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis._symboltable.IArcBasisScope;
import com.google.common.base.Preconditions;
import de.monticore.symbols.basicsymbols._symboltable.TypeVarSymbol;
import de.monticore.symboltable.IScopeSpanningSymbol;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.mcbasictypes.MCBasicTypesMill;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.prettyprint.MCBasicTypesFullPrettyPrinter;
import genericarc.GenericArcMill;
import genericarc._ast.ASTArcTypeParameter;
import genericarc._ast.ASTGenericComponentHead;

import java.util.Deque;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public class GenericArcScopesGenitor extends GenericArcScopesGenitorTOP {

  public GenericArcScopesGenitor() {
  }

  public GenericArcScopesGenitor(Deque<? extends IGenericArcScope> scopeStack) {
    super(scopeStack);
  }

  /**
   *  used for mapping types to expressions, the default can only deal with MCBasicTypes
   */
  private BiFunction<ASTMCType, IArcBasisScope, SymTypeExpression> expressionMapper
      = ArcBasisScopesGenitor.mapWith(MCBasicTypesMill.mcBasicTypesPrettyPrinter());

  /**
   * override the printer if you want to be able to process more types than {@link MCBasicTypesFullPrettyPrinter} can
   * @param newPrinter {@link #expressionMapper}
   */
  public void setMapper(BiFunction<ASTMCType, IArcBasisScope, SymTypeExpression> newPrinter) {
    expressionMapper = newPrinter;
  }

  @Override
  public void endVisit(ASTGenericComponentHead head) {
    Preconditions.checkNotNull(head);
    Preconditions.checkNotNull(head.getEnclosingScope());
    Preconditions.checkArgument(head.getEnclosingScope().isPresentSpanningSymbol());
    IScopeSpanningSymbol spanningSymbol = head.getEnclosingScope().getSpanningSymbol();
    Preconditions.checkState(spanningSymbol instanceof ComponentTypeSymbol, "According to the Grammar, the NonTerminal ComponentHead is only used in ComponentType");

    ComponentTypeSymbol component = (ComponentTypeSymbol) spanningSymbol;

    head.streamArcTypeParameters()
        .map(type -> buildVariable(type, head.getEnclosingScope()))
        .forEach(component::addTypeParameter);
  }

  /**
   * transforms an arc parameter to a variable
   * @param type type to transform
   * @param scope enclosing scope of the variable to create
   * @return the type as variable symbol
   */
  private TypeVarSymbol buildVariable(ASTArcTypeParameter type, IGenericArcScope scope){
    return GenericArcMill
        .typeVarSymbolBuilder()
        .addAllSuperTypes(type.streamUpperBound()
                              .map(parent -> expressionMapper.apply(parent, scope))
                              .collect(Collectors.toList()))
        .setName(type.getName())
        .setEnclosingScope(scope)
        .build();
  }
}