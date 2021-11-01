<#-- (c) https://github.com/MontiCore/monticore -->
<#-- Generates the input class for a component. -->
${tc.signature("comp", "compHelper", "isTOPClass")}
<#assign comp=comp><#assign compHelper=compHelper>
<#import "/templates/util/Copyright.ftl" as Copyright>
<#import "/templates/util/Imports.ftl" as Imports>
<#import "/templates/input/InputHeader.ftl" as Header>
<#import "/templates/input/InputBody.ftl" as Body>
<#import "/templates/util/Utils.ftl" as Utils>
<@Copyright.defaultCopyrightComment/>

<#if comp.getPackageName() != "">
  package ${comp.getPackageName()};
</#if>

<@Imports.printImportsForInputClass comp=comp compHelper=compHelper/>

<@Header.printInputClassHeader comp=comp compHelper=compHelper isTOPClass=isTOPClass/> {

<@Body.printInputClassBody comp=comp compHelper=compHelper isTOPClass=isTOPClass/>
}
