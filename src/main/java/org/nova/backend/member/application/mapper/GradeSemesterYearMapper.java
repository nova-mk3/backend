package org.nova.backend.member.application.mapper;

import org.springframework.stereotype.Component;

@Component
public class GradeSemesterYearMapper {

    /**
     * 졸업연도 파싱
     *
     * @param stringYear ex)2024년, 2025년
     * @return ex)2024, 2025,..
     */
    public int toIntYear(String stringYear) {
        if (stringYear == null || stringYear.length() <= 1) {
            return 0;
        }
        return Integer.parseInt(stringYear.substring(0, stringYear.length() - 1));
    }

    /**
     * 학년 파싱
     *
     * @param stringGrade ex)1학년, 2학년,...
     * @return ex) 1,2,..
     */
    public int toIntGrade(String stringGrade) {
        if (stringGrade == null) return 0;

        if (stringGrade.equals("초과학기") || stringGrade.equals("초과 학기")) {
            return 5;
        }
        return Integer.parseInt(String.valueOf(stringGrade.charAt(0)));
    }


    /**
     * @param stringSemester 1학기, 2학기
     * @return 재학중인 학기
     */
    public int toIntCompletionSemester(String stringSemester) {
        if (stringSemester == null || stringSemester.length() <= 2) {
            return 0;
        }

        int semester = Integer.parseInt(stringSemester.substring(0, stringSemester.length() - 2));
        return Math.min(semester, 2); //학기는 1학기, 2학기 중 선택
    }

}
