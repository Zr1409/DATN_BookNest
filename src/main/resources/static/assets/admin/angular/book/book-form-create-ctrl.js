var checkCode = 0;
var checkName = 0;
var checkAuthor = 0;
var checkPrice = 0;
var checkQuantity = 0;
var checkImage1 = 0;
var checkImage2 = 0;
var checkImage3 = 0;
var checkImage4 = 0;
var checkImage5 = 0;
var checkNameSearch = 0;
var checkSubBookCategory = 0;
var checkPublisher = 0;
var checkActive = 0;
var checkDescription = 0;
var checkPublicationYear = 0;
var checkSales = 0;
var checkWeight = 0;
var checkDimensions = 0;
var checkPageCount = 0;
var checkFormat = 0;

$(document).ready(function() {
	$("#code").keyup(function() {
		var code = this.value;
		if (code == "") {
			$("#code").addClass("is-invalid");
			$("#showErrorCode").text("Vui lòng nhập mã sản phẩm!");
			checkCode = 10;
		} else {
			var length = code.length;
			var minLength = $("#code").attr("data-minlength");
			var maxLength = $("#code").attr("data-maxlength");

			if (length < minLength || length > maxLength) {
				$("#code").addClass("is-invalid");
				$("#showErrorCode").text("Nhập giá trị từ 5 đến 10 ký tự!");
				checkCode = 10;
			} else {
				$("#code").removeClass("is-invalid");
				$("#showErrorCode").text("");
				checkCode = 1;
			}
		}
		handlerButtonSave();
	});

	$("#name").keyup(function() {
		var name = this.value;
		if (name == "") {
			$("#name").addClass("is-invalid");
			$("#showErrorName").text("Vui lòng nhập tên sách!");
			checkName = 10;
		} else {
			var length = name.length;
			var minLength = $("#name").attr("data-minlength");
			var maxLength = $("#name").attr("data-maxlength");

			if (length < minLength || length > maxLength) {
				$("#name").addClass("is-invalid");
				$("#showErrorName").text("Nhập giá trị từ 5 đến 50 ký tự!");
				checkName = 10;
			} else {
				$("#name").removeClass("is-invalid");
				$("#showErrorName").text("");
				checkName = 1;
			}
		}
		handlerButtonSave();
	});
	$("#author").keyup(function() {
		var author = this.value;
		if (author == "") {
			$("#author").addClass("is-invalid");
			$("#showErrorAuthor").text("Vui lòng nhập tên tác giả!");
			checkAuthor = 10;
		} else {
			var length = author.length;
			var minLength = $("#author").attr("data-minlength");
			var maxLength = $("#author").attr("data-maxlength");

			if (length < minLength || length > maxLength) {
				$("#author").addClass("is-invalid");
				$("#showErrorAuthor").text("Nhập giá trị từ 5 đến 50 ký tự!");
				checkAuthor = 10;
			} else {
				$("#author").removeClass("is-invalid");
				$("#showErrorAuthor").text("");
				checkAuthor = 1;
			}
		}
		handlerButtonSave();
	});

	$("#price").keyup(function() {
		var price = this.value;
		if (price == 0) {
			$("#price").addClass("is-invalid");
			$("#showErrorPrice").text("Vui lòng nhập giá sản phẩm!");
			checkPrice = 10;
		} else {
			if (price < 1000) {
				$("#price").addClass("is-invalid");
				$("#showErrorPrice").text("Giá sản phẩm phải lớn hơn 1 nghìn!");
				checkPrice = 10;
			} else {
				$("#price").removeClass("is-invalid");
				$("#showErrorPrice").text("");
				checkPrice = 1;
			}
		}
		handlerButtonSave();
	});

	$("#sales").keyup(function() {
		var price = $("#price").val();
		var sales = this.value;
		if (parseInt(sales) > parseInt(price)) {
			$("#sales").addClass("is-invalid");
			$("#showErrorSales").text("Giá khuyến mãi phải nhỏ hơn giá sách!");
			checkSales = 10;
		} else {
			$("#sales").removeClass("is-invalid");
			$("#showErrorSales").text("");
			checkSales = 1;
		}
		handlerButtonSave();
	});

	$("#quantity").keyup(function() {
		var quantity = this.value;
		if (quantity <= 0) {
			$("#quantity").addClass("is-invalid");
			$("#showErrorQuantity").text("Vui lòng nhập số lượng sản phẩm!");
			checkQuantity = 10;
		} else {
			$("#quantity").removeClass("is-invalid");
			$("#showErrorQuantity").text("");
			checkQuantity = 1;
		}
		handlerButtonSave();
	});


	$("#chooseImage1").change(function() {
		var image1 = this.value;
		if (image1 == "") {
			$("#valueImage1").addClass("is-invalid");
			$("#showErrorImage1").text("Vui lòng chọn hình ảnh 1!");
			checkImage1 = 10;
		} else {
			angular.element(this).scope().imageImage1Changed(this.files);
			$("#valueImage1").removeClass("is-invalid");
			$("#showErrorImage1").text("");
			checkImage1 = 1;
		}
		handlerButtonSave();
	});

	$("#chooseImage2").change(function() {
		var image2 = this.value;
		if (image2 == "") {
			$("#valueImage2").addClass("is-invalid");
			$("#showErrorImage2").text("Vui lòng chọn hình ảnh 2!");
			checkImage2 = 10;
		} else {
			angular.element(this).scope().imageImage2Changed(this.files);
			$("#valueImage2").removeClass("is-invalid");
			$("#showErrorImage2").text("");
			checkImage2 = 1;
		}
		handlerButtonSave();
	});

	$("#chooseImage3").change(function() {
		var image3 = this.value;
		if (image3 == "") {
			$("#valueImage3").addClass("is-invalid");
			$("#showErrorImage3").text("Vui lòng chọn hình ảnh 3!");
			checkImage3 = 10;
		} else {
			angular.element(this).scope().imageImage3Changed(this.files);
			$("#valueImage3").removeClass("is-invalid");
			$("#showErrorImage3").text("");
			checkImage3 = 1;
		}
		handlerButtonSave();
	});

	$("#chooseImage4").change(function() {
		var image4 = this.value;
		if (image4 == "") {
			$("#valueImage4").addClass("is-invalid");
			$("#showErrorImage4").text("Vui lòng chọn hình ảnh 4!");
			checkImage4 = 10;
		} else {
			angular.element(this).scope().imageImage4Changed(this.files);
			$("#valueImage4").removeClass("is-invalid");
			$("#showErrorImage4").text("");
			checkImage4 = 1;
		}
		handlerButtonSave();
	});

	$("#chooseImage5").change(function() {
		var image5 = this.value;
		if (image5 == "") {
			$("#valueImage5").addClass("is-invalid");
			$("#showErrorImage5").text("Vui lòng chọn hình ảnh 5!");
			checkImage5 = 10;
		} else {
			angular.element(this).scope().imageImage5Changed(this.files);
			$("#valueImage5").removeClass("is-invalid");
			$("#showErrorImage5").text("");
			checkImage5 = 1;
		}
		handlerButtonSave();
	});

	$("#nameSearch").keyup(function() {
		var nameSearch = this.value;
		if (nameSearch == "") {
			$("#nameSearch").addClass("is-invalid");
			$("#showErrorNameSearch").text("Vui lòng nhập tên tìm kiếm sản phẩm!");
			checkNameSearch = 10;
		} else {
			var length = nameSearch.length;
			var minLength = $("#nameSearch").attr("data-minlength");
			var maxLength = $("#nameSearch").attr("data-maxlength");

			if (length < minLength || length > maxLength) {
				$("#nameSearch").addClass("is-invalid");
				$("#showErrorNameSearch").text("Nhập giá trị từ 5 đến 50 ký tự!");
				checkNameSearch = 10;
			} else {
				$("#nameSearch").removeClass("is-invalid");
				$("#showErrorNameSearch").text("");
				checkNameSearch = 1;
			}
		}
		handlerButtonSave();
	});

	$("#subBookCategory").change(function() {
			var subBookCategory = this.value;
			if (subBookCategory == "") {
				$("#subBookCategory").addClass("is-invalid");
				$("#showErrorSubBookCategory").text("Vui lòng chọn thể loại!");
				checkSubBookCategory = 10;
			}
			else {
				$("#subBookCategory").removeClass("is-invalid");
				$("#showErrorSubBookCategory").text("");
				checkSubBookCategory = 1;
			}
			handlerButtonSave();
		});


	$("#publisher").change(function() {
		var publisher = this.value;
		if (publisher == "") {
			$("#publisher").addClass("is-invalid");
			$("#showErrorPublisher").text("Vui lòng chọn tên nhà sản xuất!");
			checkPublisher = 10;
		} else {
			$("#publisher").removeClass("is-invalid");
			$("#showErrorPublisher").text("");
			checkPublisher = 1;
		}
		handlerButtonSave();
	});

	$("#active").change(function() {
		var active = this.value;
		if (active == "") {
			$("#active").addClass("is-invalid");
			$("#showErrorActive").text("Vui lòng chọn trạng thái!");
			checkActive = 10;
		} else {
			$("#active").removeClass("is-invalid");
			$("#showErrorActive").text("");
			checkActive = 1;
		}
		handlerButtonSave();
	});

	$("#description").keyup(function() {
		var description = this.value;
		if (description == "") {
			$("#description").addClass("is-invalid");
			$("#showErrorDescription").text("Vui lòng nhập mô tả sản phẩm!");
			checkDescription = 10;
		} else {
			var length = description.length;
			var minLength = $("#description").attr("data-minlength");

			if (length < minLength) {
				$("#description").addClass("is-invalid");
				$("#showErrorDescription").text("Mô tả phải lớn hơn 200 ký tự!");
				checkDescription = 10;
			} else {
				$("#description").removeClass("is-invalid");
				$("#showErrorDescription").text("");
				checkDescription = 1;
			}
		}
		handlerButtonSave();
	});


	$("#publicationYear").keyup(function() {
		var publicationYear = this.value;
		if (publicationYear == "") {
			$("#publicationYear").addClass("is-invalid");
			$("#showErrorPublicationYear").text("Vui lòng nhập năm xuất bản!");
			checkPublicationYear = 10;
		} else if (publicationYear.length !== 4 || publicationYear < 1900 || publicationYear > new Date().getFullYear()) {
			$("#publicationYear").addClass("is-invalid");
			$("#showErrorPublicationYear").text("Năm xuất bản không hợp lệ!");
			checkPublicationYear = 10;
		} else {
			$("#publicationYear").removeClass("is-invalid");
			$("#showErrorPublicationYear").text("");
			checkPublicationYear = 1;
		}
		handlerButtonSave();
	});


	$("#weight").keyup(function() {
		var weight = this.value;
		if (weight == "") {
			$("#weight").addClass("is-invalid");
			$("#showErrorWeight").text("Vui lòng nhập trọng lượng!");
			checkWeight = 10;
		} else {
			var length = weight.length;
			var minLength = $("#weight").attr("data-minlength");

			if (length < minLength) {
				$("#weight").addClass("is-invalid");
				$("#showErrorWeight").text();
				checkWeight = 10;
			} else {
				$("#weight").removeClass("is-invalid");
				$("#showErrorWeight").text("");
				checkWeight = 1;
			}
		}
		handlerButtonSave();
	});
	$("#dimensions").keyup(function() {
		var dimensions = this.value;
		if (dimensions == "") {
			$("#dimensions").addClass("is-invalid");
			$("#showErrorDimensions").text("Vui lòng nhập kích thước!");
			checkDimensions = 10;
		} else {
			var length = dimensions.length;
			var minLength = $("#dimensions").attr("data-minlength");

			if (length < minLength) {
				$("#dimensions").addClass("is-invalid");
				$("#showErrorDimensions").text();
				checkDimensions = 10;
			} else {
				$("#dimensions").removeClass("is-invalid");
				$("#showErrorDimensions").text("");
				checkDimensions = 1;
			}
		}
		handlerButtonSave();
	});

	$("#pageCount").keyup(function() {
		var pageCount = this.value;
		if (pageCount == "") {
			$("#pageCount").addClass("is-invalid");
			$("#showErrorPageCount").text("Vui lòng nhập số trang!");
			checkPageCount = 10;
		} else {
			var length = pageCount.length;
			var minLength = $("#pageCount").attr("data-minlength");

			if (length < minLength) {
				$("#pageCount").addClass("is-invalid");
				$("#showErrorPageCount").text();
				checkPageCount = 10;
			} else {
				$("#pageCount").removeClass("is-invalid");
				$("#showErrorPageCount").text("");
				checkPageCount = 1;
			}
		}
		handlerButtonSave();
	});
	$("#format").keyup(function() {
		var format = this.value;
		if (format == "") {
			$("#format").addClass("is-invalid");
			$("#showErrorFormat").text("Vui lòng nhập hình thức sách!");
			checkFormat = 10;
		} else {
			var length = format.length;
			var minLength = $("#format").attr("data-minlength");

			if (length < minLength) {
				$("#format").addClass("is-invalid");
				$("#showErrorFormat").text();
				checkFormat = 10;
			} else {
				$("#format").removeClass("is-invalid");
				$("#showErrorFormat").text("");
				checkFormat = 1;
			}
		}
		handlerButtonSave();
	});
});

function checkForm() {
	$("#code").keyup();
	$("#name").keyup();
	$("#author").keyup();
	$("#price").keyup();
	$("#quality").keyup();
	$("#chooseImage1").change();
	$("#chooseImage2").change();
	$("#chooseImage3").change();
	$("#chooseImage4").change();
	$("#chooseImage5").change();
	$("#nameSearch").keyup();
	$("#subBookCategory").change();
	$("#publisher").change();
	$("#active").change();
	$("#quantity").keyup();
	$("#description").keyup();
	$("#publicationYear").keyup();
	$("#weight").keyup();
	$("#dimensions").keyup();
	$("#pageCount").keyup();
	$("#format").keyup();
	return handlerButtonSave();
}

function handlerButtonSave() {
	var check = false;
	if (
		checkCode !== 10 &&
		checkName !== 10 &&
		checkAuthor !== 10 &&
		checkPrice !== 10 &&
		checkQuantity !== 10 &&
		checkImage1 !== 10 &&
		checkImage2 !== 10 &&
		checkImage3 !== 10 &&
		checkImage4 !== 10 &&
		checkImage5 !== 10 &&
		checkNameSearch !== 10 &&
		checkPublisher !== 10 &&
		checkSubBookCategory !== 10 &&
		checkActive !== 10 &&
		checkPublicationYear !== 10 &&
		checkDescription !== 10 &&
		checkSales !== 10 &&
		checkWeight !== 10 &&
		checkDimensions !== 10 &&
		checkPageCount !== 10 &&
		checkFormat !== 10
	) {
		check = true;
		$("#btnSave").prop("disabled", false);
	} else {
		check = false;
		$("#btnSave").prop("disabled", true);
	}
	return check;
}

var app = angular.module("book-form-app", []);

app.controller("book-form-ctrl", function($scope, $http) {
	$scope.itemSubBookCategory  = [];
	$scope.itemPublisher = [];
	$scope.form = {};
	$scope.info = {};

	$scope.initializeCate = function() {
		$http.get("/rest/subBookCategory").then((resp) => {
			$scope.itemSubBookCategory = resp.data;
		});
	};

	$scope.initializePublisher = function() {
		$http.get("/rest/publisher").then((resp) => {
			$scope.itemPublisher = resp.data;
		});
	};
	$scope.initializeCate();
	$scope.initializePublisher();

	$scope.save = function() {
		if (checkForm()) {
			var item = angular.copy($scope.form);
			$http.post(`/rest/book/form`, item).then((resp) => {
				$scope.info.status = true;
				$scope.info.content = "Bạn đã thêm mới thành công một sản phẩm!";
				$("#modalSuccess").modal("show");
				var path = "/admin/book/form";
				$("a").attr("href", path);
				console.log(resp);
			});
		}
	};

	// upload image 1
	$scope.imageImage1Changed = function(files) {
		var data = new FormData();
		data.append("file", files[0]);
		$http
			.post("/rest/upload/imageProduct", data, {
				transformRequest: angular.identity,
				headers: { "Content-Type": undefined },
			})
			.then((resp) => {
				$scope.form.image1 = resp.data.name;
			})
			.catch((error) => {
				alert("loi load hinh");
				console.log(error);
			});
	};

	// upload image 2
	$scope.imageImage2Changed = function(files) {
		var data = new FormData();
		data.append("file", files[0]);
		$http
			.post("/rest/upload/imageProduct", data, {
				transformRequest: angular.identity,
				headers: { "Content-Type": undefined },
			})
			.then((resp) => {
				$scope.form.image2 = resp.data.name;
			})
			.catch((error) => {
				alert("loi load hinh");
				console.log(error);
			});
	};

	// upload image 3
	$scope.imageImage3Changed = function(files) {
		var data = new FormData();
		data.append("file", files[0]);
		$http
			.post("/rest/upload/imageProduct", data, {
				transformRequest: angular.identity,
				headers: { "Content-Type": undefined },
			})
			.then((resp) => {
				$scope.form.image3 = resp.data.name;
			})
			.catch((error) => {
				alert("loi load hinh");
				console.log(error);
			});
	};

	// upload image 4
	$scope.imageImage4Changed = function(files) {
		var data = new FormData();
		data.append("file", files[0]);
		$http
			.post("/rest/upload/imageProduct", data, {
				transformRequest: angular.identity,
				headers: { "Content-Type": undefined },
			})
			.then((resp) => {
				$scope.form.image4 = resp.data.name;
			})
			.catch((error) => {
				alert("loi load hinh");
				console.log(error);
			});
	};

	// upload image 5
	$scope.imageImage5Changed = function(files) {
		var data = new FormData();
		data.append("file", files[0]);
		$http
			.post("/rest/upload/imageProduct", data, {
				transformRequest: angular.identity,
				headers: { "Content-Type": undefined },
			})
			.then((resp) => {
				$scope.form.image5 = resp.data.name;
			})
			.catch((error) => {
				alert("loi load hinh");
				console.log(error);
			});
	};
});
