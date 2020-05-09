package net.hor1zont.csv.comparator.twofiles;

import java.util.Arrays;

/**
 * Object to store information about found error of difference between 2 csv files
 */
public class DetectedError {

    /**
     * Reason of failure
     */
    private String reason;

    /**
     * Files where error is found
     */
    private String fileA;
    private String fileB;

    /**
     * Folder with tests files location
     */
    private String testFolder;

    /**
     * Columns titles for values for mapping
     */
    private String[] keysColumnA;
    private String[] keysColumnB;

    /**
     * Values for comparision
     */
    private String valueA;
    private String valueB;

    /**
     * Values in title columns
     */
    private String primaryKeyA;
    private String primaryKeyB;

    public DetectedError(String reason) {
        this.reason = reason;
    }

    public DetectedError setFileA(String fileA) {
        this.fileA = fileA;
        return this;
    }

    public DetectedError setFileB(String fileB) {
        this.fileB = fileB;
        return this;
    }

    public DetectedError setTestFolder(String testFolder) {
        this.testFolder = testFolder;
        return this;
    }

    public DetectedError setTitleColumnA(String[] titleColumnA) {
        this.keysColumnA = titleColumnA;
        return this;
    }

    public DetectedError setTitleColumnB(String[] titleColumnB) {
        this.keysColumnB = titleColumnB;
        return this;
    }

    public DetectedError setValueA(String valueA) {
        this.valueA = valueA;
        return this;
    }

    public DetectedError setValueB(String valueB) {
        this.valueB = valueB;
        return this;
    }

    public DetectedError setPrimaryKeyA(String primaryKeyA) {
        this.primaryKeyA = primaryKeyA;
        return this;
    }

    public DetectedError setPrimaryKeyB(String primaryKeyB) {
        this.primaryKeyB = primaryKeyB;
        return this;
    }

    @Override
    public String toString() {
        return "reason='" + reason + '\'' +
                returnIfNotNull("primaryKeyA", primaryKeyA) +
                returnIfNotNull("primaryKeyB", primaryKeyB) +
                returnIfNotNull("valueA", valueA) +
                returnIfNotNull("valueB", valueB) +
                returnIfNotNull("titleColumnA", Arrays.toString(keysColumnA)) +
                returnIfNotNull("fileA", fileA) +
                returnIfNotNull("fileB", fileB) +
                returnIfNotNull("titleColumnB", Arrays.toString(keysColumnB)) +
                returnIfNotNull("testFolder", testFolder)
                ;

    }

    private static String returnIfNotNull(String name, String value) {
        return value != null ? String.format(", %s='%s'", name, value) : "";
    }

    public String toCSVString() {
        return reason +
                getCVSValueIfNotNull(primaryKeyA) +
                getCVSValueIfNotNull(valueA) +
                getCVSValueIfNotNull(Arrays.toString(keysColumnA)) +
                getCVSValueIfNotNull(fileA) +
                getCVSValueIfNotNull(primaryKeyB) +
                getCVSValueIfNotNull(valueB) +
                getCVSValueIfNotNull(Arrays.toString(keysColumnB)) +
                getCVSValueIfNotNull(fileB) +
                getCVSValueIfNotNull(testFolder)
                ;
    }

    public static String getCSVTitle() {
        return "reason, " +
                "primaryKeyA," +
                "valueA," +
                "titleColumnA," +
                "fileA," +
                "primaryKeyB," +
                "valueB," +
                "titleColumnB," +
                "fileB," +
                "testFolder"
                ;
    }

    private String getCVSValueIfNotNull(String value) {
        return value != null ? String.format(",%s", value) : ",";
    }
}

