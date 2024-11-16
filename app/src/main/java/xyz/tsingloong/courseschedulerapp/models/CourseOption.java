package xyz.tsingloong.courseschedulerapp.models;

import java.util.List;

public class CourseOption {
    public String courseName; // 新增字段：课程名称
    public String section;    // 班级编号
    public List<String> timeSlots; // 时间段列表

    public CourseOption(String courseName, String section, List<String> timeSlots) {
        this.courseName = courseName;
        this.section = section;
        this.timeSlots = timeSlots;
    }

    @Override
    public String toString() {
        return "CourseOption{" +
                "courseName='" + courseName + '\'' +
                ", section='" + section + '\'' +
                ", timeSlots=" + timeSlots +
                '}';
    }
}
