package xyz.tsingloong.courseschedulerapp.models;

import java.util.ArrayList;
import java.util.List;

public class Course {
    public String name; // 课程名称
    public List<CourseOption> options; // 班级选项列表

    public Course(String name) {
        this.name = name;
        this.options = new ArrayList<>();
    }

    @Override
    public String toString() {
        return "Course{" +
                "name='" + name + '\'' +
                ", options=" + options +
                '}';
    }
}
