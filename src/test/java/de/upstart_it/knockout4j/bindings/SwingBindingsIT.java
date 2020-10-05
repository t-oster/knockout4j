/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.upstart_it.knockout4j.bindings;

import de.upstart_it.knockout4j.KnockoutObservable;
import de.upstart_it.knockout4j.Ko;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.JFrame;
import static javax.swing.WindowConstants.EXIT_ON_CLOSE;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author Thomas Oster <thomas.oster@upstart-it.de>
 */
public class SwingBindingsIT {
    
    public static class ViewModel {
        Ko ko = new Ko();
        public KnockoutObservable<String> vorname = ko.observable("Peter");
        public KnockoutObservable<String> nachname = ko.observable("Pan");
        public KnockoutObservable<String> vollerName = ko.computed(() -> vorname.get()+ " " +nachname.get());
    }
    
    public SwingBindingsIT() {
    }

    @Test
    public void testTextBindings() throws InterruptedException {
        TextFrame f = new TextFrame();
        f.applyBindings(new ViewModel());
        f.setVisible(true);
        f.setDefaultCloseOperation(EXIT_ON_CLOSE);
        while (f.isVisible()) {
            Thread.sleep(500);
        }
    }
    
}
