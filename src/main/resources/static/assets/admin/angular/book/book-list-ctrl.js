angular.module("book-list-app", ["book-list-app.controllers", "datatables"]);
angular
	.module("book-list-app.controllers", [])
	.controller(
		"book-list-ctrl",
		function(
			$scope,
			DTOptionsBuilder,
			DTColumnBuilder,
			DTColumnDefBuilder,
			$http
		) {
			$scope.items = [];
			$scope.form = {};
			$scope.info = {};
			$scope.initialize = function() {
				$http.get("/rest/book").then((resp) => {
					$scope.items = resp.data;
				});
			};
			$scope.initialize();

			$scope.showModal = function(item) {
				$scope.form = item;
				$("#modal").modal("show");
			};

			$scope.delete = function() {
				$http
					.delete(`/rest/book/` + $scope.form.id)
					.then((resp) => {
						var index = $scope.items.findIndex((p) => p.id == $scope.form.id);
						$scope.items.splice(index, 1);

						$scope.info.status = true;
						$scope.info.alert = "Thành Công!";
						$scope.info.content = "Bạn đã xóa sản phẩm thành công!";
						$("#modalInfo").modal("show");

						//alert("Xoá sản phẩm thành công!");
					})
					.catch((error) => {
						console.log(error);
					});
			};

			$scope.update = function(item) {
				var path = "/admin/book/update/" + item.id;
				$("a").attr("href", path);
			};

			$scope.vm = {};
			$scope.vm.dtInstance = {};
			$scope.vm.dtColumnDefs = [
				DTColumnDefBuilder.newColumnDef(8).notSortable(),
			];
			$scope.vm.dtOptions = DTOptionsBuilder.newOptions()
				.withOption("paging", true)
				.withOption("searching", true)
				.withOption("info", true)
				//.withOption("pageLength", 5)       // Mặc định hiển thị 25 dòng
				//.withOption("lengthMenu", [5, 10, 15, 20, 100]); // Cho phép chọn số dòng
				.withLanguage({
					"sProcessing": "Đang xử lý...",
					"sLengthMenu": "Hiển thị _MENU_ sản phẩm",
					"sZeroRecords": "Không tìm thấy dữ liệu",
					"sInfo": "Hiển thị _START_ đến _END_ trong tổng số _TOTAL_ sản phẩm",
					"sInfoEmpty": "Hiển thị 0 đến 0 của 0 dòng",
					"sInfoFiltered": "(lọc từ _MAX_ dòng)",
					"sSearch": "Tìm kiếm:",
					"oPaginate": {
						"sFirst": "Đầu",
						"sLast": "Cuối",
						"sNext": "Tiếp",
						"sPrevious": "Trước"
					}
				});
		}
	);
