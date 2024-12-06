package com.lion.demo.controller;

import com.lion.demo.entity.Book;
import com.lion.demo.service.BookService;
import com.lion.demo.service.CsvFileReaderService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/book")
public class BookController {
    @Autowired private CsvFileReaderService csvFileReaderService;
    @Autowired private BookService bookService;

    @GetMapping("/yes24")
    public String yes24() {
        csvFileReaderService.csvFileToH2();
        return "redirect:/user/list";
    }

    @GetMapping("/list")
    public String list(@RequestParam(name="p", defaultValue = "1") int page,
                       @RequestParam(name="f", defaultValue = "title") String field,
                       @RequestParam(name="q", defaultValue = "") String query,
                       HttpSession session, Model model) {

//        List<Book> bookList = bookService.getBooksByPage(page); // 아래 서비스 메소드에서 if문 마지막에 else추가해줘서 했음
        List<Book> bookList = bookService.getBookList(page,field,query);

        int totalPage = 11, startPage = 1, endPage = 10;
        List<Integer> pageList = new ArrayList<>();
        for (int i = startPage; i <= endPage; i++) {
            pageList.add(i);
        }

        session.setAttribute("menu", "book");
        session.setAttribute("currentPage", page);
        model.addAttribute("bookList", bookList);
        model.addAttribute("field", field);
        model.addAttribute("query", query);
        model.addAttribute("totalPage", totalPage);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        model.addAttribute("pageList", pageList);
        return "book/list";
    }

    @GetMapping("/detail/{bid}")
    public String detail(@PathVariable long bid,
                         @RequestParam(name="q", defaultValue = "") String query,
                         Model model) {
        Book book = bookService.findByBid(bid);
        if (!query.equals("")) {
            String highlightedSummary = book.getSummary()
                    .replaceAll(query, "<span style='background-color: skyblue;'>" + query + "</span>");
            book.setSummary(highlightedSummary);
        }
        model.addAttribute("book", book);
        return "book/detail";
    }

    @GetMapping("/insert")
    public String insertForm() {
        return "book/insert";
    }

    @PostMapping("/insert")
    public String insertPoc(Book book) { // 화면에서 bid를 0으로 넘겨주면 insert할때 수정해서 맞게 넣어줌
        bookService.insertBook(book);
        return "redirect:/book/list";
    }

}
