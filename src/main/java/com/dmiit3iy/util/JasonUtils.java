package com.dmiit3iy.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SequenceWriter;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class JasonUtils {
    /**
     * Метод для записи объектов в JASON файл
     * @param fileName
     * @param a
     * @param <T>
     * @throws IOException
     */
    public static <T> void write(String fileName, T a) throws IOException {
        File file = new File(fileName);
        ObjectMapper objectMapper = JsonMapper.builder().addModule(new JavaTimeModule()).build();
        if (!file.exists() || file.length() == 0) {
            SequenceWriter sequenceWriter = objectMapper.writer().writeValuesAsArray(file);
            sequenceWriter.write(a);
            sequenceWriter.close();
        } else {
            ArrayList<T> arrayList = objectMapper.readValue(file, new TypeReference<ArrayList<T>>() {});
            arrayList.add(a);
            objectMapper.writeValue(file, arrayList);
        }
    }

    public static <T> ArrayList<T> read(String fileName) throws IOException {
        File file = new File(fileName);
        ObjectMapper objectMapper = JsonMapper.builder().addModule(new JavaTimeModule()).build();
        if (!file.exists() || file.length() == 0) {
            return null;
        } else {
            ArrayList<T> arrayList = objectMapper.readValue(file, new TypeReference<ArrayList<T>>() {});
           return arrayList;
        }

    }
}
