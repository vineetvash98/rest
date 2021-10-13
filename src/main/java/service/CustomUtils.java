package service;

import pojo.Order;

import java.util.ArrayList;
import java.util.List;

public class CustomUtils {

    public static List<Order> createDummyList() {
        List<Order> orders = new ArrayList<Order>();

        for(int idx=0;idx<5;idx++){
            Order order = new Order("Order_" + idx, 1, 1);
            orders.add(order);
        }

        return orders;
    }
}
