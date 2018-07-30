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
    if (!node.getSymbolOpt().isPresent()) {
      Log.error(
          String.format("0xMA010 ASTComponent node \"%s\" has no " +
                            "symbol. Did you forget to run the " +
                            "SymbolTableCreator before checking cocos?",
              node.getName()));
      return;
    }
    ArrayList<Identifier> names = new ArrayList<>();
    ComponentSymbol comp = (ComponentSymbol) node.getSymbolOpt().get();

    for (ASTElement e : node.getBody().getElementList()) {

      // Check variable declarations
      if (e instanceof ASTVariableDeclaration) {
        ASTVariableDeclaration decl = (ASTVariableDeclaration) e;
        for (String variableName : decl.getNameList()) {
          names.add(new Identifier(variableName, IdentifierTypes.VARIABLE, e.get_SourcePositionStart()));
        }
      }

      // Check port names
      else if (e instanceof ASTInterface) {
        ASTInterface decl = (ASTInterface) e;
        for (ASTPort port : decl.getPortsList()) {
          List<String> portInstanceNames = port.getNameList();
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
    List<ASTParameter> parameters = node.getHead().getParameterList();
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
      switch (nameDuplicate.getType()) {
        case CONFIG_PARAMETER:
          logError(nameDuplicate, "0xMA069", "configuration parameter");
          break;
        case PORT:
          logError(nameDuplicate, "0xMA053", "port");
          break;
        case INVARIANT:
          logError(nameDuplicate, "0xMA052", "invariant");
          break;
        case SUBCOMPONENTINSTANCE:
          logError(nameDuplicate, "0xMA061", "subcomponent instance");
          break;
        case VARIABLE:
          logError(nameDuplicate, "0xMA035", "variable");
          break;
        default:
          logError(nameDuplicate, "0xMA035", "identifier");
      }
    }
  }

  private void logError(Identifier nameDuplicate, String errorCode, String type) {
    Log.error(String.format("%s The name of %s '%s' is ambiguous.",
        errorCode, type, nameDuplicate.getName()),
        nameDuplicate.getSourcePosition());
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
