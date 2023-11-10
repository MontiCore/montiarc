/* (c) https://github.com/MontiCore/monticore */
package genericarc._cocos;

import arcbasis._ast.ASTComponentInstance;
import arcbasis._ast.ASTComponentInstantiation;
import arcbasis._cocos.ArcBasisASTComponentInstantiationCoCo;
import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis.check.CompTypeExpression;
import com.google.common.base.Preconditions;
import de.monticore.symbols.basicsymbols._symboltable.TypeVarSymbol;
import de.monticore.types.check.CompKindExpression;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.mccollectiontypes._ast.ASTMCTypeArgument;
import de.monticore.types.mcsimplegenerictypes._ast.ASTMCBasicGenericType;
import de.monticore.types3.SymTypeRelations;
import de.se_rwth.commons.logging.Log;
import genericarc.check.TypeExprOfGenericComponent;
import montiarc.util.GenericArcError;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.List;
import java.util.Optional;

/**
 * A generic component may have type parameters that have upper bounds. In this case this coco checks for component
 * instantiations that type arguments passed for bounded type parameters are subtypes of these bounds.
 */
public class SubcomponentTypeBound implements ArcBasisASTComponentInstantiationCoCo {

  /**
   * Checks that type arguments for bounded type parameters are subtypes of the parameter bounds. Else logs an error.
   *
   * @param compTypeExpr     The instantiating component type expression that contains the type arguments that should be
   *                         checked against the upper bounds of their corresponding type parameters.
   * @param astInstantiation The AST node of the component instantiation for which the type arguments should be checked.
   *                         We need the AST node is needed for printing the error message.
   */
  protected void checkTypeBoundSatisfaction(@NotNull TypeExprOfGenericComponent compTypeExpr, @NotNull ASTComponentInstantiation astInstantiation) {
    Preconditions.checkNotNull(compTypeExpr);
    Preconditions.checkNotNull(astInstantiation);

    ComponentTypeSymbol compTypeSymbol = compTypeExpr.getTypeInfo();

    for (TypeVarSymbol typeVar : compTypeSymbol.getTypeParameters()) {
      Optional<SymTypeExpression> typeVarBinding = compTypeExpr.getTypeBindingFor(typeVar);
      if (typeVarBinding.isPresent()) {
        for (SymTypeExpression aBound : typeVar.getSuperTypesList()) {
          SymTypeExpression bound = aBound.deepClone();
          bound.replaceTypeVariables(compTypeExpr.getTypeVarBindings());
          if (!SymTypeRelations.isSubTypeOf(typeVarBinding.get(), bound)) {
            Log.error(
                GenericArcError.TYPE_ARG_IGNORES_UPPER_BOUND.format(typeVarBinding.get().print(), bound.print()),
                astInstantiation.get_SourcePositionStart(), astInstantiation.get_SourcePositionEnd()
            );
          }
        }
      } else {
        Log.debug(String.format("Not checking coco '%s' on type parameter '%s' of component type '%s' for component " +
                "instantiation at '%s' because the binding for that type parameter is not set.",
            SubcomponentTypeBound.class.getSimpleName(), typeVar.getName(), compTypeSymbol.getName(),
            astInstantiation.get_SourcePositionStart()), "CoCos");
        return;
      }
    }
  }

  @Override
  public void check(@NotNull ASTComponentInstantiation node) {
    Preconditions.checkNotNull(node);
    Preconditions.checkArgument(!node.getComponentInstanceList().isEmpty());

    Preconditions.checkArgument(node.streamComponentInstances().allMatch(ASTComponentInstance::isPresentSymbol));

    if (!node.streamComponentInstances().allMatch(inst -> inst.getSymbol().isTypePresent())) {
      Log.debug("Could not perform coco check '" + this.getClass().getSimpleName() + "', due to missing type.", this.getClass().getSimpleName());
      return;
    }

    Preconditions.checkArgument(
        node.streamComponentInstances().skip(1).allMatch(inst -> inst.getSymbol().getType().deepEquals(node.getComponentInstance(0).getSymbol().getType())),
        "Some instances of '%s' at '%s' have mismatching '%s's as their types. Your symbol table completion seems to " + "be inconsistent.",
        ASTComponentInstantiation.class.getSimpleName(), node.get_SourcePositionStart(), CompTypeExpression.class.getSimpleName());

    CompKindExpression compTypeExpr = node.getComponentInstance(0).getSymbol().getType();
    if (compTypeExpr instanceof TypeExprOfGenericComponent) {
      checkTypeArgsAreNotTooFew((TypeExprOfGenericComponent) compTypeExpr, node);

      checkTypeArgsAreNotTooMany((TypeExprOfGenericComponent) compTypeExpr, node);
      checkTypeBoundSatisfaction((TypeExprOfGenericComponent) compTypeExpr, node);
    }
  }

  /**
   * Checks that there are enough type arguments provided to bind all mandatory type parameters of the
   * component type that should be instantiated.
   *
   * @param node The AST node of the component instance whose type arguments should be checked.
   */
  protected void checkTypeArgsAreNotTooFew(@NotNull TypeExprOfGenericComponent compTypeExpr, @NotNull ASTComponentInstantiation node) {
    ComponentTypeSymbol compTypeSymbol = compTypeExpr.getTypeInfo();

    List<TypeVarSymbol> parentSymTypeParameters = compTypeSymbol.getTypeParameters();
    List<SymTypeExpression> args = compTypeExpr.getTypeBindingsAsList();
    if (parentSymTypeParameters.size() > args.size()) {
      Log.error(
        GenericArcError.TOO_FEW_TYPE_ARGUMENTS.format(parentSymTypeParameters.size(), args.size()),
        node.get_SourcePositionStart(), node.get_SourcePositionEnd()
      );
    }
  }

  /**
   * Checks that there are not more type arguments provided than there are type parameters in the
   * component type that should be instantiated.
   *
   * @param node The AST node of the component instance whose type arguments should be checked.
   */
  protected void checkTypeArgsAreNotTooMany(@NotNull TypeExprOfGenericComponent compTypeExpr, @NotNull ASTComponentInstantiation node) {
    Preconditions.checkArgument(node.getMCType() instanceof ASTMCBasicGenericType);
    ComponentTypeSymbol compTypeSymbol = compTypeExpr.getTypeInfo();

    List<TypeVarSymbol> parentSymTypeParameters = compTypeSymbol.getTypeParameters();
    List<ASTMCTypeArgument> args = ((ASTMCBasicGenericType) node.getMCType()).getMCTypeArgumentList();

    if (parentSymTypeParameters.size() < args.size()) {
      Log.error(
        GenericArcError.TOO_MANY_TYPE_ARGUMENTS.format(parentSymTypeParameters.size(), args.size()),
        node.get_SourcePositionStart(), node.get_SourcePositionEnd()
      );
    }
  }
}
