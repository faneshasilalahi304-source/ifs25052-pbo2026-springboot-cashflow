package org.delcom.usernamepbo2026springbootcashflow;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Pemuat sederhana file .env ke System properties.
 * Nilai yang sudah ditentukan lewat environment variable atau -D tidak ditimpa.
 */
public final class DotEnv {

    private DotEnv() {
    }

    /** Memuat file .env di direktori kerja (jika ada). */
    public static void load() {
        load(Path.of(".env"));
    }

    /** Memuat file .env dari path tertentu; diam-diam diabaikan jika tidak ada. */
    public static void load(Path path) {
        if (!Files.isRegularFile(path)) {
            return;
        }
        List<String> lines;
        try {
            lines = Files.readAllLines(path);
        } catch (IOException ex) {
            return;
        }
        for (String raw : lines) {
            String line = raw.trim();
            if (line.isEmpty() || line.startsWith("#")) {
                continue;
            }
            int separator = line.indexOf('=');
            if (separator <= 0) {
                continue;
            }
            String key = line.substring(0, separator).trim();
            String value = stripQuotes(line.substring(separator + 1).trim());
            if (System.getProperty(key) == null && System.getenv(key) == null) {
                System.setProperty(key, value);
            }
        }
    }

    private static String stripQuotes(String value) {
        if (value.length() >= 2) {
            char first = value.charAt(0);
            char last = value.charAt(value.length() - 1);
            if ((first == '"' && last == '"') || (first == '\'' && last == '\'')) {
                return value.substring(1, value.length() - 1);
            }
        }
        return value;
    }
}
