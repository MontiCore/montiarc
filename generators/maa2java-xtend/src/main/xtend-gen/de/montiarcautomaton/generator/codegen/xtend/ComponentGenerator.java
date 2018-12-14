/**
 * Copyright (c) 2012 itemis AG (http://www.itemis.eu) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.montiarcautomaton.generator.codegen.xtend;

import de.montiarcautomaton.generator.codegen.xtend.AutomatonGenerator;
import de.montiarcautomaton.generator.codegen.xtend.JavaPGenerator;
import de.montiarcautomaton.generator.helper.ComponentHelper;
import de.monticore.ast.ASTCNode;
import de.monticore.ast.ASTNode;
import de.monticore.codegen.mc2cd.TransformationHelper;
import de.monticore.io.FileReaderWriter;
import de.monticore.io.paths.IterablePath;
import de.monticore.symboltable.ImportStatement;
import de.monticore.symboltable.types.JFieldSymbol;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import montiarc._ast.ASTAutomatonBehavior;
import montiarc._ast.ASTBehaviorElement;
import montiarc._ast.ASTComponent;
import montiarc._ast.ASTElement;
import montiarc._ast.ASTJavaPBehavior;
import montiarc._symboltable.ComponentInstanceSymbol;
import montiarc._symboltable.ComponentSymbol;
import montiarc._symboltable.ConnectorSymbol;
import montiarc._symboltable.PortSymbol;
import montiarc._symboltable.VariableSymbol;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.xbase.lib.Exceptions;
import org.eclipse.xtext.xbase.lib.InputOutput;
import org.eclipse.xtext.xbase.lib.StringExtensions;

@SuppressWarnings("all")
public class ComponentGenerator {
  public void generateAll(final File targetPath, final File hwc, final ComponentSymbol comp) {
    IterablePath _from = IterablePath.from(hwc, ".java");
    String _packageName = comp.getPackageName();
    String _plus = (_packageName + ".");
    String _name = comp.getName();
    String _plus_1 = (_plus + _name);
    boolean existsHWCClass = TransformationHelper.existsHandwrittenClass(_from, _plus_1);
    String _name_1 = comp.getName();
    String _plus_2 = (_name_1 + "Input");
    this.toFile(targetPath, _plus_2, this.generateInput(comp));
    String _name_2 = comp.getName();
    String _plus_3 = (_name_2 + "Result");
    this.toFile(targetPath, _plus_3, this.generateResult(comp));
    boolean _isAtomic = comp.isAtomic();
    if (_isAtomic) {
      this.toFile(targetPath, comp.getName(), this.generateAtomicComponent(comp));
      if ((!existsHWCClass)) {
        String _name_3 = comp.getName();
        String _plus_4 = (_name_3 + "Impl");
        this.toFile(targetPath, _plus_4, this.generateBehaviorImplementation(comp));
      }
    } else {
      this.toFile(targetPath, comp.getName(), this.generateComposedComponent(comp));
    }
    boolean _containsKey = comp.getStereotype().containsKey("deploy");
    if (_containsKey) {
      String _name_4 = comp.getName();
      String _plus_5 = ("Deploy" + _name_4);
      this.toFile(targetPath, _plus_5, this.generateDeploy(comp));
    }
  }
  
  public void toFile(final File targetPath, final String name, final String content) {
    String _absolutePath = targetPath.getAbsolutePath();
    String _plus = (_absolutePath + "\\");
    String _plus_1 = (_plus + name);
    String _plus_2 = (_plus_1 + ".java");
    Path path = Paths.get(_plus_2);
    FileReaderWriter writer = new FileReaderWriter();
    InputOutput.<String>println((("Writing to file " + path) + "."));
    writer.storeInFile(path, content);
  }
  
  protected String _generateBehavior(final ASTJavaPBehavior ajava, final ComponentSymbol comp) {
    try {
      return JavaPGenerator.class.newInstance().generate(comp);
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  protected String _generateBehavior(final ASTAutomatonBehavior automaton, final ComponentSymbol comp) {
    try {
      return AutomatonGenerator.class.newInstance().generate(comp);
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  public String generateBehaviorImplementation(final ComponentSymbol comp) {
    ASTNode _get = comp.getAstNode().get();
    ASTComponent compAST = ((ASTComponent) _get);
    boolean hasBehavior = false;
    List<ASTElement> _elementList = compAST.getBody().getElementList();
    for (final ASTElement element : _elementList) {
      if ((element instanceof ASTBehaviorElement)) {
        hasBehavior = true;
        return this.generateBehavior(((ASTCNode) element), comp);
      }
    }
    if ((!hasBehavior)) {
      return this.generateAbstractAtomicImplementation(comp);
    }
    return null;
  }
  
  public String generateAbstractAtomicImplementation(final ComponentSymbol comp) {
    ComponentHelper helper = new ComponentHelper(comp);
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("package ");
    String _packageName = comp.getPackageName();
    _builder.append(_packageName);
    _builder.append(";");
    _builder.newLineIfNotEmpty();
    _builder.newLine();
    _builder.append("import de.montiarcautomaton.runtimes.timesync.implementation.IComputable;");
    _builder.newLine();
    String _printImports = this.printImports(comp);
    _builder.append(_printImports);
    _builder.newLineIfNotEmpty();
    _builder.newLine();
    _builder.append("class ");
    String _name = comp.getName();
    _builder.append(_name);
    _builder.append("Impl");
    String _printGenerics = this.printGenerics(comp);
    _builder.append(_printGenerics);
    _builder.append("     ");
    _builder.newLineIfNotEmpty();
    _builder.append("implements IComputable<");
    String _name_1 = comp.getName();
    _builder.append(_name_1);
    _builder.append("Input");
    String _printGenerics_1 = this.printGenerics(comp);
    _builder.append(_printGenerics_1);
    _builder.append(", ");
    String _name_2 = comp.getName();
    _builder.append(_name_2);
    _builder.append("Result");
    String _printGenerics_2 = this.printGenerics(comp);
    _builder.append(_printGenerics_2);
    _builder.append("> {");
    _builder.newLineIfNotEmpty();
    _builder.newLine();
    _builder.append("  ");
    _builder.append("public ");
    String _name_3 = comp.getName();
    _builder.append(_name_3, "  ");
    _builder.append("Impl(");
    {
      List<JFieldSymbol> _configParameters = comp.getConfigParameters();
      boolean _hasElements = false;
      for(final JFieldSymbol param : _configParameters) {
        if (!_hasElements) {
          _hasElements = true;
        } else {
          _builder.appendImmediate(",", "  ");
        }
        _builder.append(" ");
        String _paramTypeName = helper.getParamTypeName(param);
        _builder.append(_paramTypeName, "  ");
        _builder.append(" ");
        String _name_4 = param.getName();
        _builder.append(_name_4, "  ");
        _builder.append(" ");
      }
    }
    _builder.append(") {");
    _builder.newLineIfNotEmpty();
    _builder.append("    ");
    _builder.append("throw new Error(\"Invoking constructor on abstract implementation ");
    String _packageName_1 = comp.getPackageName();
    _builder.append(_packageName_1, "    ");
    _builder.append(".");
    String _name_5 = comp.getName();
    _builder.append(_name_5, "    ");
    _builder.append("\");");
    _builder.newLineIfNotEmpty();
    _builder.append("  ");
    _builder.append("}");
    _builder.newLine();
    _builder.newLine();
    _builder.append("  ");
    _builder.append("public ");
    String _name_6 = comp.getName();
    _builder.append(_name_6, "  ");
    _builder.append("Result");
    String _printGenerics_3 = this.printGenerics(comp);
    _builder.append(_printGenerics_3, "  ");
    _builder.append(" getInitialValues() {");
    _builder.newLineIfNotEmpty();
    _builder.append("    ");
    _builder.append("throw new Error(\"Invoking getInitialValues() on abstract implementation ");
    String _packageName_2 = comp.getPackageName();
    _builder.append(_packageName_2, "    ");
    _builder.append(".");
    String _name_7 = comp.getName();
    _builder.append(_name_7, "    ");
    _builder.append("\");");
    _builder.newLineIfNotEmpty();
    _builder.append("  ");
    _builder.append("}");
    _builder.newLine();
    _builder.append(" ");
    _builder.append("public ");
    String _name_8 = comp.getName();
    _builder.append(_name_8, " ");
    _builder.append("Result");
    String _printGenerics_4 = this.printGenerics(comp);
    _builder.append(_printGenerics_4, " ");
    _builder.append(" compute(");
    String _name_9 = comp.getName();
    _builder.append(_name_9, " ");
    _builder.append("Input");
    String _printGenerics_5 = this.printGenerics(comp);
    _builder.append(_printGenerics_5, " ");
    _builder.append(" ");
    String _inputName = helper.getInputName();
    _builder.append(_inputName, " ");
    _builder.append(") {");
    _builder.newLineIfNotEmpty();
    _builder.append("    ");
    _builder.append("throw new Error(\"Invoking compute() on abstract implementation ");
    String _packageName_3 = comp.getPackageName();
    _builder.append(_packageName_3, "    ");
    _builder.append(".");
    String _name_10 = comp.getName();
    _builder.append(_name_10, "    ");
    _builder.append("\");");
    _builder.newLineIfNotEmpty();
    _builder.append("}");
    _builder.newLine();
    _builder.newLine();
    _builder.append("}");
    _builder.newLine();
    return _builder.toString();
  }
  
  public String printGenerics(final ComponentSymbol comp) {
    ComponentHelper helper = new ComponentHelper(comp);
    StringConcatenation _builder = new StringConcatenation();
    {
      boolean _isGeneric = helper.isGeneric();
      if (_isGeneric) {
        _builder.append("<");
        _builder.newLine();
        {
          List<String> _genericParameters = helper.getGenericParameters();
          boolean _hasElements = false;
          for(final String generic : _genericParameters) {
            if (!_hasElements) {
              _hasElements = true;
            } else {
              _builder.appendImmediate(",", "  ");
            }
            _builder.append("  ");
            _builder.append(generic, "  ");
            _builder.newLineIfNotEmpty();
          }
        }
        _builder.append(">");
        _builder.newLine();
      }
    }
    return _builder.toString();
  }
  
  public String generateInput(final ComponentSymbol comp) {
    ComponentHelper helper = new ComponentHelper(comp);
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("package ");
    String _packageName = comp.getPackageName();
    _builder.append(_packageName);
    _builder.append(";");
    _builder.newLineIfNotEmpty();
    _builder.newLine();
    String _printImports = this.printImports(comp);
    _builder.append(_printImports);
    _builder.newLineIfNotEmpty();
    _builder.append("import de.montiarcautomaton.runtimes.timesync.implementation.IInput;");
    _builder.newLine();
    _builder.newLine();
    _builder.newLine();
    _builder.append("public class ");
    String _name = comp.getName();
    _builder.append(_name);
    _builder.append("Input");
    String _printGenerics = this.printGenerics(comp);
    _builder.append(_printGenerics);
    _builder.newLineIfNotEmpty();
    {
      boolean _isPresent = comp.getSuperComponent().isPresent();
      if (_isPresent) {
        _builder.append(" extends ");
        _builder.newLineIfNotEmpty();
        _builder.append("      ");
        String _fullName = comp.getSuperComponent().get().getFullName();
        _builder.append(_fullName, "      ");
        _builder.append("Input");
        _builder.newLineIfNotEmpty();
        _builder.append("      ");
        {
          boolean _isSuperComponentGeneric = helper.isSuperComponentGeneric();
          if (_isSuperComponentGeneric) {
            _builder.append("<");
            _builder.newLineIfNotEmpty();
            {
              List<String> _superCompActualTypeArguments = helper.getSuperCompActualTypeArguments();
              boolean _hasElements = false;
              for(final String scTypeParams : _superCompActualTypeArguments) {
                if (!_hasElements) {
                  _hasElements = true;
                } else {
                  _builder.appendImmediate(",", "      ");
                }
                _builder.append("      ");
                _builder.append(scTypeParams, "      ");
                _builder.newLineIfNotEmpty();
                _builder.append("      ");
              }
            }
            _builder.append(" > ");
          }
        }
        _builder.newLineIfNotEmpty();
      }
    }
    _builder.append("implements IInput ");
    _builder.newLine();
    _builder.append(" ");
    _builder.append("{");
    _builder.newLine();
    _builder.append("  ");
    _builder.newLine();
    {
      Collection<PortSymbol> _incomingPorts = comp.getIncomingPorts();
      for(final PortSymbol port : _incomingPorts) {
        _builder.append("  ");
        _builder.append("private ");
        String _realPortTypeString = helper.getRealPortTypeString(port);
        _builder.append(_realPortTypeString, "  ");
        _builder.append(" ");
        String _name_1 = port.getName();
        _builder.append(_name_1, "  ");
        _builder.append(";");
        _builder.newLineIfNotEmpty();
      }
    }
    _builder.append("  ");
    _builder.newLine();
    _builder.append("  ");
    _builder.append("public ");
    String _name_2 = comp.getName();
    _builder.append(_name_2, "  ");
    _builder.append("Input() {");
    _builder.newLineIfNotEmpty();
    {
      boolean _isPresent_1 = comp.getSuperComponent().isPresent();
      if (_isPresent_1) {
        _builder.append("    ");
        _builder.append("super();");
        _builder.newLine();
      }
    }
    _builder.append("  ");
    _builder.append("}");
    _builder.newLine();
    _builder.append("  ");
    _builder.newLine();
    {
      boolean _isEmpty = comp.getAllIncomingPorts().isEmpty();
      boolean _not = (!_isEmpty);
      if (_not) {
        _builder.append("  ");
        _builder.append("public ");
        String _name_3 = comp.getName();
        _builder.append(_name_3, "  ");
        _builder.append("Input(");
        {
          List<PortSymbol> _allIncomingPorts = comp.getAllIncomingPorts();
          boolean _hasElements_1 = false;
          for(final PortSymbol port_1 : _allIncomingPorts) {
            if (!_hasElements_1) {
              _hasElements_1 = true;
            } else {
              _builder.appendImmediate(",", "  ");
            }
            _builder.append(" ");
            String _realPortTypeString_1 = helper.getRealPortTypeString(port_1);
            _builder.append(_realPortTypeString_1, "  ");
            _builder.append(" ");
            String _name_4 = port_1.getName();
            _builder.append(_name_4, "  ");
            _builder.append(" ");
          }
        }
        _builder.append(") {");
        _builder.newLineIfNotEmpty();
        {
          boolean _isPresent_2 = comp.getSuperComponent().isPresent();
          if (_isPresent_2) {
            _builder.append("  ");
            _builder.append("  ");
            _builder.append("super(");
            {
              List<PortSymbol> _allIncomingPorts_1 = comp.getSuperComponent().get().getAllIncomingPorts();
              for(final PortSymbol port_2 : _allIncomingPorts_1) {
                _builder.append(" ");
                String _name_5 = port_2.getName();
                _builder.append(_name_5, "    ");
                _builder.append(" ");
              }
            }
            _builder.append(");");
            _builder.newLineIfNotEmpty();
          }
        }
        {
          Collection<PortSymbol> _incomingPorts_1 = comp.getIncomingPorts();
          for(final PortSymbol port_3 : _incomingPorts_1) {
            _builder.append("  ");
            _builder.append("  ");
            _builder.append("this.");
            String _name_6 = port_3.getName();
            _builder.append(_name_6, "    ");
            _builder.append(" = ");
            String _name_7 = port_3.getName();
            _builder.append(_name_7, "    ");
            _builder.append("; ");
            _builder.newLineIfNotEmpty();
          }
        }
        _builder.append("  ");
        _builder.append("  ");
        _builder.newLine();
        _builder.append("  ");
        _builder.append("}");
        _builder.newLine();
      }
    }
    _builder.append("  ");
    _builder.newLine();
    {
      Collection<PortSymbol> _incomingPorts_2 = comp.getIncomingPorts();
      for(final PortSymbol port_4 : _incomingPorts_2) {
        _builder.append("public ");
        String _realPortTypeString_2 = helper.getRealPortTypeString(port_4);
        _builder.append(_realPortTypeString_2);
        _builder.append(" get");
        String _firstUpper = StringExtensions.toFirstUpper(port_4.getName());
        _builder.append(_firstUpper);
        _builder.append("() {");
        _builder.newLineIfNotEmpty();
        _builder.append("  ");
        _builder.append("return this.");
        String _name_8 = port_4.getName();
        _builder.append(_name_8, "  ");
        _builder.append(";");
        _builder.newLineIfNotEmpty();
        _builder.append("}");
        _builder.newLine();
      }
    }
    _builder.newLine();
    _builder.append("@Override");
    _builder.newLine();
    _builder.append("public String toString() {");
    _builder.newLine();
    _builder.append("  ");
    _builder.append("String result = \"[\";");
    _builder.newLine();
    {
      Collection<PortSymbol> _incomingPorts_3 = comp.getIncomingPorts();
      for(final PortSymbol port_5 : _incomingPorts_3) {
        _builder.append("  ");
        _builder.append("result += \"");
        String _name_9 = port_5.getName();
        _builder.append(_name_9, "  ");
        _builder.append(": \" + this.");
        String _name_10 = port_5.getName();
        _builder.append(_name_10, "  ");
        _builder.append(" + \" \";");
        _builder.newLineIfNotEmpty();
      }
    }
    _builder.append("  ");
    _builder.append("return result + \"]\";");
    _builder.newLine();
    _builder.append("}  ");
    _builder.newLine();
    _builder.append("  ");
    _builder.newLine();
    _builder.append("} ");
    _builder.newLine();
    _builder.newLine();
    return _builder.toString();
  }
  
  public String generateResult(final ComponentSymbol comp) {
    ComponentHelper helper = new ComponentHelper(comp);
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("package ");
    String _packageName = comp.getPackageName();
    _builder.append(_packageName);
    _builder.append(";");
    _builder.newLineIfNotEmpty();
    _builder.newLine();
    String _printImports = this.printImports(comp);
    _builder.append(_printImports);
    _builder.newLineIfNotEmpty();
    _builder.append("import de.montiarcautomaton.runtimes.timesync.implementation.IResult;");
    _builder.newLine();
    _builder.newLine();
    _builder.newLine();
    _builder.append("public class ");
    String _name = comp.getName();
    _builder.append(_name);
    _builder.append("Result");
    String _printGenerics = this.printGenerics(comp);
    _builder.append(_printGenerics);
    _builder.append("   ");
    _builder.newLineIfNotEmpty();
    {
      boolean _isPresent = comp.getSuperComponent().isPresent();
      if (_isPresent) {
        _builder.append(" extends ");
        _builder.newLineIfNotEmpty();
        String _fullName = comp.getSuperComponent().get().getFullName();
        _builder.append(_fullName);
        _builder.append("Result ");
        {
          boolean _isSuperComponentGeneric = helper.isSuperComponentGeneric();
          if (_isSuperComponentGeneric) {
            _builder.append("< ");
            {
              List<String> _superCompActualTypeArguments = helper.getSuperCompActualTypeArguments();
              boolean _hasElements = false;
              for(final String scTypeParams : _superCompActualTypeArguments) {
                if (!_hasElements) {
                  _hasElements = true;
                } else {
                  _builder.appendImmediate(",", "");
                }
                _builder.newLineIfNotEmpty();
                _builder.append(scTypeParams);
              }
            }
            _builder.append(">");
          }
        }
        _builder.newLineIfNotEmpty();
      }
    }
    _builder.append("implements IResult ");
    _builder.newLine();
    _builder.append(" ");
    _builder.append("{");
    _builder.newLine();
    _builder.append("  ");
    _builder.newLine();
    {
      Collection<PortSymbol> _outgoingPorts = comp.getOutgoingPorts();
      for(final PortSymbol port : _outgoingPorts) {
        _builder.append("  ");
        _builder.append("private ");
        String _realPortTypeString = helper.getRealPortTypeString(port);
        _builder.append(_realPortTypeString, "  ");
        _builder.append(" ");
        String _name_1 = port.getName();
        _builder.append(_name_1, "  ");
        _builder.append(";");
        _builder.newLineIfNotEmpty();
      }
    }
    _builder.append("  ");
    _builder.newLine();
    _builder.append("  ");
    _builder.append("public ");
    String _name_2 = comp.getName();
    _builder.append(_name_2, "  ");
    _builder.append("Result() {");
    _builder.newLineIfNotEmpty();
    {
      boolean _isPresent_1 = comp.getSuperComponent().isPresent();
      if (_isPresent_1) {
        _builder.append("    ");
        _builder.append("super();");
        _builder.newLine();
      }
    }
    _builder.append("  ");
    _builder.append("}");
    _builder.newLine();
    _builder.append("  ");
    _builder.newLine();
    {
      boolean _isEmpty = comp.getAllOutgoingPorts().isEmpty();
      boolean _not = (!_isEmpty);
      if (_not) {
        _builder.append("  ");
        _builder.append("public ");
        String _name_3 = comp.getName();
        _builder.append(_name_3, "  ");
        _builder.append("Result(");
        {
          List<PortSymbol> _allOutgoingPorts = comp.getAllOutgoingPorts();
          boolean _hasElements_1 = false;
          for(final PortSymbol port_1 : _allOutgoingPorts) {
            if (!_hasElements_1) {
              _hasElements_1 = true;
            } else {
              _builder.appendImmediate(",", "  ");
            }
            _builder.append(" ");
            String _realPortTypeString_1 = helper.getRealPortTypeString(port_1);
            _builder.append(_realPortTypeString_1, "  ");
            _builder.append(" ");
            String _name_4 = port_1.getName();
            _builder.append(_name_4, "  ");
            _builder.append(" ");
          }
        }
        _builder.append(") {");
        _builder.newLineIfNotEmpty();
        {
          boolean _isPresent_2 = comp.getSuperComponent().isPresent();
          if (_isPresent_2) {
            _builder.append("  ");
            _builder.append("  ");
            _builder.append("super(");
            {
              List<PortSymbol> _allOutgoingPorts_1 = comp.getSuperComponent().get().getAllOutgoingPorts();
              for(final PortSymbol port_2 : _allOutgoingPorts_1) {
                _builder.append(" ");
                String _name_5 = port_2.getName();
                _builder.append(_name_5, "    ");
                _builder.append(" ");
              }
            }
            _builder.append(");");
            _builder.newLineIfNotEmpty();
          }
        }
        {
          Collection<PortSymbol> _outgoingPorts_1 = comp.getOutgoingPorts();
          for(final PortSymbol port_3 : _outgoingPorts_1) {
            _builder.append("  ");
            _builder.append("  ");
            _builder.append("this.");
            String _name_6 = port_3.getName();
            _builder.append(_name_6, "    ");
            _builder.append(" = ");
            String _name_7 = port_3.getName();
            _builder.append(_name_7, "    ");
            _builder.append("; ");
            _builder.newLineIfNotEmpty();
          }
        }
        _builder.append("  ");
        _builder.append("  ");
        _builder.newLine();
        _builder.append("  ");
        _builder.append("}");
        _builder.newLine();
      }
    }
    _builder.newLine();
    _builder.append("//getter  ");
    _builder.newLine();
    {
      Collection<PortSymbol> _outgoingPorts_2 = comp.getOutgoingPorts();
      for(final PortSymbol port_4 : _outgoingPorts_2) {
        _builder.append("public ");
        String _realPortTypeString_2 = helper.getRealPortTypeString(port_4);
        _builder.append(_realPortTypeString_2);
        _builder.append(" get");
        String _firstUpper = StringExtensions.toFirstUpper(port_4.getName());
        _builder.append(_firstUpper);
        _builder.append("() {");
        _builder.newLineIfNotEmpty();
        _builder.append("  ");
        _builder.append("return this.");
        String _name_8 = port_4.getName();
        _builder.append(_name_8, "  ");
        _builder.append(";");
        _builder.newLineIfNotEmpty();
        _builder.append("}");
        _builder.newLine();
      }
    }
    _builder.newLine();
    _builder.append("  ");
    _builder.append("// setter");
    _builder.newLine();
    {
      Collection<PortSymbol> _outgoingPorts_3 = comp.getOutgoingPorts();
      for(final PortSymbol port_5 : _outgoingPorts_3) {
        _builder.append("  ");
        _builder.append("public void set");
        String _firstUpper_1 = StringExtensions.toFirstUpper(port_5.getName());
        _builder.append(_firstUpper_1, "  ");
        _builder.append("(");
        String _realPortTypeString_3 = helper.getRealPortTypeString(port_5);
        _builder.append(_realPortTypeString_3, "  ");
        _builder.append(" ");
        String _name_9 = port_5.getName();
        _builder.append(_name_9, "  ");
        _builder.append(") {");
        _builder.newLineIfNotEmpty();
        _builder.append("  ");
        _builder.append("  ");
        _builder.append("this.");
        String _name_10 = port_5.getName();
        _builder.append(_name_10, "    ");
        _builder.append(" = ");
        String _name_11 = port_5.getName();
        _builder.append(_name_11, "    ");
        _builder.append(";");
        _builder.newLineIfNotEmpty();
        _builder.append("  ");
        _builder.append("}");
        _builder.newLine();
      }
    }
    _builder.newLine();
    _builder.append("@Override");
    _builder.newLine();
    _builder.append("public String toString() {");
    _builder.newLine();
    _builder.append("  ");
    _builder.append("String result = \"[\";");
    _builder.newLine();
    {
      Collection<PortSymbol> _outgoingPorts_4 = comp.getOutgoingPorts();
      for(final PortSymbol port_6 : _outgoingPorts_4) {
        _builder.append("  ");
        _builder.append("result += \"");
        String _name_12 = port_6.getName();
        _builder.append(_name_12, "  ");
        _builder.append(": \" + this.");
        String _name_13 = port_6.getName();
        _builder.append(_name_13, "  ");
        _builder.append(" + \" \";");
        _builder.newLineIfNotEmpty();
      }
    }
    _builder.append("  ");
    _builder.append("return result + \"]\";");
    _builder.newLine();
    _builder.append("}  ");
    _builder.newLine();
    _builder.append("  ");
    _builder.newLine();
    _builder.append("} ");
    _builder.newLine();
    _builder.newLine();
    return _builder.toString();
  }
  
  public String generateDeploy(final ComponentSymbol comp) {
    String name = comp.getName();
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("package ");
    String _packageName = comp.getPackageName();
    _builder.append(_packageName);
    _builder.newLineIfNotEmpty();
    _builder.newLine();
    _builder.append("public class Deploy");
    _builder.append(name);
    _builder.append(" {");
    _builder.newLineIfNotEmpty();
    _builder.append("  ");
    _builder.append("final static int CYCLE_TIME = 50; // in ms");
    _builder.newLine();
    _builder.append("    ");
    _builder.newLine();
    _builder.append("  ");
    _builder.append("public static void main(String[] args) {");
    _builder.newLine();
    _builder.append("    ");
    _builder.append("final ");
    _builder.append(name, "    ");
    _builder.append(" cmp = new ");
    _builder.append(name, "    ");
    _builder.append("();");
    _builder.newLineIfNotEmpty();
    _builder.append("    ");
    _builder.newLine();
    _builder.append("    ");
    _builder.append("cmp.setUp();");
    _builder.newLine();
    _builder.append("    ");
    _builder.append("cmp.init();");
    _builder.newLine();
    _builder.append("             ");
    _builder.newLine();
    _builder.append("    ");
    _builder.append("long time;");
    _builder.newLine();
    _builder.append("       ");
    _builder.newLine();
    _builder.append("    ");
    _builder.append("while (!Thread.interrupted()) {");
    _builder.newLine();
    _builder.append("      ");
    _builder.append("time = System.currentTimeMillis();");
    _builder.newLine();
    _builder.append("      ");
    _builder.append("cmp.compute();");
    _builder.newLine();
    _builder.append("      ");
    _builder.append("cmp.update();");
    _builder.newLine();
    _builder.append("      ");
    _builder.append("while((System.currentTimeMillis()-time) < CYCLE_TIME){");
    _builder.newLine();
    _builder.append("        ");
    _builder.append("Thread.yield();");
    _builder.newLine();
    _builder.append("      ");
    _builder.append("}");
    _builder.newLine();
    _builder.append("    ");
    _builder.append("}");
    _builder.newLine();
    _builder.append("  ");
    _builder.append("}");
    _builder.newLine();
    _builder.append("}");
    _builder.newLine();
    return _builder.toString();
  }
  
  public String generateAtomicComponent(final ComponentSymbol comp) {
    ComponentHelper helper = new ComponentHelper(comp);
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("package ");
    String _packageName = comp.getPackageName();
    _builder.append(_packageName);
    _builder.append(";");
    _builder.newLineIfNotEmpty();
    _builder.newLine();
    String _printImports = this.printImports(comp);
    _builder.append(_printImports);
    _builder.newLineIfNotEmpty();
    _builder.append("import ");
    String _packageName_1 = comp.getPackageName();
    _builder.append(_packageName_1);
    _builder.append(".");
    String _name = comp.getName();
    _builder.append(_name);
    _builder.append("Input;");
    _builder.newLineIfNotEmpty();
    _builder.append("import ");
    String _packageName_2 = comp.getPackageName();
    _builder.append(_packageName_2);
    _builder.append(".");
    String _name_1 = comp.getName();
    _builder.append(_name_1);
    _builder.append("Result;");
    _builder.newLineIfNotEmpty();
    _builder.append("import de.montiarcautomaton.runtimes.timesync.delegation.IComponent;");
    _builder.newLine();
    _builder.append("import de.montiarcautomaton.runtimes.timesync.delegation.Port;");
    _builder.newLine();
    _builder.append("import de.montiarcautomaton.runtimes.timesync.implementation.IComputable;");
    _builder.newLine();
    _builder.append("import de.montiarcautomaton.runtimes.Log;");
    _builder.newLine();
    _builder.newLine();
    _builder.append("public class ");
    String _name_2 = comp.getName();
    _builder.append(_name_2);
    String _printGenerics = this.printGenerics(comp);
    _builder.append(_printGenerics);
    _builder.append("      ");
    _builder.newLineIfNotEmpty();
    {
      boolean _isPresent = comp.getSuperComponent().isPresent();
      if (_isPresent) {
        _builder.append(" extends ");
        String _fullName = comp.getSuperComponent().get().getFullName();
        _builder.append(_fullName);
        _builder.append(" ");
        _builder.newLineIfNotEmpty();
        _builder.append("  ");
        {
          boolean _isSuperComponentGeneric = helper.isSuperComponentGeneric();
          if (_isSuperComponentGeneric) {
            _builder.append("<");
            {
              List<String> _superCompActualTypeArguments = helper.getSuperCompActualTypeArguments();
              boolean _hasElements = false;
              for(final String scTypeParams : _superCompActualTypeArguments) {
                if (!_hasElements) {
                  _hasElements = true;
                } else {
                  _builder.appendImmediate(",", "  ");
                }
                _builder.newLineIfNotEmpty();
                _builder.append("  ");
                _builder.append(scTypeParams, "  ");
                _builder.newLineIfNotEmpty();
                _builder.append("        ");
              }
            }
            _builder.append(">");
          }
        }
        _builder.newLineIfNotEmpty();
      }
    }
    _builder.append("implements IComponent {");
    _builder.newLine();
    _builder.append("  ");
    _builder.newLine();
    _builder.append("  ");
    _builder.append("// component variables");
    _builder.newLine();
    {
      Collection<VariableSymbol> _variables = comp.getVariables();
      for(final VariableSymbol v : _variables) {
        _builder.append("  ");
        String _printMember = this.printMember(helper.printVariableTypeName(v), v.getName(), "protected");
        _builder.append(_printMember, "  ");
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
        String _printMember_1 = this.printMember(helper.printParamTypeName(param), param.getName(), "private final");
        _builder.append(_printMember_1, "  ");
        _builder.newLineIfNotEmpty();
      }
    }
    _builder.append("  ");
    _builder.newLine();
    _builder.append("  ");
    _builder.append("// port fields");
    _builder.newLine();
    {
      Collection<PortSymbol> _ports = comp.getPorts();
      for(final PortSymbol port : _ports) {
        _builder.append("  ");
        String _printPortType = helper.printPortType(port);
        String _plus = ("Port<" + _printPortType);
        String _plus_1 = (_plus + ">");
        String _printMember_2 = this.printMember(_plus_1, port.getName(), "protected");
        _builder.append(_printMember_2, "  ");
        _builder.newLineIfNotEmpty();
      }
    }
    _builder.newLine();
    _builder.append("  ");
    _builder.append("// port setter");
    _builder.newLine();
    {
      Collection<PortSymbol> _ports_1 = comp.getPorts();
      for(final PortSymbol inPort : _ports_1) {
        _builder.append("  ");
        _builder.append("public void setPort");
        String _firstUpper = StringExtensions.toFirstUpper(inPort.getName());
        _builder.append(_firstUpper, "  ");
        _builder.append("(Port<");
        String _printPortType_1 = helper.printPortType(inPort);
        _builder.append(_printPortType_1, "  ");
        _builder.append("> port) {");
        _builder.newLineIfNotEmpty();
        _builder.append("  ");
        _builder.append("  ");
        _builder.append("this.");
        String _name_3 = inPort.getName();
        _builder.append(_name_3, "    ");
        _builder.append(" = port;");
        _builder.newLineIfNotEmpty();
        _builder.append("  ");
        _builder.append("}");
        _builder.newLine();
        _builder.append("      ");
        _builder.newLine();
        _builder.append("        ");
        _builder.append("// port getter");
        _builder.newLine();
        _builder.append("  ");
        _builder.append("public Port<");
        String _printPortType_2 = helper.printPortType(inPort);
        _builder.append(_printPortType_2, "  ");
        _builder.append("> getPort");
        String _firstUpper_1 = StringExtensions.toFirstUpper(inPort.getName());
        _builder.append(_firstUpper_1, "  ");
        _builder.append("() {");
        _builder.newLineIfNotEmpty();
        _builder.append("  ");
        _builder.append("  ");
        _builder.append("return this.");
        String _name_4 = inPort.getName();
        _builder.append(_name_4, "    ");
        _builder.append(";");
        _builder.newLineIfNotEmpty();
        _builder.append("  ");
        _builder.append("}");
        _builder.newLine();
      }
    }
    _builder.append("  ");
    _builder.newLine();
    _builder.append("  ");
    _builder.append("// the components behavior implementation");
    _builder.newLine();
    _builder.append("  ");
    _builder.append("private final IComputable");
    _builder.newLine();
    _builder.append("  ");
    _builder.append("<");
    String _name_5 = comp.getName();
    _builder.append(_name_5, "  ");
    _builder.append("Input");
    String _printGenerics_1 = this.printGenerics(comp);
    _builder.append(_printGenerics_1, "  ");
    _builder.append(", ");
    String _name_6 = comp.getName();
    _builder.append(_name_6, "  ");
    _builder.append("Result");
    String _printGenerics_2 = this.printGenerics(comp);
    _builder.append(_printGenerics_2, "  ");
    _builder.append(">");
    _builder.newLineIfNotEmpty();
    _builder.append("  ");
    String _behaviorImplName = helper.getBehaviorImplName();
    _builder.append(_behaviorImplName, "  ");
    _builder.append(";      ");
    _builder.newLineIfNotEmpty();
    _builder.newLine();
    _builder.append("  ");
    _builder.newLine();
    _builder.append("  ");
    _builder.append("public ");
    String _name_7 = comp.getName();
    _builder.append(_name_7, "  ");
    _builder.append("(");
    {
      List<JFieldSymbol> _configParameters_1 = comp.getConfigParameters();
      boolean _hasElements_1 = false;
      for(final JFieldSymbol param_1 : _configParameters_1) {
        if (!_hasElements_1) {
          _hasElements_1 = true;
        } else {
          _builder.appendImmediate(",", "  ");
        }
        _builder.append(" ");
        String _paramTypeName = helper.getParamTypeName(param_1);
        _builder.append(_paramTypeName, "  ");
        _builder.append(" ");
        String _name_8 = param_1.getName();
        _builder.append(_name_8, "  ");
      }
    }
    _builder.append(") {");
    _builder.newLineIfNotEmpty();
    {
      boolean _isPresent_1 = comp.getSuperComponent().isPresent();
      if (_isPresent_1) {
        _builder.append("    ");
        _builder.append("super(");
        {
          List<String> _inheritedParams = helper.getInheritedParams();
          boolean _hasElements_2 = false;
          for(final String inhParam : _inheritedParams) {
            if (!_hasElements_2) {
              _hasElements_2 = true;
            } else {
              _builder.appendImmediate(",", "    ");
            }
            _builder.append(" ");
            _builder.append(inhParam, "    ");
            _builder.append(" ");
          }
        }
        _builder.append(");");
        _builder.newLineIfNotEmpty();
      }
    }
    _builder.append("    ");
    String _behaviorImplName_1 = helper.getBehaviorImplName();
    _builder.append(_behaviorImplName_1, "    ");
    _builder.append(" = new ");
    String _name_9 = comp.getName();
    _builder.append(_name_9, "    ");
    _builder.append("Impl");
    String _printGenerics_3 = this.printGenerics(comp);
    _builder.append(_printGenerics_3, "    ");
    _builder.append("(");
    _builder.newLineIfNotEmpty();
    _builder.append("    ");
    {
      boolean _hasConfigParameters = comp.hasConfigParameters();
      if (_hasConfigParameters) {
        _builder.append(" ");
        {
          List<JFieldSymbol> _configParameters_2 = comp.getConfigParameters();
          boolean _hasElements_3 = false;
          for(final JFieldSymbol param_2 : _configParameters_2) {
            if (!_hasElements_3) {
              _hasElements_3 = true;
            } else {
              _builder.appendImmediate(",", "    ");
            }
            _builder.append(" ");
            String _name_10 = param_2.getName();
            _builder.append(_name_10, "    ");
            _builder.append(" ");
          }
        }
        _builder.append(" ");
      }
    }
    _builder.append(");");
    _builder.newLineIfNotEmpty();
    _builder.append("    ");
    _builder.newLine();
    _builder.append("    ");
    _builder.append("// config parameters");
    _builder.newLine();
    {
      List<JFieldSymbol> _configParameters_3 = comp.getConfigParameters();
      for(final JFieldSymbol param_3 : _configParameters_3) {
        _builder.append("this.");
        String _name_11 = param_3.getName();
        _builder.append(_name_11);
        _builder.append(" = ");
        String _name_12 = param_3.getName();
        _builder.append(_name_12);
        _builder.append(";");
        _builder.newLineIfNotEmpty();
      }
    }
    _builder.append("  ");
    _builder.append("}");
    _builder.newLine();
    _builder.append("  ");
    _builder.newLine();
    _builder.append("  ");
    _builder.append("@Override");
    _builder.newLine();
    _builder.append("  ");
    _builder.append("public void setUp() {");
    _builder.newLine();
    {
      boolean _isPresent_2 = comp.getSuperComponent().isPresent();
      if (_isPresent_2) {
        _builder.append("  ");
        _builder.append("super.setUp();");
        _builder.newLine();
      }
    }
    _builder.append("  ");
    _builder.append("// set up output ports");
    _builder.newLine();
    {
      Collection<PortSymbol> _outgoingPorts = comp.getOutgoingPorts();
      for(final PortSymbol portOut : _outgoingPorts) {
        _builder.append("  ");
        _builder.append("this.");
        String _name_13 = portOut.getName();
        _builder.append(_name_13, "  ");
        _builder.append(" = new Port<");
        String _printPortType_3 = helper.printPortType(portOut);
        _builder.append(_printPortType_3, "  ");
        _builder.append(">();");
        _builder.newLineIfNotEmpty();
      }
    }
    _builder.append("  ");
    _builder.newLine();
    _builder.append("  ");
    _builder.append("this.initialize();");
    _builder.newLine();
    _builder.append("  ");
    _builder.append("}");
    _builder.newLine();
    _builder.append("  ");
    _builder.newLine();
    _builder.append("  ");
    _builder.append("@Override");
    _builder.newLine();
    _builder.append("  ");
    _builder.append("public void init() {");
    _builder.newLine();
    {
      boolean _isPresent_3 = comp.getSuperComponent().isPresent();
      if (_isPresent_3) {
        _builder.append("  ");
        _builder.append("super.init();");
        _builder.newLine();
      }
    }
    _builder.append("  ");
    _builder.append("// set up unused input ports");
    _builder.newLine();
    {
      Collection<PortSymbol> _incomingPorts = comp.getIncomingPorts();
      for(final PortSymbol portIn : _incomingPorts) {
        _builder.append("  ");
        _builder.append("if (this.");
        String _name_14 = portIn.getName();
        _builder.append(_name_14, "  ");
        _builder.append(" == null) {");
        _builder.newLineIfNotEmpty();
        _builder.append("  ");
        _builder.append("  ");
        _builder.append("this.");
        String _name_15 = portIn.getName();
        _builder.append(_name_15, "    ");
        _builder.append(" = Port.EMPTY;");
        _builder.newLineIfNotEmpty();
        _builder.append("  ");
        _builder.append("}");
        _builder.newLine();
      }
    }
    _builder.append("  ");
    _builder.append("}");
    _builder.newLine();
    _builder.append("  ");
    _builder.newLine();
    _builder.append(" ");
    _builder.append("private void setResult(");
    String _name_16 = comp.getName();
    _builder.append(_name_16, " ");
    _builder.append("Result");
    String _printGenerics_4 = this.printGenerics(comp);
    _builder.append(_printGenerics_4, " ");
    _builder.append(" result) {");
    _builder.newLineIfNotEmpty();
    {
      Collection<PortSymbol> _outgoingPorts_1 = comp.getOutgoingPorts();
      for(final PortSymbol portOut_1 : _outgoingPorts_1) {
        _builder.append(" ");
        _builder.append("this.getPort");
        String _firstUpper_2 = StringExtensions.toFirstUpper(portOut_1.getName());
        _builder.append(_firstUpper_2, " ");
        _builder.append("().setNextValue(result.get");
        String _firstUpper_3 = StringExtensions.toFirstUpper(portOut_1.getName());
        _builder.append(_firstUpper_3, " ");
        _builder.append("());");
        _builder.newLineIfNotEmpty();
      }
    }
    _builder.append(" ");
    _builder.newLine();
    _builder.append(" ");
    _builder.append("}");
    _builder.newLine();
    _builder.newLine();
    _builder.append("  ");
    _builder.append("@Override");
    _builder.newLine();
    _builder.append("  ");
    _builder.append("public void compute() {");
    _builder.newLine();
    _builder.append("    ");
    _builder.append("// collect current input port values");
    _builder.newLine();
    _builder.append("  ");
    _builder.append("final ");
    String _name_17 = comp.getName();
    _builder.append(_name_17, "  ");
    _builder.append("Input");
    String _printGenerics_5 = this.printGenerics(comp);
    _builder.append(_printGenerics_5, "  ");
    _builder.append(" input = new ");
    String _name_18 = comp.getName();
    _builder.append(_name_18, "  ");
    _builder.append("Input");
    String _printGenerics_6 = this.printGenerics(comp);
    _builder.append(_printGenerics_6, "  ");
    _builder.newLineIfNotEmpty();
    _builder.append("  ");
    _builder.append("(");
    {
      List<PortSymbol> _allIncomingPorts = comp.getAllIncomingPorts();
      boolean _hasElements_4 = false;
      for(final PortSymbol inPort_1 : _allIncomingPorts) {
        if (!_hasElements_4) {
          _hasElements_4 = true;
        } else {
          _builder.appendImmediate(",", "  ");
        }
        _builder.append("this.getPort");
        String _firstUpper_4 = StringExtensions.toFirstUpper(inPort_1.getName());
        _builder.append(_firstUpper_4, "  ");
        _builder.append("().getCurrentValue()");
      }
    }
    _builder.append(");");
    _builder.newLineIfNotEmpty();
    _builder.append("  ");
    _builder.newLine();
    _builder.append("  ");
    _builder.append("try {");
    _builder.newLine();
    _builder.append("  ");
    _builder.append("// perform calculations");
    _builder.newLine();
    _builder.append("    ");
    _builder.append("final ");
    String _name_19 = comp.getName();
    _builder.append(_name_19, "    ");
    _builder.append("Result");
    String _printGenerics_7 = this.printGenerics(comp);
    _builder.append(_printGenerics_7, "    ");
    _builder.append(" result = ");
    String _behaviorImplName_2 = helper.getBehaviorImplName();
    _builder.append(_behaviorImplName_2, "    ");
    _builder.append(".compute(input); ");
    _builder.newLineIfNotEmpty();
    _builder.append("    ");
    _builder.newLine();
    _builder.append("    ");
    _builder.append("// set results to ports");
    _builder.newLine();
    _builder.append("    ");
    _builder.append("setResult(result);");
    _builder.newLine();
    _builder.append("    ");
    _builder.append("} catch (Exception e) {");
    _builder.newLine();
    _builder.append("  ");
    _builder.append("Log.error(\"");
    String _name_20 = comp.getName();
    _builder.append(_name_20, "  ");
    _builder.append("\", e);");
    _builder.newLineIfNotEmpty();
    _builder.append("    ");
    _builder.append("}");
    _builder.newLine();
    _builder.append("  ");
    _builder.append("}");
    _builder.newLine();
    _builder.newLine();
    _builder.append("  ");
    _builder.append("@Override");
    _builder.newLine();
    _builder.append("  ");
    _builder.append("public void update() {");
    _builder.newLine();
    _builder.append("    ");
    {
      boolean _isPresent_4 = comp.getSuperComponent().isPresent();
      if (_isPresent_4) {
        _builder.append("super.update();");
      }
    }
    _builder.newLineIfNotEmpty();
    _builder.newLine();
    _builder.append("    ");
    _builder.append("// update computed value for next computation cycle in all outgoing ports");
    _builder.newLine();
    {
      Collection<PortSymbol> _outgoingPorts_2 = comp.getOutgoingPorts();
      for(final PortSymbol portOut_2 : _outgoingPorts_2) {
        _builder.append("    ");
        _builder.append("this.");
        String _name_21 = portOut_2.getName();
        _builder.append(_name_21, "    ");
        _builder.append(".update();");
        _builder.newLineIfNotEmpty();
      }
    }
    _builder.append("  ");
    _builder.append("}");
    _builder.newLine();
    _builder.append("  ");
    _builder.newLine();
    _builder.append("  ");
    _builder.append("private void initialize() {");
    _builder.newLine();
    _builder.append("     ");
    _builder.append("// get initial values from behavior implementation");
    _builder.newLine();
    _builder.append("  ");
    _builder.append("final ");
    String _name_22 = comp.getName();
    _builder.append(_name_22, "  ");
    _builder.append("Result");
    String _printGenerics_8 = this.printGenerics(comp);
    _builder.append(_printGenerics_8, "  ");
    _builder.append(" result = ");
    String _behaviorImplName_3 = helper.getBehaviorImplName();
    _builder.append(_behaviorImplName_3, "  ");
    _builder.append(".getInitialValues();");
    _builder.newLineIfNotEmpty();
    _builder.append("  ");
    _builder.newLine();
    _builder.append("  ");
    _builder.append("// set results to ports");
    _builder.newLine();
    _builder.append("  ");
    _builder.append("setResult(result);");
    _builder.newLine();
    _builder.append("  ");
    _builder.append("}");
    _builder.newLine();
    _builder.append("  ");
    _builder.newLine();
    _builder.append("}");
    _builder.newLine();
    return _builder.toString();
  }
  
  public String generateComposedComponent(final ComponentSymbol comp) {
    ComponentHelper helper = new ComponentHelper(comp);
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("package ");
    String _packageName = comp.getPackageName();
    _builder.append(_packageName);
    _builder.append(";");
    _builder.newLineIfNotEmpty();
    _builder.newLine();
    String _printImports = this.printImports(comp);
    _builder.append(_printImports);
    _builder.newLineIfNotEmpty();
    _builder.newLine();
    _builder.append("import de.montiarcautomaton.runtimes.timesync.delegation.IComponent;");
    _builder.newLine();
    _builder.append("import de.montiarcautomaton.runtimes.timesync.delegation.Port;");
    _builder.newLine();
    _builder.newLine();
    _builder.append("public class ");
    String _name = comp.getName();
    _builder.append(_name);
    String _printGenerics = this.printGenerics(comp);
    _builder.append(_printGenerics);
    _builder.newLineIfNotEmpty();
    {
      boolean _isPresent = comp.getSuperComponent().isPresent();
      if (_isPresent) {
        _builder.append(" extends ");
        String _fullName = comp.getSuperComponent().get().getFullName();
        _builder.append(_fullName);
      }
    }
    _builder.newLineIfNotEmpty();
    _builder.append("implements IComponent {");
    _builder.newLine();
    _builder.append(" ");
    _builder.newLine();
    _builder.append("  ");
    _builder.append("// port fields");
    _builder.newLine();
    {
      Collection<PortSymbol> _ports = comp.getPorts();
      for(final PortSymbol port : _ports) {
        _builder.append("  ");
        _builder.append("protected Port<");
        String _printPortType = helper.printPortType(port);
        _builder.append(_printPortType, "  ");
        _builder.append("> ");
        String _name_1 = port.getName();
        _builder.append(_name_1, "  ");
        _builder.append(";");
        _builder.newLineIfNotEmpty();
        _builder.append("           ");
        _builder.append("// port setter");
        _builder.newLine();
        _builder.append("           ");
        _builder.append("public void setPort");
        String _firstUpper = StringExtensions.toFirstUpper(port.getName());
        _builder.append(_firstUpper, "           ");
        _builder.append("(Port<");
        String _printPortType_1 = helper.printPortType(port);
        _builder.append(_printPortType_1, "           ");
        _builder.append("> port) {");
        _builder.newLineIfNotEmpty();
        _builder.append("  ");
        _builder.append(" ");
        _builder.append("this.");
        String _name_2 = port.getName();
        _builder.append(_name_2, "   ");
        _builder.append(" = port;");
        _builder.newLineIfNotEmpty();
        _builder.append("           ");
        _builder.append("}");
        _builder.newLine();
        _builder.append("           ");
        _builder.newLine();
        _builder.append("           ");
        _builder.append("// port getter");
        _builder.newLine();
        _builder.append("           ");
        _builder.append("public Port<");
        String _printPortType_2 = helper.printPortType(port);
        _builder.append(_printPortType_2, "           ");
        _builder.append("> getPort");
        String _firstUpper_1 = StringExtensions.toFirstUpper(port.getName());
        _builder.append(_firstUpper_1, "           ");
        _builder.append("() {");
        _builder.newLineIfNotEmpty();
        _builder.append("  ");
        _builder.append(" ");
        _builder.append("return this.");
        String _name_3 = port.getName();
        _builder.append(_name_3, "   ");
        _builder.append(";");
        _builder.newLineIfNotEmpty();
        _builder.append("           ");
        _builder.append("}");
        _builder.newLine();
      }
    }
    _builder.newLine();
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
        String _name_4 = param.getName();
        _builder.append(_name_4, "  ");
        _builder.append(";");
        _builder.newLineIfNotEmpty();
      }
    }
    _builder.append("  ");
    _builder.newLine();
    _builder.append("  ");
    _builder.append("// subcomponents");
    _builder.newLine();
    {
      Collection<ComponentInstanceSymbol> _subComponents = comp.getSubComponents();
      for(final ComponentInstanceSymbol subcomp : _subComponents) {
        _builder.append("  ");
        _builder.append("private ");
        String _subComponentTypeName = helper.getSubComponentTypeName(subcomp);
        _builder.append(_subComponentTypeName, "  ");
        _builder.append(" ");
        String _name_5 = subcomp.getName();
        _builder.append(_name_5, "  ");
        _builder.append(";  ");
        _builder.newLineIfNotEmpty();
      }
    }
    _builder.newLine();
    _builder.append("  ");
    _builder.append("// subcomponent getter");
    _builder.newLine();
    {
      Collection<ComponentInstanceSymbol> _subComponents_1 = comp.getSubComponents();
      for(final ComponentInstanceSymbol subcomp_1 : _subComponents_1) {
        _builder.append("  ");
        _builder.append("public ");
        String _subComponentTypeName_1 = helper.getSubComponentTypeName(subcomp_1);
        _builder.append(_subComponentTypeName_1, "  ");
        _builder.append(" getComponent");
        String _firstUpper_2 = StringExtensions.toFirstUpper(subcomp_1.getName());
        _builder.append(_firstUpper_2, "  ");
        _builder.append("() {");
        _builder.newLineIfNotEmpty();
        _builder.append("  ");
        _builder.append("  ");
        _builder.append("return this.");
        String _name_6 = subcomp_1.getName();
        _builder.append(_name_6, "    ");
        _builder.append(";");
        _builder.newLineIfNotEmpty();
        _builder.append("  ");
        _builder.append("}");
        _builder.newLine();
      }
    }
    _builder.append("  ");
    _builder.newLine();
    _builder.append("  ");
    _builder.append("public ");
    String _name_7 = comp.getName();
    _builder.append(_name_7, "  ");
    _builder.append("(");
    {
      List<JFieldSymbol> _configParameters_1 = comp.getConfigParameters();
      boolean _hasElements = false;
      for(final JFieldSymbol param_1 : _configParameters_1) {
        if (!_hasElements) {
          _hasElements = true;
        } else {
          _builder.appendImmediate(",", "  ");
        }
        String _printParamTypeName_1 = helper.printParamTypeName(param_1);
        _builder.append(_printParamTypeName_1, "  ");
        _builder.append(" ");
        String _name_8 = param_1.getName();
        _builder.append(_name_8, "  ");
      }
    }
    _builder.append(") {");
    _builder.newLineIfNotEmpty();
    {
      boolean _isPresent_1 = comp.getSuperComponent().isPresent();
      if (_isPresent_1) {
        _builder.append("    ");
        _builder.append("super();");
        _builder.newLine();
      }
    }
    {
      List<JFieldSymbol> _configParameters_2 = comp.getConfigParameters();
      for(final JFieldSymbol param_2 : _configParameters_2) {
        _builder.append("    ");
        _builder.append("this.");
        String _name_9 = param_2.getName();
        _builder.append(_name_9, "    ");
        _builder.append(" = ");
        String _name_10 = param_2.getName();
        _builder.append(_name_10, "    ");
        _builder.append(";");
        _builder.newLineIfNotEmpty();
      }
    }
    _builder.append("  ");
    _builder.append("}");
    _builder.newLine();
    _builder.newLine();
    _builder.append("  ");
    String _printInit = this.printInit(comp);
    _builder.append(_printInit, "  ");
    _builder.newLineIfNotEmpty();
    _builder.append("  ");
    String _printSetup = this.printSetup(comp);
    _builder.append(_printSetup, "  ");
    _builder.newLineIfNotEmpty();
    _builder.append("  ");
    String _printUpdate = this.printUpdate(comp);
    _builder.append(_printUpdate, "  ");
    _builder.newLineIfNotEmpty();
    _builder.append("  ");
    _builder.newLine();
    _builder.newLine();
    _builder.append("  ");
    _builder.append("@Override");
    _builder.newLine();
    _builder.append("  ");
    _builder.append("public void compute() {");
    _builder.newLine();
    _builder.append("    ");
    _builder.append("// trigger computation in all subcomponent instances");
    _builder.newLine();
    {
      Collection<ComponentInstanceSymbol> _subComponents_2 = comp.getSubComponents();
      for(final ComponentInstanceSymbol subcomponent : _subComponents_2) {
        _builder.append("    ");
        _builder.append("this.");
        String _name_11 = subcomponent.getName();
        _builder.append(_name_11, "    ");
        _builder.append(".compute();");
        _builder.newLineIfNotEmpty();
      }
    }
    _builder.append("  ");
    _builder.append("}");
    _builder.newLine();
    _builder.newLine();
    _builder.append("}");
    _builder.newLine();
    _builder.append("  ");
    _builder.newLine();
    return _builder.toString();
  }
  
  public String printMember(final String type, final String name, final String visibility) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append(visibility);
    _builder.append(" ");
    _builder.append(type);
    _builder.append(" ");
    _builder.append(name);
    _builder.append(";");
    _builder.newLineIfNotEmpty();
    return _builder.toString();
  }
  
  public String printGetter(final String type, final String name, final String methodPostfix) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("public ");
    _builder.append(type);
    _builder.append(" get");
    _builder.append(methodPostfix);
    _builder.append("() {");
    _builder.newLineIfNotEmpty();
    _builder.append("  ");
    _builder.append("return this.");
    _builder.append(name, "  ");
    _builder.append(";");
    _builder.newLineIfNotEmpty();
    _builder.append("}");
    _builder.newLine();
    return _builder.toString();
  }
  
  public String printSetter(final String type, final String name, final String methodPostfix) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("public void set");
    _builder.append(methodPostfix);
    _builder.append("(");
    _builder.append(type);
    _builder.append(" ");
    _builder.append(name);
    _builder.append(") {");
    _builder.newLineIfNotEmpty();
    _builder.append("  ");
    _builder.append("this.");
    _builder.append(name, "  ");
    _builder.append(" = ");
    _builder.append(name, "  ");
    _builder.append(";");
    _builder.newLineIfNotEmpty();
    _builder.append("}");
    _builder.newLine();
    return _builder.toString();
  }
  
  public String printInit(final ComponentSymbol comp) {
    ComponentHelper helper = new ComponentHelper(comp);
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("@Override");
    _builder.newLine();
    _builder.append("  ");
    _builder.append("public void init() {");
    _builder.newLine();
    {
      boolean _isPresent = comp.getSuperComponent().isPresent();
      if (_isPresent) {
        _builder.append("  ");
        _builder.append("super.init();");
        _builder.newLine();
      }
    }
    _builder.append("  ");
    _builder.append("// set up unused input ports");
    _builder.newLine();
    {
      Collection<PortSymbol> _incomingPorts = comp.getIncomingPorts();
      for(final PortSymbol portIn : _incomingPorts) {
        _builder.append("  ");
        _builder.append("if (this.");
        String _name = portIn.getName();
        _builder.append(_name, "  ");
        _builder.append(" == null) {");
        _builder.newLineIfNotEmpty();
        _builder.append("  ");
        _builder.append("  ");
        _builder.append("this.");
        String _name_1 = portIn.getName();
        _builder.append(_name_1, "    ");
        _builder.append(" = Port.EMPTY;");
        _builder.newLineIfNotEmpty();
        _builder.append("  ");
        _builder.append("}");
        _builder.newLine();
      }
    }
    _builder.append("  ");
    _builder.newLine();
    _builder.append("  ");
    _builder.append("// connect outputs of children with inputs of children, by giving ");
    _builder.newLine();
    _builder.append("  ");
    _builder.append("// the inputs a reference to the sending ports");
    _builder.newLine();
    {
      Collection<ConnectorSymbol> _connectors = comp.getConnectors();
      for(final ConnectorSymbol connector : _connectors) {
        {
          boolean _isIncomingPort = helper.isIncomingPort(comp, connector, false);
          if (_isIncomingPort) {
            _builder.append("  ");
            String _connectorComponentName = helper.getConnectorComponentName(connector, false);
            _builder.append(_connectorComponentName, "  ");
            _builder.append(".setPort");
            String _firstUpper = StringExtensions.toFirstUpper(helper.getConnectorPortName(connector, false));
            _builder.append(_firstUpper, "  ");
            _builder.append("(");
            String _connectorComponentName_1 = helper.getConnectorComponentName(connector, true);
            _builder.append(_connectorComponentName_1, "  ");
            _builder.append(".getPort");
            String _firstUpper_1 = StringExtensions.toFirstUpper(helper.getConnectorPortName(connector, true));
            _builder.append(_firstUpper_1, "  ");
            _builder.append("());");
            _builder.newLineIfNotEmpty();
          }
        }
      }
    }
    _builder.append("  ");
    _builder.newLine();
    _builder.append("  ");
    _builder.append("// init all subcomponents");
    _builder.newLine();
    _builder.append("  ");
    _builder.newLine();
    {
      Collection<ComponentInstanceSymbol> _subComponents = comp.getSubComponents();
      for(final ComponentInstanceSymbol subcomponent : _subComponents) {
        _builder.append("  ");
        _builder.append("this.");
        String _name_2 = subcomponent.getName();
        _builder.append(_name_2, "  ");
        _builder.append(".init();");
        _builder.newLineIfNotEmpty();
      }
    }
    _builder.append("  ");
    _builder.append("}");
    _builder.newLine();
    return _builder.toString();
  }
  
  public String printUpdate(final ComponentSymbol comp) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("@Override");
    _builder.newLine();
    _builder.append("  ");
    _builder.append("public void update() {");
    _builder.newLine();
    _builder.append("    ");
    _builder.append("// update subcomponent instances");
    _builder.newLine();
    {
      boolean _isPresent = comp.getSuperComponent().isPresent();
      if (_isPresent) {
        _builder.append("    ");
        _builder.append("super.update();");
        _builder.newLine();
      }
    }
    {
      Collection<ComponentInstanceSymbol> _subComponents = comp.getSubComponents();
      for(final ComponentInstanceSymbol subcomponent : _subComponents) {
        _builder.append("    ");
        _builder.append("this.");
        String _name = subcomponent.getName();
        _builder.append(_name, "    ");
        _builder.append(".update();");
        _builder.newLineIfNotEmpty();
      }
    }
    _builder.append("  ");
    _builder.append("}");
    _builder.newLine();
    return _builder.toString();
  }
  
  public String printSetup(final ComponentSymbol comp) {
    ComponentHelper helper = new ComponentHelper(comp);
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("@Override");
    _builder.newLine();
    _builder.append("  ");
    _builder.append("public void setUp() {");
    _builder.newLine();
    {
      boolean _isPresent = comp.getSuperComponent().isPresent();
      if (_isPresent) {
        _builder.append("  ");
        _builder.append("super.setUp();");
        _builder.newLine();
      }
    }
    _builder.append("  ");
    _builder.append("// instantiate all subcomponents");
    _builder.newLine();
    {
      Collection<ComponentInstanceSymbol> _subComponents = comp.getSubComponents();
      for(final ComponentInstanceSymbol subcomponent : _subComponents) {
        _builder.append("  ");
        _builder.append("this.");
        String _name = subcomponent.getName();
        _builder.append(_name, "  ");
        _builder.append(" = new ");
        String _subComponentTypeName = helper.getSubComponentTypeName(subcomponent);
        _builder.append(_subComponentTypeName, "  ");
        _builder.append("(");
        _builder.newLineIfNotEmpty();
        {
          Collection<String> _paramValues = helper.getParamValues(subcomponent);
          boolean _hasElements = false;
          for(final String param : _paramValues) {
            if (!_hasElements) {
              _hasElements = true;
            } else {
              _builder.appendImmediate(",", "  ");
            }
            _builder.append("  ");
            _builder.append(param, "  ");
            _builder.newLineIfNotEmpty();
            _builder.append("        ");
          }
        }
        _builder.append(");");
        _builder.newLineIfNotEmpty();
        _builder.append("  ");
        _builder.newLine();
      }
    }
    _builder.newLine();
    _builder.append("    ");
    _builder.append("//set up all sub components  ");
    _builder.newLine();
    {
      Collection<ComponentInstanceSymbol> _subComponents_1 = comp.getSubComponents();
      for(final ComponentInstanceSymbol subcomponent_1 : _subComponents_1) {
        _builder.append("this.");
        String _name_1 = subcomponent_1.getName();
        _builder.append(_name_1);
        _builder.append(".setUp();");
        _builder.newLineIfNotEmpty();
      }
    }
    _builder.append("    ");
    _builder.newLine();
    _builder.append("    ");
    _builder.append("// set up output ports");
    _builder.newLine();
    {
      Collection<PortSymbol> _outgoingPorts = comp.getOutgoingPorts();
      for(final PortSymbol portOut : _outgoingPorts) {
        _builder.append("this.");
        String _name_2 = portOut.getName();
        _builder.append(_name_2);
        _builder.append(" = new Port<");
        String _printPortType = helper.printPortType(portOut);
        _builder.append(_printPortType);
        _builder.append(">();");
        _builder.newLineIfNotEmpty();
      }
    }
    _builder.append("    ");
    _builder.newLine();
    _builder.append("    ");
    _builder.append("// propagate children\'s output ports to own output ports");
    _builder.newLine();
    {
      Collection<ConnectorSymbol> _connectors = comp.getConnectors();
      for(final ConnectorSymbol connector : _connectors) {
        {
          boolean _isIncomingPort = helper.isIncomingPort(comp, connector, false);
          boolean _not = (!_isIncomingPort);
          if (_not) {
            String _connectorComponentName = helper.getConnectorComponentName(connector, false);
            _builder.append(_connectorComponentName);
            _builder.append(".setPort");
            String _firstUpper = StringExtensions.toFirstUpper(helper.getConnectorPortName(connector, false));
            _builder.append(_firstUpper);
            _builder.append("(");
            String _connectorComponentName_1 = helper.getConnectorComponentName(connector, true);
            _builder.append(_connectorComponentName_1);
            _builder.append(".getPort");
            String _firstUpper_1 = StringExtensions.toFirstUpper(helper.getConnectorPortName(connector, true));
            _builder.append(_firstUpper_1);
            _builder.append("());");
            _builder.newLineIfNotEmpty();
          }
        }
      }
    }
    _builder.append("    ");
    _builder.newLine();
    _builder.append("  ");
    _builder.append("}");
    _builder.newLine();
    return _builder.toString();
  }
  
  public String printImports(final ComponentSymbol comp) {
    StringConcatenation _builder = new StringConcatenation();
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
    return _builder.toString();
  }
  
  public String generateBehavior(final ASTCNode automaton, final ComponentSymbol comp) {
    if (automaton instanceof ASTAutomatonBehavior) {
      return _generateBehavior((ASTAutomatonBehavior)automaton, comp);
    } else if (automaton instanceof ASTJavaPBehavior) {
      return _generateBehavior((ASTJavaPBehavior)automaton, comp);
    } else {
      throw new IllegalArgumentException("Unhandled parameter types: " +
        Arrays.<Object>asList(automaton, comp).toString());
    }
  }
}
