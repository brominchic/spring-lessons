package com.example.spring.service.component;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class TestComponent {

    public String getPage(String firstname, String lastname) {
        return "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01//EN\" \"http://www.w3.org/TR/html4/strict.dtd\">\n" +
                "<html>\n" +
                "<head>\n" +
                "    <meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">\n" +
                "    <title>Главная</title>\n" +
                "</head>\n" +
                "<body>\n" +
                "<h1>Главная</h1>\n" +
                "<!-- Комментарий -->\n" +
                "<p>" + firstname + "</p>\n" +
                "<p>" + lastname + "</p>\n" +
                "</body>\n" +
                "</html>";
    }
}
