/* (c) https://github.com/MontiCore/monticore */
package montiarc.check;

import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis.check.CompTypeExpression;
import arcbasis.check.SynthCompTypeResult;
import com.google.common.base.Preconditions;
import de.monticore.symboltable.resolving.ResolvedSeveralEntriesForSymbolException;
import de.monticore.types.check.CompKindExpression;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mccollectiontypes._ast.ASTMCBasicTypeArgument;
import de.monticore.types.mccollectiontypes._ast.ASTMCPrimitiveTypeArgument;
import de.monticore.types.mccollectiontypes._ast.ASTMCTypeArgument;
import de.monticore.types.mcsimplegenerictypes._ast.ASTMCBasicGenericType;
import de.monticore.types.mcsimplegenerictypes._ast.ASTMCCustomTypeArgument;
import de.monticore.types.mcsimplegenerictypes._visitor.MCSimpleGenericTypesHandler;
import de.monticore.types.mcsimplegenerictypes._visitor.MCSimpleGenericTypesTraverser;
import de.monticore.types3.TypeCheck3;
import de.se_rwth.commons.logging.Log;
import arcbasis.check.TypeExprOfGenericComponent;
import montiarc._symboltable.IMontiArcScope;
import montiarc.util.ArcError;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A visitor (a handler indeed) that creates {@link CompTypeExpression}s from {@link ASTMCBasicGenericType}s, given
 * that there is a ComponentTypeSymbol with matching TypeParameters which is represented by the
 * {@link ASTMCBasicGenericType}.
 */
public class SynthesizeComponentFromMCSimpleGenericTypes implements MCSimpleGenericTypesHandler {

  protected MCSimpleGenericTypesTraverser traverser;

  /**
   * Common state with other visitors, if this visitor is part of a visitor composition.
   */
  protected SynthCompTypeResult resultWrapper;

  public SynthesizeComponentFromMCSimpleGenericTypes(@NotNull SynthCompTypeResult resultWrapper) {
    Preconditions.checkNotNull(resultWrapper);

    this.resultWrapper = resultWrapper;
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

    IMontiArcScope enclScope = (IMontiArcScope) mcType.getEnclosingScope();
    String compName = String.join(".", mcType.getNameList());
    List<ComponentTypeSymbol> compSym = enclScope.resolveComponentTypeMany(compName);

    if (compSym.isEmpty()) {
      Log.error(ArcError.MISSING_COMPONENT.format(mcType.getNameList().stream().reduce("", String::concat)),
        mcType.get_SourcePositionStart(), mcType.get_SourcePositionEnd()
      );
      this.resultWrapper.setResultAbsent();
    } else {
      if (compSym.size() > 1) {
        Log.error(ArcError.AMBIGUOUS_REFERENCE.format(compSym.get(0).getFullName(), compSym.get(1).getFullName()),
          mcType.get_SourcePositionStart(), mcType.get_SourcePositionEnd()
        );
      }
      List<SymTypeExpression> typeArgExpressions = typeArgumentsToTypes(mcType.getMCTypeArgumentList()).stream()
        .map(typeArg -> {
          SymTypeExpression typeResult = null;
          try {
            typeResult = TypeCheck3.symTypeFromAST(typeArg);
          }  catch (ResolvedSeveralEntriesForSymbolException ignored) { }
          return typeResult;
        })
        .collect(Collectors.toList());

      CompKindExpression result = new TypeExprOfGenericComponent(compSym.get(0), typeArgExpressions);
      result.setSourceNode(mcType);
      this.resultWrapper.setResult(result);
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
      "Only Type arguments of the types '%s', '%s', '%s' are supported in ArcBasis. For you that means " +
        "that you can use other MontiCore types as type arguments. But you can not use WildCards as type arguments, " +
        "such as GenericType<? extends Person>.", ASTMCBasicTypeArgument.class.getName(),
      ASTMCPrimitiveTypeArgument.class.getName(), ASTMCCustomTypeArgument.class.getName()
    );

    List<ASTMCType> types = new ArrayList<>(typeArgs.size());
    for (ASTMCTypeArgument typeArg : typeArgs) {
      if (typeArg instanceof ASTMCBasicTypeArgument) {
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
