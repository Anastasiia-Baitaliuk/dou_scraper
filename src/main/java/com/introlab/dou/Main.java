package com.introlab.dou;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.introlab.dou.domain.Vacancy;
import com.introlab.dou.dto.ResponseDou;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) throws IOException {
        new Main().run();
    }

    private void run() throws IOException {

        System.out.println("=============================== G E T ===============================");
        requestGet();
        System.out.println("=====================================================================");

//        System.out.println("============================== P O S T ==============================");
//        requestPost();
//        System.out.println("=====================================================================");
    }

    // работает
    private void requestPost() throws IOException {
        int count = 0;
        Gson gson = new GsonBuilder().create();
        Connection.Response connectionResponse;
        ResponseDou responseDou;

        do {
            connectionResponse = createResponsePost(count);
            responseDou = gson.fromJson(connectionResponse.body(), ResponseDou.class);
            Document doc = Jsoup.parse(responseDou.getHtml());

            List<Vacancy> vacancies = parseVacanciesFromHtml(doc);
            printVacancies(vacancies);
            count += responseDou.getNum();
        } while (!responseDou.getLast());
    }


    private Connection.Response createResponsePost(int count) throws IOException {
        return Jsoup.connect("https://jobs.dou.ua/vacancies/xhr-load/?search=java")
                .ignoreContentType(true)
                .data("csrfmiddlewaretoken", "PzgSEDwB1ZfEgC35od3dpBwHn4DUjpleHQ5Em4oZUFqRS4uB8n5YWsKpXF3yEB61")
                .data("count", Integer.toString(count))
                //.userAgent("Mozilla")
                .cookie("csrftoken", "gHkgb5XHSMEzL5QLOMw0eUl9H3EUNfpQ8Y92TwP5LsPMnxhhyWyLLLzRhE4y8raD")
                .referrer("https://jobs.dou.ua/vacancies/?search=java")
                .method(Connection.Method.POST)
                .execute();
    }

    // НЕ РАБОТАЕТ. пропускает вакансии в начале и в конце списка
    private void requestGet() throws IOException {
        Document doc = Jsoup.connect("https://jobs.dou.ua/vacancies/?search=java")
                .header("Host", "jobs.dou.ua")
                .get();
        List<Vacancy> vacancies = parseVacanciesFromHtml(doc);
        printVacancies(vacancies);
    }

    private void printVacancies(List<Vacancy> vacancies) {
        System.out.println((long) vacancies.size());
        vacancies.stream().map(v -> "++++++++++\n" + v + "\n").forEach(System.out::println);
    }

    private List<Vacancy> parseVacanciesFromHtml(Document doc) {
        List<Vacancy> vacancies = new ArrayList<>();
        if (doc != null) {
            Elements elements = doc.select("li");

            elements.stream()
                    .map(this::getVacancyData)
                    .forEach(vacancies::add);
        }
        return vacancies;

    }

    private Vacancy getVacancyData(Element element) {
        String title = element.select(".title a").text();
        String location = element.select(".cities").text();
        String company = element.select(".title strong a").text();
        String shortDescription = element.select(".sh-info").text();
        String link = element.select(".title a ").attr("href");
        return new Vacancy(title, location, company, shortDescription, link);
    }





    private Document getDocument(String url) {
        try {
            return Jsoup.connect(url).get();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}