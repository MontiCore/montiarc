/* (c) https://github.com/MontiCore/monticore */
package arcbasis._ast;

import com.google.common.base.Preconditions;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Represents component bodies and collections of {@link ASTArcElement}s. Extends {@link ASTComponentBodyTOP} with
 * utility functions for easy access.
 */
public class ASTComponentBody extends ASTComponentBodyTOP {

  /**
   * Returns a {@link Stream} of all {@link ASTArcElement}s of the type defined by {@code typeToSearch}, in no
   * specific order. The list is empty if this body contains none of such elements.
   *
   * @return a {@link Stream} of all ArcElements of the given type
   */
  public <T extends ASTArcElement> Stream<T> streamArcElementsOfType(@NotNull Class<T> typeToSearch) {
    return streamArcElements()
      .filter(typeToSearch::isInstance)
      .map(typeToSearch::cast);
  }

  /**
   * Returns a {@link List} of all {@link ASTArcElement}s of the type defined by {@code typeToSearch}, in no
   * specific order. The list is empty if this body contains none of such elements.
   *
   * @return a {@link List} of all ArcElements of the given type
   */
  public <T extends ASTArcElement> List<T> getElementsOfType(@NotNull Class<T> typeToSearch) {
    Preconditions.checkNotNull(typeToSearch);
    return streamArcElementsOfType(typeToSearch).collect(Collectors.toList());
  }
}
