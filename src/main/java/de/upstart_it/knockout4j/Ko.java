/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.upstart_it.knockout4j;

import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
import java.util.function.Supplier;

/**
 *
 * @author Thomas Oster <thomas.oster@upstart-it.de>
 */
public class Ko {
    //Static stuff if only one registry is needed
    private static Ko instance;
    public static Ko getInstance() {
        if (instance == null) {
            instance = new Ko();
        }
        return instance;
    }
    public static <T> KnockoutObservable<T> Observable(T val) {
        return getInstance().observable(val);
    }
    public static <T> KnockoutComputed<T> Computed(Supplier<T> val) {
        return getInstance().computed(val);
    }
    public static <T> KnockoutComputed<T> Computed(Supplier<T> val, boolean deferEvaluation) {
        return getInstance().computed(val, deferEvaluation);
    }
    public static <T> KnockoutComputed<T> PureComputed(Supplier<T> val) {
        return getInstance().pureComputed(val);
    }
    
    private final Stack<KnockoutComputed> recording = new Stack<>();
    private final Stack<List<KnockoutObservable>> dependencies = new Stack<>();
    
    public <T> KnockoutObservable<T> observable(T val) {
        return new KnockoutObservable<>(this, val);
    }
    public <T> KnockoutComputed<T> computed(Supplier<T> val) {
        return  new KnockoutComputed<>(this, val, false, false);
    }
    public <T> KnockoutComputed<T> computed(Supplier<T> val, boolean deferEvaluation) {
        return new KnockoutComputed<>(this, val, false, deferEvaluation);
    }
    public <T> KnockoutComputed<T> pureComputed(Supplier<T> val) {
        return new KnockoutComputed<>(this, val, true, false);
    }
    
    /**
     * Starts dependency tracking for the given computed.
     * A new list of dependencies is created.
     * If the same computed is already tracking, it returns false (cycle avoidance)
     * @param c The computed which is currently evaluating
     * @return 
     */
    boolean startDependencyTracking(KnockoutComputed c) {
        if (recording.contains(c)) {
            return false;
        }
        recording.push(c);
        dependencies.add(new LinkedList<>());
        return true;
    }
    
    /**
     * Stops dependency tracking and returns the list of Dependencies
     * @param c 
     */
    List<KnockoutObservable> stopDependencyTracking(KnockoutComputed c) {
        if (!c.equals(recording.pop())) {
            throw new RuntimeException("Stack corrupted");
        }
        return dependencies.pop();
    }
    
    void registerDependency(KnockoutObservable observed) {
        if (recording.isEmpty() || dependencies.peek().contains(observed)) {
            return;
        }
        dependencies.peek().add(observed);
    }
    
}
