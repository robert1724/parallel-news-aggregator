import java.io.*;
import java.util.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ArticlesParser {

    public static void parse_articles(String articles_path, List<String> uuid_arr, List<String> title_arr, List<String> author_arr, List<String> url_arr, List<String> text_arr, List<String> published_arr, List<String> language_arr, List<List<String>> categories_arr) {
        
        try {
            BufferedReader br =  new BufferedReader(new FileReader(articles_path));
            int number = Integer.parseInt(br.readLine());

            ObjectMapper mapper = new ObjectMapper();

            for(int i = 0; i < number; i++) {
                String json_current = br.readLine();
                File articlesDir = new File(articles_path).getParentFile();
                File jsonFile = new File(articlesDir, json_current);

                JsonNode array = mapper.readTree(jsonFile);

                for(JsonNode node : array) {
                    String uuid = node.get("uuid").asText();
                    uuid_arr.add(uuid);
                    String title = node.get("title").asText();
                    title_arr.add(title);
                    String author = node.get("author").asText();
                    author_arr.add(author);
                    String url = node.get("url").asText();
                    url_arr.add(url);
                    String text = node.get("text").asText();
                    text_arr.add(text);
                    String published = node.get("published").asText();
                    published_arr.add(published);
                    String language = node.get("language").asText();
                    language_arr.add(language);

                    List<String> cats = new ArrayList<>();
                    if (node.get("categories") != null)
                        for (JsonNode c : node.get("categories"))
                            cats.add(c.asText());
                    categories_arr.add(cats);
                }   
            }

            br.close();

        } catch(Exception e) {
            System.err.println("Eroare la citirea articolelor");
            e.printStackTrace();
            System.exit(1);
        }
    }
}
