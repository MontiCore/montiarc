package montiarc.cocos;

import de.monticore.types.TypesPrinter;
import de.se_rwth.commons.SourcePosition;
import de.se_rwth.commons.StringTransformations;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.*;
import montiarc._cocos.MontiArcASTComponentCoCo;
import montiarc._symboltable.ComponentInstanceSymbol;
import montiarc._symboltable.ComponentSymbol;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author (last commit) Michael Mutert
 * @implements [Wor16] AU1: The name of each state is unique. (p. 97. Lst. 5.8)
 * @implements [Wor16] AU3: The names of all inputs, outputs, and variables
 * are unique. (p. 98. Lst. 5.10)
 * @implements [Hab16] B1: All names of model elements within a component
 * namespace have to be unique. (p. 59. Lst. 3.31)
 */
public class IdentifiersAreUnique implements MontiArcASTComponentCoCo {

  @Override
  public void check(ASTComponent node) {
    ArrayList<Identifier> names = new ArrayList<>();
    ComponentSymbol comp = (ComponentSymbol) node.getSymbol().get();

    for (ASTElement e : node.getBody().getElements()) {

      // Check variable declarations
      if (e instanceof ASTVariableDeclaration) {
        ASTVariableDeclaration decl = (ASTVariableDeclaration) e;
        for (String variableName : decl.getNames()) {
          names.add(new Identifier(variableName, IdentifierTypes.VARIABLE, e.get_SourcePositionStart()));
        }
      }

      // Check port names
      else if (e instanceof ASTInterface) {
        ASTInterface decl = (ASTInterface) e;
        for (ASTPort port : decl.getPorts()) {
          List<String> portInstanceNames = port.getNames();
          if (portInstanceNames.isEmpty()) {
            String implicitName = TypesPrinter.printType(port.getType());
            portInstanceNames.add(StringTransformations.uncapitalize(implicitName));
          }
          for (String name : portInstanceNames) {
            names.add(new Identifier(name, IdentifierTypes.PORT, e.get_SourcePositionStart()));
          }
        }
      }

      // Check constraints
      else if (e instanceof ASTMontiArcInvariant) {
        ASTMontiArcInvariant invariant = (ASTMontiArcInvariant) e;
        String name = invariant.getName();
        names.add(new Identifier(name, IdentifierTypes.INVARIANT, e.get_SourcePositionStart()));
      }
    }

    // Component Instances
    for (ComponentInstanceSymbol subComp : comp.getSubComponents()) {

      SourcePosition pos
          = subComp.getAstNode().isPresent()
                ? subComp.getAstNode().get().get_SourcePositionStart()
                : SourcePosition.getDefaultSourcePosition();

      names.add(new Identifier(subComp.getName(),
          IdentifierTypes.SUBCOMPONENTINSTANCE, pos));
    }

    // Configuration Parameters
    List<ASTParameter> parameters = node.getHead().getParameters();
    for (ASTParameter parameter : parameters) {
      names.add(new Identifier(parameter.getName(),
          IdentifierTypes.CONFIG_PARAMETER, parameter.get_SourcePositionStart()));
    }

    Set<Identifier> nameDuplicates = new HashSet<>();
    for (int startIndex = 0; startIndex < names.size(); startIndex++) {
      for (int searchIndex = startIndex + 1; searchIndex < names.size(); searchIndex++) {
        if (names.get(searchIndex).getName().equals(names.get(startIndex).getName())) {
          nameDuplicates.add(names.get(searchIndex));
          nameDuplicates.add(names.get(startIndex));
        }
      }
    }

    for (Identifier nameDuplicate : nameDuplicates) {
      String errorCode = "0xMA035";
      String type = "identifier";
      switch (nameDuplicate.getType()) {
        case CONFIG_PARAMETER:
          errorCode = "0xMA069";
          type = "configuration parameter";
          break;
        case PORT:
          errorCode = "0xMA053";
          type = "port";
          break;
        case INVARIANT:
          errorCode = "0xMA052";
          type = "invariant";
          break;
        case SUBCOMPONENTINSTANCE:
          errorCode = "0xMA061";
          type = "subcomponent instance";
          break;
        case VARIABLE:
          type = "variable";
          break;
      }

      Log.error(String.format("%s The name of %s '%s' is ambiguous.",
          errorCode, type, nameDuplicate.getName()),
          nameDuplicate.getSourcePosition());
    }
  }

  private enum IdentifierTypes {
    CONFIG_PARAMETER, PORT, INVARIANT, SUBCOMPONENTINSTANCE, VARIABLE
  }

  /**
   * Used to store the information about a found identifier.
   */
  private class Identifier {

    private final IdentifierTypes type;
    private final SourcePosition sourcePosition;
    private final String name;

    /**
     * @param name           The identifier
     * @param type           Type of the identifier
     * @param sourcePosition SourcePosition that is the position of the
     *                       element in the model.
     */
    public Identifier(String name, IdentifierTypes type, SourcePosition sourcePosition) {
      this.sourcePosition = sourcePosition;
      this.type = type;
      this.name = name;
    }

    public IdentifierTypes getType() {
      return type;
    }

    public SourcePosition getSourcePosition() {
      return sourcePosition;
    }

    public String getName() {
      return name;
    }
  }

}
