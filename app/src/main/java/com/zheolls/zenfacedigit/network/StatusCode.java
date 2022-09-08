package com.zheolls.zenfacedigit.network;


import java.util.HashSet;
import java.util.NoSuchElementException;

/**
 * 代表不同含义的状态码枚举常量
 * - 可以在这里添加不同应用会使用到的状态码
 * - 添加自定义的状态码时请 务必添加 description(描述信息)
 * - 这里的枚举成员需要保持与服务器的同步
 */
public enum StatusCode {
    // 常用的一些状态码
    Ok(200, "一切OK"),
    BadRequest(400),
    Forbidden(403),
    NotFound(404),
    ServerError(500, "服务器错误"),

    // 一些预定义的公用状态码
    Unset(201, "未设置状态码，服务器逻辑错误"),
    Unknown(202, "未知错误"),
    RequestMethodError(203, "请求方式错误"),

    // 文件相关的状态码，从210开始，占有5个
    FileSaveError(210, "文件保存失败"),
    NoFileBodyError(211, "没有在请求中找到文件体"),
    FileNotFound(212, "文件不存在"),

    // 数据库相关的状态码，从215开始，占有15个
    DatabaseError(215, "未预见的数据库操作错误"),
    UserInfoError(216, "用户名或密码错误"),
    GradeInfoError(217, "年级班级信息错误"),
    // 用途比较宽泛的一个状态码
    NoDataError(218, "没有相关数据"),
    NotWritable(219, "数据不可写入数据库"),

    // 心跳相关的状态码，从230开始，占有10个
    // StatusName(230, "description"),

    // 管理员相关状态码，从240开始，占有10个
    // StatusName(240, "description"),

    // 教师相关状态码，从250开始，占有10个
    // StatusName(250, "description"),
    SchoolInfoError(250, "学校信息错误"),
    AlertHistoryInfoError(251, "霸凌历史信息错误"),

    // 监护人相关状态码，从260开始，占有10个
    // StatusName(260, "description"),
    NoRelation(260, "没有监护人学生关系"),
    AddChildFailed(261, "添加孩子失败"),
    AddRelationFailed(262, "添加监护人孩子关系失败"),

    // 学生相关状态码，从270开始，占有10个
    // StatusName(270, "description"),
    BindWatchIdFailed(270, "绑定手表失败"),

    // 剩余状态码，从280开始，占有20个
    // StatusName(280, "description"),

    // 这里的分号标志着枚举成员定义的结束，不可以删除
    ;

    // 用来维护跟某个枚举常量相关的数据，不需关心
    private final int statusCode;
    private final String description;

    /**
     * 构造一个枚举成员
     *
     * @param statusCode 对应的状态码值
     * @param description 与该状态码对应的描述信息
     */
    private StatusCode(int statusCode, String description) {
        StatusCodeValue.checkRepeat(statusCode);
        this.statusCode = statusCode;
        this.description = description;
    }

    /**
     * 构造一个枚举成员
     * 该构造函数不接受描述信息，将自动将描述信息初始化为枚举常量的名称
     * 例如：ErrorStatus(600) 的 description 就是 ErrorStatus
     *
     * @param statusCode 对应的状态码值
     */
    private StatusCode(int statusCode) {
        StatusCodeValue.checkRepeat(statusCode);
        this.statusCode = statusCode;
        this.description = name();
    }

    /**
     * 获取该枚举常量对应的描述信息，在处理多个状态码的情形下比较有用
     *
     * 比如上传多个文件时，将会针对每一个文件都返回一个状态码，
     * 在遇到问题时，使用该方法可以比较优雅方便地构造提示信息或日志
     */
    public String getDescription() {
        return description;
    }

    /**
     * 获取枚举成员对应的 状态码值
     */
    public int getStatusCodeValue() {
        return statusCode;
    }

    /**
     * 从一个整型值构造一个枚举常量实例
     * 若找不到该状态码值对应的枚举常量，则抛出 NoSuchElementException 异常
     */
    public static StatusCode fromValue(int statusCode) {
        // 首先检查该状态码值是否已注册
        if (!StatusCodeValue.exists(statusCode)) {
            throw new NoSuchElementException("没有 {" + statusCode + "} 对应的枚举成员，" +
                    "请检查你是否在枚举成员中定义了该状态码值对应的枚举成员");
        }
        for (StatusCode value : values()) {
            if (value.statusCode == statusCode) {
                return value;
            }
        }
        // 这里理论上永远不会被执行，仅仅是为了除去编译器的告警
        return StatusCode.Ok;
    }
}


/**
 * 用于保证 枚举类 StatusCode 的 statusCode 唯一性
 *
 * java enum 初始化顺序是先初始化所有的 枚举成员
 * 所有的枚举成员都是静态变量，然后再初始化类内部的其他变量，
 * 所以直接在枚举类内部定义静态集合实现 状态码检重 是做不到的，
 * 必须要在枚举类的外部实现检重策略
 */
class StatusCodeValue {
    // 已被用于定义枚举成员的 状态码值
    private static final HashSet<Integer> registeredStatusCodes = new HashSet<>();

    /**
     * 检查 statusCode 对应的枚举成员是否已经被添加
     * 若已添加将抛出 IllegalArgumentException 异常
     * 否则则将其添加到已注册列表中
     */
    public static void checkRepeat(int statusCode) {
        if (exists(statusCode)) {
            throw new IllegalArgumentException("该状态码值 {" + statusCode + "} 对应的枚举成员已存在");
        }
        registeredStatusCodes.add(statusCode);
    }

    /**
     * 检查是否存在某个状态码值
     */
    public static boolean exists(int statusCode) {
        return registeredStatusCodes.contains(statusCode);
    }
}

