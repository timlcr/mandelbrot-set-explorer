package gui;

import javax.swing.JTextField;
import java.util.function.Function;

class ValueField<E> extends JTextField {

    private E lastValidValue;
    Function<String, E> parser;

    public ValueField(E initialValue, Function<String, E> parser, int cols) {
        super("" + initialValue, cols);
        this.lastValidValue = initialValue;
        this.parser = parser;
    }

    public E get() {
        try {
            E val = parser.apply(getText());
            lastValidValue = val;
            return val;
        } catch(IllegalArgumentException iae) {
            setText("" + lastValidValue);
            return lastValidValue;
        }
    }

    public static ValueField<Integer> intField(int initialValue, int cols) {
        return new ValueField<>(initialValue, Integer::parseInt, cols);
    }

    public static ValueField<Double> doubleField(double initialValue, int cols) {
        return new ValueField<>(initialValue, Double::parseDouble, cols);
    }

}
