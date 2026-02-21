import java.util.ArrayList;
import java.util.List;

public class Merge{

    public static List<Integer> concat_lists(List<Integer> a, List<Integer> b, List<String> publisheds, List<String> uuids) {
        List<Integer> c = new ArrayList<>();
        int i = 0;
        int j = 0;

        while(i < a.size() && j < b.size()) {

            int cmp = publisheds.get(b.get(j)).compareTo(publisheds.get(a.get(i)));
            if (cmp == 0)
                cmp = uuids.get(a.get(i)).compareTo(uuids.get(b.get(j)));
            
            if(cmp < 0) {
                c.add(a.get(i));
                i++;
            }
            else {
                c.add(b.get(j));
                j++;
            }
        }

        if (i < a.size()) {
            while (i < a.size()) {
                c.add(a.get(i));
                i++;
            }
        }

        if (j < b.size()) {
            while (j < b.size()) {
                c.add(b.get(j));
                j++;
            }
        }

        return c;
    }

    //geeks for geeks
    public static List<Integer> merge_two(List<List<Integer>> lists, int st, int fin, List<String> publisheds, List<String> uuids) {
        if(st == fin)
            return lists.get(st);

        int mid = (st + fin) / 2;
        List<Integer> left = merge_two(lists, st, mid, publisheds, uuids);
        List<Integer> right = merge_two(lists, mid + 1, fin, publisheds, uuids);

        List<Integer> done = concat_lists(left, right, publisheds, uuids);
        return done;
    } 

    public static List<Integer> mergeArrays(List<List<Integer>> lists, List<String> publisheds, List<String> uuids) { 
        if (lists.isEmpty()) 
            return new ArrayList<>(); 
        return merge_two(lists, 0, lists.size() - 1, publisheds, uuids); 
    }


}