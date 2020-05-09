package net.hor1zont.csv.comparator.twofiles;

/**
 * Class to control logic on found differences. Class instance can be overridden in {@link ActionsOnDifference}
 */
public class ActionsOnDifference {

    private String pathFileA;
    private String pathFileB;
    private String[] keysFileA;
    private String[] keysFileB;
    private String testFolder;

    void init(
            String pathFileA,
            String pathFileB,
            String[] keyFileA,
            String[] keyFileB,
            String testFolder
    ) {
        this.pathFileA = pathFileA;
        this.pathFileB = pathFileB;
        this.keysFileA = keyFileA;
        this.keysFileB = keyFileB;
        this.testFolder = testFolder;
    }

    public void elementIsNotFoundByPrimaryKey(
            DetectedError detectedError
    ) {
        detectedError.setFileA(pathFileA)
                .setFileB(pathFileB)
                .setTestFolder(testFolder)
                .setTitleColumnA(keysFileA)
                .setTitleColumnB(keysFileB);
    }

    public void valuesAreNotEqual(
            DetectedError detectedError,
            String primaryKey,
            String[] keysA,
            String[] keysB,
            String valueA,
            String valueB
    ) {
        detectedError.setTitleColumnA(keysA)
                .setTitleColumnB(keysB)
                .setPrimaryKeyA(primaryKey)
                .setPrimaryKeyB(primaryKey)
                .setFileA(pathFileA)
                .setFileB(pathFileB)
                .setTestFolder(testFolder)
                .setValueA(valueA)
                .setValueB(valueB);
    }

    public void columnIsNotFound(
            DetectedError detectedError,
            ColumnNotFoundException ex
    ) {
        // Ignore detected error by default and throw exception - column is not found - nothing to compare,
        // looks like inbound file of column name is incorrect
        throw new IllegalStateException(ex);
    }

    public boolean stopOnDuplicationFound() {
        return false;

    }
}
