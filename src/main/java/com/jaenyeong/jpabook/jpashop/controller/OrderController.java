package com.jaenyeong.jpabook.jpashop.controller;

import com.jaenyeong.jpabook.jpashop.domain.Member;
import com.jaenyeong.jpabook.jpashop.domain.Order;
import com.jaenyeong.jpabook.jpashop.domain.item.Item;
import com.jaenyeong.jpabook.jpashop.repository.OrderSearch;
import com.jaenyeong.jpabook.jpashop.service.ItemService;
import com.jaenyeong.jpabook.jpashop.service.MemberService;
import com.jaenyeong.jpabook.jpashop.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final MemberService memberService;
    private final ItemService itemService;

    @GetMapping("/orders/new")
    public String createForm(final Model model) {
        final List<Member> members = memberService.findMembers();
        final List<Item> items = itemService.findItems();

        model.addAttribute("members", members);
        model.addAttribute("items", items);

        return "orders/createOrderForm";
    }

    @PostMapping("/orders/new")
    public String create(@RequestParam("memberId") final Long memberId,
                         @RequestParam("itemId") final Long itemId,
                         @RequestParam("count") final int count) {

        orderService.order(memberId, itemId, count);
        return "redirect:/orders";
    }

    @GetMapping("/orders")
    public String orderList(@ModelAttribute("orderSearch") final OrderSearch orderSearch, final Model model) {
        final List<Order> orders = orderService.findOrders(orderSearch);
        model.addAttribute("orders", orders);

        return "orders/orderList";
    }

    @PostMapping("/orders/{orderId}/cancel")
    public String cancelOrder(@PathVariable("orderId") final Long orderId) {
        orderService.cancelOrder(orderId);
        return "redirect:/orders";
    }
}
