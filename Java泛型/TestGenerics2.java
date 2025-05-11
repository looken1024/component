import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class TestGenerics2 {

    public static class InnerClass <T> {
        private T t;
        public InnerClass(T t) {
            this.t = t;
        }
        void print() {
            System.out.println(t);
        }
    }

    public static boolean isEmpty(List<?> list) {
        return list == null || list.isEmpty();
    }

    public static <T extends Comparable<T>> boolean compare(T x, T y) {
        return x.compareTo(y) > 0;
    }

    public static void main(String[] args) {
        List<Integer> list = new ArrayList<Integer>();
        list.add(1);
        System.out.println(isEmpty(list));

        InnerClass<String> s = new InnerClass<String>("123");
        s.print();

        InnerClass<Integer> s2 = new InnerClass<Integer>(1233);
        s2.print();

        String s3 = "567";
        String s4 = "453";
        System.out.println(compare(s3, s4));
    }
}

