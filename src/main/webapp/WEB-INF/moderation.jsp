<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Cost Moderation Page</title>
        <style>
            .clearfix {
                clear: both
            }

            form {
                width: fit-content;
            }

            input {
                text-align: right;
            }

            #btnSubmit {
                float: right;
            }

            .first_content label {  
                width: 200px;
                display: inline-block;
                margin-bottom: 10px;
            }

            .second_content {
                padding: 5px;
            }

            .second_content label{
                display: inline-block;
                margin-bottom: 5px;
            }

            .second_content_first {
                width: 230px;
            }

            .second_content_next {
                width: 340px;
                margin-left: 10px;
            }
        </style>

        <script>
            function loadTime() {
                var xhttp = new XMLHttpRequest();
                try {
                    xhttp.open("GET", "http://localhost:8084/smhu/api/system/time/milisecond", false);
                    xhttp.send(null);
                    if (xhttp.readyState == 4 && xhttp.status == 200) {
                        return +xhttp.responseText;
                    }
                } catch (e) {
                    return new Date().getTime();
                }
            }

            function checkEmptyField() {
                var x = document.getElementsByTagName("input");
                for (let i = 0; i < x.length; i++) {
                    if (!x[i].value) {
                        document.getElementById("error").innerHTML = "Tất cả các thông tin không được để trống" + "<br/>"
                                + "All fields are not allowed empty";
                        return true;
                    }
                }
                return false;
            }

            function convertToMilisecond(applyTime) {
                var arr = applyTime.split(':');
                var pow = 2;
                var curTimeMilisecond = +0;
                for (let i = 0; i < arr.length; i++) {
                    curTimeMilisecond += arr[i] * Math.pow(60, pow - i);
                }
                return curTimeMilisecond = curTimeMilisecond * 1000;
            }

            function checkCurDate() {
                var milisecond = loadTime();

                var applyDate = document.getElementById("applyDate").value;
                var applyTime = document.getElementById("applyTime").value;

                var curDate = new Date(applyDate);
                var curTimeMilisecond = convertToMilisecond(applyTime);
                var curMilisecond = curDate.getTime() + curTimeMilisecond - (7 * Math.pow(60, 2) * 1000);

                if (curMilisecond >= milisecond) {
                    return false;
                } else {
                    document.getElementById("error").innerHTML = "Ngày giờ thiết lập phải lớn hơn hiện tại" + "<br/>"
                            + "Apply date and time must be after current now";
                    return true;
                }
            }

            function checkTimeEachSession() {
                var eleMorning = document.getElementById("timeMorning").value;
                var eleMidday = document.getElementById("timeMidday").value;
                var eleAfternoon = document.getElementById("timeAfternoon").value;
                var eleEvening = document.getElementById("timeEvening").value;

                var miliMorning = convertToMilisecond(eleMorning);
                var miliMidday = convertToMilisecond(eleMidday);
                var miliAfternoon = convertToMilisecond(eleAfternoon);
                var miliEvening = convertToMilisecond(eleEvening);

                if (miliMorning >= miliMidday) {
                    document.getElementById("error").innerHTML = "Thời gian buổi sáng phải nhỏ hơn buổi trưa" + "<br/>"
                            + "Morning time must be less than Midday time";
                    return true;
                } else if (miliMidday >= miliAfternoon) {
                    document.getElementById("error").innerHTML = "Thời gian buổi trưa phải nhỏ hơn buổi chiều" + "<br/>"
                            + "Midday time must be less than Afternoon time";
                    return true;
                } else if (miliAfternoon >= miliEvening) {
                    document.getElementById("error").innerHTML = "Thời gian buổi chiều phải nhỏ hơn buổi tối" + "<br/>"
                            + "Afternoon time must be less than Evening time";
                    return true;
                }
                return false;
            }

            function validate() {
                var isEmptyField = checkEmptyField();
                if (isEmptyField) {
                    return false;
                } else {
                    document.getElementById("error").innerHTML = "";
                }

                var isCurDate = checkCurDate();
                if (isCurDate) {
                    return false;
                } else {
                    document.getElementById("error").innerHTML = "";
                }

                var isSessionTime = checkTimeEachSession();
                if (isSessionTime) {
                    return false;
                } else {
                    document.getElementById("error").innerHTML = "";
                }
            }
        </script>
    </head>
    <body>
        <h1>Cost Moderation (đơn giá: nghìn đồng)</h1>
        <h4 id="error" style="color: red"></h4>
        <form action="updateCostModeration" method="post">
            <div>
                <div class="first_content">
                    <h3>Ngày - giờ áp dụng</h3>
                    <label>Ngày tháng (Date):</label><input type="date" id="applyDate" name="applyDate" value="${curDate}" style="text-align: left"> <br/>
                    <label>Thời gian (Time):</label> <input type="time" id="applyTime" name="applyTime" value="${curTime}">
                </div>

                <div class="first_content">
                    <h3>Số km / Số lượng thực phẩm tối thiểu</h3>
                    <label>Di chuyển (Shipping):</label> <input type="number" name="firstShipping" value="${commission.firstShipping}"/> <br/>
                    <label>Đi chợ (Shopping):</label> <input type="number" name="firstShopping" value="${commission.firstShopping}"/>
                </div>

                <div class="first_content">
                    <h3>Lợi nhuận thu được (% của ứng dụng)</h3>
                    <label style="text-align: left">
                        Phí di chuyển <br/>
                        (Shipping Commission):
                    </label>
                    <input type="number" name="commissionShipping" value="${commission.commissionShipping}"/> <br/>
                    <div class="clearfix"></div>
                    <label style="text-align: left">
                        Phí đi chợ <br/>
                        (Shopping Commission):
                    </label> 
                    <input type="number" name="commissionShopping" value="${commission.commissionShopping}"/>
                </div>
            </div>
            <div class="clearfix"></div>

            <div>
                <h3>Chi phí vào từng thời điểm (buổi)</h3>
                <div>
                    <label>Buổi sáng (Morning) - Thời gian (Time):</label> 
                    <input type="time" id="timeMorning" name="timeMorning" value="${commission.timeMorning}"> <br/>

                    <div class="second_content">
                        <label class="second_content_first">Phí di chuyển tối thiểu (${commission.firstShipping}km):</label> <input type="number" name="fsiMorCost" value="${commission.fsiMorCost}"/> &nbsp;
                        <label class="second_content_next">Phí di chuyển từ những km tiếp theo (${commission.firstShipping}km + 1km):</label> <input type="number" name="nsiMorCost" value="${commission.nsiMorCost}"/> <br/>
                        <label class="second_content_first">Phí đi chợ tối thiểu (${commission.firstShopping} thực phẩm):</label> <input type="number" name="fsoMorCost" value="${commission.fsoMorCost}"/> &nbsp;
                        <label class="second_content_next">Phí đi chợ từ những thực phẩm tiếp theo (${commission.firstShopping}tp + 1tp):</label> <input type="number" name="nsoMorCost" value="${commission.nsoMorCost}"/>
                    </div>
                </div> <br/>

                <div>
                    <label>Buổi trưa (Midday) - Thời gian (Time):</label>
                    <input type="time" id="timeMidday" name="timeMidday" value="${commission.timeMidday}"> <br/>

                    <div class="second_content">
                        <label class="second_content_first">Phí di chuyển tối thiểu (${commission.firstShipping}km):</label> <input type="number" name="fsiMidCost" value="${commission.fsiMidCost}"/> &nbsp;
                        <label class="second_content_next">Phí di chuyển từ những km tiếp theo (${commission.firstShipping}km + 1km):</label> <input type="number" name="nsiMidCost" value="${commission.nsiMidCost}"/> <br/>
                        <label class="second_content_first">Phí đi chợ tối thiểu (${commission.firstShopping} thực phẩm):</label> <input type="number" name="fsoMidCost" value="${commission.fsoMidCost}"/> &nbsp;
                        <label class="second_content_next">Phí đi chợ từ những thực phẩm tiếp theo (${commission.firstShopping}tp + 1tp):</label> <input type="number" name="nsoMidCost" value="${commission.fsoMidCost}"/>
                    </div>
                </div> <br/>

                <div>
                    <label>Buổi chiều (Afternoon) - Thời gian (Time):</label>
                    <input type="time" id="timeAfternoon" name="timeAfternoon" value="${commission.timeAfternoon}"> <br/>

                    <div class="second_content">
                        <label class="second_content_first">Phí di chuyển tối thiểu (${commission.firstShipping}km):</label> <input type="number" name="fsiAfCost" value="${commission.fsiAfCost}"/> &nbsp;
                        <label class="second_content_next">Phí di chuyển từ những km tiếp theo (${commission.firstShipping}km + 1km):</label> <input type="number" name="nsiAfCost" value="${commission.nsiAfCost}"/> <br/>
                        <label class="second_content_first">Phí đi chợ tối thiểu (${commission.firstShopping} thực phẩm):</label> <input type="number" name="fsoAfCost" value="${commission.fsoAfCost}"/> &nbsp;
                        <label class="second_content_next">Phí đi chợ từ những thực phẩm tiếp theo (${commission.firstShopping}tp + 1tp):</label> <input type="number" name="nsoAfCost" value="${commission.nsoAfCost}"/>
                    </div>
                </div> <br/>

                <div>
                    <label>Buổi tối (Evening) - Thời gian (Time):</label>
                    <input type="time" id="timeEvening" name="timeEvening" value="${commission.timeEvening}"> <br/>
                    <div class="second_content">
                        <label class="second_content_first">Phí di chuyển tối thiểu (${commission.firstShipping}km):</label> <input type="number" name="fsiEveCost" value="${commission.fsiEveCost}"/> &nbsp;
                        <label class="second_content_next">Phí di chuyển từ những km tiếp theo (${commission.firstShipping}km + 1km):</label> <input type="number" name="nsiEveCost" value="${commission.nsiEveCost}"/> <br/>
                        <label class="second_content_first">Phí đi chợ tối thiểu (${commission.firstShopping} thực phẩm):</label> <input type="number" name="fsoEveCost" value="${commission.fsoEveCost}"/> &nbsp;
                        <label class="second_content_next">Phí đi chợ từ những thực phẩm tiếp theo (${commission.firstShopping}tp + 1tp):</label> <input type="number" name="nsoEveCost" value="${commission.nsoEveCost}"/>
                    </div>
                </div>
            </div>
            <input type="submit" id="btnSubmit" value="Xác nhận" onclick="return validate()"/>
        </form>
    </body>
</html>