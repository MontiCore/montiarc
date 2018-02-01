package components.body.subcomponents;

import java.util.*;

/**
 * Valid model. 
 */
component UsingComplexParams {
    component ComplexParams(new int[]{1, 2, 3}, 
                            new HashMap<List<String>, 
                            List<Integer>>()) cp;
}