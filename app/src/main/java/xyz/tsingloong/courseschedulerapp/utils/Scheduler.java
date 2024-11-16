package xyz.tsingloong.courseschedulerapp.utils;

import xyz.tsingloong.courseschedulerapp.models.Course;
import xyz.tsingloong.courseschedulerapp.models.CourseOption;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Scheduler {
    private int minEarlyClasses = Integer.MAX_VALUE;
    private List<CourseOption> bestSchedule = new ArrayList<>();
    private boolean solutionFound = false;

    // 定义早八时间段
    public static final Set<String> EARLY_SLOTS = Set.of("一1-2", "二1-2", "三1-2", "四1-2", "五1-2");

    /**
     * 主方法：选择最优课程安排
     */
    public List<CourseOption> selectOptimalCourses(List<Course> courses) {
        recurse(0, courses, new ArrayList<>(), new HashSet<>(), 0);
        return solutionFound ? bestSchedule : null;
    }

    /**
     * 递归搜索最优课程安排
     */
    private void recurse(int courseIndex, List<Course> courses, List<CourseOption> currentSchedule,
                         Set<String> selectedTimes, int earlyClassCount) {
        if (courseIndex == courses.size()) {
            // 所有课程都已处理
            solutionFound = true;
            if (earlyClassCount < minEarlyClasses) {
                minEarlyClasses = earlyClassCount;
                bestSchedule = new ArrayList<>(currentSchedule);
            }
            return;
        }

        Course course = courses.get(courseIndex);

        for (CourseOption option : course.options) {
            // 检查时间冲突
            if (hasTimeConflict(selectedTimes, option.timeSlots)) {
                continue;
            }

            // 判断是否包含早八课程
            boolean containsEarly = option.timeSlots.stream().anyMatch(this::isEarly);
            currentSchedule.add(option);
            selectedTimes.addAll(option.timeSlots);

            // 递归处理下一个课程
            recurse(courseIndex + 1, courses, currentSchedule, selectedTimes,
                    earlyClassCount + (containsEarly ? 1 : 0));

            // 回溯
            selectedTimes.removeAll(option.timeSlots);
            currentSchedule.remove(currentSchedule.size() - 1);
        }
    }

    /**
     * 判断是否为早八时间段
     */
    private boolean isEarly(String time) {
        return EARLY_SLOTS.contains(time);
    }

    /**
     * 检查时间冲突
     */
    private boolean hasTimeConflict(Set<String> selectedTimes, List<String> timeSlots) {
        for (String time : timeSlots) {
            if (selectedTimes.contains(time)) {
                return true;
            }
        }
        return false;
    }

    public int getMinEarlyClasses() {
        return minEarlyClasses;
    }

    public boolean isSolutionFound() {
        return solutionFound;
    }
}
