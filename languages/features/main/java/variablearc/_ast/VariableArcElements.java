/* (c) https://github.com/MontiCore/monticore */
package variablearc._ast;

import arcbasis._ast.ASTArcComponentType;
import arcbasis._ast.ASTArcElement;
import arcbasis._ast.ASTComponentBody;
import com.google.common.base.Preconditions;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Collects the elements of a component body, including (nested) variation
 * points and their elements, but excluding those of inner component types.
 */
public final class VariableArcElements {

  private VariableArcElements() {}

  public static <T> List<T> of(@NotNull ASTArcComponentType component, @NotNull Class<T> kind) {
    Preconditions.checkNotNull(component);
    Preconditions.checkNotNull(kind);
    List<T> result = new ArrayList<>();
    collect(component.getBody(), kind, result);
    return result;
  }

  private static <T> void collect(@NotNull ASTArcElement element, @NotNull Class<T> kind, @NotNull List<T> result) {
    if (element instanceof ASTComponentBody body) {
      body.getArcElementList().forEach(child -> collect(child, kind, result));
    } else if (element instanceof ASTArcVarIf varIf) {
      if (kind.isInstance(varIf)) {
        result.add(kind.cast(varIf));
      }
      collect(varIf.getThen(), kind, result);
      if (varIf.isPresentOtherwise()) {
        collect(varIf.getOtherwise(), kind, result);
      }
    } else if (kind.isInstance(element)) {
      result.add(kind.cast(element));
    }
  }
}
