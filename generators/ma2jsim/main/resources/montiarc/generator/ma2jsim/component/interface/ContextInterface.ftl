<#-- (c) https://github.com/MontiCore/monticore -->
<#-- @ftlvariable name="ast" type=" arcbasis._ast.ASTArcComponentType" -->
<#-- @ftlvariable name="helper" type="montiarc.generator.util.Helper" -->
<#import "/montiarc/generator/ma2jsim/util/Util.ftl" as Util>

@de.se_rwth.commons.Generated("montiarc.generators.ma2jsim")
public interface ${ast.getName()}${suffixes.context()}<#if isTop>${suffixes.top()}</#if> <@Util.printTypeParameters ast/>
  extends ${ast.getName()}${suffixes.output()} <@Util.printTypeParameters ast false/>,
          ${ast.getName()}${suffixes.parameters()} <@Util.printTypeParameters ast false/>,
          ${ast.getName()}${suffixes.fields()} <@Util.printTypeParameters ast false/>,
          ${ast.getName()}${suffixes.features()} <@Util.printTypeParameters ast false/>,
          montiarc.rte.oracle.OracleOwner { }
