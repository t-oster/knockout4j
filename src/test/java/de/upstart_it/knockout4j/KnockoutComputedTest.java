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
    public void testPureComputed() {
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
    public void testDeferredEvaluation() {
        Ko ko = new Ko();
        var vorname = ko.observable("Peter");
        var nachname = ko.observable("Pan");
        var evaluated = new AtomicBoolean(false);
        var vollerName = ko.computed(() -> {
            evaluated.set(true);
            if ("Kurt".equals(vorname.get())) {
                return "Kurt, einfach Kurt";
            }
            //Dependency for nachname only created when vorname not 'Kurt'
            return vorname.get()+ " " +nachname.get();
        }, true);
        assertFalse(evaluated.get());
        vorname.set("Peter2");
        assertFalse(evaluated.get());
        assertEquals("Peter2 Pan", vollerName.get());
        assertTrue(evaluated.get());
        evaluated.set(false);
        vorname.set("Peter3");
        assertTrue(evaluated.get());
    }
    
    @Test
    public void testCaching() {
        Ko ko = new Ko();
        var age = ko.observable(42);
        var level1evaluated = new AtomicBoolean(false);
        var level1 = ko.computed(() -> {
            level1evaluated.set(true);
            return "I am "+age.get()+" years old";
        });
        assertTrue(level1evaluated.get());
        level1evaluated.set(false);
        assertEquals(42, age.get());
        age.set(42);//same value should not trigger reevaluation
        assertFalse(level1evaluated.get(), "same value should not trigger reevaluation");
    }
    
    @Test
    public void testNestedEvaluation() {
        Ko ko = new Ko();
        var age = ko.observable(42);
        var level1evaluated = new AtomicBoolean(false);
        var level1 = ko.computed(() -> {
            level1evaluated.set(true);
            return "I am "+age.get()+" years old";
        });
        var level2evaluated = new AtomicBoolean(false);
        var level2 = ko.computed(() -> {
            level2evaluated.set(true);
            return "I say: "+level1.get();
        });
        assertTrue(level1evaluated.get());
        assertTrue(level2evaluated.get());
        level1evaluated.set(false);
        level2evaluated.set(false);
        assertEquals(42, age.get());
        assertEquals("I am 42 years old", level1.get());
        assertEquals("I say: I am 42 years old", level2.get());
        assertFalse(level1evaluated.get());
        assertFalse(level2evaluated.get());
        age.set(23);
        assertTrue(level1evaluated.get());
        assertTrue(level2evaluated.get());
        assertEquals(23, age.get());
        assertEquals("I am 23 years old", level1.get());
        assertEquals("I say: I am 23 years old", level2.get());
    }
}
