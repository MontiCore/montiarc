package montiarc.cocos;

import de.monticore.ast.ASTNode;
import de.monticore.types.TypesPrinter;
import de.se_rwth.commons.StringTransformations;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.*;
import montiarc._cocos.MontiArcASTComponentCoCo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @implements [Wor16] AU1: The name of each state is unique. (p. 97. Lst. 5.8)
 * @implements [Wor16] AU3: The names of all inputs, outputs, and variables are unique. (p. 98. Lst.
 * 5.10)
 */
public class IdentifiersAreUnique implements MontiArcASTComponentCoCo {

  @Override
  public void check(ASTComponent node) {
    ArrayList<Identifier> names = new ArrayList<>();

    for (ASTElement e : node.getBody().getElements()) {

      // Check variable declarations
      if (e instanceof ASTVariableDeclaration) {
        ASTVariableDeclaration decl = (ASTVariableDeclaration) e;
        for (String variableName : decl.getNames()) {
          names.add(new Identifier(variableName, IdentifierTypes.VARIABLE, e));
//          checkList(names, variableName, "0xMA035", "variable", e);
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
            names.add(new Identifier(name, IdentifierTypes.PORT, e));
//            checkList(names, name, "0xMA053", "port", e);
          }
        }
      }

      // Check constraints
      else if (e instanceof ASTMontiArcInvariant) {
        ASTMontiArcInvariant invariant = (ASTMontiArcInvariant) e;
        String name = invariant.getName();
        names.add(new Identifier(name, IdentifierTypes.INVARIANT, e));
//        checkList(names, name, "0xMA052", "invariant", e);
      }
    }

    // Configuration Parameters
    List<ASTParameter> parameters = node.getHead().getParameters();
    for (ASTParameter parameter : parameters) {
      names.add(new Identifier(parameter.getName(),
          IdentifierTypes.CONFIG_PARAMETER, parameter));
//      checkList(names, parameter.getName(), "0xMA069",
//        "configuration parameter", parameter);
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
        case VARIABLE:
          errorCode = "0xMA035";
          type = "variable";
          break;
      }

      Log.error(String.format("%s The name of %s '%s' is ambiguous.",
          errorCode, type, nameDuplicate.getName()),
          nameDuplicate.getNode().get_SourcePositionStart());
    }
  }

  private enum IdentifierTypes {
      CONFIG_PARAMETER, PORT, INVARIANT, VARIABLE
  }

  /**
   * Used to store the information about a found identifier.
   */
  private class Identifier {

    private final ASTNode astNode;
    private final IdentifierTypes type;

    public String getName() {
      return name;
    }

    private final String name;

    /**
     * @param name    The identifier
     * @param type    Type of the identifier
     * @param astNode ASTNode that contains the definition of the identifier
     */
    public Identifier(String name, IdentifierTypes type, ASTNode astNode) {
      this.astNode = astNode;
      this.type = type;
      this.name = name;
    }

    public IdentifierTypes getType() {
      return type;
    }

    public ASTNode getNode() {
      return astNode;
    }
  }

}
