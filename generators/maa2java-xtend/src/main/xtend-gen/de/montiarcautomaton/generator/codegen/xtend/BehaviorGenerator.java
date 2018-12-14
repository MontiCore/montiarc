/**
 * Copyright (c) 2018 RWTH Aachen. All rights reserved.
 * 
 * http://www.se-rwth.de/
 */
package de.montiarcautomaton.generator.codegen.xtend;

import de.montiarcautomaton.generator.helper.ComponentHelper;
import de.monticore.symboltable.ImportStatement;
import de.monticore.symboltable.types.JFieldSymbol;
import java.util.Collection;
import java.util.List;
import montiarc._symboltable.ComponentSymbol;
import montiarc._symboltable.VariableSymbol;
import org.eclipse.xtend2.lib.StringConcatenation;

/**
 * TODO: Write me!
 * 
 * @author  (last commit) $Author$
 * @version $Revision$,
 *          $Date$
 * @since   TODO: add version number
 */
@SuppressWarnings("all")
public abstract class BehaviorGenerator {
  public abstract String generateCompute(final ComponentSymbol comp);
  
  public abstract String generateGetInitialValues(final ComponentSymbol comp);
  
  public abstract String hook(final ComponentSymbol comp);
  
  public String generate(final ComponentSymbol comp) {
    ComponentHelper helper = new ComponentHelper(comp);
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("package ");
    String _packageName = comp.getPackageName();
    _builder.append(_packageName);
    _builder.append(";");
    _builder.newLineIfNotEmpty();
    _builder.newLine();
    _builder.append("import ");
    String _packageName_1 = comp.getPackageName();
    _builder.append(_packageName_1);
    _builder.append(".");
    String _name = comp.getName();
    _builder.append(_name);
    _builder.append("Result;");
    _builder.newLineIfNotEmpty();
    _builder.append("import ");
    String _packageName_2 = comp.getPackageName();
    _builder.append(_packageName_2);
    _builder.append(".");
    String _name_1 = comp.getName();
    _builder.append(_name_1);
    _builder.append("Input;");
    _builder.newLineIfNotEmpty();
    {
      List<ImportStatement> _imports = comp.getImports();
      for(final ImportStatement _import : _imports) {
        _builder.append("import ");
        String _statement = _import.getStatement();
        _builder.append(_statement);
        {
          boolean _isStar = _import.isStar();
          if (_isStar) {
            _builder.append(".*");
          }
        }
        _builder.append(";");
        _builder.newLineIfNotEmpty();
      }
    }
    _builder.newLine();
    _builder.append("import de.montiarcautomaton.runtimes.timesync.implementation.IComputable;");
    _builder.newLine();
    _builder.newLine();
    _builder.append("public class ");
    String _name_2 = comp.getName();
    _builder.append(_name_2);
    _builder.append("Impl");
    _builder.newLineIfNotEmpty();
    {
      boolean _isGeneric = helper.isGeneric();
      if (_isGeneric) {
        {
          List<String> _genericParameters = helper.getGenericParameters();
          boolean _hasElements = false;
          for(final String generic : _genericParameters) {
            if (!_hasElements) {
              _hasElements = true;
            } else {
              _builder.appendImmediate(",", "");
            }
            _builder.append(generic);
            _builder.newLineIfNotEmpty();
          }
        }
      }
    }
    _builder.append("implements IComputable<");
    String _name_3 = comp.getName();
    _builder.append(_name_3);
    _builder.append("Input, ");
    String _name_4 = comp.getName();
    _builder.append(_name_4);
    _builder.append("Result> {");
    _builder.newLineIfNotEmpty();
    _builder.append("  ");
    _builder.newLine();
    _builder.append("  ");
    _builder.append("//component variables");
    _builder.newLine();
    {
      Collection<VariableSymbol> _variables = comp.getVariables();
      for(final VariableSymbol compVar : _variables) {
        _builder.append("  ");
        _builder.append("private ");
        String _printVariableTypeName = helper.printVariableTypeName(compVar);
        _builder.append(_printVariableTypeName, "  ");
        _builder.append(" ");
        String _name_5 = compVar.getName();
        _builder.append(_name_5, "  ");
        _builder.append(";");
        _builder.newLineIfNotEmpty();
      }
    }
    _builder.append("  ");
    _builder.newLine();
    _builder.append("  ");
    _builder.append("// config parameters");
    _builder.newLine();
    {
      List<JFieldSymbol> _configParameters = comp.getConfigParameters();
      for(final JFieldSymbol param : _configParameters) {
        _builder.append("  ");
        _builder.append("private final ");
        String _printParamTypeName = helper.printParamTypeName(param);
        _builder.append(_printParamTypeName, "  ");
        _builder.append(" ");
        String _name_6 = param.getName();
        _builder.append(_name_6, "  ");
        _builder.append("; ");
        _builder.newLineIfNotEmpty();
      }
    }
    _builder.append("  ");
    _builder.newLine();
    _builder.append("  ");
    _builder.newLine();
    _builder.append("  ");
    String _hook = this.hook(comp);
    _builder.append(_hook, "  ");
    _builder.newLineIfNotEmpty();
    _builder.append("  ");
    _builder.newLine();
    _builder.append("  ");
    String _generateConstructor = this.generateConstructor(comp);
    _builder.append(_generateConstructor, "  ");
    _builder.newLineIfNotEmpty();
    _builder.append("  ");
    _builder.newLine();
    _builder.append("  ");
    String _generateGetInitialValues = this.generateGetInitialValues(comp);
    _builder.append(_generateGetInitialValues, "  ");
    _builder.newLineIfNotEmpty();
    _builder.append("  ");
    _builder.newLine();
    _builder.append("  ");
    String _generateCompute = this.generateCompute(comp);
    _builder.append(_generateCompute, "  ");
    _builder.newLineIfNotEmpty();
    _builder.append("  ");
    _builder.newLine();
    _builder.append("}");
    _builder.newLine();
    _builder.newLine();
    return _builder.toString();
  }
  
  public String generateConstructor(final ComponentSymbol comp) {
    ComponentHelper helper = new ComponentHelper(comp);
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("public ");
    String _name = comp.getName();
    _builder.append(_name);
    _builder.append("Impl(");
    {
      List<JFieldSymbol> _configParameters = comp.getConfigParameters();
      boolean _hasElements = false;
      for(final JFieldSymbol param : _configParameters) {
        if (!_hasElements) {
          _hasElements = true;
        } else {
          _builder.appendImmediate(",", "");
        }
        _builder.append(" ");
        String _paramTypeName = helper.getParamTypeName(param);
        _builder.append(_paramTypeName);
        _builder.append(" ");
        String _name_1 = param.getName();
        _builder.append(_name_1);
        _builder.append(" ");
      }
    }
    _builder.append(") {");
    _builder.newLineIfNotEmpty();
    {
      List<JFieldSymbol> _configParameters_1 = comp.getConfigParameters();
      for(final JFieldSymbol param_1 : _configParameters_1) {
        _builder.append("  ");
        _builder.append("this.");
        String _name_2 = param_1.getName();
        _builder.append(_name_2, "  ");
        _builder.append(" = ");
        String _name_3 = param_1.getName();
        _builder.append(_name_3, "  ");
        _builder.append("; ");
        _builder.newLineIfNotEmpty();
      }
    }
    _builder.append("}");
    _builder.newLine();
    return _builder.toString();
  }
}
