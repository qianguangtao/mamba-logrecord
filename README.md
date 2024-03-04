﻿﻿# 操作记录

## 前章

项目中我们会遇到这样的需求，记录核心业务表所有操作记录（数据变化），用来做安全审计，记录谁在什么时候改变了什么数据，并将操作记录入库提供查询。操作记录本质是比较前端传入字段和数据库字段进行比较，将变更记录到数据库。

问题：

1. po或dto的哪些字段要进行比较。
2. po或dto的字段如果是枚举、字典、集合等复杂字段，比如性别，数据库存的1|2，前端要展示男|女，如何统一处理。
3. oldObj如何获取，新增的时候，数据库没有记录；编辑的时候，怎么获取id去查对应的记录？
4. oldObj和newObj的比较，要嵌入到各个业务代码里么？

## logrecord-starter

本工程下**starter/logrecord-starter**基于切面+注解解决了上述问题，可以无侵入的记录操作，针对复杂字段的中文说明做了统一的处理，并且可扩展。

涉及的技术点：

1. Spring aop
2. Spel表达式
3. Reflect反射
4. 模板方法模式
5. Disruptor队列
6. Mybatis TypeHandler

## 表设计

## UML类图

## 使用说明

### po或dto属性添加注解@LogRecordField

```java
@Target({ElementType.FIELD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface LogRecordField {
    /** 被标注的字段的中文名 */
    String value() default "";

    /** 空值描述 */
    String nullDesc() default "空";

    /** 填充策略 */
    FieldStrategy fieldStrategy() default FieldStrategy.DEFAULT;

    /** 修改描述翻译（基本数据类型） */
    Class<? extends Translator> translator() default Translator.None.class;

    /** 自定义字段比较模板 */
    Class<? extends TranslatorTemplate> translatorTemplate() default TranslatorTemplate.None.class;
}
```

下面示例了
1. 普通字段：@LogRecordField(value = "用户名")，标记字段中文名即可
2. 枚举字段：@LogRecordField(value = "用户类型", translator = EnumTranslator.class)，基于EnumDefinition的注解，要添加转换器translator = EnumTranslator.class
3. 字典字段：@LogRecordField(value = "用户来源")，标记字段中文名即可，会根据@Dictionary(DictionaryEnum.Names.UserSource)找字典中文
4. List比较器：@LogRecordField(value = "用户类型", translator = ListTranslator.class)，ListTranslator中，将List转为json string输出比较
5. 自定义比较器：@LogRecordField(value = "用户类型", translatorTemplate = AddressTranslatorTemplate.class)，在AddressTranslatorTemplate中，开发人员根据Object oldObject, Object newObject比较差异

```java
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@ApiModel(description = "用户表")
@TableName(value = "user", autoResultMap = true)
public class User extends BaseEntity {

    @LogRecordField(value = "用户名")
    @ApiModelProperty(value = "用户名")
    private String username;

    @ApiModelProperty(value = "密码")
    @JsonIgnore
    private String password;

    @LogRecordField(value = "手机号")
    @ApiModelProperty(value = "手机号")
    private String cellphone;

    @LogRecordField(value = "邮箱")
    @ApiModelProperty(value = "邮箱")
    private String email;

    @Max(100)
    @Min(1)
    @ApiModelProperty(value = "年龄")
    @LogRecordField(value = "年龄", fieldStrategy = FieldStrategy.UPDATE)
    private Integer age;

    @LogRecordField(value = "用户来源")
    @Dictionary(DictionaryEnum.Names.UserSource)
    @ApiModelProperty(value = "用户来源")
    private String source;

    @LogRecordField(value = "用户类型", translator = EnumTranslator.class)
    @ApiModelProperty(value = "用户类型")
    private SexEnum sex;

    @LogRecordField(value = "用户类型", translator = ListTranslator.class)
    @ApiModelProperty(value = "用户类型")
    @TableField(value = "roles", typeHandler = StringListTypeHandler.class)
    private List<String> roles;

    @LogRecordField(value = "用户类型", translator = BoolTranslator.class)
    @ApiModelProperty(value = "用户类型")
    private Boolean enabled;

    @LogRecordField(value = "用户类型", translatorTemplate = AddressTranslatorTemplate.class)
    @ApiModelProperty(value = "用户类型")
    private String address;

}
```

#### 自定义翻译模板（抽象模板）

```java
public abstract class TranslatorTemplate {
    /**
     * 自定义翻译器抽象模板方法
     * @param oldObject
     * @param newObject
     * @return
     */
    public abstract List<FieldDiffDTO> translate(Object oldObject, Object newObject, LogRecordField logRecordField);

    public abstract class None extends TranslatorTemplate {
        @Override
        public List<FieldDiffDTO> translate(Object oldObject, Object newObject, LogRecordField logRecordField) {
            return new ArrayList<>();
        }
    }
}

```

注意
1. 自己根据oldObject，newObject判断是否字段有修改，构造List<FieldDiffDTO>，处理EditType
2. fieldDiffDTO的setName(), setFieldName()可以不塞（根据实际情况），ObjectDiffUtil会塞值

```java
public class TestTranslatorTemplate extends TranslatorTemplate {
    @Override
    public List<FieldDiffDTO> translate(Object oldObject, Object newObject, LogRecordField logRecordField) {
        FieldDiffDTO fieldDiffDTO = new FieldDiffDTO();
        fieldDiffDTO.setFieldName("age")
                .setName("年龄")
                .setOldValue(12)
                .setNewValue(13)
                .setNewValueShow(13)
                .setOldValueShow(12)
                .setEditType(EditType.UPDATE);
        return ListUtil.of(fieldDiffDTO);
    }
}
```

### service方法添加@LogRecord

```java
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface LogRecord {

    /** 必填，业务主键spel表达式 */
    String key() default "";

    /** 必填，描述文本 */
    String desc() default "";

    /** 必填，操作类型，使用枚举LogOperate.Type.APPLY */
    String operateType() default "";

    /** 修改时候必填，获取数据库记录的spel表达式。 */
    String method() default "";

    /** 必填，
     * 新增的时候，用来反射newInstance出空对象进行下一步比较；
     * 修改的时候，Spel执行查库方法返回Object转的Bean。
     */
    Class oldObjClass() default Object.class;
}
```

#### 新增service接口

key = "#result.id"，使用Spel从返回值中取businessId

```java
@LogRecord(key = "#result.id",
		desc = "新增用户",
		operateType = LogOperate.Type.SAVE,
		oldObjClass = User.class)
@Transactional(rollbackFor = Exception.class)
@Override
public User insert(@LogRecordModel("userDto") UserDto userDto) {
	User user = BeanUtil.toBean(userDto, User.class);
	user.setAddress(JSON.toJSONString(userDto.getAddress()));
	this.save(user);
	return user;
}
```

#### 更新service接口

key = "@userMapper.selectById(#root)"，Spel执行userMapper.selectById(#root)查询数据库记录。

参数#root由LogRecordAop获取，这里填写#root即可。

```java
@LogRecord(key = "#userDto.id",
		desc = "更新用户",
		operateType = LogOperate.Type.UPDATE,
		method = "@userMapper.selectById(#root)",
		oldObjClass = User.class)
@Transactional(rollbackFor = Exception.class)
@Override
public User edit(@LogRecordModel("userDto") UserDto userDto) {
	User user = BeanUtil.toBean(userDto, User.class);
	user.setAddress(JSON.toJSONString(userDto.getAddress()));
	this.updateById(user);
	return user;
}
```

#### service方法参数添加@LogRecordModel

@LogRecordModel用来标记newObj

```java
@Override
public User insert(@LogRecordModel("userDto") UserDto userDto)
```

## 测试
### 新增用户
POST http://127.0.0.1:8080/users
请求参数
```java
{
    "source": "1",
    "age": 35,
    "address": {
        "country": "中国",
        "province": "云南省",
        "city": "六盘水市"
    },
    "roles": [
        "admin",
        "teacher"
    ],
    "sex": "1",
    "email": "12345@qq.com",
    "cellphone": "18657158538",
    "username": "李四"
}
```
日志打印ObjectDiffDTO
```java
{
    "businessId": "5",
    "description": "新增用户",
    "fieldDiffDTOList": [
        {
            "editType": "SAVE",
            "fieldName": "country",
            "name": "国家",
            "newValue": "中国",
            "newValueShow": "中国",
            "oldValueShow": "空"
        },
        {
            "editType": "SAVE",
            "fieldName": "province",
            "name": "省",
            "newValue": "云南省",
            "newValueShow": "云南省",
            "oldValueShow": "空"
        },
        {
            "editType": "SAVE",
            "fieldName": "city",
            "name": "市",
            "newValue": "六盘水市",
            "newValueShow": "六盘水市",
            "oldValueShow": "空"
        },
        {
            "editType": "SAVE",
            "fieldName": "sex",
            "name": "用户类型",
            "newValue": "Female",
            "newValueShow": "男性",
            "oldValueShow": "空"
        },
        {
            "editType": "SAVE",
            "fieldName": "roles",
            "name": "用户类型",
            "newValue": [
                "admin",
                "teacher"
            ],
            "newValueShow": "[\"admin\",\"teacher\"]",
            "oldValueShow": "空"
        },
        {
            "editType": "SAVE",
            "fieldName": "cellphone",
            "name": "手机号",
            "newValue": "18657158538",
            "newValueShow": "18657158538",
            "oldValueShow": "空"
        },
        {
            "editType": "SAVE",
            "fieldName": "source",
            "name": "用户来源",
            "newValue": "1",
            "newValueShow": "系统",
            "oldValueShow": "空"
        },
        {
            "editType": "SAVE",
            "fieldName": "email",
            "name": "邮箱",
            "newValue": "12345@qq.com",
            "newValueShow": "12345@qq.com",
            "oldValueShow": "空"
        },
        {
            "editType": "SAVE",
            "fieldName": "age",
            "name": "年龄",
            "newValue": 35,
            "newValueShow": 35,
            "oldValueShow": "空"
        },
        {
            "editType": "SAVE",
            "fieldName": "username",
            "name": "用户名",
            "newValue": "李四",
            "newValueShow": "李四",
            "oldValueShow": "空"
        }
    ],
    "jsonAfter": "{\"address\":{\"city\":\"六盘水市\",\"country\":\"中国\",\"province\":\"云南省\"},\"age\":35,\"cellphone\":\"18657158538\",\"email\":\"12345@qq.com\",\"roles\":[\"admin\",\"teacher\"],\"sex\":\"Female\",\"source\":\"1\",\"username\":\"李四\"}",
    "jsonBefore": "{}",
    "logOperate": "SAVE",
    "newClassName": "com.app.demo.pojo.dto.UserDto",
    "oldClassName": "com.app.demo.entity.User",
    "operatorName": "ANONYMOUS"
}

```
![image](https://github.com/qianguangtao/mamba-logrecord/assets/6427290/930745cd-6c6a-4c75-806c-5192623394fd)
数据库截图，其中：
性别枚举SexEnum保存了中文“男性”；
用户来源保存了字典表dict_item中的中文“系统”；
operation_field
![image](https://github.com/qianguangtao/mamba-logrecord/assets/6427290/c67d3a53-72a7-4eab-a35e-6df2bb52ee21)
dict_item
![image](https://github.com/qianguangtao/mamba-logrecord/assets/6427290/6649a52e-8a01-4770-be77-cb7f9e27dd9f)

### 修改用户
PUT http://127.0.0.1:8080/users
请求参数
手机号：18657158538改成18682575358
地址：云南省六盘水市改成安徽省芜湖市
年龄：35改36
```java
{
    "email": "12345@qq.com",
    "roles": [
        "admin",
        "teacher"
    ],
    "id": 5,
    "cellphone": "18682575358",
    "address": {
        "city": "芜湖市",
        "province": "安徽省",
        "country": "中国"
    },
    "username": "李四",
    "age": 36
}
```
日志打印ObjectDiffDTO
```java
{
    "businessId": "5",
    "description": "更新用户",
    "fieldDiffDTOList": [
        {
            "editType": "UPDATE",
            "fieldName": "province",
            "name": "省",
            "newValue": "安徽省",
            "newValueShow": "安徽省",
            "oldValue": "云南省",
            "oldValueShow": "云南省"
        },
        {
            "editType": "UPDATE",
            "fieldName": "city",
            "name": "市",
            "newValue": "芜湖市",
            "newValueShow": "芜湖市",
            "oldValue": "六盘水市",
            "oldValueShow": "六盘水市"
        },
        {
            "editType": "UPDATE",
            "fieldName": "cellphone",
            "name": "手机号",
            "newValue": "18682575358",
            "newValueShow": "18682575358",
            "oldValue": "18657158538",
            "oldValueShow": "18657158538"
        },
        {
            "editType": "UPDATE",
            "fieldName": "age",
            "name": "年龄",
            "newValue": 36,
            "newValueShow": 36,
            "oldValue": 35,
            "oldValueShow": 35
        }
    ],
    "jsonAfter": "{\"address\":{\"city\":\"芜湖市\",\"country\":\"中国\",\"province\":\"安徽省\"},\"age\":36,\"cellphone\":\"18682575358\",\"email\":\"12345@qq.com\",\"id\":5,\"roles\":[\"admin\",\"teacher\"],\"username\":\"李四\"}",
    "jsonBefore": "{\"address\":\"{\\\"city\\\": \\\"六盘水市\\\", \\\"country\\\": \\\"中国\\\", \\\"province\\\": \\\"云南省\\\"}\",\"age\":35,\"cellphone\":\"18657158538\",\"createBy\":922337203685477,\"createTime\":1709535747000,\"deleted\":false,\"email\":\"12345@qq.com\",\"enabled\":true,\"id\":5,\"roles\":[\"admin\",\"teacher\"],\"sex\":\"Female\",\"source\":\"1\",\"updateBy\":922337203685477,\"updateTime\":1709535747000,\"username\":\"李四\"}",
    "logOperate": "UPDATE",
    "newClassName": "com.app.demo.pojo.dto.UserDto",
    "oldClassName": "com.app.demo.entity.User",
    "operatorName": "ANONYMOUS"
}

```
![image](https://github.com/qianguangtao/mamba-logrecord/assets/6427290/bada427b-2a42-452a-be08-abd83e81aa8f)

operation_field
![image](https://github.com/qianguangtao/mamba-logrecord/assets/6427290/e77785af-2e33-4702-a328-e45fd2107b84)

