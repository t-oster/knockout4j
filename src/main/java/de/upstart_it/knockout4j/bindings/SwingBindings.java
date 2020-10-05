package de.upstart_it.knockout4j.bindings;

import de.upstart_it.knockout4j.KnockoutObservable;
import java.util.Objects;
import javax.swing.JLabel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;

/**
 *
 * @author Thomas Oster <thomas.oster@upstart-it.de>
 */
public class SwingBindings {
    
    public static Binding textBinding(JLabel label, KnockoutObservable<String> value) {
        //set initial value
        label.setText(value.get());
        var subscription = value.subscribe(label::setText);
        return new Binding(){
            @Override
            void unbind() {
                value.unsubscribe(subscription);
            }
        };
    }
    
    public static Binding valueBinding(JTextComponent textComponent, KnockoutObservable<String> value) {
        //set initial value
        textComponent.setText(value.get());
        var subscription = value.subscribe(s -> {
            if (!Objects.equals(textComponent.getText(), s)) {
                textComponent.setText(s);
            }
        });
        Document doc = textComponent.getDocument();
        DocumentListener docListener = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                value.set(textComponent.getText());
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                value.set(textComponent.getText());
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                value.set(textComponent.getText());
            }
        };
        doc.addDocumentListener(docListener);
        return new Binding() {
            @Override
            void unbind() {
                doc.removeDocumentListener(docListener);
                value.unsubscribe(subscription);
            }
        };
    }
}
