angular.module("warehouse-book-day-app", ["warehouse-book-day-app.controllers", "datatables"]);
angular
  .module("warehouse-book-day-app.controllers", [])
  .controller(
    "warehouse-book-day-ctrl",
    function (
      $scope,
      DTOptionsBuilder,
      DTColumnBuilder,
      DTColumnDefBuilder,
      $http
    ) {
      $scope.items = [];

      $scope.info = {};
      $scope.initialize = function () {
        $http.get("/rest/statistical/book/warehouse").then((resp) => {
          $scope.items = resp.data;
        });
      };
      $scope.initialize();

      $scope.vm = {};
      $scope.vm.dtInstance = {};
      $scope.vm.dtOptions = DTOptionsBuilder.newOptions()
        .withOption("paging", true)
        .withOption("searching", true)
        .withOption("info", true)
		.withLanguage({
													"sProcessing": "Đang xử lý...",
													"sLengthMenu": "Hiển thị _MENU_ sản phẩm tồn kho",
													"sZeroRecords": "Không tìm thấy dữ liệu",
													"sInfo": "Hiển thị _START_ đến _END_ trong số _TOTAL_ sản phẩm tồn kho",
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
