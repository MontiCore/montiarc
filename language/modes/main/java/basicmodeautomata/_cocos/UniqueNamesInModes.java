/* (c) https://github.com/MontiCore/monticore */
package basicmodeautomata._cocos;

import arcbasis._ast.*;
import arcbasis._cocos.ArcBasisASTComponentTypeCoCo;
import basicmodeautomata.BasicModeAutomataMill;
import com.google.common.base.Preconditions;
import de.monticore.ast.ASTNode;
import de.se_rwth.commons.logging.Log;
import montiarc.util.ArcError;
import basicmodeautomata._ast.ASTModeDeclaration;
import basicmodeautomata._ast.ASTModeDeclarationBuilder;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * ensures that there are no symbols of the same name in two parts of the same mode
 * this is necessary since they are not bundled in a grammar-defined scope.
 * This checks for the names of Port-, Component Type- and Instance-Symbols. If you need to check for more assets,
 * you can extend this coco by overriding the method {@link #matchBodies(ASTComponentBody, ASTComponentBody, String)}.
 */
public class UniqueNamesInModes implements ArcBasisASTComponentTypeCoCo {

  @Override
  public void check(ASTComponentType component) {
    Preconditions.checkNotNull(component);
    // find all mode-declarations
    List<ASTModeDeclaration> mapping = BasicModeAutomataMill.getModeTool().streamDeclarations(component)
        // split them into new ones, each with only one name, while keeping the bodies
        .flatMap(m -> m.streamNames().map(n -> new ASTModeDeclarationBuilder().addName(n).setBody(m.getBody())))
        .map(ASTModeDeclarationBuilder::build)
        .collect(Collectors.toList());
    // compare every mode-body against all others
    IntStream.range(0, mapping.size()).forEach(i -> IntStream.range(i+1, mapping.size())
            // if two declarations have the same name...
            .filter(j -> mapping.get(i).getName(0).equals(mapping.get(j).getName(0)))
            // ...they may not use the same name twice for symbols
            .forEach(j -> matchBodies(mapping.get(i).getBody(), mapping.get(j).getBody(), mapping.get(i).getName(0))));

    // compare every mode against the static component
    mapping.forEach(m -> matchBodies(m.getBody(), component.getBody(), m.getName(0)));
  }

  /**
   * ensures that there are no symbols of the same name in two parts of the same mode
   * @param body1 a body of a mode
   * @param body2 another body that is active as the same mode
   * @param mode the name of the mode (used for error messages)
   */
  protected void matchBodies(ASTComponentBody body1, ASTComponentBody body2, String mode){
    // build pseudo-components to access ast-nodes via the usual way
    ASTComponentType a = new ASTComponentTypeBuilder().setBody(body1).setName("A").setHead(new ASTComponentHeadBuilder().build()).build();
    ASTComponentType b = new ASTComponentTypeBuilder().setBody(body2).setName("B").setHead(new ASTComponentHeadBuilder().build()).build();

    compareLists(a.getSubComponents(), b.getSubComponents(), ASTComponentInstance::getName, ArcError.INSTANCE_NAME_NOT_UNIQUE_IN_MODE, mode);
    compareLists(a.getInnerComponents(), b.getInnerComponents(), ASTComponentType::getName, ArcError.COMPONENT_NAME_NOT_UNIQUE_IN_MODE, mode);
    compareLists(a.getPorts(), b.getPorts(), ASTPort::getName, ArcError.PORT_NAME_NOT_UNIQUE_IN_MODE, mode);
  }

  /**
   * checks for name overlap between two lists and logs an error if one was found
   * @param base first collection of elements names
   * @param duplicates second collection of named elements
   * @param baptist function to retrieve an elements name
   * @param error the error to log if there are duplicates between the first and second list
   * @param mode the mode in which the name exists twice
   * @param <T> ast-node of a symbol
   */
  protected <T extends ASTNode> void compareLists(Collection<T> base, Collection<T> duplicates, Function<T, String> baptist, ArcError error, String mode){
    Set<String> uniqueNames = base.stream().map(baptist).collect(Collectors.toSet());
    duplicates.stream().filter(x -> uniqueNames.contains(baptist.apply(x))).forEach(duplicate -> Log.error(
        error.format(baptist.apply(duplicate), mode),
        duplicate.get_SourcePositionStart()
    ));
  }
}
