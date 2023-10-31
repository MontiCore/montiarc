/* (c) https://github.com/MontiCore/monticore */
package comfortablearc._ast;

import arcbasis._symboltable.ArcPortSymbol;
import com.google.common.base.Preconditions;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types3.SymTypeRelations;
import org.codehaus.commons.nullanalysis.NotNull;

public class ASTArcACType extends ASTArcACTypeTOP {

  /**
   * If the auto-connect mode is set to type, then the source and target are
   * matching if their types match.
   */
  @Override
  public boolean matches(@NotNull ArcPortSymbol source, @NotNull ArcPortSymbol target) {
    Preconditions.checkNotNull(source);
    Preconditions.checkNotNull(target);

    return matches(source.getType(), target.getType());
  }

  protected boolean matches(@NotNull SymTypeExpression source,
                            @NotNull SymTypeExpression target) {

    return !source.isObscureType() && !target.isObscureType() &&
        SymTypeRelations.isSubTypeOf(source, target) && SymTypeRelations.isSubTypeOf(source, target);
  }
}
