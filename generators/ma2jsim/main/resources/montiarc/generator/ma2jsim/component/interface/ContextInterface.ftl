<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTComponentType ast -->
<#import "/montiarc/generator/ma2jsim/util/Util.ftl" as Util>

public interface ${ast.getName()}${suffixes.context()}<#if isTop>${suffixes.top()}</#if> <@Util.printTypeParameters ast/>
  extends ${ast.getName()}${suffixes.output()} <@Util.printTypeParameters ast false/>,
          ${ast.getName()}${suffixes.parameters()} <@Util.printTypeParameters ast false/>,
          ${ast.getName()}${suffixes.fields()} <@Util.printTypeParameters ast false/>,
          ${ast.getName()}${suffixes.features()} <@Util.printTypeParameters ast false/> { }
