package ones.proxy.Filter;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import javax.servlet.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

 /**
 * Created by liYueYang on 2020/6/29.
 * 返回值输出过滤器，这里用来加密返回值
 */
public class ResponseFilter implements Filter {

    public static final String URL = "http://www.supermap.com.cn";
    public static final int PORT = 8090;
    public static final String FULLURL = URL + ":" + PORT;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException {

        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        String requestURI = httpServletRequest.getRequestURI();

        System.out.println("请求地址 = " + requestURI);
        Map<String, String> cookiesMap = new HashMap<>();
        Cookie[] cookies = httpServletRequest.getCookies();
        if (cookies == null) {
            cookies = new Cookie[0];
        }
        for (int i = 0; i < cookies.length; i++) {
            cookiesMap.put(cookies[i].getName(), cookies[i].getValue());
        }
        Connection connect = Jsoup.connect(FULLURL + requestURI).cookies(cookiesMap);
        Map<String, String> param = new HashMap<>();
        request.getParameterMap().forEach((k, v) -> {
            param.put(k, v[0]);
        });
        connect.data(param);
        String res = "";
        if (requestURI.contains(".") && !requestURI.contains(".html")) {
            URL url = new URL(FULLURL + requestURI);
            URLConnection uri = url.openConnection();
            // 获取数据流
            InputStream is = uri.getInputStream();
            //把返回值输出到客户端
            if (requestURI.endsWith("css")) {
                response.setContentType("text/css;charset=utf-8");//修改响应编码
            }
            if (requestURI.endsWith("javascript")) {
                response.setContentType("application/javascript;charset=utf-8");//修改响应编码
            }
            if (requestURI.endsWith("json")) {
                response.setContentType("application/json;charset=utf-8");//修改响应编码
            }
            if (requestURI.endsWith("svg")) {
                response.setContentType("image/svg+xml;charset=utf-8");//修改响应编码
            }
            if (requestURI.endsWith("png")) {
                response.setContentType("image/png;charset=utf-8");//修改响应编码
            }
            ServletOutputStream out = response.getOutputStream();
            int i = 0;
            while ((i = is.read()) != -1) {
                out.write(i);
            }
            out.flush();
        } else {
            try {
                Document document = connect.ignoreContentType(true).userAgent("Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN; rv:1.9.2.15)").timeout(50000).get();
                System.out.println("document.select(\"input[name=resourceID]\").text() = " + document.select("input[name=resourceID]").text());
                Element resourceID = document.getElementById("resourceID");
                resourceID.attr("id", "form_input");
                res = document.html();

            } catch (IOException e) {
                System.out.println("错误请求地址：" + requestURI);
                System.out.println("e.getMessage() = " + e.getMessage());
                res = "timed out 请求超时！";
            }
            res = res.replaceAll(FULLURL, "");
            res = res.replaceAll(URL, "");
//            res = res.replaceAll(FULLURL, "http://127.0.0.1:8080");
//            res = res.replaceAll(URL, "http://127.0.0.1:8080");
            //System.out.println("res = " + res);
            response.setCharacterEncoding("utf-8");    //设置 HttpServletResponse使用utf-8编码
            response.setContentType("text/html;charset=utf-8");  //设置响应头的编码
            response.getWriter().println(res);
        }
    }
}
