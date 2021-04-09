package com.jaenyeong.jpabook.jpashop.controller;

import com.jaenyeong.jpabook.jpashop.domain.item.Book;
import com.jaenyeong.jpabook.jpashop.domain.item.Item;
import com.jaenyeong.jpabook.jpashop.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @GetMapping("/items/new")
    public String createForm(final Model model) {
        model.addAttribute("itemForm", new BookForm());
        return "items/createItemForm";
    }

    @PostMapping("/items/new")
    public String create(final BookForm bookForm) {
        final Book book = new Book();
        book.setName(bookForm.getName());
        book.setPrice(bookForm.getPrice());
        book.setStockQuantity(bookForm.getStockQuantity());
        book.setAuthor(bookForm.getAuthor());
        book.setIsbn(bookForm.getIsbn());

        itemService.saveItem(book);

        return "redirect:/";
    }

    @GetMapping("/items")
    public String list(final Model model) {
        final List<Item> items = itemService.findItems();
        model.addAttribute("items", items);

        return "items/itemList";
    }
}
