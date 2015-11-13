# FxcnBeta

cnBeta 第三方客户端

## 缺失的功能

添加评论以及回复评论功能均不能用，对应的 api 返回

``` json
{
	status: "error",
	result: {
		error_code: "ILLEGAL_CODE",
		error_msg: "评论参数错误"
	}
}
```