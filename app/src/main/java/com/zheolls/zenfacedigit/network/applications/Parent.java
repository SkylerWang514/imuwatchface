package com.zheolls.zenfacedigit.network.applications;

/**
 * 监护人 应用相关的常量数据
 *  - 应用名称
 *  - 可用 url
 */
public class Parent {
    public static final String name = "parent";
    // 所有可用的 url
    public static final class urls {
        // 示例 url
        public static final String example = "example/";
        // 登录 url
        public static final String login = "login/";
        // 注册账户
        public static final String register = "register/";
        // 获取详细的用户信息
        public static final String userInfo = "userInfo/";
        // 更新用户的信息
        public static final String modifyUserInfo = "modifyUserInfo/";
        // 添加孩子的信息
        public static final String addChild = "addChild/";
        // 获取监护人孩子的基本信息
        public static final String childrenBaseInfo = "childrenBaseInfo/";
        // 获取监护人孩子心跳数据的统计信息
        public static final String childrenHeartBeatInfo = "childrenHeartBeatInfo/";
        // 获取监护人孩子的详细信息
        public static final String childrenInfo = "childrenInfo/";
        // 获取监护人孩子的姓名 url
        public static final String childrenName = "childrenName/";
        // 文件下载测试 url
        public static final String downloadTest = "downloadTest/";
    }
    // 所有可用的文件辨识符
    public static final class fileTags {

    }
}
