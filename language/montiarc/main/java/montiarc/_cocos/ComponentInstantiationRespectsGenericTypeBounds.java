/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcbasis._ast.ASTComponentInstantiation;
import arcbasis._cocos.ArcBasisASTComponentInstantiationCoCo;
import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis.check.CompTypeExpression;
import com.google.common.base.Preconditions;
import de.monticore.symbols.basicsymbols._symboltable.TypeVarSymbol;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.TypeCheck;
import de.monticore.types.mccollectiontypes._ast.ASTMCTypeArgument;
import de.monticore.types.mcsimplegenerictypes._ast.ASTMCBasicGenericType;
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
public class ComponentInstantiationRespectsGenericTypeBounds implements ArcBasisASTComponentInstantiationCoCo {
  /**
   * Checks that type arguments for bounded type parameters are subtypes of the parameter bounds. Else logs an error.
   *
   * @param compTypeExpr     The instantiating component type expression that contains the type arguments that should be
   *                         checked against the upper bounds of their corresponding type parameters.
   * @param astInstantiation The AST node of the component instantiation for which the type arguments should be checked.
   *                         We need the AST node is needed for printing the error message.
   */
  protected static void checkTypeBoundSatisfaction(@NotNull TypeExprOfGenericComponent compTypeExpr, @NotNull ASTComponentInstantiation astInstantiation) {
    Preconditions.checkNotNull(compTypeExpr);
    Preconditions.checkNotNull(astInstantiation);

    ComponentTypeSymbol compTypeSymbol = compTypeExpr.getTypeInfo();

    for (TypeVarSymbol typeVar : compTypeSymbol.getTypeParameters()) {
      Optional<SymTypeExpression> typeVarBinding = compTypeExpr.getBindingFor(typeVar);
      if (typeVarBinding.isPresent()) {
        for (SymTypeExpression bound : typeVar.getSuperTypesList()) {
          if (!TypeCheck.compatible(bound, typeVarBinding.get())) {
            Log.error(
                GenericArcError.TYPE_ARG_IGNORES_UPPER_BOUND.format(typeVarBinding.get().print(), bound.print(), typeVar.getName(), compTypeSymbol.getName()),
                astInstantiation.get_SourcePositionStart());
          }
        }
      } else {
        Log.debug(String.format("Not checking coco '%s' on type parameter '%s' of component type '%s' for component " +
                "instantiation at '%s' because the binding for that type parameter is not set.",
            ComponentInstantiationRespectsGenericTypeBounds.class.getSimpleName(), typeVar.getName(), compTypeSymbol.getName(),
            astInstantiation.get_SourcePositionStart()), "CoCos");
        return;
      }
    }
  }

  @Override
  public void check(@NotNull ASTComponentInstantiation node) {
    Preconditions.checkNotNull(node);
    Preconditions.checkArgument(!node.getComponentInstanceList().isEmpty(), "'%s' at '%s' misses instances.", ASTComponentInstantiation.class.getSimpleName(),
        node.get_SourcePositionStart());

    Preconditions.checkArgument(node.streamComponentInstances().allMatch(inst -> inst.isPresentSymbol()),
        "Some instances of '%s' at '%s' have no symbol. Have you run the genitor (and completer) before checking " + "coco '%s'?",
        ASTComponentInstantiation.class.getSimpleName(), node.get_SourcePositionStart(), this.getClass().getSimpleName());

    if (!node.streamComponentInstances().allMatch(inst -> inst.getSymbol().isPresentType())) {
      Log.debug("Could not perform coco check '" + this.getClass().getSimpleName() + "', due to missing type.", this.getClass().getSimpleName());
      return;
    }

    Preconditions.checkArgument(
        node.streamComponentInstances().skip(1).allMatch(inst -> inst.getSymbol().getType().deepEquals(node.getComponentInstance(0).getSymbol().getType())),
        "Some instances of '%s' at '%s' have mismatching '%s's as their types. Your symbol table completion seems to " + "be inconsistent.",
        ASTComponentInstantiation.class.getSimpleName(), node.get_SourcePositionStart(), CompTypeExpression.class.getSimpleName());

    CompTypeExpression compTypeExpr = node.getComponentInstance(0).getSymbol().getType();
    if (compTypeExpr instanceof TypeExprOfGenericComponent) {
      checkTypeArgsAreNotTooFew((TypeExprOfGenericComponent) compTypeExpr, node);
      ;
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
    List<SymTypeExpression> args = compTypeExpr.getBindingsAsList();
    if (parentSymTypeParameters.size() > args.size()) {
      Log.error(
          GenericArcError.TOO_FEW_TYPE_ARGUMENTS.format(args.size(), compTypeExpr.printName(), parentSymTypeParameters.size()), node.get_SourcePositionStart(),
          node.get_SourcePositionEnd());
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
          GenericArcError.TOO_MANY_TYPE_ARGUMENTS.format(args.size(), compTypeSymbol.getName(), parentSymTypeParameters.size()), node.get_SourcePositionStart(),
          node.get_SourcePositionEnd());
    }
  }
}
