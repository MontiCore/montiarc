/* (c) https://github.com/MontiCore/monticore */
package montiarc.generator;

import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.ToolFactory;
import org.eclipse.jdt.core.formatter.CodeFormatter;
import org.eclipse.jdt.core.formatter.DefaultCodeFormatterConstants;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;

import java.util.Map;
import java.util.Optional;

import static org.eclipse.jdt.core.formatter.CodeFormatter.K_COMPILATION_UNIT;

public class MA2JavaCodeFormatter {

  protected CodeFormatter formatter;

  public MA2JavaCodeFormatter() {
    // Start with Eclipse defaults to cover basics
    Map<String, String> options = DefaultCodeFormatterConstants.getEclipseDefaultSettings();

    // Force the formatter to IGNORE existing line breaks in the input
    options.put(DefaultCodeFormatterConstants.FORMATTER_JOIN_WRAPPED_LINES, JavaCore.ENABLED);
    options.put(DefaultCodeFormatterConstants.FORMATTER_JOIN_LINES_IN_COMMENTS, JavaCore.ENABLED);

    // Reduce empty lines
    options.put(DefaultCodeFormatterConstants.FORMATTER_NUMBER_OF_EMPTY_LINES_TO_PRESERVE, "0");

    // Remove blank lines at the start/end of methods (fixes gaps around the first line).
    options.put(DefaultCodeFormatterConstants.FORMATTER_BLANK_LINES_AT_BEGINNING_OF_METHOD_BODY, "0");
    options.put(DefaultCodeFormatterConstants.FORMATTER_BLANK_LINES_AT_END_OF_METHOD_BODY, "0");

    // Empty line at beginning of class
    options.put(DefaultCodeFormatterConstants.FORMATTER_BLANK_LINES_BEFORE_FIRST_CLASS_BODY_DECLARATION, "1");

    // Indentation
    options.put(DefaultCodeFormatterConstants.FORMATTER_TAB_CHAR, JavaCore.SPACE);
    options.put(DefaultCodeFormatterConstants.FORMATTER_TAB_SIZE, "2");
    options.put(DefaultCodeFormatterConstants.FORMATTER_INDENTATION_SIZE, "2");

    // Line Width
    options.put(DefaultCodeFormatterConstants.FORMATTER_LINE_SPLIT, "120");

    // Braces
    options.put(DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_METHOD_DECLARATION, DefaultCodeFormatterConstants.END_OF_LINE);
    options.put(DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_TYPE_DECLARATION, DefaultCodeFormatterConstants.END_OF_LINE);
    options.put(DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_BLOCK, DefaultCodeFormatterConstants.END_OF_LINE);
    options.put(DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_LAMBDA_BODY, DefaultCodeFormatterConstants.END_OF_LINE);

    // Clean up Lambda formatting
    options.put(
      DefaultCodeFormatterConstants.FORMATTER_ALIGNMENT_FOR_ARGUMENTS_IN_METHOD_INVOCATION,
      DefaultCodeFormatterConstants.createAlignmentValue(false, DefaultCodeFormatterConstants.WRAP_COMPACT, DefaultCodeFormatterConstants.INDENT_DEFAULT)
    );
    this.formatter = ToolFactory.createCodeFormatter(options);
  }

  public Optional<String> format(String code) {
    try {
      Document document = new Document(code);
      formatter.format(K_COMPILATION_UNIT, code, 0, code.length(), 0, null).apply(document);
      return Optional.of(document.get());
    } catch (BadLocationException | java.lang.Error e) {
      return Optional.empty();
    }
  }
}
