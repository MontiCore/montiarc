/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd2pojo.cocos;

import com.google.common.base.Preconditions;
import de.monticore.cd4code._symboltable.CD4CodeScopeDeSer;
import de.monticore.cd4code._symboltable.ICD4CodeScope;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.cdbasis._symboltable.ICDBasisScope;
import de.se_rwth.commons.logging.Log;
import org.codehaus.commons.nullanalysis.NotNull;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class CDSymTabPrinter {
  
  public static void printCDSymTab2Json(@NotNull CDTypeSymbol typeSymbol, @NotNull Path targetPath) {
    Preconditions.checkNotNull(typeSymbol);
    Preconditions.checkNotNull(targetPath);
    ICDBasisScope scope = typeSymbol.getEnclosingScope();
    if (!(scope instanceof ICD4CodeScope)) {
      Log.debug("Scope spanned by type symbol '" + typeSymbol.getFullName() +
        "' is expected to be an instance of ICD4CodeScope, but it is not.", "Error");
      Log.error("Error while trying to serialize type " + typeSymbol.getFullName());
      return;
    }
    
    ICD4CodeScope typeScope = (ICD4CodeScope) typeSymbol.getEnclosingScope();
    String serializedCDSymTab = new CD4CodeScopeDeSer().serialize(typeScope);
    
    //TODO first: implement fixSerializedCDSymTab
    // second: remove function call when cd4a serializes correctly
    serializedCDSymTab = CDDeSerWorkarounds.fixSerializedCDSymTab(serializedCDSymTab);
    
    File outputFile = targetPath.resolve(
      typeSymbol.getFullName().replaceAll("\\.", "/") + cdSymTabFileExt).toAbsolutePath().toFile();
    
    doPrintToFile(serializedCDSymTab, outputFile);
  }
  
  protected static String cdSymTabFileExt = ".cdsym";
  
  /**
   * Writes the JSON string to the specified file in a nicely formatted way.
   * The file to which should be written must not exist before calling the method as to prevent overwriting of important files.
   *
   * @param cdSymTabJSON a string representation of JSON
   * @param outputFile   the file to which the JSON should be written
   */
  protected static void doPrintToFile(@NotNull String cdSymTabJSON, @NotNull File outputFile) {
    Preconditions.checkNotNull(cdSymTabJSON);
    Preconditions.checkNotNull(outputFile);
    outputFile = outputFile.isAbsolute() ? outputFile : outputFile.getAbsoluteFile();
    if (!prepareOutputFile(outputFile)) {
      Log.error("Error while preparing output file for CD symbol table " + outputFile);
      return;
    }
    try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
      Indent indent = new Indent();
      for (String line : splitIntoSingleLines(cdSymTabJSON)) {
        if (line.isEmpty()) {
          continue;
        }
        if (line.charAt(0) == ']' || line.charAt(0) == '}') {
          indent.decreaseIndent(2);
        }
        writer.write(indent + line);
        writer.newLine();
        if (line.charAt(0) == '[' || line.charAt(0) == '{'
          || line.charAt(line.length() - 1) == '[' || line.charAt(line.length() - 1) == '{') {
          indent.increaseIndent(2);
        }
      }
    } catch (IOException e) {
      Log.error("Error while writing CD symbol table to file " + outputFile, e);
    }
  }
  
  /**
   * Creates the desired output file.
   * Ensures it did not exist before so that nothing will be overwritten.
   * Further ensures that the created file can be written to.
   *
   * @param outputFile the file to create and check
   * @return true if, and only if, the file was freshly created and can be written to
   */
  protected static boolean prepareOutputFile(@NotNull File outputFile) {
    Preconditions.checkNotNull(outputFile);
    File parentDirectory = new File(
      outputFile.toString().substring(0, outputFile.toString().length() - outputFile.getName().length()));
    if (!parentDirectory.exists() && !parentDirectory.mkdirs()) {
      Log.error(String.format("Could not create parent directory '%s' for file '%s'.", parentDirectory.toString(), outputFile.toString()));
      return false;
    }
    if (!outputFile.exists()) {
      try {
        outputFile.createNewFile();
      } catch (IOException e) {
        Log.error("Could not create new file " + outputFile, e);
        return false;
      }
    } else {
      Log.error("File '" + outputFile + "' already exists.");
      return false;
    }
    if (outputFile.exists() && !outputFile.canWrite()) {
      Log.error("Cannot write to file " + outputFile);
      return false;
    }
    return true;
  }
  
  /**
   * Splits a JSON string into single lines. Splits at '{', '}', '[', ']' and ',', ignores any line breaks present.
   *
   * @param cdSymTabJSON A string representing JSON
   * @return the JSON split into lines, as a list of lines
   */
  protected static List<String> splitIntoSingleLines(@NotNull String cdSymTabJSON) {
    Preconditions.checkNotNull(cdSymTabJSON);
    List<String> lines = new ArrayList<>();
    StringBuilder line = new StringBuilder();
    char[] chars = cdSymTabJSON.toCharArray();
    for (int i = 0; i < chars.length; i++) {
      if (chars[i] == ',') { //always start a new line after a comma
        line.append(chars[i]);
        lines.add(line.toString());
        line.delete(0, line.length());
      } else if (chars[i] == '{' || chars[i] == '[') { //opening brackets always have a line break before and after them them, with two exceptions
        if (chars[i] == '[') { // exception 1: square brackets may be the start of a cardinality which is not preceded or followed by a line break
          boolean cardinalitySingleDigit = ((chars[i] == '[') && ((i + 2 < chars.length) && (chars[i + 2] == ']')));
          if (cardinalitySingleDigit) { //simple check for cardinality [1] or [*]
            line.append(chars[i]).append(chars[i + 1]).append(chars[i + 2]);
            i += 2;
            continue;
          }
          boolean cardinalityOneToMany = ((chars[i] == '[') && ((i + 5 < chars.length) && (chars[i + 5] == ']')));
          if (cardinalityOneToMany) { //simple check for cardinality [1..*]
            line.append(chars[i]).append(chars[i + 1]).append(chars[i + 2])
              .append(chars[i + 3]).append(chars[i + 4]).append(chars[i + 5]);
            i += 5;
            continue;
          }
        }
        if (!(i - 1 > 0 && chars[i - 1] == ':')) { //opening brackets are only preceded by a line break if they are not preceded by a colon
          lines.add(line.toString());
          lines.add("" + chars[i]);
        } else {
          line.append(chars[i]);
          lines.add(line.toString());
        }
        line.delete(0, line.length());
      } else if (chars[i] == '}' || chars[i] == ']') { //closing brackets always have a line break before them
        lines.add(line.toString());
        line.delete(0, line.length());
        if (!(i + 1 < chars.length && chars[i + 1] == ',')) { //closing brackets only have a line break after them if they are not followed by a comma
          lines.add("" + chars[i]);
        } else {
          line.append(chars[i]);
        }
      } else { //at this point, chars[i] is no special character so can we just append it to the line
        line.append(chars[i]);
      }
    }
    if (!String.join("", lines).equals(cdSymTabJSON)) { //TODO clarify: is ths sanity check really necessary?
      Log.error("There was an error while transforming the JSON string. Nothing will be printed.");
      return new ArrayList<>();
    }
    return lines;
  }
  
  /**
   * A not so sophisticated wrapper for a StringBuffer to simplify indentation handling while printing to a file.
   * Allows setting the character with which it realizes indents for no particular reason.
   */
  private static class Indent {
  
    public char indentChar = ' ';
    private StringBuilder sb = new StringBuilder();
  
    public Indent() {}
  
    public Indent(char indentChar) { this.indentChar = indentChar; }
  
    public void increaseIndent(int increase) {
      for (int i = 0; i < increase; i++) {
        sb.append(indentChar);
      }
    }
  
    public void decreaseIndent(int decrease) {
      if (sb.length() > 0) {
        sb.delete(0, decrease);
      }
    }
  
    @Override
    public String toString() {
      return sb.toString();
    }
  }
}

class CDDeSerWorkarounds {
  
  public static String fixSerializedCDSymTab(String serializedSymTab) {
    //TODO implement
    return serializedSymTab;
  }
  
  static Map<String, List<String>> mapTypeToSuperTypes; //Key extends/implements Values
  public static void initMap() {
    mapTypeToSuperTypes.put("de.monticore.cdassociation._symboltable.CDRoleSymbol", new ArrayList<>(Arrays.asList(
      "de.monticore.symbols.oosymbols._symboltable.FieldSymbol")));
    
    mapTypeToSuperTypes.put("de.monticore.cdbasis._symboltable.CDTypeSymbol", new ArrayList<>(Arrays.asList(
      "de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol")));
    
    mapTypeToSuperTypes.put("de.monticore.symbols.oosymbols._symboltable.FieldSymbol", new ArrayList<>(Arrays.asList(
      "de.monticore.symbols.basicsymbols._symboltable.VariableSymbol")));
    
    mapTypeToSuperTypes.put("de.monticore.cd4codebasis._symboltable.CDMethodSignatureSymbol", new ArrayList<>(Arrays.asList(
      "de.monticore.symbols.oosymbols._symboltable.MethodSymbol")));
    
    mapTypeToSuperTypes.put("de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol", new ArrayList<>(Arrays.asList(
      "de.monticore.symbols.basicsymbols._symboltable.TypeSymbol")));
    
    mapTypeToSuperTypes.put("de.monticore.symbols.oosymbols._symboltable.MethodSymbol", new ArrayList<>(Arrays.asList(
      "de.monticore.symbols.basicsymbols._symboltable.FunctionSymbol")));
    
    mapTypeToSuperTypes.put("de.monticore.symbols.basicsymbols._symboltable.TypeVarSymbol", new ArrayList<>(Arrays.asList(
      "de.monticore.symbols.basicsymbols._symboltable.TypeSymbol")));
  }
}