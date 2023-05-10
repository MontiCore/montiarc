<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTMCImportStatement ast -->
import ${ast.getQName()}<#if ast.isStar()>.*</#if>;

import com.microsoft.z3.*;
import java.util.ArrayList;
import java.util.List;
