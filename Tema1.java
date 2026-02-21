import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicIntegerArray;

public class Tema1 {

    public static CyclicBarrier barrier;
    public static List<Integer> finalIndexes = new ArrayList<>();
    public static ArticleWorker[] workersObj;
    public static ConcurrentHashMap<String, Integer> uuid_apps = new ConcurrentHashMap<>(); 
    public static ConcurrentHashMap<String, Integer> title_apps = new ConcurrentHashMap<>();
    public static ConcurrentHashMap<String, Integer> word_apps = new ConcurrentHashMap<>();
    public static ConcurrentHashMap<String, Integer> author_apps = new ConcurrentHashMap<>();
    public static ConcurrentHashMap<String, Integer> language_apps = new ConcurrentHashMap<>();
    public static ConcurrentHashMap<String, String> published_hash = new ConcurrentHashMap<>();
    public static ConcurrentHashMap<String, Integer> categories_apps = new ConcurrentHashMap<>();
    public static List<Integer> valid_articles = Collections.synchronizedList(new ArrayList<>());
    public static Set<Integer> valid_articles_set = Collections.synchronizedSet(new HashSet<>());
    public static List<List<Integer>> sorted_parts = new ArrayList<>();
    public static List<Map<String, Integer>> words_map;

    public static void main(String[] args) {

        if(args.length != 3) { 
            System.err.println("Inputul trebuie sa fie de forma: 'java Tema1 <num_threads> <articles.txt> <inputs.txt>' ");
            System.exit(1);
        }
        
        int threads = 0;

        if (Integer.parseInt(args[0]) < 1){
            System.err.println("Trebuie minim 1");
            System.exit(1);
        }
        else {
            threads = Integer.parseInt(args[0]);
        }

        String articles_path = args[1];
        String extras_path = args[2];

        Set<String> validLanguages, validCategories, validWords;
        String languagesPath = null;
        String categoriesPath = null;
        String wordsPath = null;

        try (BufferedReader br = new BufferedReader(new FileReader(extras_path))) {

            int count = Integer.parseInt(br.readLine().trim());

            File extrasFile = new File(extras_path).getAbsoluteFile();
            File extrasDir = extrasFile.getParentFile();

            String languagesLine = br.readLine().trim();
            String categoriesLine = br.readLine().trim();
            String wordsLine = br.readLine().trim();

            File languagesFile = new File(languagesLine);
            if (!languagesFile.isAbsolute())
                languagesFile = new File(extrasDir, languagesLine);

            File categoriesFile = new File(categoriesLine);
            if (!categoriesFile.isAbsolute())
                categoriesFile = new File(extrasDir, categoriesLine);

            File wordsFile = new File(wordsLine);
            if (!wordsFile.isAbsolute())
                wordsFile = new File(extrasDir, wordsLine);

            languagesPath = languagesFile.getAbsolutePath();
            categoriesPath = categoriesFile.getAbsolutePath();
            wordsPath = wordsFile.getAbsolutePath();
        } catch(Exception e) {
            System.exit(1);
        }

        validLanguages = new HashSet<>(InputParser.parseLanguages(languagesPath));
        validCategories = new HashSet<>(InputParser.parseCategories(categoriesPath));
        validWords = new HashSet<>(InputParser.parseWords(wordsPath));

        List<String> uuid_arr = new ArrayList<>();
        List<String> title_arr = new ArrayList<>();
        List<String> authors_arr = new ArrayList<>();
        List<String> url_arr = new ArrayList<>();
        List<String> text_arr = new ArrayList<>();
        List<String> published_arr = new ArrayList<>();
        List<String> language_arr = new ArrayList<>();
        List<List<String>> categories_arr = new ArrayList<>();

        ArticlesParser.parse_articles(articles_path, uuid_arr, title_arr, authors_arr, url_arr, text_arr, published_arr, language_arr, categories_arr);

        int articles_number = uuid_arr.size();
        int thread_part = articles_number / threads;

        Thread[] workers = new Thread[threads];
        Tema1.workersObj = new ArticleWorker[threads];
        words_map = new ArrayList<>(threads);
        
        for (int i = 0; i < threads; i++) {
            words_map.add(new HashMap<>());
        }

        for(int k = 0; k < threads; k++)
            sorted_parts.add(new ArrayList<>());

        barrier = new CyclicBarrier(threads);

        for(int i = 0; i < threads; i++) {
            int start = i * thread_part;
            int end;
            if(i == threads - 1) {
                end = articles_number;
            } else {
                end = start + thread_part;
            }

            workersObj[i] = new ArticleWorker(threads, i, start, end, uuid_arr, title_arr, authors_arr, url_arr, text_arr, published_arr, language_arr, categories_arr, validCategories, validLanguages, validWords);

            workers[i] = new Thread(Tema1.workersObj[i]);
            workers[i].start();
        }

        for (Thread t : workers) {
            try {
                t.join(); 
            } catch (InterruptedException e) {}
        }
    }
}