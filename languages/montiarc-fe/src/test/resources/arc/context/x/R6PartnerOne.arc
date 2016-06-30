package x;

import myTypes.SubType;
import myTypes.SuperType;

component R6PartnerOne {

  port 
    in SubType subTypeIn,
    out SubType subTypeOut,
    in SuperType superTypeIn,
    out SuperType superTypeOut;
}