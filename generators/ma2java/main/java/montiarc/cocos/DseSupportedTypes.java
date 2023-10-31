/* (c) https://github.com/MontiCore/monticore */
package montiarc.cocos;

import arcbasis._ast.ASTComponentType;
import arcbasis._ast.ASTArcPort;
import arcbasis._cocos.ArcBasisASTComponentTypeCoCo;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;
import de.se_rwth.commons.SourcePosition;

import java.util.List;

public abstract class DseSupportedTypes {

  private DseSupportedTypes() {
  }

  /**
   * helper function for checkDataTypes
   */
  public static boolean checkUnsupported(String name) {
    switch (name) {
      case "short":
      case "Short":
      case "Byte":
      case "byte":
        return false;
      default:
        return true;
    }
  }

  /**
   * helper function for checkDataTypes
   */
  public static boolean checkUnsupportedVariables(String name) {
    switch (name) {
      case "short":
      case "Short":
      case "Byte":
      case "byte":
      case "Long":
      case "long":
        return false;
      default:
        return true;
    }
  }

  /**
   * helper function for checkDataTypes
   */
  public static boolean checkSupported(String name) {
    switch (name) {
      case "Integer":
      case "int":
      case "Boolean":
      case "boolean":
      case "Double":
      case "double":
      case "Float":
      case "float":
      case "String":
      case "Character":
      case "char":
      case "Long":
      case "long":
        return true;
      default:
        return false;
    }
  }

  public static void printError(String compName, String dataType, SourcePosition position) {
    montiarc.rte.log.Log.error(compName + ": the type \'" + dataType + "\' is not supported for " +
      "variables/ parameters." + " Source position: " + position);
  }

  public static void printError(String compName, String dataType, SourcePosition start,
                                SourcePosition end) {
    montiarc.rte.log.Log.error(compName + ": the type \'" + dataType + "\' is not supported for " +
      "ports." + " Source position start: " + start + " end: " + end);
  }

  public static class DseParameters_VariablesTypes implements ArcBasisASTComponentTypeCoCo {
    @Override
    public void check(ASTComponentType componentType) {
      for (ASTArcPort port : componentType.getPorts()) {
        if (!checkUnsupported(port.getSymbol().getType().print())) {
          printError(componentType.getName(), port.getSymbol().getType()
            .print(), port.get_SourcePositionStart(), port.get_SourcePositionEnd());
        }
        if (port.getSymbol().getTypeInfo() instanceof OOTypeSymbol) {
          if (!(((OOTypeSymbol) port.getSymbol().getType()
            .getTypeInfo()).isIsEnum()) && !checkSupported(port.getSymbol().getType().print())) {
            printError(componentType.getName(), port.getSymbol().getType()
              .print(), port.get_SourcePositionStart(), port.get_SourcePositionEnd());

          }
        }
      }
      List<VariableSymbol> symbols = componentType.getSymbol().getFields();
      symbols.removeAll(componentType.getSymbol().getParameters());
      for (VariableSymbol symbol : symbols) {

        if (!checkUnsupportedVariables(symbol.getType().print())) {
          printError(componentType.getName(), symbol.getType()
            .print(), symbol.getSourcePosition());
        }
      }
      for (VariableSymbol symbol : componentType.getSymbol().getParameters()) {

        if (!checkUnsupported(symbol.getType().print())) {
          printError(componentType.getName(), symbol.getType()
            .print(), symbol.getSourcePosition());
        }
      }
      for (VariableSymbol symbol : componentType.getSymbol().getFields()) {
        if (symbol.getType().getTypeInfo() instanceof OOTypeSymbol) {

          if (!((OOTypeSymbol) symbol.getType()
            .getTypeInfo()).isIsEnum() && !checkSupported(symbol.getType().print())) {
            printError(componentType.getName(), symbol.getType()
              .print(), symbol.getSourcePosition());

          }
        }
      }
    }
  }
}


