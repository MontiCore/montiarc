/* (c) https://github.com/MontiCore/monticore */
package montiarc.util;

import com.google.common.base.Preconditions;
import de.monticore.ast.ASTNode;
import de.se_rwth.commons.logging.Log;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

public class UniqueNameHelper {
  private UniqueNameHelper(){}

  /**
   * searches for duplicated names
   * @param nodes all nodes to whose names have to be checked for uniqueness
   * @param name function to retrieve the name of a node
   * @param component name of the component to use in error messages
   * @param error the error to log if a duplicate was detected, that uses the node name firstly and component name second
   * @param <Node> type of ast node to check
   */
  public static <Node extends ASTNode> void checkNames(
      @NotNull Collection<Node> nodes,
      @NotNull Function<Node, String> name,
      @NotNull String component,
      @NotNull Error error){
    Preconditions.checkNotNull(nodes);
    Preconditions.checkNotNull(name);
    Preconditions.checkNotNull(component);
    Preconditions.checkNotNull(error);
    Set<String> names = new HashSet<>();
    nodes.forEach(node -> {
      if(!names.add(name.apply(node))){
        Log.error(String.format(error.toString(), name.apply(node), component), node.get_SourcePositionStart());
      }
    });
  }
}