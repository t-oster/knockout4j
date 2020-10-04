/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.upstart_it.knockout4j.examples;

import de.upstart_it.knockout4j.KnockoutComputed;
import de.upstart_it.knockout4j.KnockoutObservable;
import de.upstart_it.knockout4j.Ko;

/**
 *
 * @author Thomas Oster <thomas.oster@upstart-it.de>
 */
public class ExampleViewModel {
    Ko ko = new Ko();
    KnockoutObservable<String> vorname = ko.observable("Peter");
    KnockoutObservable<String> nachname = ko.observable("Pan");
    KnockoutComputed<String> vollerName = ko.pureComputed(() -> this.vorname.get() +  " " + this.nachname.get());
    
    public ExampleViewModel() {
        ko.computed(() -> {
            //create dependency
            this.vorname.get();
            System.out.println("I was called because vorname changed to "+this.vorname.get());
            return null;
        });
        nachname.subscribe((String newVal) -> System.out.println("I was called because nachname changed to"+newVal));
    }
    
    public static void main(String[] args) {
        
    }
}
