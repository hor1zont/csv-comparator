package net.hor1zont.csv.comparator.driver;

import net.hor1zont.csv.comparator.twofiles.DetectedError;
import net.hor1zont.csv.comparator.twofiles.TwoFilesComparisonActions;
import net.hor1zont.csv.comparator.twofiles.actions.ComparisonActionExample;
import net.hor1zont.csv.comparator.utils.CsvUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Main class o run this util to compare 2 CSV files with specified rules
 */
public class Runner {

    private static final Logger LOG = LogManager.getLogger(Runner.class);

    public static void main(String[] args) {
        TwoFilesComparisonActions twoFilesComparisonActions = testToRun();
        List<DetectedError> detectedErrors = new ArrayList<>();
        List<DetectedError> initDetectedErrors = twoFilesComparisonActions.initComparision(baseFolder());
        detectedErrors.addAll(initDetectedErrors);

        if (detectedErrors.size() > 1 && twoFilesComparisonActions.getActionsOnDifference().stopOnDuplicationFound()) {

            // There are keys duplication in target files. Next process can be incorrect. Stop progress - files should be
            // checked before next run. This can be disabled be overriding stopOnDuplicationFound method in ActionsOnDifference class.
            // Object of this class is available in TwoFilesComparisonActions object.
            reportResults(twoFilesComparisonActions, detectedErrors);
            throw new IllegalStateException("Duplication is found: stop progress. Check logs of report file for more details");
        }
        List<DetectedError> comparisonDetectedErrors = twoFilesComparisonActions.checkFiles();
        detectedErrors.addAll(comparisonDetectedErrors);
        if (detectedErrors.size() == 0) {
            LOG.info("No differences were found");
            // OK - no errors
            System.exit(0);
        } else {
            reportResults(twoFilesComparisonActions, detectedErrors);
        }
    }

    private static void reportResults(TwoFilesComparisonActions twoFilesComparisonActions, List<DetectedError> detectedErrors) {
        LOG.warn(String.format("%s differences were found:", detectedErrors.size()));
        for (int i = 0; i < detectedErrors.size(); i++) {
            LOG.info(i + 1 + ": " + detectedErrors.get(i).toString());
        }
        CsvUtil.writeDetectedErrorsToCsvFile(
                baseFolder() + twoFilesComparisonActions.getTestFolder() + twoFilesComparisonActions.getReportFilename(),
                detectedErrors,
                twoFilesComparisonActions.getReportFileCharset()
        );
    }

    /**
     * Here returned class should be changed to change test
     */
    private static TwoFilesComparisonActions testToRun() {
        return new ComparisonActionExample();

    }

    /**
     * @return base folder where all files for comparisons are located
     */
    private static String baseFolder() {
        return "";
    }
}
