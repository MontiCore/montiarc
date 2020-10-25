/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;

import arcbasis._ast.*;
import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis.util.ArcError;
import com.google.common.base.Preconditions;
import de.monticore.symbols.basicsymbols._symboltable.TypeVarSymbol;
import de.se_rwth.commons.logging.Log;
import org.codehaus.commons.nullanalysis.NotNull;

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
 * (p.57)
 */
public class NamesCorrectlyCapitalized implements ArcBasisASTComponentTypeCoCo {

  @Override
  public void check(@NotNull ASTComponentType node) {
    Preconditions.checkArgument(node != null);
    Preconditions.checkArgument(node.isPresentSymbol(), "ASTComponent node '%s' has no symbol. "
      + "Did you forget to run the SymbolTableCreator before checking cocos?", node.getName());
    if (!Character.isUpperCase(node.getName().charAt(0))) {
      Log.error("0xMA055 Component names must be in upper-case",
        node.get_SourcePositionStart());
    }

    for (ASTArcField field : node.getFields())
      if (!Character.isLowerCase(field.getName().charAt(0))) {
        Log.warn(
          String.format(ArcError.VARIABLE_LOWER_CASE.toString(), field.getName(), node.getName()),
          field.get_SourcePositionStart());
      }

    for (ASTPort port : node.getPorts()) {
      if (!Character.isLowerCase(port.getName().charAt(0))) {
        Log.error(String.format(ArcError.PORT_LOWER_CASE.toString(), port.getName(),
          node.getName()), port.get_SourcePositionStart());
      }
    }

    final List<ASTArcParameter> parameters = node.getHead().getArcParametersList();
    for (ASTArcParameter parameter : parameters) {
      if (!Character.isLowerCase(parameter.getName().charAt(0))) {
        Log.error(String.format(ArcError.PARAMETER_LOWER_CASE.toString(),
          parameter.getName(), node.getName()),
          parameter.get_SourcePositionStart());
      }
    }

    ComponentTypeSymbol symbol = node.getSymbol();
    if (!symbol.getTypeParameters().isEmpty()) {
      for (TypeVarSymbol typeParameter : symbol.getTypeParameters()) {
        if (!Character.isUpperCase(typeParameter.toString().charAt(0))) {
          Log.error(String.format(ArcError.TYPE_PARAMETER_UPPER_CASE_LETTER.toString(),
            typeParameter.getName(), symbol.getFullName()), node.get_SourcePositionStart());
        }
      }
    }
  }
}