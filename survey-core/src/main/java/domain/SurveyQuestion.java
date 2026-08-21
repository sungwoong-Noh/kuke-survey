package domain;

import java.util.List;

public class SurveyQuestion {

    private String title;
    private String description;
    private QuestionType type;
    private boolean isRequired;
    private int orderNo;

    private List<SurveyQuestionOption> options;

}
