package net.hor1zont.csv.comparator.twofiles;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import net.hor1zont.csv.comparator.twofiles.actions.ComparisonActionExample;
import net.hor1zont.csv.comparator.utils.CsvUtil;

/**
 * Class-interface to configure comparision rule/actions. To compare 2 files should be created new class which extends
 * this class. See {@link ComparisonActionExample} for usage examples
 */
public abstract class TwoFilesComparisonActions {

    private Map<String, Map<String, String>> fileA;
    private Map<String, Map<String, String>> fileB;

    private CsvUtil csvUtil = new CsvUtil();

    private String pathFileA = getNameFileA();
    private String pathFileB = getNameFileB();
    private String[] keysFileA = getKeysFileA();
    private String[] keysFileB = getKeysFileB();

    /**
     * Should be called before to init tests before comparision
     */
    public List<DetectedError> initComparision(String baseFolder) {
        List<DetectedError> detectedErrors = new ArrayList<>();
        fileA = csvUtil.convertFileToFormattedMap(
                baseFolder + getTestFolder() + pathFileA,
                keysFileA,
                getKeySeparatorFileA(),
                getKeyComparator(),
                getCharsetFileA(),
                detectedErrors
        );
        fileB = csvUtil.convertFileToFormattedMap(
                baseFolder + getTestFolder() + pathFileB,
                keysFileB,
                getKeySeparatorFileB(),
                getKeyComparator(),
                getCharsetFileB(),
                detectedErrors
        );
        return detectedErrors;
    }

    /**
     * Compare files
     */
    public List<DetectedError> checkFiles() {
        FilesComparator filesComparator = getFilesComparator();
        filesComparator.init(
                pathFileA,
                pathFileB,
                keysFileA,
                keysFileB,
                getTestFolder(),
                fileA,
                fileB
        );
        return filesComparator.compare(
                getValuesComparisionRules()
        );
    }

    /**
     * Can be used to override some actions of difference logic
     */
    public ActionsOnDifference getActionsOnDifference() {
        return new ActionsOnDifference();
    }

    /**
     * Can be used to set specific comparator to change default logic
     */
    public FilesComparator getFilesComparator() {
        return new FilesComparator(getActionsOnDifference());
    }


    public Comparator<String> getKeyComparator() {
        return null;
    }

    /**
     * @return charset to read file A, {@link CsvUtil#DEFAULT_FILE_CHARSET} is used is no charset is set
     */
    public Charset getCharsetFileA() {
        return null;
    }

    /**
     * @return charset to read file B, {@link CsvUtil#DEFAULT_FILE_CHARSET} is used is no charset is set
     */
    public Charset getCharsetFileB() {
        return null;
    }

    /**
     * @return charset to write report file, {@link CsvUtil#DEFAULT_FILE_CHARSET} is used is no charset is set
     */
    public Charset getReportFileCharset() {
        return null;
    }

    /**
     * name of column with primary key value of file A
     */
    public String getKeySeparatorFileA() {
        return "";
    }

    /**
     * name of column with primary key value of file B
     */
    public String getKeySeparatorFileB() {
        return "";
    }

    /**
     * name of file A
     */
    public abstract String getNameFileA();

    /**
     * name of file B
     */
    public abstract String getNameFileB();

    /**
     * path to files location
     */
    public abstract String getTestFolder();

    /**
     * Names of columns with primary key value of file A
     */
    public abstract String[] getKeysFileA();

    /**
     * Names of columns with primary key value of file B
     */
    public abstract String[] getKeysFileB();

    /**
     * Rules to compare 2 files
     */
    public abstract ValuesComparisionRule[] getValuesComparisionRules();

    /**
     * @return name of csv report file (contains comparision results)
     */
    public abstract String getReportFilename();

}
