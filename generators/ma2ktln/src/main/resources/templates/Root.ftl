<#-- (c) https://github.com/MontiCore/monticore -->
<#-- Generates the component class for atomic and composed components. -->
<#-- @ftlvariable name="component" type="arcbasis._symboltable.ComponentTypeSymbol" -->
<#-- @ftlvariable name="util" type="TemplateUtilities" -->
${tc.signature("symbol", "utility")}
<#import "/templates/util/Copyright.ftl" as Copyright>
<#import "/templates/util/Imports.ftl" as Imports>
<#import "/templates/Header.ftl" as Header>
<#import "/templates/Structure.ftl" as Structure>
<#import "/templates/behavior/Behavior.ftl" as Behavior>
<#import "/templates/Import.ftl" as Imp>
<#import "/templates/component/ComponentBody.ftl" as Body>
<@Copyright.defaultCopyrightComment/>
<#global component=symbol>
<#global util=utility>

<#if component.getPackageName() != "">
package ${component.getPackageName()};
</#if>

<@Imp.printImports/>

<@Header.ofClass/>
{
<@Structure.attributes/>
  init {
<@Structure.init/>
  }

<@Behavior.printBehavior/>
<@Behavior.printRequiredAttributes/>

}