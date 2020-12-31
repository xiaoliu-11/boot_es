package com.example.boot_es.utils;

import com.example.boot_es.pojo.Tupian;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Component
public class HtmlParseUtil {

    public static void main(String[] args) throws Exception {
   new HtmlParseUtil().parseHtml("15").forEach(System.out::println);
    }

    public List<Tupian> parseHtml(String keywords) throws Exception {

        //你要解析那个网址
        String url = "http://pic.netbian.com/e/search/result/?searchid="+keywords;
        //解析网页返回document对象
        Document document = Jsoup.parse(new URL(url), 30000);
//        Element element = document.getElementById("J_goodsList");
//        System.out.println(element);
        Elements elements = document.getElementsByClass("slist");
        // System.out.println(elements);
        //获取所有的li元素
        Elements elements1 = elements.first().getElementsByTag("li");

        ArrayList<Tupian> allimage = new ArrayList<>();

        for (Element el : elements1) {


            String img = el.getElementsByTag("img").eq(0).attr("src");
            String name = el.getElementsByTag("b").eq(0).text();
            System.out.println("-------------------------------------------------------------");
            System.out.println(img);
            System.out.println(name);

            Tupian tupian = new Tupian();
            tupian.setImg(img);
            tupian.setName(name);
            allimage.add(tupian);
        }
        return  allimage;

    }
}
