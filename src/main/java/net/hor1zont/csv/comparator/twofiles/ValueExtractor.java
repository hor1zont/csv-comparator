package net.hor1zont.csv.comparator.twofiles;

import java.util.Arrays;
import java.util.Map;
import java.util.StringJoiner;

/**
 * Class to keep logic how to extract/get values from line in Map representation, where key in column title value of
 * csv file and value is column value from specific line
 */
public class ValueExtractor {

    /**
     * Separator/delimiter to use if need to join several column to next comparision
     */
    private final String separator;

    public ValueExtractor(String separator) {
        this.separator = separator;
    }

    public ValueExtractor() {
        // default separator is " "
        separator = " ";
    }

    /**
     * Can be overridden to change logic how to get target value from several columns.
     * Default logic is extracting values from {@code row} (one line) one by one with
     * keys from {@code keys} values.
     *
     * @param keys column names to extract values from line
     * @param row line with data to extract
     * @return String with extracted data from all columns separated by separator
     */
    public String extractValue(
            Map<String, String> row,
            String... keys
    ) throws ColumnNotFoundException {
        StringJoiner stringJoiner = new StringJoiner(separator);
        for (String key : keys) {
            String value = row.get(key);
            if (value == null) {
                throw new ColumnNotFoundException(String.format("In row %s, key [%s] is not found. All keys list: %s",
                        row,
                        key,
                        Arrays.toString(keys)
                ));
            }
            stringJoiner.add(value);
        }
        return stringJoiner.toString();
    }
}
