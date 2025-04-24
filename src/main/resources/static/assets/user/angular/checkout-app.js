$(document).ready(function() {
	disableMethod();
	disableConfirm();
	useAddressExits();
});

function disableAddress() {
	$("#txtAddress").text("Bước 2: Địa chỉ giao hàng");
	$("#btnAddress").css("display", "none");
	$("#collapse-payment-address").css("display", "none");
}

function enableAddress() {
	// $("#txtMethod").text("");
	$("#btnAddress").removeAttr("style");
	$("#collapse-payment-address").removeAttr("style");
}

function disableMethod() {
	$("#txtMethod").text("Bước 3: Phương thức thanh toán");
	$("#btnMethod").css("display", "none");
	$("#collapse-shipping-method").css("display", "none");
}

function enableMethod() {
	// $("#txtMethod").text("");
	$("#btnMethod").removeAttr("style");
	$("#collapse-shipping-method").removeAttr("style");
}

function disableConfirm() {
	$("#txtConfirm").text("Bước 4: Xác nhận đơn hàng");
	$("#btnConfirm").css("display", "none");
	$("#collapse-checkout-confirm").css("display", "none");
}

function enableConfirm() {
	// $("#txtMethod").text("");
	$("#btnConfirm").removeAttr("style");
	$("#collapse-checkout-confirm").removeAttr("style");
}

function useAddressExits() {
	$("#btnAddressExits").css("display", "block");
	$("#btnAddressAnother").css("display", "none");
}

function useAddressAnother() {
	$("#btnAddressExits").css("display", "none");
	$("#btnAddressAnother").css("display", "block");
}

function checkForm() {
	var check = 0;
	var submit = false;
	var fullName = $("#fullName").val();
	if (fullName == "") {
		$("#errorFullName").text("Họ và tên không được bỏ trống!");
		$("#fullName").css("border-color", "#dc3545");
		$("#lblFullName").css("color", "#dc3545");
		check = 0;
	}
	else {
		$("#errorFullName").text("");
		$("#fullName").removeAttr("style");
		$("#lblFullName").css("color", "black");
		check++;
	}

	var phone = $("#phone").val();
	if (phone == "") {
		$("#errorPhone").text("Số điện thoại không được bỏ trống!");
		$("#phone").css("border-color", "#dc3545");
		$("#lblPhone").css("color", "#dc3545");
		check = 0;
	}
	else {
		if (isVietnamesePhoneNumber(phone) == false) {
			$("#errorPhone").text("Số điện thoại không đúng định dạng!");
			$("#phone").css("border-color", "#dc3545");
			$("#lblPhone").css("color", "#dc3545");
			check = 0;
		}
		else {
			$("#errorPhone").text("");
			$("#phone").removeAttr("style");
			$("#lblPhone").css("color", "black");
			check++;
		}
	}

	var detail = $("#detail").val();
	if (detail == "") {
		$("#errorDetail").text("Địa chỉ nhận hàng không được bỏ trống!");
		$("#detail").css("border-color", "#dc3545");
		$("#lblDetail").css("color", "#dc3545");
		check = 0;
	}
	else {
		if (detail.length < 10 || detail.length > 200) {
			$("#errorDetail").text("Địa chỉ nhận hàng phải từ 10 đến 200 ký tự!");
			$("#detail").css("border-color", "#dc3545");
			$("#lblDetail").css("color", "#dc3545");
			check = 0;
		}
		else {
			$("#errorDetail").text("");
			$("#detail").removeAttr("style");
			$("#lblDetail").css("color", "black");
			check++;
		}
	}

	var province = $("#province").val();
	if (province == "") {
		$("#errorProvince").text("Vui lòng chọn tỉnh/thành!");
		$("#province").css("border-color", "#dc3545");
		$("#lblProvince").css("color", "#dc3545");
		check = 0;
	}
	else {
		$("#errorProvince").text("");
		$("#province").removeAttr("style");
		$("#lblProvince").css("color", "black");
		check++;
	}

	var district = $("#district").val();
	if (district == "") {
		$("#errorDistrict").text("Vui lòng chọn quận/huyện!");
		$("#district").css("border-color", "#dc3545");
		$("#lblDistrict").css("color", "#dc3545");
		check = 0;
	}
	else {
		$("#errorDistrict").text("");
		$("#district").removeAttr("style");
		$("#lblDistrict").css("color", "black");
		check++;
	}

	var ward = $("#ward").val();
	if (ward == "") {
		$("#errorWard").text("Vui lòng chọn phường/xã!");
		$("#ward").css("border-color", "#dc3545");
		$("#lblWard").css("color", "#dc3545");
		check = 0;
	}
	else {
		$("#errorWard").text("");
		$("#ward").removeAttr("style");
		$("#lblWard").css("color", "black");
		check++;
	}

	if (check == 6) {
		submit = true;
	}

	return submit;
}


function isVietnamesePhoneNumber(number) {
	return /(03|05|07|08|09|01[2|6|8|9])+([0-9]{8})\b/.test(number);
}

var app = angular.module("checkout-app", []);

app.controller("checkout-ctrl", function($scope, $http) {
	$scope.province = [];
	$scope.district = [];
	$scope.ward = [];
	$scope.form = {};
	$scope.shippingFee = 30000; // Phí ship mặc định
	$scope.initialize = function() {
		$http.get("/rest/province").then((resp) => {
			$scope.province = resp.data;
		});
	};

	$scope.initialize();

	// Hàm lấy danh sách tỉnh/thành phố
	  $scope.loadProvinces = function () {
	      $http.get("/rest/province").then((resp) => {
	          $scope.province = resp.data;
	      });
	  };

	  // Hàm lấy danh sách quận/huyện dựa theo tỉnh đã chọn
	  $scope.loadDistricts = function () {
	      var provinceId = $scope.form.provinceId;
	      if (provinceId) {
	          $http.get("/rest/district/" + provinceId).then((resp) => {
	              $scope.district = resp.data;
	              $scope.form.districtId = null;
	              $scope.ward = [];
	          });
	      }
	  };

	  // Hàm lấy danh sách phường/xã dựa theo quận/huyện đã chọn
	  $scope.loadWards = function () {
	      var provinceId = $scope.form.provinceId;
	      var districtId = $scope.form.districtId;
	      if (provinceId && districtId) {
	          $http.get("/rest/ward/" + provinceId + "/" + districtId).then((resp) => {
	              $scope.ward = resp.data;
	              $scope.form.wardId = null;
	          });
	      }
	  };
	// Tính phí ship
	$scope.calculateShippingFee = function () {
	    if ($scope.form.districtId && $scope.form.wardId) {  // ✅ Kiểm tra đã chọn đủ Quận/Huyện & Phường/Xã
	        var requestData = {
	            toDistrictId: parseInt($scope.form.districtId), // Chuyển thành số nguyên
	            toWardCode: $scope.form.wardId,
	            weight: 2000  // Trọng lượng mặc định
	        };

	        console.log("Gửi request tính phí ship:", requestData); // Debug kiểm tra dữ liệu

	        $http.post(`/rest/shipping-fee`, requestData)
	            .then((resp) => {
	                if (resp.data.fee !== undefined) {
	                    console.log("Phí ship nhận được:", resp.data.fee);
	                    $scope.shippingFee = resp.data.fee;
	                } else {
	                    console.error("Lỗi: Phí ship không hợp lệ.", resp.data);
	                    $scope.shippingFee = 30000;
	                }
	            })
	            .catch((error) => {
	                console.error("Lỗi khi lấy phí ship:", error);
	                alert("Không thể lấy phí ship. Vui lòng thử lại!");
	                $scope.shippingFee = 30000;
	            });
	    } else {
	        console.log("Chưa chọn đủ địa chỉ để tính phí ship.");
	        $scope.shippingFee = 30000;
	    }
	};


	   // Khởi tạo dữ liệu tỉnh/thành phố
	   $scope.loadProvinces();


	$scope.addAddress = function() {
		if (checkForm()) {
			$scope.form.province = $scope.province.find(p => p.id == $scope.form.provinceId)?.name || "";
			$scope.form.district = $scope.district.find(d => d.id == $scope.form.districtId)?.name || "";
			$scope.form.ward = $scope.ward.find(w => w.id == $scope.form.wardId)?.name || "";
			var item = angular.copy($scope.form);
			$http.post(`/rest/address/form`, item).then((resp) => {
				$("#alert").fadeIn(); // Hiển thị thông báo
				setTimeout(() => {
					$("#alert").fadeOut(); // Tự ẩn sau 5 giây
					location.reload(); // Reload sau khi thông báo hiển thị đủ lâu
				}, 3000);
			});
		}
	}
});


function transferAddress() {
	enableAddress();
	disableMethod();
	disableConfirm();
}

function transferMethod() {
	disableAddress();
	enableMethod();
	disableConfirm();
}

function transferConfirm() {
	disableAddress();
	disableMethod();
	enableConfirm();
}
