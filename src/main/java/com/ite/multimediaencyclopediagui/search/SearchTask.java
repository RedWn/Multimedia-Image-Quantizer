package com.ite.multimediaencyclopediagui.search;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Callable;

public class SearchTask implements Callable {
    private String directory;
    private boolean byColor;
    private boolean byDate;
    private boolean bySize;

    public SearchTask(String directory,
                      boolean byColor,
                      boolean byDate,
                      boolean bySize) {
        this.directory = directory;
        this.byColor = byColor;
        this.bySize = bySize;
        this.byDate = byDate;

    }

    public File[] call() throws IOException {
        return Searcher.Search(directory, byColor, byDate, bySize);
    }
}
