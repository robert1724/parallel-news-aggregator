
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.concurrent.atomic.AtomicIntegerArray;

public class ArticleWorker implements Runnable {

    int id, start, end, threads;
    List<String> uuids, titles, authors, urls, texts, publisheds, languages;
    List<List<String>> categories;

    Set<String> validCategories, validLanguages, validWords;

    public ArticleWorker(int threads, int id, int start, int end, List<String> uuids, List<String> titles, List<String> authors, List<String> urls, List<String> texts, List<String> publisheds, List<String> languages, List<List<String>> categories, Set<String> validCategories, Set<String> validLanguages, Set<String> validWords) {
        this.threads = threads;
        this.id = id;
        this.start = start;
        this.end = end;
        this.uuids = uuids;
        this.titles = titles;
        this.authors = authors;
        this.urls = urls;
        this.texts = texts;
        this.publisheds=publisheds;
        this.languages = languages;
        this.categories=categories;
        this.validCategories = validCategories;
        this.validLanguages = validLanguages;
        this.validWords = validWords;
    }

    @Override
    public void run() {

        Functions.duplicate_rmv(id, start, end, uuids, titles);

        try { Tema1.barrier.await(); } catch (Exception ignored) {}

        Functions.form_final_indexes(id, start, end, uuids, titles);

        try { Tema1.barrier.await(); } catch (Exception ignored) {}

        Functions.sort_final_indexes(threads, id, start, end, publisheds, uuids);

        try { Tema1.barrier.await(); } catch (Exception ignored) {}
        
        if(id == 0)
            WriteFiles.write_all_articles(uuids, publisheds);

        try { Tema1.barrier.await(); } catch (Exception ignored) {}
        
        if (id == 0)
            WriteFiles.write_categories(validCategories, categories, uuids);

        try { Tema1.barrier.await(); } catch (Exception ignored) {}
        
        if (id == 0)
            WriteFiles.write_languages(validLanguages, languages, uuids);

        try { Tema1.barrier.await(); } catch (Exception ignored) {}

        Functions.keyword_count(id, start, end, texts, languages, validWords);

        try { Tema1.barrier.await(); } catch (Exception ignored) {}

        WriteFiles.write_report(id, start, end, authors, uuids, languages, publisheds, urls, categories);
    }
}
