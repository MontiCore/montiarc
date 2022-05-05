/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcbasis._ast.ASTComponentInstantiation;
import arcbasis._cocos.ArcBasisASTComponentInstantiationCoCo;
import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis.check.CompTypeExpression;
import montiarc.util.ArcError;
import com.google.common.base.Preconditions;
import de.monticore.symbols.basicsymbols._symboltable.TypeVarSymbol;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.TypeCheck;
import de.se_rwth.commons.logging.Log;
import genericarc.check.TypeExprOfGenericComponent;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.Optional;

/**
 * A generic component may have type parameters that have upper bounds. In this case this coco checks for component
 * instantiations that type arguments passed for bounded type parameters are subtypes of these bounds.
 */
public class ComponentInstantiationRespectsGenericTypeBounds implements ArcBasisASTComponentInstantiationCoCo {
  @Override
  public void check(@NotNull ASTComponentInstantiation node) {
    Preconditions.checkNotNull(node);
    Preconditions.checkArgument(!node.getComponentInstanceList().isEmpty(), "'%s' at '%s' misses instances.",
      ASTComponentInstantiation.class.getSimpleName(), node.get_SourcePositionStart());

    Preconditions.checkArgument(node.streamComponentInstances().allMatch(inst -> inst.isPresentSymbol()),
      "Some instances of '%s' at '%s' have no symbol. Have you run the genitor (and completer) before checking " +
      "coco '%s'?", ASTComponentInstantiation.class.getSimpleName(), node.get_SourcePositionStart(),
      this.getClass().getSimpleName());

    if (!node.streamComponentInstances().allMatch(inst -> inst.getSymbol().isPresentType())){
      Log.debug("Could not perform coco check '" + this.getClass().getSimpleName() + "', due to missing type.",
          this.getClass().getSimpleName());
      return;
    }

    Preconditions.checkArgument(node.streamComponentInstances().skip(1).allMatch(
      inst -> inst.getSymbol().getType().deepEquals(node.getComponentInstance(0).getSymbol().getType())),
      "Some instances of '%s' at '%s' have mismatching '%s's as their types. Your symbol table completion seems to " +
      "be inconsistent.", ASTComponentInstantiation.class.getSimpleName(), node.get_SourcePositionStart(),
      CompTypeExpression.class.getSimpleName());

    CompTypeExpression compTypeExpr = node.getComponentInstance(0).getSymbol().getType();
    if(compTypeExpr instanceof TypeExprOfGenericComponent) {
      checkTypeBoundSatisfaction((TypeExprOfGenericComponent) compTypeExpr, node);
    }
  }

  /**
   * Checks that type arguments for bounded type parameters are subtypes of the parameter bounds. Else logs an error.
   * @param compTypeExpr The instantiating component type expression that contains the type arguments that should be
   *                     checked against the upper bounds of their corresponding type parameters.
   * @param astInstantiation The AST node of the component instantiation for which the type arguments should be checked.
   *                         We need the AST node is needed for printing the error message.
   */
  protected static void checkTypeBoundSatisfaction(@NotNull TypeExprOfGenericComponent compTypeExpr,
                                                   @NotNull ASTComponentInstantiation astInstantiation) {
    Preconditions.checkNotNull(compTypeExpr);
    Preconditions.checkNotNull(astInstantiation);

    ComponentTypeSymbol compTypeSymbol = compTypeExpr.getTypeInfo();

    for(TypeVarSymbol typeVar : compTypeSymbol.getTypeParameters()) {
      Optional<SymTypeExpression> typeVarBinding = compTypeExpr.getBindingFor(typeVar);
      if(typeVarBinding.isPresent()) {
        for(SymTypeExpression bound : typeVar.getSuperTypesList()) {
          if(!TypeCheck.compatible(bound, typeVarBinding.get())) {
            Log.error(ArcError.TYPE_ARG_IGNORES_UPPER_BOUND.format(
              typeVarBinding.get().print(), bound.print(), typeVar.getName(), compTypeSymbol.getName()
            ), astInstantiation.get_SourcePositionStart());
          }
        }
      } else {
        Log.debug(String.format("Not checking coco '%s' on type parameter '%s' of component type '%s' for component " +
          "instantiation at '%s' because the binding for that type parameter is not set.",
            ComponentInstantiationRespectsGenericTypeBounds.class.getSimpleName(), typeVar.getName(),
            compTypeSymbol.getName(), astInstantiation.get_SourcePositionStart()),
          "CoCos");
        return;
      }
    }
  }
}
