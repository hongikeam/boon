package org.boon.core;

/**
 * Place holder or JDK 1.8 Function
 */
public interface Function<IN, OUT> {

    /**
     * Compute the result of applying the function to the input argument
     *
     * @param in the input object
     * @return the function result
     */
    OUT apply( IN in );

}
