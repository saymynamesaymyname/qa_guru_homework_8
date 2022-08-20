package tests;

import com.codeborne.pdftest.PDF;
import com.codeborne.xlstest.XLS;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVReader;
import domain.Cat;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;

public class ParseFilesTests {

    ClassLoader classLoader = ParseFilesTests.class.getClassLoader();
    String archiveName = "test.zip";
    String xlsFileName = "example.xls";
    String pdfFileName = "sample.pdf";
    String csvFileName = "sample.csv";

    String jsonFileName = "cat.json";

    @Test
    void  parseXlsFileTest() throws Exception {
        InputStream xlsFileStream = getFile(archiveName, xlsFileName);
        XLS xls = new XLS(xlsFileStream);
        assertThat(xls.excel
                .getSheetAt(0)
                .getRow(11)
                .getCell(4)
                .getStringCellValue()).contains("Great Britain");
        closeInputStream(xlsFileStream);
    }

    @Test
    void parsePdfTest() throws Exception {
        InputStream pdfFileStream = getFile(archiveName, pdfFileName);
        PDF pdf = new PDF(pdfFileStream);
        assertThat(pdf.numberOfPages).isEqualTo(2);
        closeInputStream(pdfFileStream);
    }

    @Test
    void parseCsvTest() throws Exception {
        InputStream csvFileStream = getFile(archiveName, csvFileName);
        CSVReader csvReader = new CSVReader(new InputStreamReader(csvFileStream, UTF_8));
        List<String[]> csv = csvReader.readAll();
        assertThat(csv).contains(
                new String[] {"Jack","McGinnis","220 hobo Av.","Phila"," PA","09119"}
                );
        closeInputStream(csvFileStream);
    }

    @Test
    void parseJsonTest() throws Exception {
        InputStream jsonFileStream = classLoader.getResourceAsStream(jsonFileName);
        ObjectMapper objectMapper = new ObjectMapper();
        Cat cat = objectMapper.readValue(jsonFileStream,Cat.class);
        assertThat(cat.name).isEqualTo("Ryzhik");
        assertThat(cat.vaccination.date).isEqualTo("25.11.2021");
        assertThat(cat.colors).contains("white", "red");
        closeInputStream(jsonFileStream);
    }

    private InputStream getFile(String archiveName, String fileName) throws Exception {
        URL zipUrl = classLoader.getResource(archiveName);
        File zipFile = new File(zipUrl.toURI());
        ZipFile zip = new ZipFile(zipFile);
        Enumeration<? extends ZipEntry> e = zip.entries();
        return zip.getInputStream(zip.getEntry(fileName));
    }

    private void closeInputStream (InputStream inputStream) throws IOException {
        if(inputStream!=null)
            inputStream.close();

    }
}
