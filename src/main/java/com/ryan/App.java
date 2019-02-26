package com.ryan;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author ryan
 */
public class App {
    public static void main(String[] args){
        new Thread(){
            @Override
            public void run() {
                Document document = null;
                try {
                    document = Jsoup.connect("https://guangzhou.anjuke.com/community/").get();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Element element = document.selectFirst("div.items > span.elems-l");
                if(element != null) {
                    Elements a = element.select("a");
                    System.out.println(a.size());
                    for (int i = 1; i < a.size() - 1; i++) {
                        new MyThread(a.get(i).attr("href"), "guangzhou/" + a.get(i).text() + ".xlsx").start();
                    }
                }
            }
        }.start();

        new Thread(){
            @Override
            public void run() {
                Document document = null;
                try {
                    document = Jsoup.connect("https://hangzhou.anjuke.com/community/").get();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Element element = document.selectFirst("div.items > span.elems-l");
                if(element != null) {
                    Elements a = element.select("a");
                    System.out.println(a.size());
                    for (int i = 1; i < a.size() - 1; i++) {
                        new MyThread(a.get(i).attr("href"), "hangzhou/" + a.get(i).text() + ".xlsx").start();
                    }
                }
            }
        }.start();

        new Thread(){
            @Override
            public void run() {
                Document document = null;
                try {
                    document = Jsoup.connect("https://shenzhen.anjuke.com/community/").get();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Element element = document.selectFirst("div.items > span.elems-l");
                if(element != null) {
                    Elements a = element.select("a");
                    System.out.println(a.size());
                    for (int i = 1; i < a.size() - 1; i++) {
                        new MyThread(a.get(i).attr("href"), "shenzhen/" + a.get(i).text() + ".xlsx").start();
                    }
                }
            }
        }.start();

    }

    public static void parser(String url,ArrayList<Community> list) throws IOException {
        Document document = Jsoup.connect(url).get();
        if (document == null) {
            throw new IOException("end");
        }
        Elements elementsByClass = document.getElementsByClass("li-itemmod");
        for (int i = 0; i < elementsByClass.size(); i++) {
            Community community = new Community();

            Element href = elementsByClass.get(i).selectFirst("a");
            if(href != null) {
                String contentURL = href.attr("href") + "?from=Filter_1&hfilter=filterlist";
                Document content = Jsoup.connect(contentURL).get();
                Element advantage = content.selectFirst("div.comment-info > dl.character-mod > dd.multi-dd");
                if (advantage != null) {
                    community.setAdvantage(advantage.text());
                }

                Element disadvantage = content.selectFirst("div.comment-info > dl.character-mod > dd.single-dd");
                if (disadvantage != null) {
                    community.setDistrict(disadvantage.text());
                }

                Elements basicInfo = content.select("div.basic-infos-box > dl.basic-parms-mod");
                if (basicInfo != null) {
                    community.setBasicInfo(basicInfo.text());
                }
            }

            Elements title = elementsByClass.get(i).select("div.li-info > h3 > a");
            if (title != null) {
                community.setTitle(title.text());
            }

            Elements address = elementsByClass.get(i).select("div.li-info > address");
            if(address != null) {
                String addr = address.text();
                community.setAddress(addr);

                if(addr != "" && addr != null) {
                    String[] split = address.text().substring(1, addr.indexOf(65341)).split("-");
                    if (split.length >= 2) {
                        community.setDistrict(split[0]);
                        community.setRegion(split[1]);
                    }else if (split.length == 1){
                        community.setDistrict(split[0]);
                    }
                }
            }

            Elements date = elementsByClass.get(i).select("div.li-info > p.date");
            if(date != null){
                community.setDate(date.text());
            }

            Element price = elementsByClass.get(i).selectFirst("div.li-side > p > strong");
            if (price != null) {
                community.setPrice(price.text() + "元/平米");
            }
            list.add(community);
        }
        System.out.println(url);
    }

    public static void writeExcel(String name,ArrayList<Community> list) throws IOException {
        Workbook workbook = new HSSFWorkbook();
        Sheet sheet = workbook.createSheet("小区信息");
        Row row0 = sheet.createRow(0);
        row0.createCell(0).setCellValue("名称");
        row0.createCell(1).setCellValue("区域");
        row0.createCell(2).setCellValue("片区");
        row0.createCell(3).setCellValue("地址");
        row0.createCell(4).setCellValue("价格");
        row0.createCell(5).setCellValue("建筑日期");
        row0.createCell(6).setCellValue("特色");
        row0.createCell(7).setCellValue("不足");
        row0.createCell(8).setCellValue("基本信息");
        ;


        for (int i = 0; i < list.size(); i++) {
            Row row = sheet.createRow(i + 1);
            Community community = list.get(i);
            row.createCell(0).setCellValue(community.getTitle());
            row.createCell(1).setCellValue(community.getDistrict());
            row.createCell(2).setCellValue(community.getRegion());
            row.createCell(3).setCellValue(community.getAddress());
            row.createCell(4).setCellValue(community.getPrice());
            row.createCell(5).setCellValue(community.getDate());
            row.createCell(6).setCellValue(community.getAdvantage());
            row.createCell(7).setCellValue(community.getDisadvantage());
            row.createCell(8).setCellValue(community.getBasicInfo());
        }

        File file = new File(name);
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        workbook.write(fileOutputStream);
        fileOutputStream.close();

    }
}

class MyThread extends Thread{
    private String url;
    private String name;
    public MyThread(String url,String name){
        this.url = url;
        this.name = name;
    }
    @Override
    public void run() {
        ArrayList<Community> list = new ArrayList();
        Document document = null;
        try {
            document = Jsoup.connect(url).get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Elements em = document.select("span.tit > em");
        System.out.println(em.eachText());
        List<String> strings = em.eachText().subList(em.eachText().size() - 1, em.eachText().size());
        int total = Integer.parseInt(strings.get(0));
        int sum = total % 30 == 0 ? total / 30 : total / 30 + 1;
        for (int i = 0; i < sum; i++) {
            try {
                App.parser(url+"p"+(i+1),list);
                Thread.sleep(100);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        try {
            App.writeExcel(name,list);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
