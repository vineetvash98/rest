package servlet;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.thoughtworks.xstream.XStream;
import pojo.Order;
import service.CustomUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/orders")
public class OrderServlet extends HttpServlet {

    private List<Order> orderList;
    static int id;

    @Override
    public void init() throws ServletException {
        orderList = CustomUtils.createDummyList();
        id = 0;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String id = req.getParameter("id");
        Gson gson = new GsonBuilder().create();
        String accept = req.getHeader("accept");
        if (id != null && !id.equals("")){
            Order order = getOrderById(Integer.parseInt(id));
            if (order == null){
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }

            if (accept.equals("application/xml")){
                resp.setContentType("application/xml");
                XStream xStream = new XStream();
                xStream.alias("order", Order.class);
                String xml = xStream.toXML(order);
                OutputStream out = resp.getOutputStream();
                out.write(xml.getBytes());
                out.flush();
            } else {
                resp.setContentType("application/json");
                PrintWriter out = resp.getWriter();
                out.println(gson.toJson(order));
                out.close();
            }
        }else {
            if (accept.equals("application/xml")){
                resp.setContentType("application/xml");
                OutputStream out = resp.getOutputStream();
                XStream xStream = new XStream();
                xStream.alias("order", Order.class);
                StringBuilder xml = new StringBuilder();
                for (Order order : orderList){
                    xml.append(xStream.toXML(order));
                }
                out.write(xml.toString().getBytes());
                out.flush();
            } else {
                resp.setContentType("application/json");
                PrintWriter out = resp.getWriter();
                out.println(gson.toJson(orderList));
                out.close();
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String name = req.getParameter("name");
        String quantity = req.getParameter("quantity");


        if (name != null && quantity != null){
            String status = addOrder(Integer.parseInt(quantity), name, ++id, resp);

            PrintWriter out = resp.getWriter();
            out.println(status);
            out.close();
        } else{
            resp.setContentType("text/plain");
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            PrintWriter out = resp.getWriter();
            out.write("Please try again later!");
            out.close();
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String name = req.getParameter("name");
        String id = req.getParameter("id");
        String quantity = req.getParameter("quantity");

        if (name != null  && id != null){
            String status = updateById(Integer.parseInt(id), name, Integer.parseInt(quantity), resp);
            PrintWriter out = resp.getWriter();
            out.println(status);
            out.close();
        }else {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            PrintWriter out = resp.getWriter();
            out.println("Please try again later!");
            out.close();
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String orderIdString = req.getParameter("id");
        int orderIdToBeDeleted = Integer.parseInt(orderIdString);
        int idx = 0;
        while(idx < orderList.size()){
            if(orderIdToBeDeleted == id){
                orderList.remove(idx);
                resp.setStatus(200);
                PrintWriter out = resp.getWriter();
                out.write("Order successfully deleted");
                out.close();
            }else if (idx == orderList.size()-1 && orderIdToBeDeleted != id){
                resp.setStatus(404);
                PrintWriter out = resp.getWriter();
                out.write("Order not found");
                out.close();
            }else{
                idx++;
            }
        }
    }

    @Override
    public void destroy() {
        this.orderList.clear();
        id = 1;
    }

    private String addOrder(int quantity, String name, int id,  HttpServletResponse response){

        this.orderList.add(new Order(name, quantity, id));
        response.setStatus(HttpServletResponse.SC_CREATED);
        return "Order successfully added.";
    }

    private Order getOrderById(int id){
        for (Order order : orderList){
            if (order.getId() == id){
                return order;
            }
        }
        return null;
    }

    private String updateById(int id, String name, int quantity, HttpServletResponse response){
        for (Order order : orderList){
            if (order.getId() == id){
                order.setName(name);
                order.setQuantity(quantity);
                return "Order updated successfully";
            }
        }
        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        return "Order id not found";
    }


}
