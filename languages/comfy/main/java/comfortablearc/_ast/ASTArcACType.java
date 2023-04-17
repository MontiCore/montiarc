/* (c) https://github.com/MontiCore/monticore */
package comfortablearc._ast;

import arcbasis._symboltable.PortSymbol;
import com.google.common.base.Preconditions;
import de.monticore.types.check.ITypeRelations;
import de.monticore.types.check.SymTypeExpression;
import org.codehaus.commons.nullanalysis.NotNull;

public class ASTArcACType extends ASTArcACTypeTOP {

  /**
   * If the auto-connect mode is set to type, then the source and target are
   * matching if their types match.
   */
  @Override
  public boolean matches(@NotNull PortSymbol source, @NotNull PortSymbol target, @NotNull ITypeRelations tr) {
    Preconditions.checkNotNull(source);
    Preconditions.checkNotNull(target);
    Preconditions.checkNotNull(tr);

    return matches(source.getType(), target.getType(), tr);
  }

  protected boolean matches(@NotNull SymTypeExpression source,
                            @NotNull SymTypeExpression target,
                            @NotNull ITypeRelations tr) {

    return !source.isObscureType() && !target.isObscureType() &&
      tr.isSubtypeOf(source, target) && tr.isSubtypeOf(source, target);
  }
}
