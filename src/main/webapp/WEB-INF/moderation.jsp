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

            function checkTimeMatchEachSession() {
                var eleMidday = document.getElementsByClassName("timeMidday");
                var eleAfternoon = document.getElementsByClassName("timeAfternoon");
                var eleEvening = document.getElementsByClassName("timeEvening");

                var miliMidday1 = convertToMilisecond(eleMidday[0].value);
                var miliMidday2 = convertToMilisecond(eleMidday[1].value);

                var miliAfternoon1 = convertToMilisecond(eleAfternoon[0].value);
                var miliAfternoon2 = convertToMilisecond(eleAfternoon[1].value);

                var miliEvening1 = convertToMilisecond(eleEvening[0].value);
                var miliEvening2 = convertToMilisecond(eleEvening[1].value);

                if (miliMidday1 !== miliMidday2) {
                    document.getElementById("error").innerHTML = "Thời gian kết thúc buổi sáng phải bằng thời gian bắt đầu buổi trưa" + "<br/>"
                            + "Morning end time must be equal to midday start time";
                    return true;
                } else if (miliAfternoon1 !== miliAfternoon2) {
                    document.getElementById("error").innerHTML = "Thời gian kết thúc buổi trưa phải bằng thời gian bắt đầu buổi chiều" + "<br/>"
                            + "Midday end time must be equal to afternoon start time";
                    return true;
                } else if (miliEvening1 !== miliEvening2) {
                    document.getElementById("error").innerHTML = "Thời gian kết thúc buổi chiều phải bằng thời gian bắt đầu buổi tối" + "<br/>"
                            + "Afternoon end time must be equal to evening start time";
                    return true;
                }
            }

            function checkTimeEachSession() {
                var eleMorning = document.getElementsByClassName("timeMorning");
                var eleMidday = document.getElementsByClassName("timeMidday");
                var eleAfternoon = document.getElementsByClassName("timeAfternoon");
                var eleEvening = document.getElementsByClassName("timeEvening");
                //var eleMidnight = document.getElementsByClassName("timeMidnight");

                var miliMorning = convertToMilisecond(eleMorning[0].value);
                var miliMidday = convertToMilisecond(eleMidday[0].value);
                var miliAfternoon = convertToMilisecond(eleAfternoon[0].value);
                var miliEvening = convertToMilisecond(eleEvening[0].value);
                //var miliMidnight = convertToMilisecond(eleMidnight[0].value);

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
//                else if (miliEvening >= miliMidnight) {
//                    document.getElementById("error").innerHTML = "Thời gian buổi tối phải nhỏ hơn thời gian kết thúc ngày" + "<br/>"
//                            + "Evening time must be less than the end time of day";
//                    return true;
//                }
                return false;
            }

            function validate() {
                if (checkEmptyField()) {
                    return false;
                } else {
                    document.getElementById("error").innerHTML = "";
                }

                if (checkCurDate()) {
                    return false;
                } else {
                    document.getElementById("error").innerHTML = "";
                }

                if (checkTimeMatchEachSession()) {
                    return false;
                } else {
                    document.getElementById("error").innerHTML = "";
                }

                if (checkTimeEachSession()) {
                    return false;
                } else {
                    document.getElementById("error").innerHTML = "";
                }
            }

            function formatOnlyNumber(id, type) {
                var x = document.getElementById(id);
                if (x.value.length === 0) {
                    x.value = 0;
                } else if (isNaN(x.value)) {
                    x.value = x.value.replace(/[^\d]/g, "");
                } else {
                    if (type) {
                        if (x.value > 100) {
                            x.value = 100;
                        } else if (x.value < 0) {
                            x.value = 0;
                        }
                        x.value = x.value.replace(/[+-]/g, "");
                    }
                }
            }

            function formatDigit(id) {
                formatOnlyNumber(id, false);

                var formatter = new Intl.NumberFormat('vi-VN', {
                    style: 'currency',
                    currency: 'VND'
                });
                var x = document.getElementById(id);
                x.value = x.value.replaceAll(",", "");
                var currency = formatter.format(x.value).replaceAll(".", ",");
                x.value = currency.substring(0, currency.length - 2);
            }

            function initFormat() {
                var div = document.getElementsByClassName('second_content');
                for (let i = 0; i < div.length; i++) {
                    var input = div[i].getElementsByTagName('input');
                    for (let j = 0; j < input.length; j++) {
                        formatDigit(input[j].id);
                    }
                }
            }
        </script>
    </head>
    <body>
        <h1>Cost Moderation (đơn giá: nghìn đồng)</h1>
        <h4 id="error" style="color: red">
            <%
                if (request.getAttribute("RESULT") != null) {
            %>
            <script>
                window.alert('${RESULT}');
            </script>
            <%
                }
            %>
        </h4>
        <form action="updateCostModeration" method="post">
            <div>
                <div class="first_content">
                    <h3>Thời gian áp dụng chính sách giá mới</h3>
                    <label>Ngày tháng (Date):</label><input type="date" id="applyDate" name="applyDate" value="${curDate}" style="text-align: left"> <br/>
                    <label>Thời gian (Time):</label> <input type="time" id="applyTime" name="applyTime" value="${curTime}">
                </div>

                <div class="first_content">
                    <h3>Khoảng cách - Số lượng thực phẩm tối thiểu (tối đa 100 km - thực phẩm)</h3>
                    <label>Khoảng cách tối thiểu:</label> <input type="text" id="firstShipping" name="firstShipping" value="${commission.firstShipping}" size="3" maxlength="5"
                                                                 pattern="^\d+((,\d+)+)?" title="Định dạng chính xác (chữ số): ###,###" onkeyup="formatOnlyNumber(this.id, true)"/>
                    <label>km</label><br/>
                    <label>Số lượng thực phẩm tối thiểu:</label> <input type="text" id="firstShopping" name="firstShopping" value="${commission.firstShopping}" size="3" maxlength="5"
                                                                        pattern="^\d+((,\d+)+)?" title="Định dạng chính xác (chữ số): ###,###" onkeyup="formatOnlyNumber(this.id, true)"/>
                    <label>thực phẩm</label>
                </div>

                <div class="first_content">
                    <h3>Lợi nhuận thu được của từng đơn hàng (% của ứng dụng) (tối đa 100%)</h3>
                    <label style="text-align: left">
                        Phí di chuyển (Shipping):
                    </label>
                    <input type="text" id="commissionShipping" name="commissionShipping" value="${commission.commissionShipping}" size="3" maxlength="5"
                           pattern="^\d+((.\d+)+)?" title="Định dạng chính xác (chữ số): #.#" onkeyup="formatOnlyNumber(this.id, true)"/>
                    <label>%</label><br/>
                    <div class="clearfix"></div>
                    <label style="text-align: left">
                        Phí đi chợ (Shopping):
                    </label> 
                    <input type="text" id="commissionShopping" name="commissionShopping" value="${commission.commissionShopping}" size="3" maxlength="5"
                           pattern="^\d+((.\d+)+)?" title="Định dạng chính xác (chữ số): #.#" onkeyup="formatOnlyNumber(this.id, true)"/>
                    <label>%</label><br/>
                </div>
            </div>
            <div class="clearfix"></div>

            <div>
                <h3>Chi phí vào từng thời điểm (buổi)</h3>
                <div>
                    <label>Buổi sáng (Morning) - Từ (From):</label> 
                    <input type="time" class="timeMorning" name="timeMorning" value="${commission.timeMorning}">
                    <label>Tới (To):</label> 
                    <input type="time" class="timeMidday" value="${commission.timeMidday}"> <br/>

                    <div class="second_content">
                        <label class="second_content_first">Phí di chuyển tối thiểu (${commission.firstShipping}km):</label> 
                        <input type="text" id="fsiMorCost" name="fsiMorCost" value="${commission.fsiMorCost}" size="10" maxlength="10"
                               pattern="^\d+((,\d+)+)?" title="Định dạng chính xác (chữ số): ###,###" onkeyup="formatDigit(this.id)"/>
                        <label>đồng</label> &nbsp;
                        <label class="second_content_next">Phí di chuyển từ những km tiếp theo (${commission.firstShipping}km + 1km):</label> 
                        <input type="text" id="nsiMorCost" name="nsiMorCost" value="${commission.nsiMorCost}" size="10" maxlength="10"
                               pattern="^\d+((,\d+)+)?" title="Định dạng chính xác (chữ số): ###,###" onkeyup="formatDigit(this.id)"/> 
                        <label>đồng</label> <br/>

                        <label class="second_content_first">Phí đi chợ tối thiểu (${commission.firstShopping} thực phẩm):</label> 
                        <input type="text" id="fsoMorCost" name="fsoMorCost" value="${commission.fsoMorCost}" size="10" maxlength="10" 
                               pattern="^\d+((,\d+)+)?" title="Định dạng chính xác (chữ số): ###,###" onkeyup="formatDigit(this.id)"/>
                        <label>đồng</label> &nbsp;
                        <label class="second_content_next">Phí đi chợ từ những thực phẩm tiếp theo (${commission.firstShopping}tp + 1tp):</label> 
                        <input type="text" id="nsoMorCost" name="nsoMorCost" value="${commission.nsoMorCost}" size="10" maxlength="10" 
                               pattern="^\d+((,\d+)+)?" title="Định dạng chính xác (chữ số): ###,###" onkeyup="formatDigit(this.id)"/>
                        <label>đồng</label>
                    </div>
                </div> <br/>

                <div>
                    <label>Buổi trưa (Midday) - Từ (From):</label>
                    <input type="time" class="timeMidday" name="timeMidday" value="${commission.timeMidday}">
                    <label>Tới (To):</label> 
                    <input type="time" class="timeAfternoon" value="${commission.timeAfternoon}"> <br/>

                    <div class="second_content">
                        <label class="second_content_first">Phí di chuyển tối thiểu (${commission.firstShipping}km):</label> 
                        <input type="text" id="fsiMidCost" name="fsiMidCost" value="${commission.fsiMidCost}" size="10" maxlength="10" 
                               pattern="^\d+((,\d+)+)?" title="Định dạng chính xác (chữ số): ###,###" onkeyup="formatDigit(this.id)"/>
                        <label>đồng</label> &nbsp;
                        <label class="second_content_next">Phí di chuyển từ những km tiếp theo (${commission.firstShipping}km + 1km):</label>
                        <input type="text" id="nsiMidCost" name="nsiMidCost" value="${commission.nsiMidCost}" size="10" maxlength="10" 
                               pattern="^\d+((,\d+)+)?" title="Định dạng chính xác (chữ số): ###,###" onkeyup="formatDigit(this.id)"/> 
                        <label>đồng</label> <br/>

                        <label class="second_content_first">Phí đi chợ tối thiểu (${commission.firstShopping} thực phẩm):</label> 
                        <input type="text" id="fsoMidCost" name="fsoMidCost" value="${commission.fsoMidCost}" size="10" maxlength="10" 
                               pattern="^\d+((,\d+)+)?" title="Định dạng chính xác (chữ số): ###,###" onkeyup="formatDigit(this.id)"/> 
                        <label>đồng</label> &nbsp;
                        <label class="second_content_next">Phí đi chợ từ những thực phẩm tiếp theo (${commission.firstShopping}tp + 1tp):</label>
                        <input type="text" id="nsoMidCost" name="nsoMidCost" value="${commission.nsoMidCost}" size="10" maxlength="10" 
                               pattern="^\d+((,\d+)+)?" title="Định dạng chính xác (chữ số): ###,###" onkeyup="formatDigit(this.id)"/>
                        <label>đồng</label>
                    </div>
                </div> <br/>

                <div>
                    <label>Buổi chiều (Afternoon) - Từ (From):</label>
                    <input type="time" class="timeAfternoon" name="timeAfternoon" value="${commission.timeAfternoon}">
                    <label>Tới (To):</label> 
                    <input type="time" class="timeEvening" value="${commission.timeEvening}"> <br/>

                    <div class="second_content">
                        <label class="second_content_first">Phí di chuyển tối thiểu (${commission.firstShipping}km):</label> 
                        <input type="text" id="fsiAfCost" name="fsiAfCost" value="${commission.fsiAfCost}" size="10" maxlength="10" 
                               pattern="^\d+((,\d+)+)?" title="Định dạng chính xác (chữ số): ###,###" onkeyup="formatDigit(this.id)"/>
                        <label>đồng</label> &nbsp;
                        <label class="second_content_next">Phí di chuyển từ những km tiếp theo (${commission.firstShipping}km + 1km):</label> 
                        <input type="text" id="nsiAfCost" name="nsiAfCost" value="${commission.nsiAfCost}" size="10" maxlength="10" 
                               pattern="^\d+((,\d+)+)?" title="Định dạng chính xác (chữ số): ###,###" onkeyup="formatDigit(this.id)"/> 
                        <label>đồng</label> <br/>

                        <label class="second_content_first">Phí đi chợ tối thiểu (${commission.firstShopping} thực phẩm):</label> 
                        <input type="text" id="fsoAfCost" name="fsoAfCost" value="${commission.fsoAfCost}" size="10" maxlength="10" 
                               pattern="^\d+((,\d+)+)?" title="Định dạng chính xác (chữ số): ###,###" onkeyup="formatDigit(this.id)"/> 
                        <label>đồng</label> &nbsp;
                        <label class="second_content_next">Phí đi chợ từ những thực phẩm tiếp theo (${commission.firstShopping}tp + 1tp):</label> 
                        <input type="text" id="nsoAfCost" name="nsoAfCost" value="${commission.nsoAfCost}" size="10" maxlength="10" 
                               pattern="^\d+((,\d+)+)?" title="Định dạng chính xác (chữ số): ###,###" onkeyup="formatDigit(this.id)"/>
                        <label>đồng</label>
                    </div>
                </div> <br/>

                <div>
                    <label>Buổi tối (Evening) - Từ (From):</label>
                    <input type="time" class="timeEvening" name="timeEvening" value="${commission.timeEvening}"> <br/>
                    <div class="second_content">
                        <label class="second_content_first">Phí di chuyển tối thiểu (${commission.firstShipping}km):</label> 
                        <input type="text" id="fsiEveCost" name="fsiEveCost" value="${commission.fsiEveCost}" size="10" maxlength="10" 
                               pattern="^\d+((,\d+)+)?" title="Định dạng chính xác (chữ số): ###,###" onkeyup="formatDigit(this.id)"/>
                        <label>đồng</label> &nbsp;
                        <label class="second_content_next">Phí di chuyển từ những km tiếp theo (${commission.firstShipping}km + 1km):</label> 
                        <input type="text" id="nsiEveCost" name="nsiEveCost" value="${commission.nsiEveCost}" size="10" maxlength="10" 
                               pattern="^\d+((,\d+)+)?" title="Định dạng chính xác (chữ số): ###,###" onkeyup="formatDigit(this.id)"/> 
                        <label>đồng</label> <br/>

                        <label class="second_content_first">Phí đi chợ tối thiểu (${commission.firstShopping} thực phẩm):</label> 
                        <input type="text" id="fsoEveCost" name="fsoEveCost" value="${commission.fsoEveCost}" size="10" maxlength="10" 
                               pattern="^\d+((,\d+)+)?" title="Định dạng chính xác (chữ số): ###,###" onkeyup="formatDigit(this.id)"/>
                        <label>đồng</label> &nbsp;
                        <label class="second_content_next">Phí đi chợ từ những thực phẩm tiếp theo (${commission.firstShopping}tp + 1tp):</label> 
                        <input type="text" id="nsoEveCost" name="nsoEveCost" value="${commission.nsoEveCost}" size="10" maxlength="10" 
                               pattern="^\d+((,\d+)+)?" title="Định dạng chính xác (chữ số): ###,###" onkeyup="formatDigit(this.id)"/>
                        <label>đồng</label>
                    </div>
                </div>
                <script>
                    initFormat();
                </script>
            </div>
            <input type="submit" id="btnSubmit" value="Xác nhận" onclick="return validate()"/>
        </form>
    </body>
</html>