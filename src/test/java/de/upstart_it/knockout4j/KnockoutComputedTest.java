/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.upstart_it.knockout4j;

import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author Thomas Oster <thomas.oster@upstart-it.de>
 */
public class KnockoutComputedTest {
    
    public KnockoutComputedTest() {
    }

    @Test
    public void testSimpleEvaluation() {
        Ko ko = new Ko();
        var vorname = ko.observable("Peter");
        var nachname = ko.observable("Pan");
        var vollerName = ko.computed(() -> vorname.get()+ " " +nachname.get());
        assertEquals("Peter", vorname.get());
        assertEquals("Pan", nachname.get());
        assertEquals("Peter Pan", vollerName.get());
    }
    
    @Test
    public void testDeferredEvaluation() {
        Ko ko = new Ko();
        var vorname = ko.observable("Peter");
        var nachname = ko.observable("Pan");
        var evaluated = new AtomicBoolean(false);
        var vollerName = ko.pureComputed(() -> {
            evaluated.set(true);
            if ("Kurt".equals(vorname.get())) {
                return "Kurt, einfach Kurt";
            }
            //Dependency for nachname only created when vorname not 'Kurt'
            return vorname.get()+ " " +nachname.get();
        });
        
        assertFalse(evaluated.get());
        assertEquals("Peter Pan", vollerName.get());//triggers evaluation
        assertTrue(evaluated.get());
        evaluated.set(false);
        assertEquals("Peter Pan", vollerName.get());//nothing, changed so no evaluation
        assertFalse(evaluated.get());
        vorname.set("Kurt");//no evaluation
        assertFalse(evaluated.get());
        assertEquals("Kurt, einfach Kurt", vollerName.get());//triggers evaluation, changes dependency for vorname only
        assertTrue(evaluated.get());
        evaluated.set(false);
        nachname.set("Müller");//does not trigger evaluation
        assertEquals("Kurt, einfach Kurt", vollerName.get());//still no evaluation, because vorname did not change
        assertFalse(evaluated.get());
        vorname.set("Peter");//should trigger change
        assertFalse(evaluated.get());
        assertEquals("Peter Müller", vollerName.get());
        assertTrue(evaluated.get());
    }
    
    @Test
    public void testNestedEvaluation() {
        Ko ko = new Ko();
        var age = ko.observable(42);
        var level1 = ko.computed(() -> "I am "+age.get()+" years old");
        var level2 = ko.computed(() -> "I say: "+level1.get());
        assertEquals(42, age.get());
        assertEquals("I am 42 years old", level1.get());
        assertEquals("I say: I am 42 years old", level2.get());
        age.set(23);
        assertEquals(23, age.get());
        assertEquals("I am 23 years old", level1.get());
        assertEquals("I say: I am 23 years old", level2.get());
    }
}
