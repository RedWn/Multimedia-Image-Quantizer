package com.ite.multimediaencyclopediagui.search;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Callable;

public class SearchTask implements Callable {
    private String directory;
    private File fileToSearchFor;

    public SearchTask(String directory) {
        this.directory = directory;
    }

    public File[] call() throws IOException {
        return Searcher.Search(directory, true, true, true);
    }
}
