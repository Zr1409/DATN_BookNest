var statusChart = false;
var chart = "bar";
var chartShowing = "";
var myChartDay;
var myChartMonth;
var myChartYear;

$("#btnFind").prop("disabled", true);

function showModal() {
	$("#optionModal").modal("show");
}

function hideModel() {
	$("#optionModal").modal("hide");
}
function changeChart() {
	statusChart = !statusChart;
	if (statusChart) {
		chart = "line";
		$("#chartBar").html("BIỂU ĐỒ ĐƯỜNG");
		$("#chartLine").html("BIỂU ĐỒ CỘT");
	} else {
		chart = "bar";
		$("#chartBar").html("BIỂU ĐỒ CỘT");
		$("#chartLine").html("BIỂU ĐỒ ĐƯỜNG");
	}

	if (chartShowing == "day") {
		angular.element("#chartLine").scope().statisticalByDay();
	}

	if (chartShowing == "month") {
		angular.element("#chartLine").scope().statisticalByMonth();
	}

	if (chartShowing == "year") {
		angular.element("#chartLine").scope().statisticalByYear();
	}
	if (chartShowing == "option") {
		angular.element("#chartLine").scope().statisticalByOption();
	}
}

function loadChartDay() {
	chartShowing = "day";
	$("#bar-chart-grouped-day").show();
	$("#bar-chart-grouped-month").hide();
	$("#bar-chart-grouped-year").hide();
}

function loadChartMonth() {
	chartShowing = "month";
	$("#bar-chart-grouped-day").hide();
	$("#bar-chart-grouped-month").show();
	$("#bar-chart-grouped-year").hide();
}

function loadChartYear() {
	chartShowing = "year";
	$("#bar-chart-grouped-day").hide();
	$("#bar-chart-grouped-month").hide();
	$("#bar-chart-grouped-year").show();
}
function loadChartOption() {
	chartShowing = "option";
	$("#bar-chart-grouped-option").hide();
	$("#bar-chart-grouped-day").hide();
	$("#bar-chart-grouped-month").hide();
	$("#bar-chart-grouped-year").hide();
}

var app = angular.module("statistical-revenue-app", []);

app.controller("statistical-revenue-ctrl", function($scope, $http) {
	$scope.items = [];
	$scope.statisticalByDay = function() {
		loadChartDay();
		var date = new Date();
		$http
			.get(
				"/rest/statistical/revenue/day/" + date.getDate() +
				"/" +
				(date.getMonth() + 1) +
				"/" +
				date.getFullYear()
			)
			.then((resp) => {
				$scope.items = resp.data;
				var arrDay = [];
				var arrPrice = [];
				var length = resp.data.length;
				for (var i = 0; i < length; i++) {
					arrDay[i] = i + 1;
					arrPrice[i] = resp.data[i].price;
				}
				var dataMyBarDay = {
					type: "bar",
					data: {
						labels: arrDay,
						datasets: [
							{
								label: "Triệu đồng",
								backgroundColor: "#3e95cd",
								data: arrPrice,
							},
						],
					},
					options: {
						title: {
							display: true,
							text:
								"Biểu đồ doanh thu " +
								length +
								" ngày của tháng " +
								(date.getMonth() + 1) +
								" năm " +
								date.getFullYear(),
						},
					},
				};

				var ctx = document
					.getElementById("bar-chart-grouped-day")
					.getContext("2d");
				if (myChartDay) {
					myChartDay.destroy();
				}

				dataMyBarDay.type = chart;
				myChartDay = new Chart(ctx, dataMyBarDay);
			});
	};

	$scope.statisticalByMonth = function() {
		loadChartMonth();
		var date = new Date();
		$http
			.get("/rest/statistical/revenue/month/" + (date.getMonth() + 1) + "/" + date.getFullYear())
			.then((resp) => {
				$scope.items = resp.data;
				var arrYear = [];
				var arrPrice = [];
				for (var i = 0; i < resp.data.length; i++) {
					arrYear[i] = i + 1;
					arrPrice[i] = resp.data[i].price;
				}
				var dataMyChartMonth = {
					type: "bar",
					data: {
						labels: arrYear,
						datasets: [
							{
								label: "Triệu đồng",
								backgroundColor: "#3e95cd",
								data: arrPrice,
							},
						],
					},
					options: {
						title: {
							display: true,
							text: "Biểu đồ doanh thu 12 tháng của năm " + date.getFullYear(),
						},
					},
				};

				var ctx = document
					.getElementById("bar-chart-grouped-month")
					.getContext("2d");
				if (myChartMonth) {
					myChartMonth.destroy();
				}

				dataMyChartMonth.type = chart;
				myChartMonth = new Chart(ctx, dataMyChartMonth);
			});
	};

	$scope.statisticalByYear = function() {
		loadChartYear();
		var date = new Date();
		$http
			.get("/rest/statistical/revenue/year/" + date.getFullYear())
			.then((resp) => {
				$scope.items = resp.data;
				var arrYear = [];
				var arrPrice = [];
				for (var i = 0; i < resp.data.length; i++) {
					arrYear[i] = resp.data[i].date;
					arrPrice[i] = resp.data[i].price;
				}
				var dataMyChartYear = {
					type: "bar",
					data: {
						labels: arrYear,
						datasets: [
							{
								label: "Triệu đồng",
								backgroundColor: "#3e95cd",
								data: arrPrice,
							},
						],
					},
					options: {
						title: {
							display: true,
							text:
								"Biểu đồ doanh thu 10 năm từ năm " +
								arrYear[0] +
								" đến năm " +
								date.getFullYear(),
						},
					},
				};

				var ctx = document
					.getElementById("bar-chart-grouped-year")
					.getContext("2d");
				if (myChartYear) {
					myChartYear.destroy();
				}

				dataMyChartYear.type = chart;
				myChartYear = new Chart(ctx, dataMyChartYear);
			});
	};
	$scope.itemYear = [];
	var currentYear = new Date().getFullYear();
	for (var i = currentYear; i >= currentYear - 10; i--) {
		$scope.itemYear.push(i);
	}

	$scope.statisticalByOption = function() {
	    var year = $("#year").val();
	    var month = $("#month").val();
	    var day = $("#day").val();
	    console.log("Ngày:", day, "Tháng:", month, "Năm:", year);

	    hideModel();
	    loadChartOption();

	    // Hiển thị lại thẻ canvas trước khi vẽ
	    $("#bar-chart-grouped-option").show();
		
		// Xác định tiêu đề biểu đồ dựa trên lựa chọn
		    let chartTitle = "Biểu đồ doanh thu";
		    if (year != 0 && month == 0 && day == 0) {
		        chartTitle = `Biểu đồ doanh thu của năm ${year}`;
		    } else if (year != 0 && month != 0 && day == 0) {
		        chartTitle = `Biểu đồ doanh thu của tháng ${month}/${year}`;
		    } else if (year != 0 && month != 0 && day != 0) {
		        chartTitle = `Biểu đồ doanh thu của ngày ${day}/${month}/${year}`;
		    }
			
	    $http.get("/rest/statistical/revenue/option/" + day + "/" + month + "/" + year)
	        .then((resp) => {
	            $scope.items = resp.data;
	            var arrLabels = [];
	            var arrPrice = [];

	            for (var i = 0; i < resp.data.length; i++) {
	                arrLabels[i] = resp.data[i].date; // Ngày tháng năm
	                arrPrice[i] = resp.data[i].price; // Doanh thu
	            }

	            // Kiểm tra nếu đã có biểu đồ trước đó thì xóa nó
	            if (window.myChartOption) {
	                window.myChartOption.destroy();
	            }

	            var ctx = document.getElementById("bar-chart-grouped-option").getContext("2d");
	            if (!ctx) {
	                console.error("🚨 Không tìm thấy thẻ <canvas> để vẽ biểu đồ!");
	                return;
	            }

	            // Tạo biểu đồ mới
				var maxPrice = Math.max(...arrPrice); // Lấy giá trị cao nhất

				// Kiểm tra nếu giá trị cao nhất lớn hơn 1 tỷ (1,000,000 triệu)
				var unitLabel = "Triệu đồng";
				if (maxPrice >= 1000000) {
				    unitLabel = "Tỷ đồng";
				    arrPrice = arrPrice.map(price => price / 1000); // Chuyển thành đơn vị tỷ đồng
				}

				// Tạo biểu đồ mới
				window.myChartOption = new Chart(ctx, {
				    type: "bar",
				    data: {
				        labels: arrLabels,
				        datasets: [{
				            label: unitLabel,  // Đổi đơn vị động
				            backgroundColor: "#3e95cd",
				            data: arrPrice,
				        }],
				    },
				    options: {
				        title: {
				            display: true,
				            text: chartTitle, // Gán tiêu đề động
				        },
				        scales: {
				            y: {
				                ticks: {
				                    callback: function(value, index, values) {
				                        return value.toLocaleString() + " " + unitLabel;
				                    }
				                }
				            }
				        }
				    }
				});

	        });
	};

	$scope.statisticalByDay();
});
$("#year").change(function() {
	var year = this.value;
	if (year != 0) {
		destroyMonth();
		loadMonth();
		$("#btnFind").prop("disabled", false);
	} else {
		destroyMonth();
		destroyDay();
		$("#btnFind").prop("disabled", true);
	}
});

$("#month").change(function() {
	var month = this.value;
	if (month != 0) {
		destroyDay();
		loadDay();
	} else {
		destroyDay();
	}
});

function loadMonth() {
	for (var i = 1; i <= 12; i++) {
		$("#month").append("<option value=" + i + ">" + i + "</option>");
	}
}

function destroyMonth() {
	for (var i = 1; i <= 12; i++) {
		$("#month option[value='" + i + "']").remove();
	}
}

function loadDay() {
	var year = $("#year").val();
	var month = $("#month").val();
	var maxDay = new Date(year, month, 0).getDate();
	for (var i = 1; i <= maxDay; i++) {
		$("#day").append("<option value=" + i + ">" + i + "</option>");
	}
}

function destroyDay() {
	for (var i = 1; i <= 31; i++) {
		$("#day option[value='" + i + "']").remove();
	}
}
