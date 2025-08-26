package gui;

import javax.swing.JComboBox;

public class Selector<E> extends JComboBox<E> {

    public Selector(E[] objects) { super(objects); }

    public Selector() { super(); }

    @SuppressWarnings("unchecked")
    public E get() {
        return (E) getSelectedItem();
    }

}