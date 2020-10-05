/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.upstart_it.knockout4j;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

/**
 *
 * @author Thomas Oster <thomas.oster@upstart-it.de>
 */
public class CycleAvoidanceTest {
    Ko ko = new Ko();
    KnockoutComputed<String> a = ko.pureComputed(() -> "A und "+CycleAvoidanceTest.this.b.get());
    KnockoutComputed<String> b = ko.pureComputed(() -> "B und "+a.get());
    
    @Test
    public void testCycleAvoidance() {
        assertEquals("A und B und null", a.get());
        assertEquals("B und A und B und null", b.get());
    }
}
