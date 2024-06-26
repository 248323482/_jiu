package com.jiu.base.controller;

import com.jiu.base.service.SuperService;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;

/**
 * SuperController
 * <p>
 * 继承该类，就拥有了如下方法：
 * 1，page 分页查询，并支持子类扩展4个方法：handlerQueryParams、query、handlerWrapper、handlerResult
 * 2，save 保存，并支持子类扩展方法：handlerSave
 * 3，update 修改，并支持子类扩展方法：handlerUpdate
 * 4，delete 删除，并支持子类扩展方法：handlerDelete
 * 5，get 单体查询， 根据ID直接查询DB
 * 6，list 列表查询，根据参数条件，查询列表
 * 7，import 导入，并支持子类扩展方法：handlerImport
 * 8，export 导出，并支持子类扩展3个方法：handlerQueryParams、query、handlerResult
 * 9，preview 导出预览，并支持子类扩展3个方法：handlerQueryParams、query、handlerResult
 * <p>
 * 其中 page、export、preview 的查询条件一致，若子类重写了 handlerQueryParams、query、handlerResult 等任意方法，均衡收到影响
 * <p>
 * 若重写扩展方法无法满足，则可以重写page、save等方法，但切记不要修改 @RequestMapping 参数
 *
 * @param <S>         Service
 * @param <Id>        主键
 * @param <Entity>    实体
 * @param <PageDTO>   分页参数
 * @param <SaveDTO>   保存参数
 * @param <UpdateDTO> 修改参数
 */
public abstract class SuperController<S extends SuperService<Entity>, Id extends Serializable, Entity, PageDTO, SaveDTO, UpdateDTO> extends SuperSimpleController<S, Entity>
implements  SaveController<Entity, SaveDTO>, UpdateController<Entity, UpdateDTO>, DeleteController<Entity, Id>, PoiController<Entity, PageDTO>, QueryController<Entity, Id, PageDTO> {


    @Override
    public Class<Entity> getEntityClass() {
        if (entityClass == null) {
            this.entityClass = (Class) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[2];
        }
        return this.entityClass;
    }
}
