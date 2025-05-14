import cn.hutool.http.HttpUtil;

public class TestHttp {
    public static void main(String[] args) {
        String url = "https://www.baidu.com/";
        String res1 = HttpUtil.get(url);
        System.out.println(res1);

        String res2 = HttpUtil.post(url, "{}");
        System.out.println(res2);

    }
}

