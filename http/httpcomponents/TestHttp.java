import cn.hutool.http.HttpUtil;

import java.util.HashMap;

public class TestHttp {
    public static void main(String[] args) {
        String url = "https://www.baidu.com";
        String res = HttpUtils.get(url);
        System.out.println(res);

        // post参数必填，不填会报错
        HashMap<String, String> m = new HashMap<>();
        String res2 = HttpUtils.post(url, m);
        System.out.println(res2);

    }
}

