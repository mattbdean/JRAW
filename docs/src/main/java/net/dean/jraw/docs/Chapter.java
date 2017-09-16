package net.dean.jraw.docs;

import org.jetbrains.annotations.Nullable;

public final class Chapter {
    private String file;
    private String title;

    public Chapter(String file, @Nullable String title) {
        this.file = file;
        this.title = title;
    }

    /** The basename of the file relative to the content directory */
    public String getFile() {
        return file;
    }

    /** The title of the file as it will appear in the sidebar */
    public String getTitle() {
        return title != null ? title : file.substring(0, 1).toUpperCase() + file.substring(1);
    }

    @Override
    public String toString() {
        return "Page{" +
            "file='" + getFile() + '\'' +
            ", title='" + getTitle() + '\'' +
            '}';
    }
}
