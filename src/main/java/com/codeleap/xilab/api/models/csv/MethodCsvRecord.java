package com.codeleap.xilab.api.models.csv;

import java.math.BigDecimal;

import com.codeleap.xilab.api.models.entities.Method;
import com.codeleap.xilab.api.utils.DataUtils;
import com.codeleap.xilab.api.utils.StringUtils;
import com.opencsv.bean.CsvBindByPosition;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class MethodCsvRecord {

    @CsvBindByPosition(position = 0)
    private String name;

    @CsvBindByPosition(position = 1)
    private String brief;

    @CsvBindByPosition(position = 2)
    private String description;

    @CsvBindByPosition(position = 3)
    private String ownerFirstName;

    @CsvBindByPosition(position = 4)
    private String ownerLastName;

    @CsvBindByPosition(position = 5)
    private String ownerEmail;

    @CsvBindByPosition(position = 6)
    private String relevantPhases;

    @CsvBindByPosition(position = 7)
    private String changeHistory;

    @CsvBindByPosition(position = 8)
    private String whenToConduct;

    @CsvBindByPosition(position = 9)
    private String howToConduct;

    @CsvBindByPosition(position = 10)
    private String input;

    @CsvBindByPosition(position = 11)
    private String output;

    @CsvBindByPosition(position = 12)
    private String advantages;

    @CsvBindByPosition(position = 13)
    private String disadvantages;

    @CsvBindByPosition(position = 14)
    private String time;

    @CsvBindByPosition(position = 15)
    private String cost;

    @CsvBindByPosition(position = 16)
    private String isPublished;

    @CsvBindByPosition(position = 17)
    private String needInvolvement;

    @CsvBindByPosition(position = 18)
    private String reference;

    public boolean isValid(){
        if(StringUtils.isNullOrWhiteSpace(name))
            return false;
        if(StringUtils.isNullOrWhiteSpace(description))
            return false;
        if(StringUtils.isNullOrWhiteSpace(ownerFirstName))
            return false;
        if(StringUtils.isNullOrWhiteSpace(ownerLastName))
            return false;
        if(StringUtils.isNullOrWhiteSpace(ownerEmail))
            return false;

        return true;
    }

    public Method convertToCreateMethodRequest(){
        final short defaultPhase = 0;
        return new Method()
                .setName(name)
                .setAdvantages(advantages)
                .setDisadvantages(disadvantages)
                .setIsSet(false)
                .setAverageRating(0d)
                .setCertainPhase(defaultPhase)
                .setDescriptionBrief(brief)
                .setDescription(description)
                .setCost(StringUtils.isNullOrWhiteSpace(cost) ? null : new BigDecimal(cost))
                .setTime(StringUtils.isNullOrWhiteSpace(time) ? null : Integer.valueOf(time))
                .setRelevantPhases(relevantPhases)
                .setChangeHistory(changeHistory)
                .setInput(input)
                .setOutput(output)
                .setWhenToConduct(whenToConduct)
                .setHowToConduct(howToConduct)
                .setReferencePhases(reference)
                .setIsPublished(DataUtils.isYes(isPublished))
                .setNeedInvolvement(DataUtils.isYes(needInvolvement))
                ;
    }
}
