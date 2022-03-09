package com.info.model;

import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import java.util.List;

/**
 * Created by Amit Gupta on 15,July,2020
 */
public class FaqQuestionModel extends ExpandableGroup<FaqAnswersModels> {
    public FaqQuestionModel(String title, List<FaqAnswersModels> items) {
        super(title, items);
    }
}
