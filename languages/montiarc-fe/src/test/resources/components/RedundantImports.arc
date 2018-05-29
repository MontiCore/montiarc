package components;

import a.b.*;
import java.util.List;
import a.b.*;  // Import statement is redundant
import java.util.List; // Import statement is redundant

/*
 * Invalid model.
 *
 * @implements [Hab16] CV3: Duplicated imports should be avoided. (p.71, no listing)
 */
component RedundantImports {
    
    port 
        in List lIn;
}