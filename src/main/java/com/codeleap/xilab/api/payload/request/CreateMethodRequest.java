package com.codeleap.xilab.api.payload.request;

import java.math.BigDecimal;
import java.util.List;

import javax.validation.constraints.NotBlank;

import com.codeleap.xilab.api.models.entities.Method;
import com.codeleap.xilab.api.utils.StringUtils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateMethodRequest {

    @NotBlank(message = "Method name should not be blank")
    private String name;

    private String description;

    private String descriptionBrief;

    private List<String> relevantPhases;

    private String changeHistory;

    private Boolean isParent;

    private String miroLink;

    private List<String> howToConduct;

    private List<String> whenToConduct;

    private List<String> input;

    private List<String> output;

    private List<String> advantages;

    private List<String> disadvantages;

    private List<String> sameOutputMethods;

    private List<String> references;

    private BigDecimal cost;

    private Integer time;

    private Short certainPhase;

    private Boolean isPublished;

    private Boolean isMethodSet = false;

    private List<Long> usedMethodIds;

    private Boolean needInvolvement;

    private Short getCertainPhase() {
        if (this.certainPhase == null)
            return 0;
        return this.certainPhase;
    }

    public void updateDBEntity(Method method) {
        if(method == null){
            return;
        }

        method.setName(name)
                .setDescription(description)
                .setDescriptionBrief(descriptionBrief)
                .setCost(cost)
                .setTime(time)
                .setIsParent(isParent)
                .setCertainPhase(getCertainPhase())
                .setIsPublished(isPublished)
                .setNeedInvolvement(needInvolvement)
                .setAdvantages(StringUtils.toDBArrayFormat(advantages))
                .setDisadvantages(StringUtils.toDBArrayFormat(disadvantages))
                .setReferencePhases(StringUtils.toDBArrayFormat(references))
                .setRelevantPhases(StringUtils.toDBArrayFormat(relevantPhases))
                .setChangeHistory(changeHistory)
                .setMiroLink(miroLink)
                .setInput(StringUtils.toDBArrayFormat(input))
                .setOutput(StringUtils.toDBArrayFormat(output))
                .setHowToConduct(StringUtils.toDBArrayFormat(howToConduct))
                .setWhenToConduct(StringUtils.toDBArrayFormat(whenToConduct))
                .setSameOutputMethods(StringUtils.toDBArrayFormat(sameOutputMethods))
        ;
    }

    public Method toDBEntity() {
        if(this.isMethodSet == null){
            this.isMethodSet = false;
        }

        return new Method()
                .setName(name)
                .setDescription(description)
                .setDescriptionBrief(descriptionBrief)
                .setCost(cost)
                .setTime(time)
                .setIsParent(isParent)
                .setIsSet(isMethodSet)
                .setCertainPhase(getCertainPhase())
                .setIsPublished(isPublished)
                .setNeedInvolvement(needInvolvement)
                .setAdvantages(StringUtils.toDBArrayFormat(advantages))
                .setDisadvantages(StringUtils.toDBArrayFormat(disadvantages))
                .setReferencePhases(StringUtils.toDBArrayFormat(references))
                .setRelevantPhases(StringUtils.toDBArrayFormat(relevantPhases))
                .setChangeHistory(changeHistory)
                .setMiroLink(miroLink)
                .setInput(StringUtils.toDBArrayFormat(input))
                .setOutput(StringUtils.toDBArrayFormat(output))
                .setHowToConduct(StringUtils.toDBArrayFormat(howToConduct))
                .setWhenToConduct(StringUtils.toDBArrayFormat(whenToConduct))
                .setSameOutputMethods(StringUtils.toDBArrayFormat(sameOutputMethods))
                ;
    }
}
