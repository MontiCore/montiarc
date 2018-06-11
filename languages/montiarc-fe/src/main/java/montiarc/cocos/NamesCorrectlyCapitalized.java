package montiarc.cocos;

import de.se_rwth.commons.logging.Log;
import de.monticore.symboltable.types.JFieldSymbol;
import de.monticore.symboltable.types.JTypeSymbol;
import montiarc._ast.*;
import montiarc._cocos.MontiArcASTAutomatonBehaviorCoCo;
import montiarc._cocos.MontiArcASTComponentCoCo;
import montiarc._cocos.MontiArcASTJavaPBehaviorCoCo;
import montiarc._cocos.MontiArcASTStateCoCo;
import montiarc._symboltable.MontiArcModelNameCalculator;
import montiarc._symboltable.ComponentSymbol;

import java.util.List;

/**
 * CoCo that checks the correct capitalization of component elements.
 *
 * @author Gerrit Leonhardt, Andreas Wortmann, Michael Mutert
 * @implements [RRW14a] C2: The names of variables and ports start with
 * lowercase letters. (p. 31, Lst. 6.5) Context condition
 * for checking, if all fields of an IO-Automaton start with
 * a lower case letter.
 * @implements [Wor16] AC6: The names of automata start with capital letters.
 * (p. 101, Lst. 5.16)
 * @implements [Wor16] AC8: State names begin with a capital letter.
 * (p. 101, Lst. 5.18)
 * @implements [Hab16] CV2: Types start with an upper-case 
 * (p. 71, lst. 3.51)
 * @implements [Hab16] CV1: Instance names start with a lower-case letter. 
 * (p. 71, Lst. 3.51)
 * 
 */
public class NamesCorrectlyCapitalized
    implements MontiArcASTComponentCoCo,
                   MontiArcASTStateCoCo,
                   MontiArcASTAutomatonBehaviorCoCo,
                   MontiArcASTJavaPBehaviorCoCo {

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
      for (String name : varDecl.getNames())
        if (!Character.isLowerCase(name.charAt(0))) {
          Log.warn(
              String.format("0xMA018 The name of variable '%s' should " +
                                "start with a lowercase letter.", name),
              varDecl.get_SourcePositionStart());
        }
    }

    for (ASTPort port : node.getPorts()) {
      for (String name : port.getNames()) {
        if (!Character.isLowerCase(name.charAt(0))) {
          Log.error(String.format("0xMA077: The name of the port '%s' " +
                                      "should start with a lowercase letter.",
              name),
              port.get_SourcePositionStart());
        }
      }
    }

    final List<ASTParameter> parameters = node.getHead().getParameters();
    for (ASTParameter parameter : parameters) {
      if (!Character.isLowerCase(parameter.getName().charAt(0))) {
        Log.error(String.format("0xMA045: The name of the parameter '%s' should start with a lowercase letter.", parameter.getName()),
            parameter.get_SourcePositionStart());
      }
    }
    
    ComponentSymbol componentType = (ComponentSymbol) node.getSymbol().get();
    if(!componentType.getFormalTypeParameters().isEmpty()){ 
    	for (JTypeSymbol genType : componentType.getFormalTypeParameters()) {
    		if(!Character.isUpperCase(genType.toString().charAt(0))){  
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
  public void check(ASTAutomatonBehavior node) {
    if (node.getName().isPresent()) {
      if (!Character.isUpperCase(node.getName().get().charAt(0))) {
        Log.error(String.format("0xMA015 The name of the automaton %s " +
                                    "should start with an uppercase letter.",
            node.getName().get()),
            node.get_SourcePositionStart());
      }
    }
  }

  @Override
  public void check(ASTJavaPBehavior node) {
    if (node.getName().isPresent()) {
      if (!Character.isUpperCase(node.getName().get().charAt(0))) {
        Log.error(
            String.format("0xMA174 The name of the AJava compute block " +
                              "'%s' should start with an uppercase letter.",
                node.getName()),
            node.get_SourcePositionStart());
      }
    }
  }
}
