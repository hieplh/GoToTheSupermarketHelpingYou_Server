package com.smhu.iface;

import com.smhu.feedback.Feedback;
import com.smhu.order.Order;
import java.sql.SQLException;

public interface IFeedback {

    public Feedback getFeedbackById(String orderId) throws SQLException, ClassNotFoundException;

    public int feedbackOrder(Order order, String content, int rating) throws SQLException, ClassNotFoundException;
}
