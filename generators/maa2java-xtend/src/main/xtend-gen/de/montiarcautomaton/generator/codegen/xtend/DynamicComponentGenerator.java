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
import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.xbase.lib.Exceptions;
import org.eclipse.xtext.xbase.lib.InputOutput;
import org.eclipse.xtext.xbase.lib.StringExtensions;

@SuppressWarnings("all")
public class DynamicComponentGenerator {
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
      String _name_3 = comp.getName();
      String _plus_4 = ("Dynamic" + _name_3);
      this.toFile(targetPath, _plus_4, this.generateAtomicComponent(comp));
      if ((!existsHWCClass)) {
        String _name_4 = comp.getName();
        String _plus_5 = (_name_4 + "Impl");
        this.toFile(targetPath, _plus_5, this.generateBehaviorImplementation(comp));
      }
    } else {
      String _name_5 = comp.getName();
      String _plus_6 = ("Dynamic" + _name_5);
      this.toFile(targetPath, _plus_6, this.generateComposedComponent(comp));
    }
    boolean _containsKey = comp.getStereotype().containsKey("deploy");
    if (_containsKey) {
      String _name_6 = comp.getName();
      String _plus_7 = ("DynamicDeploy" + _name_6);
      this.toFile(targetPath, _plus_7, this.generateDeploy(comp));
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
    throw new Error("Unresolved compilation problems:"
      + "\nThe method getParamTypeName(JFieldSymbol) is undefined for the type ComponentHelper");
  }
  
  public String printGenerics(final ComponentSymbol comp) {
    throw new Error("Unresolved compilation problems:"
      + "\nThe method or field isGeneric is undefined for the type ComponentHelper"
      + "\nThe method or field genericParameters is undefined for the type ComponentHelper");
  }
  
  public String generateInput(final ComponentSymbol comp) {
    throw new Error("Unresolved compilation problems:"
      + "\nThe method or field isSuperComponentGeneric is undefined for the type ComponentHelper");
  }
  
  public String generateResult(final ComponentSymbol comp) {
    throw new Error("Unresolved compilation problems:"
      + "\nThe method or field isSuperComponentGeneric is undefined for the type ComponentHelper");
  }
  
  public String generateDeploy(final ComponentSymbol comp) {
    String name = comp.getName();
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("package ");
    String _packageName = comp.getPackageName();
    _builder.append(_packageName);
    _builder.newLineIfNotEmpty();
    _builder.newLine();
    _builder.append("import de.montiarcautomaton.runtimes.timesync.delegation.LoaderManager;");
    _builder.newLine();
    _builder.append("import de.montiarcautomaton.runtimes.timesync.delegation.Port;");
    _builder.newLine();
    _builder.newLine();
    _builder.newLine();
    _builder.append("public class DynamicDeploy");
    _builder.append(name);
    _builder.append(" {");
    _builder.newLineIfNotEmpty();
    _builder.append("  ");
    _builder.append("final static int CYCLE_TIME = 50; // in ms");
    _builder.newLine();
    _builder.append("  ");
    _builder.append("final static String STORE_DIR = \"\";");
    _builder.newLine();
    _builder.append("  ");
    _builder.append("final static String CLASS_DIR = \"\";");
    _builder.newLine();
    _builder.append("    ");
    _builder.newLine();
    _builder.append("  ");
    _builder.append("public static void main(String[] args) {");
    _builder.newLine();
    _builder.append("    ");
    _builder.append("final LoaderManager loman = new LoaderManager();");
    _builder.newLine();
    _builder.append("    ");
    _builder.append("final Dynamic");
    _builder.append(name, "    ");
    _builder.append(" cmp = new Dynamic");
    _builder.append(name, "    ");
    _builder.append("();");
    _builder.newLineIfNotEmpty();
    _builder.append("    ");
    _builder.newLine();
    _builder.append("    ");
    _builder.append("cmp.setUp();");
    _builder.newLine();
    _builder.append("    ");
    _builder.append("cmp.setLoaderConfiguration(");
    _builder.append(name, "    ");
    _builder.append(", STORE_DIR, CLASS_DIR, loman)");
    _builder.newLineIfNotEmpty();
    _builder.append("    ");
    _builder.append("cmp.init();");
    _builder.newLine();
    _builder.append("             ");
    _builder.newLine();
    _builder.append("    ");
    _builder.append("long time;");
    _builder.newLine();
    _builder.append("    ");
    _builder.append("List<Port> changedPorts;");
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
    _builder.append("changedPorts = cmp.reconfigure();");
    _builder.newLine();
    _builder.append("      ");
    _builder.append("cmp.propagatePortChanges(changedPorts);");
    _builder.newLine();
    _builder.append("      ");
    _builder.append("cmp.checkForCmp();");
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
    throw new Error("Unresolved compilation problems:"
      + "\nThe method or field isSuperComponentGeneric is undefined for the type ComponentHelper"
      + "\nThe method printVariableTypeName(VariableSymbol) is undefined for the type ComponentHelper"
      + "\nThe method printParamTypeName(JFieldSymbol) is undefined for the type ComponentHelper"
      + "\nThe method printPortType(PortSymbol) is undefined for the type ComponentHelper"
      + "\nThe method printPortType(PortSymbol) is undefined for the type ComponentHelper"
      + "\nThe method printPortType(PortSymbol) is undefined for the type ComponentHelper"
      + "\nThe method getParamTypeName(JFieldSymbol) is undefined for the type ComponentHelper"
      + "\nThe method getInheritedParams() is undefined for the type ComponentHelper"
      + "\nThe method printPortType(PortSymbol) is undefined for the type ComponentHelper"
      + "\nThe method printPortType(PortSymbol) is undefined for the type ComponentHelper");
  }
  
  public String generateComposedComponent(final ComponentSymbol comp) {
    throw new Error("Unresolved compilation problems:"
      + "\nThe method printPortType(PortSymbol) is undefined for the type ComponentHelper"
      + "\nThe method printPortType(PortSymbol) is undefined for the type ComponentHelper"
      + "\nThe method printPortType(PortSymbol) is undefined for the type ComponentHelper"
      + "\nThe method printPortType(PortSymbol) is undefined for the type ComponentHelper"
      + "\nThe method printParamTypeName(JFieldSymbol) is undefined for the type ComponentHelper"
      + "\nThe method printParamTypeName(JFieldSymbol) is undefined for the type ComponentHelper");
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
    throw new Error("Unresolved compilation problems:"
      + "\nThe method printPortType(PortSymbol) is undefined for the type ComponentHelper");
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
