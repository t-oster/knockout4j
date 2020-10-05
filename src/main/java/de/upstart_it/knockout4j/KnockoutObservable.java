package de.upstart_it.knockout4j;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 *
 * @author Thomas Oster <thomas.oster@upstart-it.de>
 */
public class KnockoutObservable<T> implements Supplier<T>, Consumer<T> {
    final Ko registry;
    private T value;
    final List<Consumer<T>> observers = new LinkedList<>();
    
    KnockoutObservable(Ko registry, T val) {
        this.registry = registry;
        this.value = val;
    }
    /**
     * Returns the value without generating a dependency
     * @return 
     */
    public T peek() {
        return value;
    }
    /**
     * Returns the current value and if called within a computed observable
     * it creates a dependency to this observable
     * @return 
     */
    @Override
    public T get() {
        registry.registerDependency(this);
        return peek();
    }
    /**
     * Changes the observable and updates all dependent observables
     * @param val 
     */
    public synchronized void set(T val) {
        if (
            (this.value == null && val == null)
            || this.value != null && this.value.equals(val)
        ) {
            //nothing changed.
            return;
        }
        this.value = val;
        notifyObservers();
    }
    /**
     * Alias for set(T val)
     * @param val 
     */
    @Override
    public void accept(T val) {
        set(val);
    }
    public synchronized void notifyObservers() {
        List<Consumer<T>> oldObservers = new LinkedList<>(observers);
        oldObservers.forEach(c -> {
            if (c instanceof KnockoutComputed) {
                ((KnockoutComputed<T>) c).evaluate();
            }
            else {
                c.accept(value);
            }
        });
    }
    public synchronized void subscribe(Consumer<T> o) {
        if (!observers.contains(o)) {
            observers.add(o);
        }
    }
    public synchronized void unsubscribe(Consumer<T> o) {
        observers.remove(o);
    }
    
    /**
     * Clears all observers and unregisters from all dependencies (if it's
     * a computed observable)
     */
    public void dispose() {
        observers.clear();
    }
}
