/**
 * Copyright (c) 2012 itemis AG (http://www.itemis.eu) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.montiarcautomaton.generator.codegen.xtend;

import de.montiarcautomaton.generator.codegen.xtend.util.Identifier;
import de.montiarcautomaton.generator.codegen.xtend.util.Init;
import de.montiarcautomaton.generator.codegen.xtend.util.Ports;
import de.montiarcautomaton.generator.codegen.xtend.util.Setup;
import de.montiarcautomaton.generator.codegen.xtend.util.Subcomponents;
import de.montiarcautomaton.generator.codegen.xtend.util.Update;
import de.montiarcautomaton.generator.codegen.xtend.util.Utils;
import de.montiarcautomaton.generator.helper.ComponentHelper;
import de.monticore.symboltable.types.JFieldSymbol;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import montiarc._symboltable.ComponentInstanceSymbol;
import montiarc._symboltable.ComponentSymbol;
import montiarc._symboltable.ComponentSymbolReference;
import montiarc._symboltable.PortSymbol;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.xbase.lib.StringExtensions;

/**
 * Generates the component class for atomic and composed components.
 * 
 * @author  Pfeiffer
 * @version $Revision$,
 *          $Date$
 */
@SuppressWarnings("all")
public class ComponentGenerator {
  public String generics;
  
  public ComponentHelper helper;
  
  public String generate(final ComponentSymbol comp) {
    this.generics = Utils.printFormalTypeParameters(comp);
    ComponentHelper _componentHelper = new ComponentHelper(comp);
    this.helper = _componentHelper;
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("package ");
    String _packageName = comp.getPackageName();
    _builder.append(_packageName);
    _builder.append(";");
    _builder.newLineIfNotEmpty();
    _builder.newLine();
    _builder.newLine();
    String _printImports = Utils.printImports(comp);
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
    _builder.append(this.generics);
    _builder.append("      ");
    _builder.newLineIfNotEmpty();
    _builder.append("  ");
    {
      boolean _isPresent = comp.getSuperComponent().isPresent();
      if (_isPresent) {
        _builder.append(" extends ");
        String _fullName = comp.getSuperComponent().get().getFullName();
        _builder.append(_fullName, "  ");
        _builder.append(" ");
        _builder.newLineIfNotEmpty();
        _builder.append("  ");
        _builder.append("    ");
        {
          boolean _hasFormalTypeParameters = comp.getSuperComponent().get().hasFormalTypeParameters();
          if (_hasFormalTypeParameters) {
            _builder.append("<");
            {
              List<String> _superCompActualTypeArguments = this.helper.getSuperCompActualTypeArguments();
              boolean _hasElements = false;
              for(final String scTypeParams : _superCompActualTypeArguments) {
                if (!_hasElements) {
                  _hasElements = true;
                } else {
                  _builder.appendImmediate(",", "      ");
                }
                _builder.newLineIfNotEmpty();
                _builder.append("  ");
                _builder.append("    ");
                _builder.append(scTypeParams, "      ");
              }
            }
            _builder.append(">");
            _builder.newLineIfNotEmpty();
          }
        }
      }
    }
    _builder.append("  ");
    _builder.append("implements IComponent {");
    _builder.newLine();
    _builder.append("    ");
    _builder.newLine();
    _builder.append("  ");
    _builder.append("//ports");
    _builder.newLine();
    _builder.append("  ");
    String _print = Ports.print(comp.getPorts());
    _builder.append(_print, "  ");
    _builder.newLineIfNotEmpty();
    _builder.append("  ");
    _builder.newLine();
    _builder.append("  ");
    _builder.append("// component variables");
    _builder.newLine();
    _builder.append("  ");
    String _printVariables = Utils.printVariables(comp);
    _builder.append(_printVariables, "  ");
    _builder.newLineIfNotEmpty();
    _builder.append("  ");
    _builder.newLine();
    _builder.append("  ");
    _builder.append("// config parameters");
    _builder.newLine();
    _builder.append("  ");
    String _printConfigParameters = Utils.printConfigParameters(comp);
    _builder.append(_printConfigParameters, "  ");
    _builder.newLineIfNotEmpty();
    _builder.newLine();
    {
      boolean _isDecomposed = comp.isDecomposed();
      if (_isDecomposed) {
        _builder.append("  ");
        _builder.append("// subcomponents");
        _builder.newLine();
        _builder.append("  ");
        String _print_1 = Subcomponents.print(comp);
        _builder.append(_print_1, "  ");
        _builder.newLineIfNotEmpty();
        _builder.append("  ");
        String _printComputeComposed = this.printComputeComposed(comp);
        _builder.append(_printComputeComposed, "  ");
        _builder.newLineIfNotEmpty();
        _builder.append("  ");
        _builder.newLine();
      } else {
        _builder.append("  ");
        _builder.append("// the components behavior implementation");
        _builder.newLine();
        _builder.append("  ");
        _builder.append("private final IComputable<");
        String _name_3 = comp.getName();
        _builder.append(_name_3, "  ");
        _builder.append("Input");
        _builder.append(this.generics, "  ");
        _builder.append(", ");
        String _name_4 = comp.getName();
        _builder.append(_name_4, "  ");
        _builder.append("Result");
        _builder.append(this.generics, "  ");
        _builder.append("> ");
        String _behaviorImplName = Identifier.getBehaviorImplName();
        _builder.append(_behaviorImplName, "  ");
        _builder.append(";");
        _builder.newLineIfNotEmpty();
        _builder.append("  ");
        _builder.newLine();
        _builder.append("  ");
        String _printComputeAtomic = this.printComputeAtomic(comp);
        _builder.append(_printComputeAtomic, "  ");
        _builder.newLineIfNotEmpty();
        _builder.append("  ");
        _builder.append("private void initialize() {");
        _builder.newLine();
        _builder.append("  ");
        _builder.append("  ");
        _builder.append("// get initial values from behavior implementation");
        _builder.newLine();
        _builder.append("  ");
        _builder.append("  ");
        _builder.append("final ");
        String _name_5 = comp.getName();
        _builder.append(_name_5, "    ");
        _builder.append("Result");
        _builder.append(this.generics, "    ");
        _builder.append(" result = ");
        String _behaviorImplName_1 = Identifier.getBehaviorImplName();
        _builder.append(_behaviorImplName_1, "    ");
        _builder.append(".getInitialValues();");
        _builder.newLineIfNotEmpty();
        _builder.append("  ");
        _builder.append("  ");
        _builder.newLine();
        _builder.append("  ");
        _builder.append("  ");
        _builder.append("// set results to ports");
        _builder.newLine();
        _builder.append("  ");
        _builder.append("  ");
        _builder.append("setResult(result);");
        _builder.newLine();
        _builder.append("  ");
        _builder.append("  ");
        _builder.append("this.update();");
        _builder.newLine();
        _builder.append("  ");
        _builder.append("}");
        _builder.newLine();
        _builder.append("  ");
        _builder.append("private void setResult(");
        String _name_6 = comp.getName();
        _builder.append(_name_6, "  ");
        _builder.append("Result");
        _builder.append(this.generics, "  ");
        _builder.append(" result) {");
        _builder.newLineIfNotEmpty();
        {
          Collection<PortSymbol> _outgoingPorts = comp.getOutgoingPorts();
          for(final PortSymbol portOut : _outgoingPorts) {
            _builder.append("  ");
            _builder.append("  ");
            _builder.append("this.getPort");
            String _firstUpper = StringExtensions.toFirstUpper(portOut.getName());
            _builder.append(_firstUpper, "    ");
            _builder.append("().setNextValue(result.get");
            String _firstUpper_1 = StringExtensions.toFirstUpper(portOut.getName());
            _builder.append(_firstUpper_1, "    ");
            _builder.append("());");
            _builder.newLineIfNotEmpty();
          }
        }
        _builder.append("  ");
        _builder.append("}");
        _builder.newLine();
      }
    }
    _builder.append("  ");
    _builder.newLine();
    _builder.append("  ");
    String _print_2 = Setup.print(comp);
    _builder.append(_print_2, "  ");
    _builder.newLineIfNotEmpty();
    _builder.append("  ");
    _builder.newLine();
    _builder.append("  ");
    String _print_3 = Init.print(comp);
    _builder.append(_print_3, "  ");
    _builder.newLineIfNotEmpty();
    _builder.append("  ");
    _builder.newLine();
    _builder.append("  ");
    String _print_4 = Update.print(comp);
    _builder.append(_print_4, "  ");
    _builder.newLineIfNotEmpty();
    _builder.append("  ");
    _builder.newLine();
    _builder.append("  ");
    String _printConstructor = this.printConstructor(comp);
    _builder.append(_printConstructor, "  ");
    _builder.newLineIfNotEmpty();
    _builder.append("  ");
    _builder.newLine();
    _builder.append("  ");
    _builder.append("}");
    _builder.newLine();
    _builder.append("  ");
    _builder.newLine();
    return _builder.toString();
  }
  
  public String printConstructor(final ComponentSymbol comp) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("public ");
    String _name = comp.getName();
    _builder.append(_name);
    _builder.append("(");
    String _printConfiurationParametersAsList = Utils.printConfiurationParametersAsList(comp);
    _builder.append(_printConfiurationParametersAsList);
    _builder.append(") {");
    _builder.newLineIfNotEmpty();
    {
      boolean _isPresent = comp.getSuperComponent().isPresent();
      if (_isPresent) {
        _builder.append("  ");
        _builder.append("super(");
        {
          List<String> _inheritedParams = ComponentGenerator.getInheritedParams(comp);
          boolean _hasElements = false;
          for(final String inhParam : _inheritedParams) {
            if (!_hasElements) {
              _hasElements = true;
            } else {
              _builder.appendImmediate(",", "  ");
            }
            _builder.append(" ");
            _builder.append(inhParam, "  ");
            _builder.append(" ");
          }
        }
        _builder.append(");");
        _builder.newLineIfNotEmpty();
      }
    }
    _builder.append("  ");
    _builder.newLine();
    {
      boolean _isAtomic = comp.isAtomic();
      if (_isAtomic) {
        _builder.append("  ");
        String _behaviorImplName = Identifier.getBehaviorImplName();
        _builder.append(_behaviorImplName, "  ");
        _builder.append(" = new ");
        String _name_1 = comp.getName();
        _builder.append(_name_1, "  ");
        _builder.append("Impl");
        _builder.append(this.generics, "  ");
        _builder.append("(");
        _builder.newLineIfNotEmpty();
        {
          boolean _hasConfigParameters = comp.hasConfigParameters();
          if (_hasConfigParameters) {
            {
              List<JFieldSymbol> _configParameters = comp.getConfigParameters();
              boolean _hasElements_1 = false;
              for(final JFieldSymbol param : _configParameters) {
                if (!_hasElements_1) {
                  _hasElements_1 = true;
                } else {
                  _builder.appendImmediate(",", "  ");
                }
                _builder.append("  ");
                String _name_2 = param.getName();
                _builder.append(_name_2, "  ");
                _builder.newLineIfNotEmpty();
              }
            }
            _builder.append("          ");
          }
        }
        _builder.append(");");
        _builder.newLineIfNotEmpty();
      }
    }
    _builder.append("  ");
    _builder.append("// config parameters       ");
    _builder.newLine();
    {
      List<JFieldSymbol> _configParameters_1 = comp.getConfigParameters();
      for(final JFieldSymbol param_1 : _configParameters_1) {
        _builder.append("  ");
        _builder.append("this.");
        String _name_3 = param_1.getName();
        _builder.append(_name_3, "  ");
        _builder.append(" = ");
        String _name_4 = param_1.getName();
        _builder.append(_name_4, "  ");
        _builder.append(";");
        _builder.newLineIfNotEmpty();
      }
    }
    _builder.append("}");
    _builder.newLine();
    return _builder.toString();
  }
  
  public String printComputeAtomic(final ComponentSymbol comp) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("@Override");
    _builder.newLine();
    _builder.append("public void compute() {");
    _builder.newLine();
    _builder.append("// collect current input port values");
    _builder.newLine();
    _builder.append("final ");
    String _name = comp.getName();
    _builder.append(_name);
    _builder.append("Input");
    _builder.append(this.generics);
    _builder.append(" input = new ");
    String _name_1 = comp.getName();
    _builder.append(_name_1);
    _builder.append("Input");
    _builder.append(this.generics);
    _builder.newLineIfNotEmpty();
    _builder.append("(");
    {
      List<PortSymbol> _allIncomingPorts = comp.getAllIncomingPorts();
      boolean _hasElements = false;
      for(final PortSymbol inPort : _allIncomingPorts) {
        if (!_hasElements) {
          _hasElements = true;
        } else {
          _builder.appendImmediate(",", "");
        }
        _builder.append("this.getPort");
        String _firstUpper = StringExtensions.toFirstUpper(inPort.getName());
        _builder.append(_firstUpper);
        _builder.append("().getCurrentValue()");
      }
    }
    _builder.append(");");
    _builder.newLineIfNotEmpty();
    _builder.newLine();
    _builder.append("try {");
    _builder.newLine();
    _builder.append("// perform calculations");
    _builder.newLine();
    _builder.append("  ");
    _builder.append("final ");
    String _name_2 = comp.getName();
    _builder.append(_name_2, "  ");
    _builder.append("Result");
    _builder.append(this.generics, "  ");
    _builder.append(" result = ");
    String _behaviorImplName = Identifier.getBehaviorImplName();
    _builder.append(_behaviorImplName, "  ");
    _builder.append(".compute(input); ");
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
    _builder.append("} catch (Exception e) {");
    _builder.newLine();
    _builder.append("Log.error(\"");
    String _name_3 = comp.getName();
    _builder.append(_name_3);
    _builder.append("\", e);");
    _builder.newLineIfNotEmpty();
    _builder.append("  ");
    _builder.append("}");
    _builder.newLine();
    _builder.append("}");
    _builder.newLine();
    return _builder.toString();
  }
  
  public String printComputeComposed(final ComponentSymbol comp) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("@Override");
    _builder.newLine();
    _builder.append("public void compute() {");
    _builder.newLine();
    _builder.append("// trigger computation in all subcomponent instances");
    _builder.newLine();
    {
      Collection<ComponentInstanceSymbol> _subComponents = comp.getSubComponents();
      for(final ComponentInstanceSymbol subcomponent : _subComponents) {
        _builder.append("  ");
        _builder.append("this.");
        String _name = subcomponent.getName();
        _builder.append(_name, "  ");
        _builder.append(".compute();");
        _builder.newLineIfNotEmpty();
      }
    }
    _builder.append("}");
    _builder.newLine();
    return _builder.toString();
  }
  
  private static List<String> getInheritedParams(final ComponentSymbol component) {
    List<String> result = new ArrayList<String>();
    List<JFieldSymbol> configParameters = component.getConfigParameters();
    boolean _isPresent = component.getSuperComponent().isPresent();
    if (_isPresent) {
      ComponentSymbolReference superCompReference = component.getSuperComponent().get();
      List<JFieldSymbol> superConfigParams = superCompReference.getReferencedSymbol().getConfigParameters();
      boolean _isEmpty = configParameters.isEmpty();
      boolean _not = (!_isEmpty);
      if (_not) {
        for (int i = 0; (i < superConfigParams.size()); i++) {
          result.add(configParameters.get(i).getName());
        }
      }
    }
    return result;
  }
}
