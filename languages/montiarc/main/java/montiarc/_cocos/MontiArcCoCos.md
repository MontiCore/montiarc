<!-- (c) https://github.com/MontiCore/monticore -->

# MontiArc Context Conditions (CoCos)

Legend:

- ☑ Intentionally included
- ☒ Intentionally not included
- ☐ Inclusion pending or to be discussed

## Arc CoCos

| in MA | CoCo(s)                                                | Language | Code(s)                            | Notes                                                                 |
| ----- | ------------------------------------------------------ | -------- | ---------------------------------- | --------------------------------------------------------------------- |
| ☑     | CircularInheritance                                    | ArcBasis | 0xC1010                            |                                                                       |
| ☑     | ComponentInstanceTypeExists                            | ArcBasis | 0xC1015                            |                                                                       |
| ☑     | ComponentTypeNameCapitalization                        | ArcBasis | 0xC1055                            |                                                                       |
| ☑     | ConfigurationParameterAssignment                       | ArcBasis | 0xC1083, 0xC1084,0xC1085           |                                                                       |
| ☑     | ConfigurationParametersCorrectlyInherited              | ArcBasis | 0xC1030, 0xC1031, 0xC1032          |                                                                       |
| ☑     | ConnectorSourceAndTargetComponentDiffer                | ArcBasis | 0xC1034                            |                                                                       |
| ☑     | ConnectorSourceAndTargetDiffer                         | ArcBasis | 0xC1045                            | Currently disabled                                                    |
| ☑     | ConnectorSourceAndTargetDirectionsFit                  | ArcBasis | 0xC1043, 0xC1043                   |                                                                       |
| ☑     | ConnectorSourceAndTargetExist                          | ArcBasis | 0xC1070, 0xC1071, 0xC1035, 0xC1036 |                                                                       |
| ☑     | ConnectorSourceAndTargetTypesFit                       | ArcBasis | 0xC1037                            |                                                                       |
| ☑     | FieldInitExpressionsOmitPortReferences                 | ArcBasis | 0xC1065                            |                                                                       |
| ☑     | FieldInitExpressionTypesCorrect                        | ArcBasis | 0xC1066                            |                                                                       |
| ☑     | FieldNameCapitalization                                | ArcBasis | 0xC1039                            |                                                                       |
| ☑     | FieldTypeExists                                        | ArcBasis | 0xC1069                            |                                                                       |
| ☑     | InheritedComponentTypeExists                           | ArcBasis | 0xC1014                            |                                                                       |
| ☑     | InnerComponentNotExtendsDefiningComponent              | ArcBasis | 0xC1038                            |                                                                       |
| ☑     | InstanceArgsOmitPortReferences                         | ArcBasis | 0xC1082                            |                                                                       |
| ☑     | InstanceNameCapitalisation                             | ArcBasis | 0xC1056                            |                                                                       |
| ☑     | NoBehaviorInComposedComponents                         | ArcBasis | 0xC1089                            |                                                                       |
| ☑     | NoSubComponentReferenceCycles                          | ArcBasis | 0xC1064                            |                                                                       |
| ☑     | OnlyOneBehavior                                        | ArcBasis | 0xC1088                            |                                                                       |
| ☑     | OptionalConfigurationParametersLast                    | ArcBasis | 0xC1033                            |                                                                       |
| ☑     | ParameterDefaultValuesOmitPortReferences               | ArcBasis | 0xC1068                            |                                                                       |
| ☑     | ParameterDefaultValueTypesCorrect                      | ArcBasis | 0xC1067                            |                                                                       |
| ☑     | ParameterNameCapitalization                            | ArcBasis | 0xC1041                            |                                                                       |
| ☑     | ParameterTypeExists                                    | ArcBasis | 0xC1069                            |                                                                       |
| ☑     | PortNameCapitalisation                                 | ArcBasis | 0xC1040                            |                                                                       |
| ☑     | PortTypeExists                                         | ArcBasis | 0xC1069                            |                                                                       |
| ☑     | PortUniqueSender                                       | ArcBasis | 0xC1024                            |                                                                       |
| ☑     | PortUsage                                              | ArcBasis | 0xC1020, 0xC1021, 0xC1022, 0xC1023 | No Forward not tested/working                                         |
| ☑     | SubComponentsConnected                                 | ArcBasis | 0xC1025, 0xC1026, 0xC1027, 0xC1028 |                                                                       |
| ☑     | UniqueIdentifierNames                                  | ArcBasis | 0xC1061                            |                                                                       |
| ☑     | RootComponentTypesNoInstanceName                       | MontiArc | 0xC1062                            |                                                                       |
| ☑     | UnresolvableImport                                     | MontiArc | 0xC1087                            |                                                                       |
| ☐     | Connectors may not pierce through component interfaces |          |                                    | Implicit through grammar definition                                   |
| ☐     | ImportsAreUnique                                       |          |                                    |                                                                       |
| ☐     | ImportsAreUsed                                         |          |                                    | See #464                                                              |
| ☐     | Cannot find Symbol                                     |          | 0xC1016                            | Not tested by cocos, is logged by SynthesizeComponentFromMCBasicTypes |

## Generics CoCos

| in MA | CoCo(s)                                         | Language   | Code                      | Notes                                                                    |
| ----- | ----------------------------------------------- | ---------- | ------------------------- | ------------------------------------------------------------------------ |
| ☑     | GenericTypeParameterNameCapitalization          | GenericArc | 0xC1042                   |                                                                          |
| ☑     | ComponentInheritanceRespectsGenericTypeBounds   | MontiArc   | 0xC1072, 0xC1202, 0xC1203 |                                                                          |
| ☑     | ComponentInstantiationRespectsGenericTypeBounds | MontiArc   | 0xC1072, 0xC1202, 0xC1203 |                                                                          |
| ☐     | INNER_WITH_TYPE_PARAMETER_REQUIRES_INSTANCE     |            | 0xC1029                   | Unused ArcError                                                          |
| ☐     | ArraysOfGenericTypes                            |            |                           | Original document specified: Resultat von Java-Limitierungen, siehe #224 |
| ☐     | SubcomponentGenericTypesCorrectlyAssigned       |            |                           |                                                                          |
| ☐     | AllGenericParametersOfSuperClassSet             |            |                           |                                                                          |

# Mode Automata

| in MA | CoCo(s)                   | Language          | Code                      | Notes |
| ----- | ------------------------- | ----------------- | ------------------------- | ----- |
| ☑     | InitialModeExists         | BasicModeAutomata | 0xC1080                   |       |
| ☑     | NoHierarchicalModes       | BasicModeAutomata | 0xC1076                   |       |
| ☑     | NoModesInAtomicComponents | BasicModeAutomata | 0xC1079                   |       |
| ☑     | NoModesWithoutAutomata    | BasicModeAutomata | 0xC1078                   |       |
| ☑     | OneModeAutomatonAtMax     | BasicModeAutomata | 0xC1077                   |       |
| ☑     | StaticCheckOfDynamicTypes | BasicModeAutomata | 0xC1072                   |       |
| ☑     | UniqueNamesInModes        | BasicModeAutomata | 0xC1073, 0xC1074, 0xC1075 |       |

## Automaton CoCos

| in MA | CoCo(s)                                                                | Language           | Notes                                                                                                                                                                                                                            |
| ----- | ---------------------------------------------------------------------- | ------------------ | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| ☑     | UniqueStates                                                           | SCBasis            |                                                                                                                                                                                                                                  |
| ☑     | TransitionSourceAndTargetExist                                         | SCBasis            |                                                                                                                                                                                                                                  |
| ☑     | AtLeastOneInitialState                                                 | SCBasis            |                                                                                                                                                                                                                                  |
| ☑     | OneInitialStateAtMax                                                   | ArcAutomaton       | This coco should be moved to a statechart language!                                                                                                                                                                              |
| ☐     | ExpressionStatementWellFormedness                                      | ArcAutomaton       | Currently deactivated, as Lightcontrol won't build (due to a MontiArc bug)                                                                                                                                                       |
| ☑     | FieldReadWriteAccessFitsInGuards, FieldReadWriteAccessFitsInStatements | ArcAutomaton       | Not in SC-languages, as they don't know AssignmentExpressions. Not in AssignmentExpressions, as they don't know Symbols. Moreover we check ports referenced by `NameExpression`s, who are not known by the SC-languages, either. |
| ☑     | NoInputPortsInInitialOutputDecl                                        | ArcAutomaton       |                                                                                                                                                                                                                                  |
| ☑     | TransitionPreconditionsAreBoolean                                      | SCTransitions4Code |                                                                                                                                                                                                                                  |
| ☑     | AnteBlocksOnlyForInitialStates                                         | SCTransitions4Code | Checks that AnteBlocks in state declarations occur only for initial states, as they declare actions initially performed at component instantiation.                                                                              |
| ☑     | Unsupported automaton modeling elements:                               | MontiArc           | Entry & exit actions, finale states, stereotypes. With stereotypes present, we only warn the user. With all other unsupported modelling elements, we throw errors.                                                               |
| ☒     | PackageCorrespondsToFolders                                            | SCBasis            | Not applicable - we do not have statechart artifacts                                                                                                                                                                             |
| ☒     | SCFileExtension\[is.sc\]                                               | SCBasis            | Not applicable - we do not have statechart artifacts                                                                                                                                                                             |
| ☒     | SCNameIsArtifactName                                                   | SCBasis            | Not applicable - we do not have statechart artifacts                                                                                                                                                                             |
| ☒     | CapitalStateNames                                                      | SCBasis            | Warns if a state name starts with a lower case letter. At a discussion we found this to be unneccessary.                                                                                                                         |
