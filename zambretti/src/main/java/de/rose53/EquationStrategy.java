package de.rose53;

@FunctionalInterface
public interface EquationStrategy<T> {

    T calculate(T p);
}