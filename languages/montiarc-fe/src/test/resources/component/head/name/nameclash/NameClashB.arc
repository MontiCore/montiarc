package component.head.name.nameclash;

import contextconditions.valid.Types.*;
import contextconditions.valid.Motor;

/**
 * Invalid model. Component has the same name as the component it is extending
 */
component NameClashA extends NameClashA {}
