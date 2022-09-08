package com.zheolls.zenfacedigit.network.applications;


/**
 * 数据库操作 应用相关的常量数据
 *  - 应用名称
 *  - 可用 url
 */
public class Database {
    public static final String name = "database_operator";
    // 所有可用的 url
    public static final class urls {
        // 获取数据库的版本号
        public static final String databaseVersion = "databaseVersion/";
        // 获取所有的年级班级信息
        public static final String allGradeInfo = "allGradeInfo/";
        // 获取年级班级信息
        public static final String gradeInfo = "gradeInfo/";
        // 直接通过 url 访问文件的url
        public static final String files = "files/";
        // 获取某名老师监管的所有学生的警告历史
        public static final String alertHistoryOfTeacher = "alertHistoryOfTeacher/";
        // 获取某名学生的警告历史
        public static final String alertHistoryOfStudent = "alertHistoryOfStudent/";
        // 获取某名监护人孩子的警告历史
        public static final String alertHistoryOfParent = "alertHistoryOfParent/";
        // 添加一组用户的推送信息
        public static final String addUserPushInfo = "addUserPushInfo/";
        // 更新用户头像
        public static final String setAvatar = "setAvatar/";
        // 获取所有学校的详细信息
        public static final String allSchoolInfo = "allSchoolInfo/";
        // 获取指定昵称的数量
        public static final String nicknameCount = "nicknameCount/";
        // 获取指定手机号的数量
        public static final String phoneCount = "phoneCount/";
    }
    // 所有可用的文件辨识符
    public static final class fileTags {
        public static final String avatar = "avatar";
    }
}
