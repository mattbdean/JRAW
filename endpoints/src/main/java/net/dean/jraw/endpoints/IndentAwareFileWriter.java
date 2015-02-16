package net.dean.jraw.endpoints;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * This class provides a way to write to a file that uses indentation
 */
public class IndentAwareFileWriter {
    private static final String LINE_SEP = System.getProperty("line.separator");
    private final BufferedWriter bw;
    private final String baseIndent;
    private int indent;
    private Map<Integer, String> cachedIndentation;

    public IndentAwareFileWriter(File f, int spacesPerIndent) throws IOException {
        this.bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f), StandardCharsets.UTF_8));
        this.cachedIndentation = new HashMap<>();

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < spacesPerIndent; i++) {
            sb.append(' ');
        }

        this.baseIndent = sb.toString();
        checkLevel(0); // Start the first level
    }

    public void incIndent() {
        checkLevel(++indent);
    }

    public void decIndent() {
        if (--indent < 0) {
            indent = 0;
        }
        checkLevel(indent);
    }

    public void setIndent(int indent) {
        if (indent < 0) {
            indent = 0;
        }
        checkLevel(this.indent = indent);
    }

    private void checkLevel(int indentLevel) {
        if (!cachedIndentation.containsKey(indentLevel)) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < indentLevel; i++) {
                sb.append(baseIndent);
            }

            cachedIndentation.put(indentLevel, sb.toString());
        }
    }

    public void write(String line) throws IOException {
        bw.write(cachedIndentation.get(indent));
        bw.write(line);
    }

    public void writeLine(String line) throws IOException {
        write(line);
        bw.write(LINE_SEP);
    }

    public void newline() throws IOException {
        bw.write(LINE_SEP);
    }

    public void close() throws IOException {
        bw.close();
    }
}
