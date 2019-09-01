package com.jiu.config;

import com.jiu.entity.vo.Result;
import org.hibernate.validator.HibernateValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.ObjectError;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.List;
import java.util.stream.Collectors;



@Configuration
public class ValidatorConfig {

	@Bean
	public Validator validator() {
		ValidatorFactory factory = Validation.byProvider(HibernateValidator.class).configure()
				// 将fail_fast设置为true即可，如果想验证全部，则设置为false或者取消配置即可
				.addProperty("hibernate.validator.fail_fast", "true").buildValidatorFactory();
		return factory.getValidator();
	}

	@Bean
	public MethodValidationPostProcessor methodValidationPostProcessor() {
		MethodValidationPostProcessor postProcessor = new MethodValidationPostProcessor();
		/** 设置validator模式为快速失败返回 */
		postProcessor.setValidator(validator());
		return postProcessor;
	}
	@RestControllerAdvice
	public class GlobalExceptionHandler {
		@ExceptionHandler(MethodArgumentNotValidException.class)
		public Result<?> validationErrorHandler(MethodArgumentNotValidException ex) {
			// 同样是获取BindingResult对象，然后获取其中的错误信息
			// 如果前面开启了fail_fast，事实上这里只会有一个信息
			// 如果没有，则可能又多个
			List<String> errorInformation = ex.getBindingResult().getAllErrors().stream()
					.map(ObjectError::getDefaultMessage).collect(Collectors.toList());
			return new Result<>("", errorInformation.toString().replace("[", "").replace("]", ""), Result.SYSTEM_CODE.Error,
					"");
		}
	}

}