import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

public class Functions {
    
    public static void duplicate_rmv(int id, int start, int end, List<String> uuids, List<String> titles) {
        for (int i = start; i < end; i++) {
            String uuid_curent = uuids.get(i);
                
            Tema1.uuid_apps.merge(uuid_curent, 1, Integer::sum);
        
            String title_curent = titles.get(i);
            
            Tema1.title_apps.merge(title_curent, 1, Integer::sum);
            
        }
    }

    public static void form_final_indexes(int id, int start, int end, List<String> uuids, List<String> titles) {
        for(int i = start; i < end; i++) {
            Integer val_uuid = Tema1.uuid_apps.get(uuids.get(i));
            Integer val_title = Tema1.title_apps.get(titles.get(i));
            if(val_uuid != null && val_title != null) {
                if(val_uuid == 1 && val_title == 1) {
                    Tema1.valid_articles.add(i);
                    Tema1.valid_articles_set.add(i);
                }
            }
        }
    }

    public static void sort_final_indexes(int threads, int id, int start, int end, List<String> publisheds, List<String> uuids) {

        if(Tema1.valid_articles.size() < end)
            end = Tema1.valid_articles.size();

        List<Integer> valid_per_thread = new ArrayList<>();

        for (int i = start; i < end; i++) {
            valid_per_thread.add(Tema1.valid_articles.get(i));
        }

        valid_per_thread.sort(new Comparator<Integer>() {
        @Override
        public int compare(Integer a, Integer b){ 
            int cmp = publisheds.get(b).compareTo(publisheds.get(a));
            if(cmp != 0)
                return cmp;
            else {
                int cmp2 = uuids.get(a).compareTo(uuids.get(b));
                return cmp2;
            }
        }
        });

        Tema1.sorted_parts.set(id, new ArrayList<>(valid_per_thread));

        try { Tema1.barrier.await(); } catch (Exception ignored) {}

        if (id == 0) {

            List<List<Integer>> combine = new ArrayList<>();
            for (int t = 0; t < threads; t++) {
                if(Tema1.sorted_parts.get(t) != null  && !Tema1.sorted_parts.get(t).isEmpty()) {
                    combine.add(Tema1.sorted_parts.get(t));
                }
            }

            Tema1.valid_articles = Merge.mergeArrays(combine, publisheds, uuids);
        }
    }

    public static void keyword_count(int id, int start, int end, List<String> texts, List<String> languages, Set<String> validWords) {

        Map<String, Integer> map = Tema1.words_map.get(id);

        for (int i = start; i < end; i++) {

            if (!languages.get(i).equals("english"))
                continue;

            if (!Tema1.valid_articles_set.contains(i))
                continue;

            String[] words = texts.get(i).toLowerCase().split("\\s+");
            Set<String> uniq = new HashSet<>();

            for (String w : words) {

                String w_clean = w.replaceAll("[^a-z]", "");
                if (w_clean.isEmpty())
                    continue;

                if (validWords.contains(w_clean)) 
                    continue;

                if (uniq.add(w_clean)) {
                    map.merge(w_clean, 1, Integer::sum);
                }
            }
        }

        try { Tema1.barrier.await(); } catch (Exception ignored) {}

        if (id == 0) {

            Map<String, Integer> words_map_final = new HashMap<>();

            for (Map<String, Integer> map2 : Tema1.words_map) {
                for (Map.Entry<String, Integer> m : map2.entrySet()) {
                    String key = m.getKey();
                    Integer value = m.getValue();
                    words_map_final.merge(key, value, Integer::sum);
                }
            }

            List<String> values = new ArrayList<>(words_map_final.keySet());
            Collections.sort(values, (a, b) -> {
                int cmp = words_map_final.get(b).compareTo(words_map_final.get(a));
                if (cmp != 0)
                    return cmp;
                else { 
                    int ret = a.compareTo(b);
                    return ret;
                }
            });

            try (BufferedWriter bw = new BufferedWriter(new FileWriter("keywords_count.txt"))) {
                for (String w : values) {
                    bw.write(w + " " + words_map_final.get(w));
                    bw.newLine();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }



    public static HashMap<String, Integer> best_author(int id, int start, int end, List<String> authors) {

        HashMap<String, Integer> ret_hash = new HashMap<>();

        for(int i = start; i < end; i++) {

            if(!Tema1.valid_articles_set.contains(i))
                continue;
            
            Tema1.author_apps.merge(authors.get(i), 1, Integer::sum);
        }

        try { Tema1.barrier.await(); } catch (Exception ignored) {}

        if(id == 0) {

            Map.Entry<String,Integer> best = Collections.max(Tema1.author_apps.entrySet(),(a,b) -> {
                int cmp = a.getValue().compareTo(b.getValue());
                if (cmp != 0) 
                    return cmp;
                return b.getKey().compareTo(a.getKey());
                }
            );

            ret_hash.put(best.getKey(), best.getValue());

        }
        return ret_hash;
    }

    public static HashMap<String, Integer> best_language(int id, int start, int end, List<String> languages) {

        HashMap<String, Integer> ret_hash = new HashMap<>();

        for(int i = start; i < end; i++) {

            if(!Tema1.valid_articles_set.contains(i))
                continue;
            
            Tema1.language_apps.merge(languages.get(i), 1, Integer::sum);
        }
        

        try { Tema1.barrier.await(); } catch (Exception ignored) {}

        if(id == 0) {
            Map.Entry<String,Integer> best = Collections.max(Tema1.language_apps.entrySet(),(a,b) -> {
                int cmp = a.getValue().compareTo(b.getValue());
                if (cmp != 0) 
                    return cmp;
                return b.getKey().compareTo(a.getKey());
                }
            );

            ret_hash.put(best.getKey(), best.getValue());
        }

        return ret_hash;
    }

    public static HashMap<String, String> most_recent_published(int id, int start, int end, List<String> publisheds, List<String> uuids) {

        HashMap<String, String> ret_hash = new HashMap<>();

        for(int i = start; i < end; i++) {

            if(Tema1.valid_articles_set.contains(i)) {
                Tema1.published_hash.put(publisheds.get(i), uuids.get(i));
            }
    
        }

        try { Tema1.barrier.await(); } catch (Exception ignored) {}

        if(id == 0) {

            final Map<String, String> pl = Tema1.published_hash;
            List<String> values = new ArrayList<>(pl.keySet());
            Collections.sort(values, new Comparator<String>() {
                @Override
                public int compare(String a, String b) {
                    int cmp = b.compareTo(a);
                    if (cmp != 0)
                        return cmp;
                    else {
                        int cmp2 = pl.get(a).compareTo(pl.get(b));
                        return cmp2;
                    }
                }
            });   

            ret_hash.put(values.get(0), pl.get(values.get(0)));
        }

        return ret_hash;
    }

    public static HashMap<String, Integer> best_category(int id, int start, int end, List<List<String>> categories) {

        HashMap<String, Integer> ret_hash = new HashMap<>();

        for(int i = start; i < end; i++) {

            Set<String> uniq = new HashSet<>();

            if(!Tema1.valid_articles_set.contains(i))
                continue;

            for(String categ : categories.get(i)){
                
                if(uniq.add(categ))
                    Tema1.categories_apps.merge(categ, 1, Integer::sum);
            }
        }

        try { Tema1.barrier.await(); } catch (Exception ignored) {}

        if(id == 0) {
            Map.Entry<String,Integer> best = Collections.max(Tema1.categories_apps.entrySet(),(a,b) -> {
                int cmp = a.getValue().compareTo(b.getValue());
                if (cmp != 0) 
                    return cmp;
                return b.getKey().compareTo(a.getKey());
                }
            );

            ret_hash.put(best.getKey(), best.getValue());
        }

        return ret_hash;
    }
}