angular.module("order-success-app", ["order-success-app.controllers", "datatables"]);
angular
  .module("order-success-app.controllers", [])
  .controller(
    "order-success-ctrl",
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
        $http.get("/rest/order/success").then((resp) => {
          $scope.items = resp.data;
        });
      };
      $scope.initialize();

      $scope.formDetail = [];
      $scope.modalDetail = function (detail) {
        $http.get("/rest/order/pending/" + detail.id).then((resp) => {
          $scope.formDetail = resp.data;
		  //          $scope.formDetail = resp.data;
		  //		  // Gộp tỉnh/thành phố, quận/huyện và địa chỉ cụ thể
		  //		          $scope.formDetail.fullAddress = 
		  //		              ($scope.formDetail.address || "") + ", " + 
		  //					  ($scope.formDetail.ward || "") + ", " + 
		  //		              ($scope.formDetail.district || "") + ", " + 
		  //		              ($scope.formDetail.province || "");
		  //					 ;
        });
        $("#modalDetail").modal("show");
      };

      $scope.formCancel = {};
      $scope.showModal = function (item) {
        $scope.formCancel = item;
        $("#modal").modal("show");
      };

      $scope.vm = {};
      $scope.vm.dtInstance = {};
      $scope.vm.dtColumnDefs = [
        DTColumnDefBuilder.newColumnDef(6).notSortable(),
      ];
      $scope.vm.dtOptions = DTOptionsBuilder.newOptions()
        .withOption("paging", true)
        .withOption("searching", true)
        .withOption("info", true)
		.withLanguage({
											"sProcessing": "Đang xử lý...",
											"sLengthMenu": "Hiển thị _MENU_ đơn hàng thành công",
											"sZeroRecords": "Không tìm thấy dữ liệu",
											"sInfo": "Hiển thị _START_ đến _END_ trong tổng số _TOTAL_ đơn hàng thành công",
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
