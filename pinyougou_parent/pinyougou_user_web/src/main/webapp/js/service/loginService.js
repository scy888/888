app.service("loginService",function ($http) {
    this.loginName=function () {
        return $http.get("login/info.do");
    }
    this.findOrderByUserId=function () {
        return $http.get("login/findOrderByUserId.do");
    }
})