package com.jalarbee.knihovny.workq;

/**
 * @author Abdoulaye Diallo
 */
public interface Task<T> {

    boolean doIt(T t);
}
