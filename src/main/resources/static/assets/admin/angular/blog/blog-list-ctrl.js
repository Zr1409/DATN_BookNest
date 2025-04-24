angular.module("blog-app", ["blog-app.controllers", "datatables"]);
angular
  .module("blog-app.controllers", [])
  .controller(
    "blog-ctrl",
    function (
      $scope,
      DTOptionsBuilder,
      DTColumnBuilder,
      DTColumnDefBuilder,
      $http
    ) {
      $scope.items = [];
      $scope.form = {};
      $scope.info = {};
      $scope.initialize = function () {
        $http.get("/rest/blog").then((resp) => {
          $scope.items = resp.data;
        });
      };
      $scope.initialize();

      $scope.showModal = function (item) {
        $scope.form = item;
        $("#modal").modal("show");
      };

      $scope.delete = function () {
        $http
          .delete(`/rest/blog/` + $scope.form.id)
          .then((resp) => {
            var index = $scope.items.findIndex((p) => p.id == $scope.form.id);
            $scope.items.splice(index, 1);

            $scope.info.status = true;
            $scope.info.alert = "Thành Công!";
            $scope.info.content = "Bạn đã xóa danh mục thành công!";
            $("#modalInfo").modal("show");

            //alert("Xoá sản phẩm thành công!");
          })
          .catch((error) => {
            console.log(error);
          });
      };

      $scope.update = function (item) {
        var path = "/admin/blogs/update/" + item.id;
        $("a").attr("href", path);
      };

      $scope.vm = {};
      $scope.vm.dtInstance = {};
      $scope.vm.dtColumnDefs = [
        DTColumnDefBuilder.newColumnDef(4).notSortable(),
      ];
      $scope.vm.dtOptions = DTOptionsBuilder.newOptions()
        .withOption("paging", true)
        .withOption("searching", true)
        .withOption("info", true)
		.withLanguage({
							"sProcessing": "Đang xử lý...",
							"sLengthMenu": "Hiển thị _MENU_ blogs",
							"sZeroRecords": "Không tìm thấy dữ liệu",
							"sInfo": "Hiển thị _START_ đến _END_ trong tổng số _TOTAL_ blogs",
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
