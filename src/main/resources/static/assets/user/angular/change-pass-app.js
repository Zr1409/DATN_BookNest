var app = angular.module("change-pass-app", []);

app.controller("change-pass-ctrl", function($scope, $http) {
    $scope.form = {};
    $scope.formPass = {};

    // Khởi tạo dữ liệu form
    $scope.initialize = function() {
        $http.get("/rest/user/account").then((resp) => {
            $scope.form = resp.data;
            $scope.form.birthday = new Date($scope.form.birthday);
            console.log(resp.data);
        });
    };

    // Khởi tạo form
    $scope.initialize();

    // Định nghĩa hàm checkForm trong $scope để Angular nhận diện
    $scope.checkForm = function(form) {
        var check = 0;
        var submit = false;
        var oldPass = $scope.form.oldPass;  // Lấy giá trị từ ng-model
        var passwordPattern = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\W).{8,}$/; // ✅ Yêu cầu mật khẩu mạnh
        if (oldPass == "") {
            $("#errorOldPass").text("Mật khẩu cũ không được bỏ trống!");
            $("#oldPass").css("border-color", "#dc3545");
            $("#lblOldPass").css("color", "#dc3545");
            check = 0;
        }
        else {
            $("#errorOldPass").text("");
            $("#oldPass").removeAttr("style");
            $("#lblOldPass").css("color", "black");
            check++;
        }

        var newPass = $scope.form.newPass;  // Lấy giá trị từ ng-model
        if (newPass == "") {
            $("#errorNewPass").text("Mật khẩu mới không được bỏ trống!");
            $("#newPass").css("border-color", "#dc3545");
            $("#lblNewPass").css("color", "#dc3545");
            check = 0;
        }
        else {
            if (!passwordPattern.test(newPass)) {
                $("#errorNewPass").text("Mật khẩu phải có ít nhất 8 ký tự, gồm chữ hoa, chữ thường và ký tự đặc biệt!");
                $("#newPass").css("border-color", "#dc3545");
                $("#lblNewPass").css("color", "#dc3545");
                check = 0;
            }
            else {
                // So sánh mật khẩu mới với mật khẩu cũ
                if (newPass == $scope.form.oldPass) {
                    $("#errorNewPass").text("Mật khẩu mới không được giống với mật khẩu cũ!");
                    $("#newPass").css("border-color", "#dc3545");
                    $("#lblNewPass").css("color", "#dc3545");
                    check = 0;
                }
                else {
                    $("#errorNewPass").text("");
                    $("#newPass").removeAttr("style");
                    $("#lblNewPass").css("color", "black");
                    check++;
                }
            }
        }

        var confirmPass = $scope.form.confirmPass;  // Lấy giá trị từ ng-model
        if (confirmPass == "") {
            $("#errorConfirmPass").text("Xác nhận mật khẩu không được bỏ trống!");
            $("#confirmPass").css("border-color", "#dc3545");
            $("#lblConfirmPass").css("color", "#dc3545");
            check = 0;
        }
        else {
            if (confirmPass != newPass) {
                $("#errorConfirmPass").text("Xác nhận mật khẩu không khớp!");
                $("#confirmPass").css("border-color", "#dc3545");
                $("#lblConfirmPass").css("color", "#dc3545");
                check = 0;
            }
            else {
                $("#errorConfirmPass").text("");
                $("#confirmPass").removeAttr("style");
                $("#lblConfirmPass").css("color", "black");
                check++;
            }
        }

        if (check == 3) {
            submit = true;
        }

        return submit;
    };

    // Hàm cập nhật mật khẩu
    $scope.update = function() {
        console.log($scope.form); // In ra dữ liệu form để kiểm tra
        // Kiểm tra form trước khi gửi yêu cầu
        if ($scope.checkForm($scope.form)) {
            // Tạo đối tượng ChangePassModel
            var changePassModel = {
                oldPass: $scope.form.oldPass,
                newPass: $scope.form.newPass,
                confirmPass: $scope.form.confirmPass
            };

            // Gửi yêu cầu PUT với đối tượng changePassModel
            $http.put(`/rest/user/account/change-password`, changePassModel).then(resp => {
                $("#alert").fadeIn(); // Hiển thị thông báo
                setTimeout(() => {
                    $("#alert").fadeOut(); // Tự ẩn sau 5 giây
                    location.reload(); // Reload sau khi thông báo hiển thị đủ lâu
                }, 3000);
            }).catch(error => {
                alert("Lỗi cập nhật")
            });
        }
    };

});
