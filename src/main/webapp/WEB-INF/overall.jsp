<%@page import="com.smhu.overall.Overall"%>
<%@page import="java.util.List"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Overall Statistic Page</title>
        <style>
            .term {
                width: 300px;
                float: left;
            }
            .month {
                width: 300px;
                float: left;
            }
            .clearfix {
                clear: both;
            }
            h2 {
                margin: 0;
                margin-top: 20px;
            }
            p {
                margin: 0;
                margin-top: 10px;
                font-size: large;
            }
        </style>
    </head>
    <body>
        <%
            Overall overall = (Overall) request.getAttribute("OVERALL");
        %>
        <h1>Thống kê toàn bộ đơn hàng đã hoàn thành trong năm ${YEAR}</h1>

        <div>
            <h2>Tổng đơn hàng và doanh thu của năm ${YEAR}</h2>
            <p>
                <span style="font-weight: bold">Tổng đơn:</span> ${OVERALL.year.countOrder} <br/>
                <span style="font-weight: bold">Tổng tiền vận chuyển:</span> ${OVERALL.year.amountShipping} <br/>
                <span style="font-weight: bold">Tổng tiền công đi chợ:</span> ${OVERALL.year.amountShopping} <br/>
                <span style="font-weight: bold">Tổng tiền:</span> ${OVERALL.year.amountTotal}
            </p>
        </div>

        <div>
            <h2>Tổng đơn hàng và doanh thu theo quý (3 tháng)</h2>
            <%
                for (int i = 0; i < overall.getTerm().size(); i++) {
            %>
            <div class="term">
                <p>
                    <span style="font-weight: bold"><%= overall.getTerm().get(i).getDesc()%></span>  <br/>
                    <span style="font-weight: bold">Tổng đơn:</span> <%= overall.getTerm().get(i).getCountOrder()%> <br/>
                    <span style="font-weight: bold">Tổng tiền vận chuyển:</span> <%= overall.getTerm().get(i).getAmountShipping()%> <br/>
                    <span style="font-weight: bold">Tổng tiền công đi chợ:</span> <%= overall.getTerm().get(i).getAmountShopping()%> <br/>
                    <span style="font-weight: bold">Tổng tiền:</span> <%= overall.getTerm().get(i).getAmountTotal()%>
                </p>
            </div>
            <%
                if (i % 2 != 0) {
            %>
            <div class="clearfix">
            </div>
            <%
                    }
                }
            %>
        </div>

        <div>
            <h2>Tổng đơn hàng và doanh thu theo tháng (12 tháng)</h2>
            <%
                for (int i = 0; i < overall.getMonth().size(); i++) {
            %>
            <div class="month">
                <p>
                    <span style="font-weight: bold"><%= overall.getMonth().get(i).getDesc()%></span>  <br/>
                    <span style="font-weight: bold">Tổng đơn:</span> <%= overall.getMonth().get(i).getCountOrder()%> <br/>
                    <span style="font-weight: bold">Tổng tiền vận chuyển:</span> <%= overall.getMonth().get(i).getAmountShipping()%> <br/>
                    <span style="font-weight: bold">Tổng tiền công đi chợ:</span> <%= overall.getMonth().get(i).getAmountShopping()%> <br/>
                    <span style="font-weight: bold">Tổng tiền:</span> <%= overall.getMonth().get(i).getAmountTotal()%>
                </p>
            </div>
            <%
                if ((i + 1) % 3 == 0) {
            %>
            <div class="clearfix">
            </div>
            <%
                    }
                }
            %>
        </div>
    </body>
</html>
