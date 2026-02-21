import java.io.*;
import java.util.*;

public class InputParser {

    public static List<String> parseLanguages(String path) {
        List<String> languages = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            int count = Integer.parseInt(br.readLine().trim()); // numărul de limbi

            for (int i = 0; i < count; i++) {
                String lang = br.readLine().trim();
                languages.add(lang);
            }
        } catch (Exception e) {
            System.err.println("Eroare la citirea limbilor");
            e.printStackTrace();
            System.exit(1);
        }

        return languages;
    }

    public static List<String> parseCategories(String path) {
        List<String> categories = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            int count = Integer.parseInt(br.readLine().trim()); // numarul de categorii

            for (int i = 0; i < count; i++) {
                String cat = br.readLine().trim();
                categories.add(cat);
            }
        } catch (Exception e) {
            System.err.println("Eroare la citirea categoriilor");
            e.printStackTrace();
            System.exit(1);
        }

        return categories;
    }

    public static List<String> parseWords(String path) {
        List<String> words = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            int count = Integer.parseInt(br.readLine().trim()); // numarul de cuvinte

            for (int i = 0; i < count; i++) {
                String w = br.readLine().trim();
                words.add(w);
            }
        } catch (Exception e) {
            System.err.println("Eroare la citirea de cuvinte");
            e.printStackTrace();
            System.exit(1);
        }

        return words;
    }
}
