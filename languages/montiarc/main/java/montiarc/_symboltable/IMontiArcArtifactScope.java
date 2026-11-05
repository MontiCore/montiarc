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
      if (getPackageName().isEmpty()) {
        // symbol qualifier always contains default package (i.e., empty string)
        return true;
      }
      // Checks whether the package parts are a prefix of the symbol's qualifier, i.e., all but the last
      // dot-separated part of the symbol name, ignoring blank parts. This is called for every artifact
      // scope of the global scope, thus we compare in place instead of splitting the symbol name.
      final List<String> packageParts = getPackageParts();
      int matched = 0;
      int start = 0;
      int next;
      while (matched < packageParts.size()) {
        if ((next = symbolName.indexOf('.', start)) == -1) {
          // the remainder is the simple name, which is not part of the qualifier
          return false;
        }
        if (!isBlank(symbolName, start, next)) {
          final String packagePart = packageParts.get(matched);
          if (packagePart.length() != next - start || !symbolName.startsWith(packagePart, start)) {
            return false;
          }
          matched++;
        }
        start = next + 1;
      }
      return true;
    }
    return false;
  }

  private static boolean isBlank(String s, int from, int to) {
    for (int i = from; i < to; i++) {
      // consistent with String#trim
      if (s.charAt(i) > ' ') return false;
    }
    return true;
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
