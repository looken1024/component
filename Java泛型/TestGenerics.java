public class TestGenerics {

    // 泛型方法: list中的E只能是类类型，不能是基础数据类型
    public static <E> boolean isEmpty(E[] list) {
        return list == null || list.length == 0;
    }

    // 泛型方法
    public static <N> boolean isNumEmpty(N[] list) {
        return list == null || list.length == 0;
    }

    public static void main(String[] args) {
        Integer[] a = {1, 2, 3, 4, 5};
        System.out.println(isEmpty(a));

        int[] b = {};

        /* 编译错误
         * int[]是基本类型数组，而泛型方法isNumEmpty(N[] list)的参数N[]要求必须是对象类型数组（如Integer[]、String[]）。
         * Java的泛型机制无法自动将int[]转换为Integer[]，因为两者在内存结构上完全不同（基本类型数组 vs 对象引用数组）。
         */
        // System.out.println(isNumEmpty(b));

        /* 关键知识点
         * 泛型类型擦除：Java泛型在编译后会被替换为Object（或指定上界），而基本类型无法直接替换为Object。
         * 数组类型不兼容：int[]与Integer[]在JVM中是两种完全不同的类型，无法自动转换。
         * 自动装箱限制：虽然单个int可以自动装箱为Integer，但数组级别的自动装箱不会发生。
         */
    }
}

