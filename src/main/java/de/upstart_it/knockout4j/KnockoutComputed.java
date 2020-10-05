package de.upstart_it.knockout4j;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;

/**
 *
 * @author Thomas Oster <thomas.oster@upstart-it.de>
 */
public class KnockoutComputed<T> extends KnockoutObservable<T> {
    
    final boolean isPure;
    boolean needsEvaluation;
    private final Supplier<T> valFunc;
    private final List<KnockoutObservable> dependencies = new LinkedList<>();
    
    KnockoutComputed(Ko registry, Supplier<T> valFunc, boolean isPure, boolean deferEvaluation) {
        super(registry, null);
        this.valFunc = valFunc;
        this.isPure = isPure;
        needsEvaluation = true;
        if (!this.isPure && !deferEvaluation) {
            evaluate();
        }
    }
    
    void unsubscribeAndClearDependencies() {
        dependencies.forEach(d -> d.unsubscribe(this));
        dependencies.clear();
    }
    
    final void evaluate() {
        if (!needsEvaluation && isPure && observers.isEmpty()) {
            //if nobody is listening, defer recomputing until somebody does
            needsEvaluation = true;
            return;
        }
        if (!registry.startDependencyTracking(this)) {
            return;
        }
        unsubscribeAndClearDependencies();
        set(valFunc.get());
        needsEvaluation = false;
        registry.stopDependencyTracking(this).forEach(d -> {
            d.subscribe(this);
            dependencies.add(d);
        });
    }

    @Override
    public T get() {
        if (needsEvaluation) {
            evaluate();
        }
        return super.get();
    }

    @Override
    public void dispose() {
        super.dispose();
        unsubscribeAndClearDependencies();
    }
    
}
