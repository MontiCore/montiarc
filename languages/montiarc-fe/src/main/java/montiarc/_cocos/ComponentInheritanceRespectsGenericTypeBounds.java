/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcbasis._ast.ASTComponentType;
import arcbasis._cocos.ArcBasisASTComponentTypeCoCo;
import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis.util.ArcError;
import com.google.common.base.Preconditions;
import de.monticore.symbols.basicsymbols._symboltable.TypeVarSymbol;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.TypeCheck;
import de.se_rwth.commons.SourcePosition;
import de.se_rwth.commons.logging.Log;
import genericarc.check.TypeExprOfGenericComponent;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.Optional;

/**
 * A generic component may have type parameters that have upper bounds. In this case this coco checks for component
 * inheritance declarations that type arguments passed for bounded type parameters of the parent component are subtypes
 * of these bounds.
 */
public class ComponentInheritanceRespectsGenericTypeBounds implements ArcBasisASTComponentTypeCoCo {

  @Override
  public void check(@NotNull ASTComponentType node) {
    Preconditions.checkNotNull(node);
    Preconditions.checkArgument(node.isPresentSymbol(), "Component type '%s' at %s has no symbol. Have you run the " +
      "genitor (and completer) before checking coco '%s'?", node.getName(), node.get_SourcePositionStart(),
      this.getClass().getSimpleName());

    if(node.getSymbol().isPresentParentComponent()
      && node.getSymbol().getParent() instanceof TypeExprOfGenericComponent) {

      TypeExprOfGenericComponent parentExpr = (TypeExprOfGenericComponent) node.getSymbol().getParent();
      ComponentTypeSymbol parentSym = parentExpr.getTypeInfo();

      for(TypeVarSymbol typeVar : parentSym.getTypeParameters()) {
        Optional<SymTypeExpression> typeVarBinding = parentExpr.getBindingFor(typeVar);
        if(typeVarBinding.isPresent()) {
          for(SymTypeExpression bound : typeVar.getSuperTypesList()) {
            if(!TypeCheck.compatible(bound, typeVarBinding.get())) {
              Log.error(ArcError.TYPE_ARG_IGNORES_UPPER_BOUND.format(
                typeVarBinding.get().print(), bound.print(), typeVar.getName(), parentSym.getName()
              ), parentPositionOrElseTypePosition(node));
            }
          }
        } else {
          Log.debug(String.format("Not checking coco '%s' on type parameter '%s' of component type '%s' for in " +
            "parent declaration at '%s' because the binding for that type parameter is not set.",
            this.getClass().getSimpleName(), typeVar.getName(), parentSym.getName(),
            parentPositionOrElseTypePosition(node)),"CoCos");
        }
      }
    }
  }

  private static SourcePosition parentPositionOrElseTypePosition(@NotNull ASTComponentType node) {
    Preconditions.checkNotNull(node);
    return node.getHead().isPresentParent() ?
      node.getHead().getParent().get_SourcePositionStart()
      : node.get_SourcePositionStart();
  }
}
