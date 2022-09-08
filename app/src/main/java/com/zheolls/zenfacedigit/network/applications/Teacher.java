package com.zheolls.zenfacedigit.network.applications;


/**
 * 管理老师 应用相关的常量数据
 *  - 应用名称
 *  - 可用 url
 */
public class Teacher {
    public static final String name = "teacher";
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
        // 更新用户的详细信息
        public static final String modifyUserInfo = "modifyUserInfo/";
        // 获取学校名称
        public static final String schoolName = "schoolName/";
        // 获取老师管理的所有班级 url
        public static final String getSupervisedClasses = "getSupervisedClasses/";
        // 获取老师监管的所有学生的基本信息
        public static final String studentsBaseInfo = "studentsBaseInfo/";
        // 获取老师监管的所有学生的详细信息 url
        public static final String studentsInfo = "studentsInfo/";
        // 获取老师监管的所有学生的名称的 url
        public static final String studentsName = "studentsName/";
        // 编辑霸凌历史
        public static final String modifyAlertHistory = "modifyAlertHistory/";
        // 将一个警告信息转化为一个霸凌历史记录
        public static final String finishAlertHistory = "finishAlertHistory/";
    }
    // 所有可用的文件辨识符
    public static final class fileTags {
        // 霸凌历史图片
        public static final String bully_history_image = "bully_history_image";
    }
}
