package industry;
conforms to nfp.LayoutTagSchema;

// in the example are all units in mW, but here I changed it for testing reasons
tags LayoutInformation for turbineController {
	tag piController with ComponentLayout =
	  { id = 2, pos = (10, 20), size = (15, 10), layoutPosition = 1,
	  reservedHorizontalSpace = 10 } ; // isOnTop is false (b/c it is not mentioned)

	tag filtering with ComponentLayout =
    { id = 2, pos = (10, 20), size = (15, 10), layoutPosition = 1,
      reservedHorizontalSpace = 10, isOnTop } ; // isOnTop is true (b/c it is mentioned)

  tag mainController with ComponentLayout =
  { id = "[", size = 5 } ;
}