package org.example.web.controllers;

import org.example.app.services.BookService;
import org.example.web.dto.Book;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Controller
@RequestMapping(value = "books")
public class BookShelfController {
    private static final Logger logger = LoggerFactory.getLogger(BookShelfController.class);
    private BookService bookService;

    @Autowired
    public BookShelfController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping("/shelf")
    public String books(Model model) {
        logger.info("got books shelf");
        model.addAttribute("book", new Book());
        model.addAttribute("bookList", bookService.getAllBooks());
        return "book_shelf";
    }

    @PostMapping("/save")
    public String saveBook(Book book) {
        if (nonNull(book.getAuthor()) && nonNull(book.getTitle()) && nonNull(book.getSize())) {
            bookService.saveBook(book);
            logger.info("current book repository size: {}", bookService.getAllBooks().size());
        } else {
            logger.info("book not save. some fields are empty: {}", book);
        }
        return "redirect:/books/shelf";
    }

    //todo Required parameter 'bookIdToRemove' is not present.
    @PostMapping("/remove")
    public String removeBook(@RequestParam(value = "bookIdToRemove") Integer bookIdToRemove) {
        if (bookService.removeBookById(bookIdToRemove)) {
            return "redirect:/books/shelf";
        } else {
            logger.info("book by id can not be removed. Id: {}", bookIdToRemove);
            return "redirect:/books/shelf";
        }
    }

    @PostMapping("/removeByRegex")
    public String removeBookByRegex(@RequestParam(value = "queryRegex") String queryRegex) {
//        addTmpBook();

        //todo не нашел более элегантного способа распарсить текст
        int size = 0;
        try{
            size = Integer.parseInt(queryRegex);
        }
        catch (NumberFormatException ex){
        }

        int finalSize = size;
        List<Book> removeBookList = bookService.getAllBooks().stream()
                .filter(book -> book.getAuthor().equals(queryRegex)
                        || book.getTitle().equals(queryRegex)
                        || book.getSize() == finalSize)
                .toList();
        removeBookList.forEach(book -> bookService.removeBookById(book.getId()));
        return "redirect:/books/shelf";
    }

    private void addTmpBook() {
        Book book1 = new Book();
        book1.setAuthor("1aaa");
        book1.setTitle("1bbb");
        book1.setSize(111);
        bookService.saveBook(book1);
        Book book2 = new Book();
        book2.setAuthor("2aaa");
        book2.setTitle("2bbb");
        book2.setSize(222);
        bookService.saveBook(book2);
        Book book3 = new Book();
        book3.setAuthor("3aaa");
        book3.setTitle("3bbb");
        book3.setSize(333);
        bookService.saveBook(book3);
    }

}
