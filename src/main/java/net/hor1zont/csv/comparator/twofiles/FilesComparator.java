package net.hor1zont.csv.comparator.twofiles;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Class with logic to compare 2 csv files with logic in comparison rules
 */
public class FilesComparator {

    private final ActionsOnDifference actionsOnDifference;

    private Map<String, Map<String, String>> fileA;
    private Map<String, Map<String, String>> fileB;

    public FilesComparator(ActionsOnDifference actionsOnDifference) {
        this.actionsOnDifference = actionsOnDifference;
    }

    /**
     * Should be called before compare method
     */
    void init(
            String pathFileA,
            String pathFileB,
            String[] keysFileA,
            String[] keysFileB,
            String testFolder,
            Map<String, Map<String, String>> fileA,
            Map<String, Map<String, String>> fileB
    ) {
        this.fileA = fileA;
        this.fileB = fileB;
        actionsOnDifference.init(
                pathFileA,
                pathFileB,
                keysFileA,
                keysFileB,
                testFolder
        );
    }

    /**
     * Main logic to compare files
     */
    public List<DetectedError> compare(
            ValuesComparisionRule[] valuesComparisionRules
    ) {
        List<DetectedError> detectedErrors = new ArrayList<>();

        // check every line from file A
        for (Map.Entry<String, Map<String, String>> rowAWithKey : fileA.entrySet()) {

            // find row with the same primary key from file B
            String primaryKey = rowAWithKey.getKey();
            Map<String, String> rowA = rowAWithKey.getValue();
            Map<String, String> rowB = fileB.get(primaryKey);
            if (rowB == null) {
                DetectedError detectedError = new DetectedError("primary key is not found in file B");
                detectedError.setPrimaryKeyA(primaryKey);
                detectedErrors.add(detectedError);
                actionsOnDifference.elementIsNotFoundByPrimaryKey(detectedError);
            } else {

                // Checks are possible if line is found
                // Check all configured rules
                for (ValuesComparisionRule valuesComparisionRule : valuesComparisionRules) {
                    try {
                        String[] keysA = valuesComparisionRule.getKeysA();
                        String[] keysB = valuesComparisionRule.getKeysB();
                        // pass object where we can call method get value
                        valuesComparisionRule.init(rowA, rowB);
                        String valueA = valuesComparisionRule.extractValueA();
                        String valueB = valuesComparisionRule.extractValueB();
                        if (!valuesComparisionRule.compareValues(valueA, valueB)) {
                            DetectedError detectedError = new DetectedError("values are not the expected");
                            actionsOnDifference.valuesAreNotEqual(
                                    detectedError,
                                    primaryKey,
                                    keysA,
                                    keysB,
                                    valueA,
                                    valueB
                            );
                            detectedErrors.add(detectedError);
                        }
                    } catch (ColumnNotFoundException ex) {
                        DetectedError detectedError = new DetectedError("column is not found");
                        actionsOnDifference.columnIsNotFound(detectedError, ex);
                        detectedErrors.add(detectedError);
                    }
                }
            }
        }

        // check that all records in file B are present in file A
        for (Map.Entry<String, Map<String, String>> rowBWithKey : fileB.entrySet()) {
            String primaryKey = rowBWithKey.getKey();
            Map<String, String> rowA = fileA.get(primaryKey);
            if (rowA == null) {
                DetectedError detectedError = new DetectedError("primary key is not found in file A");
                detectedError.setPrimaryKeyB(primaryKey);
                detectedErrors.add(detectedError);
                actionsOnDifference.elementIsNotFoundByPrimaryKey(detectedError);
            }
        }
        return detectedErrors;
    }
}

