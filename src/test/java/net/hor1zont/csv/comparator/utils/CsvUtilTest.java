package net.hor1zont.csv.comparator.utils;

import net.hor1zont.csv.comparator.twofiles.DetectedError;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.NoSuchFileException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RunWith(MockitoJUnitRunner.class)
public class CsvUtilTest {

    @Spy
    private CsvUtil reader;
    private List<DetectedError> detectedErrors = new ArrayList<>();

    @Before
    public void setup() throws IOException {
        mockCsvUtil(
                reader,
                getClass()
        );
    }

    @Test
    public void testBasicPositive() {
        Map<String, Map<String, String>> result = reader.convertFileToFormattedMap(
                "correct-formatted-file.csv",
                new String[]{"id"},
                " ",
                null,
                null,
                detectedErrors
        );

        // Verification
        Assert.assertEquals(4, result.size());
        Assert.assertEquals(0, detectedErrors.size());
        checkRow(result.get("11"), "Aaa", "12341", "simple");
        checkRow(result.get("221"), "Bbbb", "111111", "great");
        checkRow(result.get("2232133221"), "a", "333333", "small");
        checkRow(result.get("3"), "hero", "1", "big");
    }

    @Test
    public void testMultiKey() {
        Map<String, Map<String, String>> result = reader.convertFileToFormattedMap(
                "correct-formatted-file.csv",
                new String[]{"id", "name"},
                " ",
                null,
                null,
                detectedErrors
        );

        // Verification
        Assert.assertEquals(0, detectedErrors.size());
        Assert.assertEquals(4, result.size());
        checkRow(result.get("11 Aaa"), "Aaa", "12341", "simple");
        checkRow(result.get("221 Bbbb"), "Bbbb", "111111", "great");
        checkRow(result.get("2232133221 a"), "a", "333333", "small");
        checkRow(result.get("3 hero"), "hero", "1", "big");
    }

    @Test
    public void testMultiKeySeparator() {
        Map<String, Map<String, String>> result = reader.convertFileToFormattedMap(
                "correct-formatted-file.csv",
                new String[]{"id", "name"},
                "|",
                null,
                null,
                detectedErrors
        );

        // Verification
        Assert.assertEquals(4, result.size());
        Assert.assertEquals(0, detectedErrors.size());
        checkRow(result.get("11|Aaa"), "Aaa", "12341", "simple");
        checkRow(result.get("221|Bbbb"), "Bbbb", "111111", "great");
        checkRow(result.get("2232133221|a"), "a", "333333", "small");
        checkRow(result.get("3|hero"), "hero", "1", "big");
    }

    @Test
    public void testDuplicatedValues() {
        Map<String, Map<String, String>> result = reader.convertFileToFormattedMap(
                "duplicated-lines-file.csv",
                new String[]{"id"},
                " ",
                null,
                null,
                detectedErrors
        );

        // Verification
        Assert.assertEquals(2, result.size());
        checkRow(result.get("221"), "Bbbb", "111111", "great");
        checkRow(result.get("3"), "hero", "1", "big");


        checkDetectedError(
                "Duplication is found: value is ignored,,[11],[id],duplicated-lines-file.csv,,,,,",
                0, 1
        );
        checkDetectedError(
                "Duplication is found: value is ignored,,[2232133221],[id],duplicated-lines-file.csv,,,,,",
                2, 3
        );
    }

    @Test
    public void testDuplicatedValuesMultiKeys() {
        Map<String, Map<String, String>> result = reader.convertFileToFormattedMap(
                "duplicated-lines-file.csv",
                new String[]{"id", "name"},
                " ",
                null,
                null,
                detectedErrors
        );

        // Verification
        Assert.assertEquals(4, result.size());
        checkRow(result.get("221 Bbbb"), "Bbbb", "111111", "great");
        checkRow(result.get("3 hero"), "hero", "1", "big");

        Assert.assertEquals(2, detectedErrors.size());
        checkDetectedError(
                "Duplication is found: value is ignored,,[11, Aaa],[id, name],duplicated-lines-file.csv,,,,,",
                0, 1
        );
    }


    @Test
    public void testDuplicatedValuesCompoundKeys() {
        Map<String, Map<String, String>> result = reader.convertFileToFormattedMap(
                "compound-keys-file.csv",
                new String[]{"id"},
                " ",
                // Upgrade comparator to use only first part for key value if value is separated by '_'
                (o1, o2) -> {
                    String[] key1Parts = o1.split("_");
                    String[] key2Parts = o2.split("_");
                    return key1Parts[0].compareTo(key2Parts[0]);
                },
                null,
                detectedErrors
        );

        // Verification
        Assert.assertEquals(2, result.size());
        checkRow(result.get("11_03"), "Cccc", "432412341", null);
        checkRow(result.get("221"), "Zzz", "111111", null);

        Assert.assertEquals(6, detectedErrors.size());
        checkDetectedError(
                "Duplication is found: value is ignored,,[11_021],[id],compound-keys-file.csv,,,,,",
                0
        );
        checkDetectedError(
                "Duplication is found: value is ignored,,[11_01],[id],compound-keys-file.csv,,,,,",
                1
        );
        checkDetectedError(
                "Duplication is found: value is ignored,,[12_01],[id],compound-keys-file.csv,,,,,",
                2
        );
        checkDetectedError(
                "Duplication is found: value is ignored,,[12],[id],compound-keys-file.csv,,,,,",
                3
        );
        checkDetectedError(
                "Duplication is found: value is ignored,,[12_],[id],compound-keys-file.csv,,,,,",
                4
        );
        checkDetectedError(
                "Duplication is found: value is ignored,,[12_1],[id],compound-keys-file.csv,,,,,",
                5
        );
    }

    public static void mockCsvUtil(
            CsvUtil utilToMock,
            Class currentClass
    ) throws IOException {
        Mockito.doAnswer(invocationOnMock -> {
            String testFilePath = invocationOnMock.getArguments()[0].toString();
            InputStream testResourceStream = currentClass.getClassLoader().getResourceAsStream(testFilePath);
            if (testResourceStream == null) {
                throw new NoSuchFileException(testFilePath);
            }
            return new BufferedReader(new InputStreamReader(testResourceStream));
        }).when(utilToMock).getFileInputStream(Matchers.any(), Matchers.any());
    }


    private void checkDetectedError(String csvRepresentation, int... indexes) {
        Arrays.stream(indexes).forEach(index -> {
            DetectedError detectedError = detectedErrors.get(index);
            Assert.assertEquals(
                    csvRepresentation,
                    detectedError.toCSVString()
            );
        });
    }

    private void checkRow(Map<String, String> row, String name, String pas, String object) {
        Assert.assertNotNull(row);
        Assert.assertEquals(name, row.get("name"));
        Assert.assertEquals(pas, row.get("pas"));
        if (object != null) {
            Assert.assertEquals(object, row.get("object"));
        }
    }

}