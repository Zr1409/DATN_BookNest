angular.module("top-book-day-app", ["top-book-day-app.controllers", "datatables"]);
angular
  .module("top-book-day-app.controllers", [])
  .controller(
    "top-book-day-ctrl",
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
        $http.get("/rest/statistical/book/day").then((resp) => {
          $scope.items = resp.data;
        });
      };
      $scope.initialize();

      $scope.vm = {};
      $scope.vm.dtInstance = {};
    //   $scope.vm.dtColumnDefs = [
    //     DTColumnDefBuilder.newColumnDef(6).notSortable(),
    //   ];
      $scope.vm.dtOptions = DTOptionsBuilder.newOptions()
        .withOption("paging", true)
        .withOption("searching", true)
        .withOption("info", true)
		.withLanguage({
											"sProcessing": "Đang xử lý...",
											"sLengthMenu": "Hiển thị _MENU_ sản phẩm bán chạy",
											"sZeroRecords": "Không tìm thấy dữ liệu",
											"sInfo": "Hiển thị _START_ đến _END_ trong số _TOTAL_ sản phẩm bán chạy",
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
