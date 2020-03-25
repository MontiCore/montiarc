/* (c) https://github.com/MontiCore/monticore */
package montiarc.cocos;

import de.monticore.symboltable.types.JTypeSymbol;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.*;
import montiarc._cocos.*;
import montiarc._symboltable.ComponentSymbol;

import java.util.List;

/**
 * CoCo that checks the correct capitalization of component elements.
 *
 * @implements [RRW14a] C2: The names of variables and ports start with
 * lowercase letters. (p. 31, Lst. 6.5) Context condition
 * for checking, if all fields of an IO-Automaton start with
 * a lower case letter.
 * @implements [Wor16] AC6: The names of automata start with capital letters.
 * (p. 101, Lst. 5.16)
 * @implements [Wor16] AC8: State names begin with a capital letter.
 * (p. 101, Lst. 5.18)
 * @implements [Hab16] CV2: Types start with an upper-case letter.
 * (p. 71, lst. 3.51)
 * @implements [Hab16] CV1: Instance names start with a lower-case letter.
 * (p. 71, Lst. 3.51)
 * @implements [Wor16] MC2: Behavior model names begin with capital letters.
 *  (p.57)
 */
public class NamesCorrectlyCapitalized
    implements MontiArcASTComponentCoCo,
                   MontiArcASTStateCoCo,
                   MontiArcASTBehaviorElementCoCo {

  /**
   * @see montiarc._cocos.MontiArcASTComponentCoCo#check(montiarc._ast.ASTComponent)
   */
  @Override
  public void check(ASTComponent node) {

    // Ensures, that component names start in upper-case.
    // This is required for inner components, see MontiArcModelNameCalculator.
    if (!Character.isUpperCase(node.getName().charAt(0))) {
      Log.error("0xMA055 Component names must be in upper-case",
          node.get_SourcePositionStart());
    }

    for (ASTVariableDeclaration varDecl : node.getVariables()) {
      for (String name : varDecl.getNameList())
        if (!Character.isLowerCase(name.charAt(0))) {
          Log.warn(
              String.format("0xMA018 The name of variable '%s' should " +
                                "start with a lowercase letter.", name),
              varDecl.get_SourcePositionStart());
        }
    }

    for (ASTPort port : node.getPorts()) {
      for (String name : port.getNameList()) {
        if (!Character.isLowerCase(name.charAt(0))) {
          Log.error(String.format("0xMA077: The name of the port '%s' " +
                                      "should start with a lowercase letter.",
              name),
              port.get_SourcePositionStart());
        }
      }
    }

    final List<ASTParameter> parameters = node.getHead().getParameterList();
    for (ASTParameter parameter : parameters) {
      if (!Character.isLowerCase(parameter.getName().charAt(0))) {
        Log.error(String.format("0xMA045: The name of the parameter '%s' should start with a lowercase letter.", parameter.getName()),
            parameter.get_SourcePositionStart());
      }
    }

    if (!node.getSymbolOpt().isPresent()) {
      Log.error(
          String.format("0xMA010 ASTComponent node \"%s\" has no " +
                            "symbol. Did you forget to run the " +
                            "SymbolTableCreator before checking cocos?",
              node.getName()));
      return;
    }
    ComponentSymbol componentType = (ComponentSymbol) node.getSymbolOpt().get();
    if (!componentType.getFormalTypeParameters().isEmpty()) {
      for (JTypeSymbol genType : componentType.getFormalTypeParameters()) {
        if (!Character.isUpperCase(genType.toString().charAt(0))) {
          Log.error(String.format("0xMA049: Component generic parameter '%s' should start with an upper-case", genType.getName()),
              node.get_SourcePositionStart());
        }
      }
    }

  }

  @Override
  public void check(ASTState node) {
    if (!Character.isUpperCase(node.getName().charAt(0))) {
      Log.error(
          String.format("0xMA021 The name of state %s should start with " +
                            "an uppercase letter.", node.getName()),
          node.get_SourcePositionStart());
    }
  }

  @Override
  public void check(ASTBehaviorElement node) {
    String name = null;
    if (node instanceof ASTJavaPBehavior) {
      if (((ASTJavaPBehavior) node).isPresentName()) {
        name = ((ASTJavaPBehavior) node).getName();
      }
    } else if (node instanceof ASTAutomatonBehavior) {
      if(((ASTAutomatonBehavior) node).isPresentName()){
        name = ((ASTAutomatonBehavior) node).getName();
      }
    }
    if (name != null && !Character.isUpperCase(name.charAt(0))) {
      Log.error(String.format("0xMA015 The name of the behavior " +
                                  "element %s should start with an " +
                                  "uppercase letter.", name),
          node.get_SourcePositionStart());
    }

  }
}
