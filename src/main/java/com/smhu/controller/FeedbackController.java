package com.smhu.controller;

import com.smhu.account.Shipper;
import com.smhu.dao.FeedbackDAO;
import com.smhu.dao.OrderDAO;
import com.smhu.dao.ShipperDAO;
import com.smhu.feedback.Feedback;
import com.smhu.iface.IFeedback;
import com.smhu.iface.IOrder;
import com.smhu.iface.IShipper;
import com.smhu.iface.IStatus;
import com.smhu.order.Order;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
@RequestMapping("/api")
public class FeedbackController {

    @GetMapping("/feedback/{orderId}")
    public ResponseEntity<?> getFeedbackById(@PathVariable("orderId") String orderId) {
        try {
            IFeedback feedbackListener = new FeedbackDAO();
            Feedback result = feedbackListener.getFeedbackById(orderId);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (ClassNotFoundException | SQLException e) {
            Logger.getLogger(FeedbackController.class.getName()).log(Level.SEVERE, "FeedbackController - get feedback by id: {0}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/feedback/{orderId}/{content}/{rating}")
    public ResponseEntity<?> feedbackOrderById(@PathVariable("orderId") String orderId,
            @PathVariable("content") String content, @PathVariable("rating") int rating) {
        IOrder orderListener = new OrderDAO();
        try {
            Order order = orderListener.getOrderById(orderId, "STAFF");
            if (order == null) {
                return new ResponseEntity<>(HttpStatus.METHOD_NOT_ALLOWED.toString(), HttpStatus.METHOD_NOT_ALLOWED);
            }

            IStatus status = new StatusController().getStatusListener();
            int statusIsDone = status.getStatusIsDoneOrder();
            if (order.getStatus() != statusIsDone) {
                return new ResponseEntity<>("Đơn hàng chưa hoàn thành để feedback", HttpStatus.METHOD_NOT_ALLOWED);
            }

            IFeedback feedbackListener = new FeedbackDAO();
            int result = feedbackListener.feedbackOrder(order, content, rating);
            if (result <= 0) {
                return new ResponseEntity<>("Feedback Failed. Please try again", HttpStatus.INTERNAL_SERVER_ERROR);
            }
            
            IShipper shipperListener = new ShipperDAO();
            shipperListener.updateShipper(order.getShipper().getUsername());
        } catch (ClassNotFoundException | SQLException e) {
            Logger.getLogger(FeedbackController.class.getName()).log(Level.SEVERE, "FeedbackController - check order exist: {0}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>("Feedback success", HttpStatus.OK);
    }
}
