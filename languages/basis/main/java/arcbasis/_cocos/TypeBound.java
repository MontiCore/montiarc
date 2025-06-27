/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;

import arcbasis._ast.ASTComponentInstance;
import arcbasis._ast.ASTComponentInstantiation;
import arcbasis._ast.ASTArcComponentType;
import com.google.common.base.Preconditions;
import de.monticore.ast.ASTNode;
import de.monticore.symbols.basicsymbols._symboltable.TypeVarSymbol;
import de.monticore.symbols.compsymbols._symboltable.ComponentTypeSymbol;
import de.monticore.types.check.CompKindExpression;
import de.monticore.types.check.CompKindOfGenericComponentType;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types3.SymTypeRelations;
import de.se_rwth.commons.logging.Log;
import montiarc.util.ArcError;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.List;
import java.util.Optional;


/**
 * A generic component may have type parameters that have upper bounds. In this case this coco either checks
 * for component instantiations that type arguments passed for bounded type parameters are subtypes or
 * for component inheritance declarations that type arguments passed for bounded type parameters of the parent component are subtypes
 * of these bounds
 * Furthermore, refinement declarations must respect the type bounds of the referenced component type.
 * Therefore, this the second check method also checks for refinement declarations that are not raw, i.e., that do not omit type arguments.
 */
public class TypeBound implements ArcBasisASTComponentInstantiationCoCo, ArcBasisASTArcComponentTypeCoCo {

  // type arguments passed for bounded type parameters
  @Override
  public void check(@NotNull ASTComponentInstantiation node) {
    Preconditions.checkNotNull(node);
    Preconditions.checkArgument(!node.getComponentInstanceList().isEmpty());

    Preconditions.checkArgument(node.streamComponentInstances().allMatch(ASTComponentInstance::isPresentSymbol));

    if (!node.streamComponentInstances().allMatch(inst -> inst.getSymbol().isTypePresent())) {
      Log.debug(() -> "Could not perform coco check '" + this.getClass().getSimpleName() + "', due to missing type.", this.getClass().getSimpleName());
      return;
    }

    Preconditions.checkArgument(
      node.streamComponentInstances().skip(1).allMatch(inst -> inst.getSymbol().getType().deepEquals(node.getComponentInstance(0).getSymbol().getType())),
      "Some instances of '%s' at '%s' have mismatching '%s's as their types. Your symbol table completion seems to " + "be inconsistent.",
      ASTComponentInstantiation.class.getSimpleName(), node.get_SourcePositionStart(), CompKindExpression.class.getSimpleName());

    CompKindExpression compTypeExpr = node.getComponentInstance(0).getSymbol().getType();
    if (compTypeExpr.isGenericComponentType()) {
      checkTypeArgsAreNotTooFew(compTypeExpr.asGenericComponentType());

      checkTypeArgsAreNotTooMany(compTypeExpr.asGenericComponentType());
      checkRespectsGenericTypeBounds(compTypeExpr.asGenericComponentType(), true);
    }
  }

  // type arguments passed for bounded type parameters of the parent component
  @Override
  public void check(@NotNull ASTArcComponentType node) {
    Preconditions.checkNotNull(node);
    Preconditions.checkArgument(node.isPresentSymbol());

    for (CompKindExpression compKindExpression : node.getSymbol().getSuperComponentsList()) {
      if (compKindExpression.isGenericComponentType()) {
        checkTypeArgsAreNotTooFew(compKindExpression.asGenericComponentType());
        checkTypeArgsAreNotTooMany(compKindExpression.asGenericComponentType());
        checkRespectsGenericTypeBounds(compKindExpression.asGenericComponentType(), false);
      }
    }

    for (CompKindExpression compExpr : node.getSymbol().getRefinementsList()) {
      if (compExpr.isGenericComponentType()) {
        checkTypeArgsAreNotTooFew(compExpr.asGenericComponentType());
        checkTypeArgsAreNotTooMany(compExpr.asGenericComponentType());
        checkRespectsGenericTypeBounds(compExpr.asGenericComponentType(), false);
      }
    }
  }


  /**
   * Checks that there are enough type arguments provided to bind all mandatory type parameters of the
   * component type that should be instantiated.
   */
  protected void checkTypeArgsAreNotTooFew(@NotNull CompKindOfGenericComponentType compTypeExpr) {
    ComponentTypeSymbol compTypeSymbol = compTypeExpr.getTypeInfo();

    List<TypeVarSymbol> parentSymTypeParameters = compTypeSymbol.getTypeParameters();
    List<SymTypeExpression> args = compTypeExpr.getTypeBindingsAsList();

    if (parentSymTypeParameters.size() > args.size()) {
      Log.error(
        ArcError.TOO_FEW_TYPE_ARGUMENTS.format(parentSymTypeParameters.size(), args.size()),
        compTypeExpr.getSourceNode().map(ASTNode::get_SourcePositionStart).orElse(null), compTypeExpr.getSourceNode().map(ASTNode::get_SourcePositionEnd).orElse(null)
      );
    }
  }


  /**
   * Checks that there are not more type arguments provided than there are type parameters in the
   * component type that should be instantiated.
   */
  protected void checkTypeArgsAreNotTooMany(@NotNull CompKindOfGenericComponentType compTypeExpr) {
    ComponentTypeSymbol compTypeSymbol = compTypeExpr.getTypeInfo();

    List<TypeVarSymbol> parentSymTypeParameters = compTypeSymbol.getTypeParameters();
    List<SymTypeExpression> args = compTypeExpr.getTypeBindingsAsList();

    if (parentSymTypeParameters.size() < args.size()) {
      Log.error(
        ArcError.TOO_MANY_TYPE_ARGUMENTS.format(parentSymTypeParameters.size(), args.size()),
        compTypeExpr.getSourceNode().map(ASTNode::get_SourcePositionStart).orElse(null), compTypeExpr.getSourceNode().map(ASTNode::get_SourcePositionEnd).orElse(null)
      );
    }
  }


  protected void checkRespectsGenericTypeBounds(@NotNull CompKindOfGenericComponentType typeExpr, boolean checkMode) {
    Preconditions.checkNotNull(typeExpr);

    ComponentTypeSymbol typeSym = typeExpr.getTypeInfo();

    for (TypeVarSymbol typeVar : typeSym.getTypeParameters()) {
      Optional<SymTypeExpression> typeVarBinding = typeExpr.getTypeBindingFor(typeVar);
      if (typeVarBinding.isPresent()) {
        for (SymTypeExpression aBound : typeVar.getSuperTypesList()) {
          SymTypeExpression bound = aBound.deepClone();
          bound.replaceTypeVariables(typeExpr.getTypeVarBindings());

          boolean isValid = checkMode
            ? SymTypeRelations.isSubTypeOf(typeVarBinding.get(), bound)
            : SymTypeRelations.isCompatible(bound, typeVarBinding.get());

          if (!isValid) {
            Log.error(
              ArcError.TYPE_ARG_IGNORES_UPPER_BOUND.format(typeVarBinding.get().print(), bound.print()),
              typeExpr.getSourceNode().map(ASTNode::get_SourcePositionStart).orElse(null), typeExpr.getSourceNode().map(ASTNode::get_SourcePositionEnd).orElse(null));
          }
        }
      } else {
        Log.debug(() -> String.format("Not checking coco '%s' on type parameter '%s' of component type '%s' for component at '%s-%s' because the binding for that type parameter is not set.",
          this.getClass().getSimpleName(), typeVar.getName(), typeSym.getName(), typeExpr.getSourceNode().map(ASTNode::get_SourcePositionStart).orElse(null), typeExpr.getSourceNode().map(ASTNode::get_SourcePositionEnd).orElse(null)), "CoCos");
      }
    }
  }


}
