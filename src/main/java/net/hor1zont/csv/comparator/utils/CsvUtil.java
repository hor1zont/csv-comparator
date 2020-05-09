package net.hor1zont.csv.comparator.utils;

import net.hor1zont.csv.comparator.twofiles.DetectedError;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.TreeMap;

public class CsvUtil {

    public static final Charset DEFAULT_FILE_CHARSET = StandardCharsets.ISO_8859_1;

    /**
     * Read CVS file and convert it into Map representation with keys for every row - values in {@code keyElementNames}
     * columns. If there is more the one value in {@code keyElementNames} then multivalued key will be used separated
     * by {@code keyElementNamesSeparator}. If key value should be parsed in some specific way (e.g. only one part of
     * the key should be used) this this logic can be set it {@code keyComparator}.
     *
     * @param fileName full path to target file for reading
     * @param keyElementNames column names which values should be used in every row for Map's key
     * @param keyElementNamesSeparator separator/delimiter which used only for multi valued keyElementNames
     * @param keyComparator comparator for Map to specify rules if necessary how to compare objects in the Map
     * @param fileCharset file charset to read file correctly (default is {@link CsvUtil#DEFAULT_FILE_CHARSET})
     * @param detectedErrors object to record not critical error for next analyze
     * @return Map representation of target file
     */
    public Map<String, Map<String, String>> convertFileToFormattedMap(
            @NotNull String fileName,
            @NotNull String[] keyElementNames,
            @NotNull String keyElementNamesSeparator,
            @Nullable Comparator<String> keyComparator,
            @Nullable Charset fileCharset,
            @NotNull List<DetectedError> detectedErrors
    ) {
        Path pathToFile = Paths.get(fileName);
        Map<String, Map<String, String>> fileMapRepresentation;
        if (keyComparator == null) {
            fileMapRepresentation = new TreeMap<>();
        } else {
            fileMapRepresentation = new TreeMap<>(keyComparator);
        }
        try {
            Reader inputStream = getFileInputStream(pathToFile, fileCharset);
            Iterable<CSVRecord> lines = CSVFormat.EXCEL.withFirstRecordAsHeader().parse(inputStream);
            for (CSVRecord line : lines) {
                StringJoiner keyForLine = new StringJoiner(keyElementNamesSeparator);
                for (String key : keyElementNames) {
                    keyForLine.add(line.get(key));
                }
                Map<String, String> previousRaw = fileMapRepresentation.put(keyForLine.toString(), line.toMap());

                // Ignore duplications and store info to display in report
                if (previousRaw != null) {
                    fillDetectedError(fileName, keyElementNames, detectedErrors, line.toMap());
                    fillDetectedError(fileName, keyElementNames, detectedErrors, previousRaw);
                    fileMapRepresentation.remove(keyForLine.toString());
                }
            }
        } catch (NoSuchFileException e) {
            throw new IllegalArgumentException(String.format("File [%s] is not found", pathToFile));
        } catch (IOException e) {
            throw new IllegalArgumentException(String.format("Error with reading file [%s]. Error details: %s",
                    pathToFile, e.getMessage()), e
            );
        }

        return fileMapRepresentation;
    }

    /**
     * Write detected errors into file
     *
     * @param fileName full file name to write report
     * @param detectedErrors list of detected errors
     * @param fileCharset file charset to read file correctly (default is {@link CsvUtil#DEFAULT_FILE_CHARSET})
     */
    public static void writeDetectedErrorsToCsvFile(
            String fileName,
            List<DetectedError> detectedErrors,
            @Nullable Charset fileCharset
    ) {
        Path pathToFile = Paths.get(fileName);
        try (BufferedWriter bw = Files.newBufferedWriter(pathToFile, fileCharset == null ? DEFAULT_FILE_CHARSET : fileCharset)) {
            bw.write("#," + DetectedError.getCSVTitle());
            bw.newLine();
            for (int i = 0; i < detectedErrors.size(); i++) {
                bw.write(i + 1 + "," + detectedErrors.get(i).toCSVString());
                bw.newLine();
            }
        } catch (IOException ex) {
            throw new IllegalStateException(String.format("Fail to write report into file [%s]", fileName), ex);
        }
    }

    private void fillDetectedError(
            @NotNull String fileName,
            @NotNull String[] keyElementNames,
            @NotNull List<DetectedError> detectedErrors,
            Map<String, String> previousRaw
    ) {
        DetectedError detectedError = new DetectedError("Duplication is found: value is ignored");
        detectedError.setFileA(fileName);
        detectedError.setTitleColumnA(keyElementNames);
        detectedError.setValueA(extractColumnsForKey(previousRaw, keyElementNames).toString());
        detectedErrors.add(detectedError);
    }

    private static List<String> extractColumnsForKey(Map<String, String> row, String[] columns) {
        List<String> columnsForKey = new ArrayList<>();
        Arrays.stream(columns).forEach(column -> columnsForKey.add(row.get(column)));
        return columnsForKey;
    }

    Reader getFileInputStream(Path pathToFile, Charset fileCharset) throws IOException {
        return Files.newBufferedReader(pathToFile, fileCharset == null ? DEFAULT_FILE_CHARSET : fileCharset);
    }
}
