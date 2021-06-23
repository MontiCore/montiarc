/* (c) https://github.com/MontiCore/monticore */
package montiarc.generator.codegen.xtend.util;

import com.google.common.base.Preconditions;

/**
 * mix of Java's {@link StringBuilder} and xtend's StringBuilder
 */
public class ConcatenationBuilder {

  StringBuilder builder = new StringBuilder();
  boolean isEmpty = true;
  boolean needsSeparator = true;

  @Override
  public String toString() {
    return builder.toString();
  }

  public ConcatenationBuilder append(String string) {
    builder.append(string);
    isEmpty = false;
    return this;
  }

  public ConcatenationBuilder newLine() {
    this.append("\n");
    isEmpty = true;
    return this;
  }

  public ConcatenationBuilder newLineIfNotEmpty() {
    return isEmpty?this:this.newLine();
  }

  public ConcatenationBuilder append(String string, String indent) {
    return this.append(string);
  }

  public ConcatenationBuilder appendWithFirstCharUpper(String name){
    Preconditions.checkArgument(name.length()!=0);
    return this.append(name.substring(0, 1).toUpperCase()).append(name.substring(1));
  }

  public ConcatenationBuilder separate(String separator) {
    if(needsSeparator){
      this.append(separator);
    }
    needsSeparator = true;
    return this;
  }

  public ConcatenationBuilder skipNextSeparator(){
    needsSeparator = false;
    return this;
  }
}