package com.info.adapter;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.info.adapter.viewHolders.FaqAnswerViewHolder;
import com.info.adapter.viewHolders.FaqQuestionViewHolder;
import com.info.logger.Logger;
import com.info.model.FaqAnswersModels;
import com.info.model.FaqQuestionModel;
import com.info.tradewyse.R;
import com.thoughtbot.expandablerecyclerview.ExpandableRecyclerViewAdapter;
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import java.util.List;

/**
 * Created by Amit Gupta on 15,July,2020
 */
public class FaqRvAdapter extends ExpandableRecyclerViewAdapter<FaqQuestionViewHolder, FaqAnswerViewHolder> {
    List<? extends ExpandableGroup> groups;
    Context context;
    RecyclerView recyclerView;
    public FaqRvAdapter(List<? extends ExpandableGroup> groups, Context context, RecyclerView recyclerView) {
        super(groups);
        this.groups = groups;
        this.context = context;
        this.recyclerView = recyclerView;
    }

    @Override
    public FaqQuestionViewHolder onCreateGroupViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.faq_question_row, parent, false);
        return new FaqQuestionViewHolder(view);
    }

    @Override
    public FaqAnswerViewHolder onCreateChildViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.faq_answer_row, parent, false);
        return new FaqAnswerViewHolder(view);
    }

    @Override
    public void onBindChildViewHolder(FaqAnswerViewHolder holder, int flatPosition, ExpandableGroup group, int childIndex) {
        final FaqAnswersModels faqAnswersModels = ((FaqQuestionModel) group).getItems().get(childIndex);
        holder.setArtistName(faqAnswersModels.getAnswer(), context);
    }

    @Override
    public void onBindGroupViewHolder(FaqQuestionViewHolder holder, int flatPosition, ExpandableGroup group) {
        holder.setQuestionText(group, flatPosition);
    }

    @Override
    public boolean onGroupClick(int flatPos) {
        int size = getGroups().size();
        for (int i = 0; i < size; i++) {
            if (isGroupExpanded(getGroups().get(i))) {
                if (flatPos != i) {
                    if(flatPos>i)
                        flatPos--;
                    toggleGroup(getGroups().get(i));

                    Logger.debug("FaqAdapter", "Toggle Group at position:- " + i);
                }
            }
        }
        if (flatPos == size) {
            flatPos--;
        }
        Logger.debug("FaqAdapter", "FlatPos:- " + flatPos + " and size:- " + size);
        return super.onGroupClick(flatPos);
    }
}
