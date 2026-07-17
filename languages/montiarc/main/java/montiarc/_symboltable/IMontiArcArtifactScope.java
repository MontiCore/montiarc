/* (c) https://github.com/MontiCore/monticore */
package montiarc._symboltable;

import java.util.ArrayList;
import java.util.List;

public interface IMontiArcArtifactScope extends IMontiArcArtifactScopeTOP {

  List<String> getPackageParts();

  /*
   * Override for performance improvements
   */
  @Override
  default boolean checkIfContinueAsSubScope(String symbolName) {
    if (this.isExportingSymbols()) {
      final List<String> symbolQualifierParts = splitToList(symbolName);
      if (!symbolQualifierParts.isEmpty() && !symbolQualifierParts.getLast().equals(".")) {
        symbolQualifierParts.removeLast();
      }
      symbolQualifierParts.removeIf(part -> part.trim().isEmpty());
      final List<String> packageParts = getPackageParts();

      boolean symbolNameStartsWithPackage = true;

      if (getPackageName().isEmpty()) {
        // symbol qualifier always contains default package (i.e., empty string)
        symbolNameStartsWithPackage = true;
      } else if (symbolQualifierParts.size() >= packageParts.size()) {
        for (int i = 0; i < packageParts.size(); i++) {
          if (!packageParts.get(i).equals(symbolQualifierParts.get(i))) {
            symbolNameStartsWithPackage = false;
            break;
          }
        }
      } else {
        symbolNameStartsWithPackage = false;
      }
      return symbolNameStartsWithPackage;
    }
    return false;
  }

  default List<String> splitToList(String symbolName) {
    List<String> parts = new ArrayList<>(4);

    int start = 0;
    int next;

    while ((next = symbolName.indexOf('.', start)) != -1) {
      parts.add(symbolName.substring(start, next));
      start = next + 1;
    }
    parts.add(symbolName.substring(start));

    return parts;
  }
}
