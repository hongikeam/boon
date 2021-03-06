package org.boon.criteria.internal;

import java.util.List;

public interface Visitor<KEY, ITEM> {

    public abstract void visit( KEY key, ITEM item, Object currentProperty, List<String> propertyPath );


}
