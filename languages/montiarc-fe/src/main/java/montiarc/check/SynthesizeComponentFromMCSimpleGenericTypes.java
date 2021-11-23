/* (c) https://github.com/MontiCore/monticore */
package montiarc.check;

import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis.check.CompTypeExpression;
import arcbasis.check.SynthCompTypeResult;
import arcbasis.util.ArcError;
import com.google.common.base.Preconditions;
import de.monticore.types.check.ISynthesize;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.TypeCheck;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mccollectiontypes._ast.ASTMCBasicTypeArgument;
import de.monticore.types.mccollectiontypes._ast.ASTMCPrimitiveTypeArgument;
import de.monticore.types.mccollectiontypes._ast.ASTMCTypeArgument;
import de.monticore.types.mcsimplegenerictypes._ast.ASTMCBasicGenericType;
import de.monticore.types.mcsimplegenerictypes._ast.ASTMCCustomTypeArgument;
import de.monticore.types.mcsimplegenerictypes._visitor.MCSimpleGenericTypesHandler;
import de.monticore.types.mcsimplegenerictypes._visitor.MCSimpleGenericTypesTraverser;
import de.se_rwth.commons.logging.Log;
import genericarc._symboltable.IGenericArcScope;
import genericarc.check.TypeExprOfGenericComponent;
import montiarc._symboltable.IMontiArcScope;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * A visitor (a handler indeed) that creates {@link CompTypeExpression}s from {@link ASTMCBasicGenericType}s, given
 * that there is a ComponentTypeSymbol with matching TypeParameters which is represented by the
 * {@link ASTMCBasicGenericType}.
 */
public class SynthesizeComponentFromMCSimpleGenericTypes implements MCSimpleGenericTypesHandler {

  protected MCSimpleGenericTypesTraverser traverser;

  /** Common state with other visitors, if this visitor is part of a visitor composition. */
  protected SynthCompTypeResult resultWrapper;

  /**
   * Used to create {@link SymTypeExpression}s for the ast-representation of the generic component type's type.
   * Note that the {@link TypeCheck} misses a Deriver to derive types of
   * {@link de.monticore.expressions.expressionsbasis._ast.ASTExpression}, as we are onl interested in synthesizing
   * @code SymTypeExpression}s from {@link ASTMCType}s. Thus expect errors, if you try to use the TypeCheck for
   * operating on {@code ASTExpression}s.
   */
  protected TypeCheck synthOOTypeExpressions;

  public SynthesizeComponentFromMCSimpleGenericTypes(@NotNull SynthCompTypeResult resultWrapper) {
    this(resultWrapper, new MontiArcSynthesizeType());
  }

  public SynthesizeComponentFromMCSimpleGenericTypes(@NotNull SynthCompTypeResult resultWrapper,
                                                     @NotNull ISynthesize synthOOTypeExpressions) {
    Preconditions.checkNotNull(resultWrapper);
    Preconditions.checkNotNull(synthOOTypeExpressions);

    this.resultWrapper = resultWrapper;   // We are not interested in using a ↓ Deriver for Expressions.
    this.synthOOTypeExpressions = new TypeCheck(synthOOTypeExpressions, null);
  }

  @Override
  public MCSimpleGenericTypesTraverser getTraverser() {
    return traverser;
  }

  @Override
  public void setTraverser(@NotNull MCSimpleGenericTypesTraverser traverser) {
    this.traverser = Preconditions.checkNotNull(traverser);
  }

  @Override
  public void handle(@NotNull ASTMCBasicGenericType mcType) {
    Preconditions.checkNotNull(mcType);
    Preconditions.checkNotNull(mcType.getEnclosingScope());
    Preconditions.checkArgument(mcType.getEnclosingScope() instanceof IMontiArcScope);

    IGenericArcScope enclScope = (IGenericArcScope) mcType.getEnclosingScope();
    String compName = String.join(".", mcType.getNameList());
    Optional<ComponentTypeSymbol> compSym = enclScope.resolveComponentType(compName);

    if(!compSym.isPresent()) {
      Log.error(ArcError.SYMBOL_NOT_FOUND.format(compName), mcType.get_SourcePositionStart());
      this.resultWrapper.setCurrentResultAbsent();
    } else {
      List<SymTypeExpression> typeArgExpressions = typeArgumentsToTypes(mcType.getMCTypeArgumentList()).stream()
        .map(typeArg -> synthOOTypeExpressions.symTypeFromAST(typeArg))
        .collect(Collectors.toList());
      this.resultWrapper.setCurrentResult(new TypeExprOfGenericComponent(compSym.get(), typeArgExpressions));
    }
  }

  /**
   * Given that all {@link ASTMCTypeArgument}s in {@code typeArgs} are {@link ASTMCType}s, this method returns a list
   * with these {@code ASTMCType}s in the same order. Else, an exception is thrown.
   */
  protected List<ASTMCType> typeArgumentsToTypes(@NotNull List<ASTMCTypeArgument> typeArgs) {
    Preconditions.checkNotNull(typeArgs);
    Preconditions.checkArgument(typeArgs.stream().allMatch(
        typeArg -> typeArg instanceof ASTMCBasicTypeArgument
          || typeArg instanceof ASTMCPrimitiveTypeArgument
          || typeArg instanceof ASTMCCustomTypeArgument),
      "Only Type arguments of the types '%s', '%s', '%s' are supported in GenericArc. For you that means " +
        "that you can use other MontiCore types as type arguments. But you can not use WildCards as type arguments, " +
        "such as GenericType<? extends Person>.", ASTMCBasicTypeArgument.class.getName(),
      ASTMCPrimitiveTypeArgument.class.getName(), ASTMCCustomTypeArgument.class.getName()
    );

    List<ASTMCType> types = new ArrayList<>(typeArgs.size());
    for(ASTMCTypeArgument typeArg : typeArgs) {
      if(typeArg instanceof ASTMCBasicTypeArgument) {
        types.add(((ASTMCBasicTypeArgument) typeArg).getMCQualifiedType());
      } else if (typeArg instanceof ASTMCPrimitiveTypeArgument) {
        types.add(((ASTMCPrimitiveTypeArgument) typeArg).getMCPrimitiveType());
      } else if (typeArg instanceof ASTMCCustomTypeArgument) {
        types.add(((ASTMCCustomTypeArgument) typeArg).getMCType());
      } else {
        throw new IllegalStateException(); // Should have been caught by a precondition
      }
    }
    return types;
  }
}
