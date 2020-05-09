package net.hor1zont.csv.comparator.twofiles.actions;

import net.hor1zont.csv.comparator.twofiles.ActionsOnDifference;
import net.hor1zont.csv.comparator.twofiles.ColumnNotFoundException;
import net.hor1zont.csv.comparator.twofiles.TwoFilesComparisonActions;
import net.hor1zont.csv.comparator.twofiles.TwoValuesValidator;
import net.hor1zont.csv.comparator.twofiles.ValueExtractor;
import net.hor1zont.csv.comparator.twofiles.ValuesComparisionRule;

import java.nio.charset.Charset;
import java.util.Comparator;
import java.util.Map;

public class ComparisonActionExample extends TwoFilesComparisonActions {

    @Override
    public String getNameFileA() {
        return "fileA.csv";
    }

    @Override
    public String getNameFileB() {
        return "fileB.csv";
    }

    @Override
    public String getTestFolder() {
        return "/tmp/files/";
    }

    // Values from file fileA.csv will be grouped into Map with values in column 'id' as keys - values will be Map
    // with every row values, were key - csv title value and value is line specific value
    @Override
    public String[] getKeysFileA() {
        return new String[]{"id"};
    }

    // Values from file fileB.csv will be grouped into Map with values in column 'client_ID' as keys - values will be
    // Map with every row values, were key - csv title value and value is line specific value
    @Override
    public String[] getKeysFileB() {
        return new String[]{"client_ID"};
    }

    // Key value for cvd map representation can be multivalued - file A separator to join values can be specified here
    @Override
    public String getKeySeparatorFileA() {
        return "_";
    }

    // Key value for cvd map representation can be multivalued - file B separator to join values can be specified here
    @Override
    public String getKeySeparatorFileB() {
        return " ";
    }

    // Comparision rule - what need to compare in 2 files
    @Override
    public ValuesComparisionRule[] getValuesComparisionRules() {
        return new ValuesComparisionRule[]{
                // Compare value in these columns
                new ValuesComparisionRule("title", "position"),

                // Compare values with 'Name' and 'Surname' columns after joining with default separator with value from
                // another file in column 'Full name'
                new ValuesComparisionRule(new String[]{"Name", "Surname"}, "Full name"),

                // Do the same actions like in previous check, but override separator from default to ','
                new ValuesComparisionRule(new String[]{"Name", "Surname"}, "Full name")
                        .setValueExtractorA(new ValueExtractor(",")),

                // Way how to default comparison function can be overridden
                new ValuesComparisionRule(new String[]{"Name", "Surname"}, "Full name")
                        .setValidator(new TwoValuesValidator() {

                    @Override
                    public boolean equalityFunction(String valueA, String valueB) {
                        return super.equalityFunction(valueA, valueB);
                    }
                }),

                // Example of complex custom rule
                new ValuesComparisionRule("TestTest", new String[]{"test1", "test2"})
                        .setValidator(new TwoValuesValidator() {

                            @Override
                            public boolean equalityFunction(String valueA, String valueB) {
                                return super.equalityFunction(valueA, valueB);
                            }
                        }).setValueExtractorA(new ValueExtractor(".")
                ).setValueExtractorB(new ValueExtractor() {

                    @Override
                    public String extractValue(Map<String, String> row, String... keys) throws ColumnNotFoundException {

                        // How to override extractor logic
                        return super.extractValue(row, keys);
                    }
                }),

                // Example of very complex custom rule
                new ValuesComparisionRule("complex value", new String[]{"start", "middle", "end"})
                        .setValidator(new TwoValuesValidator() {

                            @Override
                            public boolean equalityFunction(String valueA, String valueB) {

                                // Any custom logic can be here
                                if (valueA.length() < 3) {
                                    return valueA.equals("__");
                                }
                                String valueAtoCompare = valueA.substring(2);
                                return super.equalityFunction(valueAtoCompare, valueB);
                            }

                        }).setValueExtractorB(new ValueExtractor(""))
        };
    }

    @Override
    public String getReportFilename() {
        return "report.csv";
    }

    // Way how to override actions of difference default behavior
    @Override
    public ActionsOnDifference getActionsOnDifference() {
        return new ActionsOnDifference() {

            @Override
            public boolean stopOnDuplicationFound() {
                return false;
            }
        };
    }

    // Way to override logic for creation Map representation. Please refer to next usage example
    // net.hor1zont.csv.comparator.utils.CsvUtilTest#testDuplicatedValuesCompoundKeys
    // Example if in one file key is next formatted ids: "1123", "22435"
    // But 2nd file has next ids: "1_1123", "1_22435"
    @Override
    public Comparator<String> getKeyComparator() {
        return String::compareTo;
    }

    // Way how to change charsets for report file if default is not correct
    @Override
    public Charset getReportFileCharset() {
        return super.getReportFileCharset();
    }

    // Way how to change charsets for file A if default is not correct
    @Override
    public Charset getCharsetFileA() {
        return super.getCharsetFileA();
    }

    // Way how to change charsets for file B if default is not correct
    @Override
    public Charset getCharsetFileB() {
        return super.getCharsetFileB();
    }

}
