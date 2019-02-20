package com.leku.hmq.util;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * URL 参数解析器
 *
 * @author guixiaoyuan
 * @version 1.3, 2019/2/14
 * @since [HMSQ/V2.7.3]
 */
public class UrlParser {

    protected byte type;
    protected static final byte TYPE_URL = 1;
    protected static final byte TYPE_QUERY_STRING = 2;
    protected String url;
    protected String baseUrl;
    protected String queryString;
    protected String label;
    protected String charset = "utf-8";

    protected boolean compiled = false;
    public Map<String, String> parsedParams;
    protected URLDecoder urld = new URLDecoder();

    public static UrlParser fromURL(String url) {
        UrlParser parser = new UrlParser();

        parser.type = 1;
        parser.url = url;

        String[] split = url.split("\\?", 2);
        parser.baseUrl = split[0];
        parser.queryString = (split.length > 1 ? split[1] : "");

        String[] split2 = url.split("#", 2);
        parser.label = (split2.length > 1 ? split2[1] : null);

        return parser;
    }

    public static UrlParser fromQueryString(String queryString) {
        UrlParser parser = new UrlParser();

        parser.type = 2;
        parser.queryString = queryString;

        return parser;
    }

    public UrlParser useCharset(String charset) {
        this.charset = charset;
        return this;
    }

    public UrlParser compile() throws UnsupportedEncodingException {
        if (this.compiled) {
            return this;
        }
        String paramString = this.queryString.split("#")[0];
        String[] params = paramString.split("&");

        this.parsedParams = new HashMap<String, String>(params.length);
        for (String p : params) {
            String[] kv = p.split("=");
            if (kv.length == 2) {
                this.parsedParams.put(kv[0], URLDecoder.decode(kv[1], this.charset));
            }
        }
        this.compiled = true;

        return this;
    }

    public String getParameter(String name) {
        if (this.compiled) {
            return (String) this.parsedParams.get(name);
        }
        String paramString = this.queryString.split("#")[0];
        Matcher match = Pattern.compile("(^|&)" + name + "=([^&]*)").matcher(paramString);
        match.lookingAt();

        return match.group(2);
    }

    public UrlParser setParameter(String name, String value) throws UnsupportedEncodingException {
        if (!this.compiled) {
            compile();
        }
        this.parsedParams.put(name, value);

        return this;
    }

    /**
     * 获得url参数类型，支持# ？分割符
     *
     * @param url
     * @return
     */
    public static Map<String, String> getCustomParams(String url) {
        if (url == null) {
            return null;
        }
        String[] ss = url.split("\\#|\\?|\\&");
        Map<String, String> queryPairs = new HashMap<>();
        String[] p;
        for (String s : ss) {
            p = s.split("=");
            if (p.length == 2) {
                queryPairs.put(p[0], p[1]);
            }
        }
        return queryPairs;

    }

    /**
     * 获得解析后的URL参数，新的方法
     *
     * @param url url对象
     * @return URL参数map集合
     */
    public static Map<String, String> getUrlParams(String url) {
        final Map<String, String> queryPairs = new LinkedHashMap<>();
        URL mUrl = stringToURL(url);
        if (mUrl == null) {
            return queryPairs;
        }
        try {
            String query = mUrl.getQuery();
            if (query == null) {
                return queryPairs;
            }
            //除url之外的参数进行解析
            if (query.length() > 0) {
                final String[] pairs = query.split("&");
                for (String pair : pairs) {
                    final int idx = pair.indexOf("=");
                    //如果等号存在且不在字符串两端，取出key、value
                    if (idx > 0 && idx < pair.length() - 1) {
                        final String key = URLDecoder.decode(pair.substring(0, idx), "UTF-8");
                        final String value = URLDecoder.decode(pair.substring(idx + 1), "UTF-8");
                        queryPairs.put(key, value);
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return queryPairs;
    }

    /**
     * 字符串转为URL对象
     *
     * @param url url字符串
     * @return url对象
     */
    private static URL stringToURL(String url) {
        if (url == null || url.length() == 0 || !url.contains("://")) {
            return null;
        }
        try {
            StringBuilder sbUrl = new StringBuilder("http");
            sbUrl.append(url.substring(url.indexOf("://")));
            URL mUrl = new URL(sbUrl.toString());
            return mUrl;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

}
