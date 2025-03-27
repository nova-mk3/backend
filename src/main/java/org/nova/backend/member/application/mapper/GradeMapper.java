package org.nova.backend.member.application.mapper;

import org.springframework.stereotype.Component;

@Component
public class GradeMapper {

    public int toIntGrade(String stringGrade) {
        return Integer.parseInt(String.valueOf(stringGrade.charAt(0)));
    }

    /**
     * @param stringSemester 1학기, 2학기
     * @return 이수학기
     */
    public int toCompletionSemester(int grade, String stringSemester) {
        int semester = Integer.parseInt(String.valueOf(stringSemester.charAt(0)));  //학기(1,2)
        return (grade - 1) * 2 + semester;
    }
}
