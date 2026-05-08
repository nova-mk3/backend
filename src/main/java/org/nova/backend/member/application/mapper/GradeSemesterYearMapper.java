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
        if (stringYear == null || stringYear.trim().isEmpty() || stringYear.length() <= 1) {
            return 0;
        }
        try {
            return Integer.parseInt(stringYear.substring(0, stringYear.length() - 1));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /**
     * 학년 파싱
     *
     * @param stringGrade ex)1학년, 2학년,...
     * @return ex) 1,2,..
     */
    public int toIntGrade(String stringGrade) {
        if (stringGrade == null || stringGrade.trim().isEmpty()) {
            return 0;
        }

        if (stringGrade.equals("초과학기") || stringGrade.equals("초과 학기")) {
            return 5;
        }
        
        try {
            return Integer.parseInt(String.valueOf(stringGrade.charAt(0)));
        } catch (NumberFormatException | StringIndexOutOfBoundsException e) {
            return 0;
        }
    }


    /**
     * @param stringSemester 1학기, 2학기
     * @return 재학중인 학기
     */
    public int toIntCompletionSemester(String stringSemester) {
        if (stringSemester == null || stringSemester.trim().isEmpty() || stringSemester.length() <= 2) {
            return 0;
        }

        try {
            int semester = Integer.parseInt(stringSemester.substring(0, stringSemester.length() - 2));
            return Math.min(semester, 2); //학기는 1학기, 2학기 중 선택
        } catch (NumberFormatException e) {
            return 0;
        }
    }

}
