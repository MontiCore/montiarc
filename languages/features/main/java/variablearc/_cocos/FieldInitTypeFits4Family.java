/* (c) https://github.com/MontiCore/monticore */
package variablearc._cocos;

import arcbasis._ast.ASTArcField;
import arcbasis._cocos.FieldInitTypeFits;
import com.google.common.base.Preconditions;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc.check.TypeCheck4Family;

import java.util.List;

/**
 * Checks that the initial value of a field fits the type of the field for
 * every combination of the symbols its names may refer to in the
 * configurations in which the field is present.
 */
public class FieldInitTypeFits4Family extends FieldInitTypeFits {

  protected final TypeCheck4Family typeCheck;

  public FieldInitTypeFits4Family(@NotNull TypeCheck4Family typeCheck) {
    this.typeCheck = Preconditions.checkNotNull(typeCheck);
  }

  @Override
  public void check(@NotNull ASTArcField node) {
    Preconditions.checkNotNull(node);
    this.typeCheck.check(node, node.getEnclosingScope(), List.of(node.getInitial()), () -> super.check(node));
  }
}
