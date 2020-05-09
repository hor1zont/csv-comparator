package net.hor1zont.csv.comparator.twofiles;

import java.util.Map;

/**
 * Rule to compare 2 lines from 2 files
 */
public class ValuesComparisionRule {

    /**
     * Title columns from which data should be compared. Can be multivalued. In this case value from several
     * columns will be separated by separator from {@link ValueExtractor}
     */
    private final String[] keysA;
    private final String[] keysB;

    /**
     * Lines/rows to compare: {@code key} is title of file (first csv file line), value is {@code value} is specific
     * line (not first file line - not title line)
     */
    private Map<String, String> rowA;
    private Map<String, String> rowB;

    /**
     * Validator to compare values in files, can be overridden with another instance to specify custom comparision logic
     */
    private TwoValuesValidator validator = new TwoValuesValidator();

    /**
     * Extractors to get values for comparision - make sense when need to extract value to comparison from several
     * columns. Can be overridden with customer implementation. Also separator to use for join can be passed like a
     * constructor in new instance creation moment
     */
    private ValueExtractor valueExtractorA = new ValueExtractor();
    private ValueExtractor valueExtractorB = new ValueExtractor();

    public ValuesComparisionRule(String[] keysA, String[] keysB) {
        this.keysA = keysA;
        this.keysB = keysB;
    }

    public ValuesComparisionRule(String[] keysA, String keyB) {
        this(keysA, new String[]{keyB});
    }

    public ValuesComparisionRule(String keyA, String[] keysB) {
        this(new String[]{keyA}, keysB);
    }

    public ValuesComparisionRule(String keyA, String keyB) {
        this(new String[]{keyA}, new String[]{keyB});
    }

    void init(Map<String, String> rowA, Map<String, String> rowB) {
        this.rowA = rowA;
        this.rowB = rowB;
    }

    public boolean compareValues(
            String valueA,
            String valueB
    ) {
        return validator.equalityFunction(valueA, valueB);
    }

    /**
     * @param validator instance with custom comparision logic
     */
    public ValuesComparisionRule setValidator(TwoValuesValidator validator) {
        this.validator = validator;
        return this;
    }

    /**
     * @param valueExtractorA extractor to specify logic to get values for comparision when need to extract value to
     * comparison from several columns (separator to use for join can be passed like a constructor)
     */
    public ValuesComparisionRule setValueExtractorA(ValueExtractor valueExtractorA) {
        this.valueExtractorA = valueExtractorA;
        return this;
    }

    /**
     * @param valueExtractorB extractor to specify logic to get values for comparision when need to extract value to
     * comparison from several columns (separator to use for join can be passed like a constructor)
     */
    public ValuesComparisionRule setValueExtractorB(ValueExtractor valueExtractorB) {
        this.valueExtractorB = valueExtractorB;
        return this;
    }

    public String[] getKeysA() {
        return keysA;
    }

    public String[] getKeysB() {
        return keysB;
    }

    public String extractValueA() throws ColumnNotFoundException {
        return valueExtractorA.extractValue(rowA, keysA);
    }

    public String extractValueB() throws ColumnNotFoundException {
        return valueExtractorB.extractValue(rowB, keysB);
    }
}
