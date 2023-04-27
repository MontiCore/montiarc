/* (c) https://github.com/MontiCore/monticore */
package genericarc._cocos;

import arcbasis._ast.ASTComponentType;
import arcbasis._cocos.ArcBasisASTComponentTypeCoCo;
import arcbasis._symboltable.ComponentTypeSymbol;
import com.google.common.base.Preconditions;
import de.monticore.symbols.basicsymbols._symboltable.TypeVarSymbol;
import de.monticore.types.check.ITypeRelations;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.mccollectiontypes._ast.ASTMCTypeArgument;
import de.monticore.types.mcsimplegenerictypes._ast.ASTMCBasicGenericType;
import de.se_rwth.commons.SourcePosition;
import de.se_rwth.commons.logging.Log;
import genericarc.check.TypeExprOfGenericComponent;
import montiarc.util.GenericArcError;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.List;
import java.util.Optional;

/**
 * A generic component may have type parameters that have upper bounds. In this case this coco checks for component
 * inheritance declarations that type arguments passed for bounded type parameters of the parent component are subtypes
 * of these bounds.
 */
public class ComponentHeritageTypeBound implements ArcBasisASTComponentTypeCoCo {

  protected final ITypeRelations tr;

  public ComponentHeritageTypeBound(@NotNull ITypeRelations tr) {
    this.tr = Preconditions.checkNotNull(tr);
  }

  private static SourcePosition parentPositionOrElseTypePosition(@NotNull ASTComponentType node) {
    Preconditions.checkNotNull(node);
    return node.getHead().isPresentParent() ? node.getHead().getParent().get_SourcePositionStart() : node.get_SourcePositionStart();
  }

  @Override
  public void check(@NotNull ASTComponentType node) {
    Preconditions.checkNotNull(node);
    Preconditions.checkArgument(node.isPresentSymbol());

    if (node.getSymbol().isPresentParent() && node.getSymbol().getParent() instanceof TypeExprOfGenericComponent) {
      checkTypeArgsAreNotTooFew(node);
      checkTypeArgsAreNotTooMany(node);
      checkRespectsGenericTypeBounds(node);
    }
  }

  /**
   * Checks that there are enough type arguments provided to bind all mandatory type parameters of the
   * component type that should be extended.
   *
   * @param node The AST node of the component type whose type arguments should be checked.
   */
  protected void checkTypeArgsAreNotTooFew(@NotNull ASTComponentType node) {
    TypeExprOfGenericComponent parentExpr = (TypeExprOfGenericComponent) node.getSymbol().getParent();
    ComponentTypeSymbol parentSym = parentExpr.getTypeInfo();

    List<TypeVarSymbol> parentSymTypeParameters = parentSym.getTypeParameters();
    List<SymTypeExpression> args = parentExpr.getTypeBindingsAsList();
    if (parentSymTypeParameters.size() > args.size()) {
      Log.error(
        GenericArcError.HERITAGE_TOO_FEW_TYPE_ARGUMENTS.format(parentSymTypeParameters.size(), args.size()),
        node.get_SourcePositionStart(), node.get_SourcePositionEnd()
      );
    }
  }

  /**
   * Checks that there are not more type arguments provided than there are type parameters in the
   * component type that should be extended.
   *
   * @param node The AST node of the component type whose type arguments should be checked.
   */
  protected void checkTypeArgsAreNotTooMany(@NotNull ASTComponentType node) {
    Preconditions.checkArgument(node.getHead().getParent() instanceof ASTMCBasicGenericType);
    TypeExprOfGenericComponent parentExpr = (TypeExprOfGenericComponent) node.getSymbol().getParent();
    ComponentTypeSymbol parentSym = parentExpr.getTypeInfo();

    List<TypeVarSymbol> parentSymTypeParameters = parentSym.getTypeParameters();
    List<ASTMCTypeArgument> args = ((ASTMCBasicGenericType) node.getHead().getParent()).getMCTypeArgumentList();

    if (parentSymTypeParameters.size() < args.size()) {
      Log.error(
        GenericArcError.HERITAGE_TOO_MANY_TYPE_ARGUMENTS.format(parentSymTypeParameters.size(), args.size()),
        node.get_SourcePositionStart(), node.get_SourcePositionEnd()
      );
    }
  }

  protected void checkRespectsGenericTypeBounds(@NotNull ASTComponentType node) {
    TypeExprOfGenericComponent parentExpr = (TypeExprOfGenericComponent) node.getSymbol().getParent();
    ComponentTypeSymbol parentSym = parentExpr.getTypeInfo();

    for (TypeVarSymbol typeVar : parentSym.getTypeParameters()) {
      Optional<SymTypeExpression> typeVarBinding = parentExpr.getTypeBindingFor(typeVar);
      if (typeVarBinding.isPresent()) {
        for (SymTypeExpression bound : typeVar.getSuperTypesList()) {
          if (!tr.compatible(bound, typeVarBinding.get())) {
            Log.error(
              GenericArcError.HERITAGE_TYPE_ARG_IGNORES_UPPER_BOUND.format(typeVarBinding.get().print(), bound.print()),
              parentPositionOrElseTypePosition(node));
          }
        }
      } else {
        Log.debug(String.format("Not checking coco '%s' on type parameter '%s' of component type '%s' for in " +
                "parent declaration at '%s' because the binding for that type parameter is not set.", this.getClass().getSimpleName(), typeVar.getName(),
            parentSym.getName(), parentPositionOrElseTypePosition(node)), "CoCos");
      }
    }

  }
}
