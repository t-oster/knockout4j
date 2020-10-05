/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.upstart_it.knockout4j;

import java.util.LinkedHashSet;
import java.util.Set;
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
    private final Set<KnockoutObservable> observables = new LinkedHashSet<>();
    
    public <T> KnockoutObservable<T> observable(T val) {
        KnockoutObservable<T> result = new KnockoutObservable<>(this, val);
        observables.add(result);
        return result;
    }
    public <T> KnockoutComputed<T> computed(Supplier<T> val) {
        KnockoutComputed<T> result =  new KnockoutComputed<>(this, val, false, false);
        observables.add(result);
        return result;
    }
    public <T> KnockoutComputed<T> computed(Supplier<T> val, boolean deferEvaluation) {
        KnockoutComputed<T> result =  new KnockoutComputed<>(this, val, false, deferEvaluation);
        observables.add(result);
        return result;
    }
    public <T> KnockoutComputed<T> pureComputed(Supplier<T> val) {
        KnockoutComputed<T> result = new KnockoutComputed<>(this, val, true, false);
        observables.add(result);
        return result;
    }
    
    boolean startDependencyTracking(KnockoutComputed c) {
        if (recording.contains(c)) {
            return false;
        }
        clearDependenciesFor(c);
        recording.push(c);
        return true;
    }
    
    void stopDependencyTracking(KnockoutComputed c) {
        if (!c.equals(recording.pop())) {
            throw new RuntimeException("Stack corrupted");
        }
    }
    
    KnockoutComputed getCurrentlyEvaluatedObservable() {
        return recording.isEmpty() ? null : recording.peek();
    }

    private void clearDependenciesFor(KnockoutComputed c) {
        observables.forEach(o -> o.unsubscribe(c));
    }
    
    //called from observable.dispose
    void dispose(KnockoutObservable o) {
        if (o instanceof KnockoutComputed) {
            clearDependenciesFor((KnockoutComputed) o);
        }
        observables.remove(o);
    }
}
