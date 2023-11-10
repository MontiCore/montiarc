/* (c) https://github.com/MontiCore/monticore */
package genericarc._cocos;

import arcbasis._ast.ASTArcParent;
import arcbasis._ast.ASTComponentType;
import arcbasis._cocos.ArcBasisASTComponentTypeCoCo;
import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis.check.CompTypeExpression;
import com.google.common.base.Preconditions;
import de.monticore.symbols.basicsymbols._symboltable.TypeVarSymbol;
import de.monticore.types.check.CompKindExpression;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.mccollectiontypes._ast.ASTMCTypeArgument;
import de.monticore.types.mcsimplegenerictypes._ast.ASTMCBasicGenericType;
import de.monticore.types3.SymTypeRelations;
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

  @Override
  public void check(@NotNull ASTComponentType node) {
    Preconditions.checkNotNull(node);
    Preconditions.checkArgument(node.isPresentSymbol());

    for (int i = 0; i < Math.min(node.getHead().getArcParentList().size(), node.getSymbol().getSuperComponentsList().size()); i++) {
      CompKindExpression parent = node.getSymbol().getSuperComponents(i);
      if (parent instanceof TypeExprOfGenericComponent) {
        checkTypeArgsAreNotTooFew(node.getHead().getArcParent(i), (TypeExprOfGenericComponent) parent);
        checkTypeArgsAreNotTooMany(node.getHead().getArcParent(i), (TypeExprOfGenericComponent) parent);
        checkRespectsGenericTypeBounds(node.getHead().getArcParent(i), (TypeExprOfGenericComponent) parent);
      }
    }
  }

  /**
   * Checks that there are enough type arguments provided to bind all mandatory type parameters of the
   * component type that should be extended.
   *
   * @param node The AST node of the component type whose type arguments should be checked.
   */
  protected void checkTypeArgsAreNotTooFew(@NotNull ASTArcParent node, @NotNull TypeExprOfGenericComponent parentExpr) {
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
  protected void checkTypeArgsAreNotTooMany(@NotNull ASTArcParent node, @NotNull TypeExprOfGenericComponent parentExpr) {
    Preconditions.checkArgument(node.getType() instanceof ASTMCBasicGenericType);
    ComponentTypeSymbol parentSym = parentExpr.getTypeInfo();

    List<TypeVarSymbol> parentSymTypeParameters = parentSym.getTypeParameters();
    List<ASTMCTypeArgument> args = ((ASTMCBasicGenericType) node.getType()).getMCTypeArgumentList();

    if (parentSymTypeParameters.size() < args.size()) {
      Log.error(
        GenericArcError.HERITAGE_TOO_MANY_TYPE_ARGUMENTS.format(parentSymTypeParameters.size(), args.size()),
        node.get_SourcePositionStart(), node.get_SourcePositionEnd()
      );
    }
  }

  protected void checkRespectsGenericTypeBounds(@NotNull ASTArcParent node, @NotNull TypeExprOfGenericComponent parentExpr) {
    ComponentTypeSymbol parentSym = parentExpr.getTypeInfo();

    for (TypeVarSymbol typeVar : parentSym.getTypeParameters()) {
      Optional<SymTypeExpression> typeVarBinding = parentExpr.getTypeBindingFor(typeVar);
      if (typeVarBinding.isPresent()) {
        for (SymTypeExpression aBound : typeVar.getSuperTypesList()) {
          SymTypeExpression bound = aBound.deepClone();
          bound.replaceTypeVariables(parentExpr.getTypeVarBindings());
          if (!SymTypeRelations.isCompatible(bound, typeVarBinding.get())) {
            Log.error(
              GenericArcError.HERITAGE_TYPE_ARG_IGNORES_UPPER_BOUND.format(typeVarBinding.get().print(), bound.print()),
              node.get_SourcePositionStart(), node.get_SourcePositionEnd());
          }
        }
      } else {
        Log.debug(String.format("Not checking coco '%s' on type parameter '%s' of component type '%s' for in " +
                "parent declaration at '%s' because the binding for that type parameter is not set.", this.getClass().getSimpleName(), typeVar.getName(),
            parentSym.getName(), node.get_SourcePositionStart(), node.get_SourcePositionEnd()), "CoCos");
      }
    }

  }
}
