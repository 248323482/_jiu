package com.jiu.entity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@ApiModel(value = "全局返回")
@AllArgsConstructor
public class Result<T> {
	@ApiModelProperty(value = "弹窗标题,如果有再显示")
	private String title; // 弹窗标题。如果有再显示
	@ApiModelProperty(value = "提示信息")
	private String message;
	@ApiModelProperty(value = "系统状态代码")
	private SYSTEM_CODE state; // 错误代码,默认没有错误为100,非100 显示state与message;
								// if(State==100){return value }else{return
								// state+message}
	@ApiModelProperty("返回内容")
	private T content; // 返回内容

	@ApiModel("状态代码")
	public enum SYSTEM_CODE {
		Success(200), 
		Error(500);
		private int code;
		SYSTEM_CODE(int code){
			this.code =code;
		}
		public int getCode(){
			return code;
		}
	}

}
