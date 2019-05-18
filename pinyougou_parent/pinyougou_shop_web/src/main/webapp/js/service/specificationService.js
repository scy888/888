//服务层
app.service('specificationService',function($http){
	    	
	//读取列表数据绑定到表单中
	this.findAll=function(loginName){
		return $http.get('../specification/findAll.do?loginName='+loginName);
	}
	//分页 
	this.findPage=function(page,rows){
		return $http.get('../specification/findPage.do?page='+page+'&rows='+rows);
	}
	//查询实体
	this.findOne=function(id){
		return $http.get('../specification/findOne.do?id='+id);
	}
	//增加 
	this.add=function(entity,loginName){
		return  $http.post('../specification/sellerAdd.do?loginName='+loginName,entity );
	}
	//修改 
	this.update=function(entity){
		return  $http.post('../specification/update.do',entity );
	}
	//删除
	this.dele=function(ids){
		return $http.get('../specification/delete.do?ids='+ids);
	}
	//搜索
	this.search=function(sellerId,page,rows,searchEntity){
		return $http.post('../specification/sellerSearch.do?sellerId='+sellerId+'&page='+page+"&rows="+rows, searchEntity);
	}    	
});
