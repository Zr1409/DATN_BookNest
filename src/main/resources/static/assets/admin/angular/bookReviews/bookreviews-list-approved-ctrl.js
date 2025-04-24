angular.module("bookreviews-app", ["bookreviews-app.controllers", "datatables"]);
angular
  .module("bookreviews-app.controllers", [])
  .controller(
    "bookreviews-ctrl",
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
        $http.get("/rest/bookReviews/approved").then((resp) => {
          $scope.items = resp.data;
        });
      };
      $scope.initialize();

      $scope.formDetail = [];
      $scope.modalDetail = function (detail) {
        $http.get("/rest/bookReviews/pending/" + detail.id).then((resp) => {
          $scope.formDetail = resp.data;
        });
        $("#modalDetail").modal("show");
      };

      $scope.formDelete = {};
      $scope.showModal = function (item) {
        $scope.formDelete = item;
        $("#modal").modal("show");
      };

      $scope.delete = function () {
        $http
          .delete(`/rest/bookReviews/form/delete/` + $scope.formDelete.id)
          .then((resp) => {
            var index = $scope.items.findIndex(
              (p) => p.id == $scope.formDelete.id
            );
            $scope.items.splice(index, 1);

            $scope.info.status = true;
            $scope.info.alert = "Thành công!";
            $scope.info.content = "Bạn đã xóa bình luận thành công!";
            $("#modalInfo").modal("show");

          })
          .catch((error) => {          
          });
      };

      $scope.vm = {};
      $scope.vm.dtInstance = {};
      $scope.vm.dtColumnDefs = [
        DTColumnDefBuilder.newColumnDef(5).notSortable(),
      ];
      $scope.vm.dtOptions = DTOptionsBuilder.newOptions()
        .withOption("paging", true)
        .withOption("searching", true)
        .withOption("info", true)
		.withLanguage({
							"sProcessing": "Đang xử lý...",
							"sLengthMenu": "Hiển thị _MENU_ đánh giá",
							"sZeroRecords": "Không tìm thấy dữ liệu",
							"sInfo": "Hiển thị _START_ đến _END_ trong tổng số _TOTAL_ đánh giá",
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
