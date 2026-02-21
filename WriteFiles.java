import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class WriteFiles {

    public static void write_all_articles(List<String> uuids, List<String> publisheds) {

        try (BufferedWriter bw = new BufferedWriter(new FileWriter("all_articles.txt"))) {
            for (int idx : Tema1.valid_articles) {
                bw.write(uuids.get(idx) + " " + publisheds.get(idx));
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void write_categories(Set<String> validCategories, List<List<String>> categories, List<String> uuids) {
        
        for (String current : validCategories) {

            String modified_name = current.replace(",", "").replace(" ","_") + ".txt";

            List<String> final_uuids = new ArrayList<>();

            for (int idx : Tema1.valid_articles) {
                List<String> catArt = categories.get(idx);

                if (catArt.contains(current)) {
                    final_uuids.add(uuids.get(idx));
                }
            }

            Collections.sort(final_uuids);

            try (BufferedWriter bw = new BufferedWriter(new FileWriter(modified_name))) {
                for (String uuid : final_uuids) {
                    bw.write(uuid);
                    bw.newLine();
                }
            } catch (IOException e) {
                System.err.println("Eroare la scrierea categ: " + current);
                e.printStackTrace();
            }

            File f = new File(modified_name);
            if(f.exists() && f.length() == 0)
                f.delete();
        }
    }

    public static void write_languages(Set<String> validLanguages, List<String> languages, List<String> uuids) {
        
        for (String current : validLanguages) {

            String modified_name = current + ".txt";

            List<String> final_uuids = new ArrayList<>();

            for (int idx : Tema1.valid_articles) {
                String lang = languages.get(idx);

                if (lang.equals(current)) {
                    final_uuids.add(uuids.get(idx));
                }
            }

            Collections.sort(final_uuids);

            try (BufferedWriter bw = new BufferedWriter(new FileWriter(modified_name))) {
                for (String uuid : final_uuids) {
                    bw.write(uuid);
                    bw.newLine();
                }
            } catch (IOException e) {
                System.err.println("Eroare la scrierea limbii: " + current);
                e.printStackTrace();
            }

            File f = new File(modified_name);
            if(f.exists() && f.length() == 0)
                f.delete();
        }
    }

    public static void write_report (int id, int start, int end, List<String> authors, List<String> uuids, List<String> languages, List<String> publisheds, List<String> urls, List<List<String>> categories) {

        int duplicates_found = uuids.size() - Tema1.valid_articles.size();
        HashMap<String, Integer> b_aut = Functions.best_author(id, start, end, authors);
        HashMap<String, Integer> lang_best = Functions.best_language(id, start, end, languages);
        HashMap<String, String> recent_pub = Functions.most_recent_published(id, start, end, publisheds, uuids);
        HashMap<String, Integer> best_categ = Functions.best_category(id, start, end, categories);

        if(id == 0) {
            String key = b_aut.keySet().iterator().next();
            Integer value = b_aut.get(key);
            String key_language = lang_best.keySet().iterator().next();
            Integer value_language = lang_best.get(key_language);
            String key_publisheds = recent_pub.keySet().iterator().next();
            Integer most_recent_pub_index = publisheds.indexOf(key_publisheds);
            String url_publisheds = urls.get(most_recent_pub_index);
            String word = null;
            Integer count = 0;
            String key_categs = best_categ.keySet().iterator().next();
            String modified_key_categs = key_categs.replace(",", "").replace(" ","_");
            Integer value_categs = best_categ.get(key_categs);

            try (BufferedReader br = new BufferedReader(new FileReader("keywords_count.txt"))) {
            String firstline = br.readLine();
            String[] parts = firstline.split(" ");
            word = parts[0];
            count = Integer.parseInt(parts[1]);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try (PrintWriter pw = new PrintWriter(new FileWriter("reports.txt"))) {
                pw.println("duplicates_found - " + duplicates_found);
                pw.println("unique_articles - " + Tema1.valid_articles.size());
                pw.println("best_author - " + key + " " + value);
                pw.println("top_language - " + key_language + " " + value_language);
                pw.println("top_category - " + modified_key_categs + " " + value_categs);
                pw.println("most_recent_article - " + key_publisheds + " " + url_publisheds);
                pw.println("top_keyword_en - " + word + " " + count);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
