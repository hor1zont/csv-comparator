package net.hor1zont.csv.comparator.twofiles;

import net.hor1zont.csv.comparator.utils.CsvUtil;
import net.hor1zont.csv.comparator.utils.CsvUtilTest;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RunWith(MockitoJUnitRunner.class)
public class FilesComparatorTest {

    private FilesComparator filesComparator;
    private List<DetectedError> detectedErrors = new ArrayList<>();

    @Spy
    private CsvUtil csvUtil;

    @Before
    public void setup() throws IOException {
        CsvUtilTest.mockCsvUtil(
                csvUtil,
                getClass()
        );
        filesComparator = new FilesComparator(new ActionsOnDifference());
    }

    @Test
    public void testComparatorValid() {

        // Preparation
        TwoFilesComparisonActions comparisonActions = prepareTest(
                mockTwoFilesComparisonRule(
                        "fileA.csv",
                        "fileV.csv",
                        new String[]{"phone"},
                        new String[]{"Mobile Phone"},
                        new ValuesComparisionRule(new String[]{"name", "surname"}, new String[]{"Full Name"})
                                .setValueExtractorA(new ValueExtractor(".")),
                        new ValuesComparisionRule(new String[]{"title"}, new String[]{"position"})
                                .setValidator(new TwoValuesValidator() {
                                    @Override
                                    public boolean equalityFunction(String valueA, String valueB) {
                                        return super.equalityFunction(valueA.toUpperCase(), valueB);
                                    }
                                })
                )
        );
        detectedErrors = filesComparator.compare(comparisonActions.getValuesComparisionRules());

        // Verification
        Assert.assertEquals(6, detectedErrors.size());
        checkDetectedError("values are not the expected,134123333,Alice.Jame,[name, surname],fileA.csv,134123333,Alice.James,[Full Name],fileV.csv,", 0);
        checkDetectedError("values are not the expected,23412323123,Max.Kooks,[name, surname],fileA.csv,23412323123,Max.Kook,[Full Name],fileV.csv,", 1);
        checkDetectedError("values are not the expected,23412323123,staff,[title],fileA.csv,23412323123,NULL,[position],fileV.csv,", 2);
        checkDetectedError("primary key is not found in file B,99034234234,,[phone],fileA.csv,,,[Mobile Phone],fileV.csv,", 3);
        checkDetectedError("primary key is not found in file A,,,[phone],fileA.csv,11111111111,,[Mobile Phone],fileV.csv,", 4);
        checkDetectedError("primary key is not found in file A,,,[phone],fileA.csv,99034234233,,[Mobile Phone],fileV.csv,", 5);
    }

    private void checkDetectedError(String csvRepresentation, int index) {
        DetectedError detectedError = detectedErrors.get(index);
        Assert.assertEquals(
                csvRepresentation,
                detectedError.toCSVString()
        );
    }

    @NotNull
    private TwoFilesComparisonActions prepareTest(TwoFilesComparisonActions comparisonActions) {

        List<DetectedError> detectedErrors = new ArrayList<>();

        Map<String, Map<String, String>> fileA = csvUtil.convertFileToFormattedMap(
                comparisonActions.getNameFileA(),
                comparisonActions.getKeysFileA(),
                comparisonActions.getKeySeparatorFileA(),
                comparisonActions.getKeyComparator(),
                comparisonActions.getCharsetFileA(),
                detectedErrors

        );
        Map<String, Map<String, String>> fileB = csvUtil.convertFileToFormattedMap(
                comparisonActions.getNameFileB(),
                comparisonActions.getKeysFileB(),
                comparisonActions.getKeySeparatorFileB(),
                comparisonActions.getKeyComparator(),
                comparisonActions.getCharsetFileB(),
                detectedErrors
        );

        filesComparator.init(
                comparisonActions.getNameFileA(),
                comparisonActions.getNameFileB(),
                comparisonActions.getKeysFileA(),
                comparisonActions.getKeysFileB(),
                comparisonActions.getTestFolder(),
                fileA,
                fileB
        );
        return comparisonActions;
    }

    private TwoFilesComparisonActions mockTwoFilesComparisonRule(
            String fileA,
            String fileB,
            String[] keysA,
            String[] keysB,
            ValuesComparisionRule... valuesComparisionRules

    ) {
        return new TwoFilesComparisonActions() {
            @Override
            public String getNameFileA() {
                return fileA;
            }

            @Override
            public String getNameFileB() {
                return fileB;
            }

            @Override
            public String getTestFolder() {
                return "";
            }

            @Override
            public String[] getKeysFileA() {
                return keysA;
            }

            @Override
            public String[] getKeysFileB() {
                return keysB;
            }

            @Override
            public ValuesComparisionRule[] getValuesComparisionRules() {
                return valuesComparisionRules;
            }

            @Override
            public String getReportFilename() {
                return "";
            }
        };
    }
}
