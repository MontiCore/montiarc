/* (c) https://github.com/MontiCore/monticore */
package montiarc.cocos;

import com.google.common.collect.Lists;
import de.monticore.ast.ASTNode;
import de.monticore.types.TypesPrinter;
import de.se_rwth.commons.SourcePosition;
import de.se_rwth.commons.StringTransformations;
import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.*;
import montiarc._cocos.MontiArcASTComponentCoCo;
import montiarc._cocos.MontiArcCoCoChecker;
import montiarc._symboltable.ComponentInstanceSymbol;
import montiarc._symboltable.ComponentSymbol;
import montiarc._symboltable.PortSymbol;
import montiarc._symboltable.VariableSymbol;

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
 * @implements [Wor16] MU1: The name of each component variable is unique
 *  among ports, variables, and configuration parameters. (p.54, Lst. 4.5)
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
    
    // In case the model is faulty and there are inheritance cycles, we have to 
    // check for those before actually trying to check the uniqueness of the 
    // connector names
    // Findings up to this point are saved to not alter the results
    final List<Finding> findings = Lists.newArrayList(Log.getFindings());
    Log.getFindings().clear();
    CircularInheritance cycleCoCo = new CircularInheritance();
    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(cycleCoCo);
    checker.checkAll(node);
    final boolean inheritanceCycle
        = Log.getFindings().stream()
              .map(Finding::getMsg)
              .anyMatch(m -> m.contains("xMA017"));
    if(inheritanceCycle){
      Log.warn("Could not check for uniqueness of names in inherited " +
                   "ports due to an inheritance cycle.");
    } else {
      // Collect port names
      for (PortSymbol portSymbol : comp.getAllPorts()) {
        SourcePosition sourcePosition = SourcePosition.getDefaultSourcePosition();
        if(portSymbol.getAstNode().isPresent()){
          sourcePosition = portSymbol.getAstNode().get().get_SourcePositionStart();
        }
        names.add(new Identifier(portSymbol.getName(), IdentifierTypes.PORT, sourcePosition));
      }
    }
    // Restore the saved findings
    Log.getFindings().clear();
    Log.getFindings().addAll(findings);

    // Collect variable declarations
    for (VariableSymbol variableSymbol : comp.getVariables()) {
      names.add(new Identifier(variableSymbol.getName(),
          IdentifierTypes.VARIABLE, variableSymbol.getSourcePosition()));
    }

    for (ASTElement e : node.getBody().getElementList()) {

      // Collect constraints
      if (e instanceof ASTMontiArcInvariant) {
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
    // Due to inheritance of parameters no checking of super component parameters
    // required.
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
