package net.hor1zont.csv.comparator.twofiles;

public class TwoValuesValidator {

    /**
     * Method can be overridden to change logic of values comparison
     */
    public boolean equalityFunction(
            String valueA,
            String valueB
    ) {
        return valueA.equals(valueB);
    }
}
