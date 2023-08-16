package com.codeleap.xilab.api.payload.response.method;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import com.codeleap.xilab.api.models.entities.Method;
import com.codeleap.xilab.api.payload.response.data.MethodSetItem;
import com.codeleap.xilab.api.utils.CollectionUtils;
import com.codeleap.xilab.api.utils.StringUtils;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class MethodResponse {

    private Long id;

    private String name;

    private String description;

    private String descriptionBrief;

    private List<String> relevantPhases;

    private String changeHistory;

    private String miroLink; 

    private Boolean isParent;

    private List<String> howToConduct;

    private List<String> whenToConduct;

    private List<String> input;

    private List<String> output;

    private List<String> advantages;

    private List<String> disadvantages;

    private List<String> sameOutputMethods;

    private List<String> references;

    private List<String> usedMethods;

    private BigDecimal cost;

    private Integer time;

    private Boolean editable;

    private Boolean isPublished;

    private Boolean isMethodSet;

    private Boolean needInvolvement;

    private Double rate;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    List<MethodSetItem> simpleUsedMethods;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    List<MethodResponse> fullUsedMethods;

    private String owner;

    private List<String> ownerBadges;

    private Long ownerId;

    private Integer numberOfComments;

    private List<MethodSetItem> getUsedMethodsSimple(Method method){
        return method.getUsedMethods().stream().filter(x -> x != null)
                .map(x -> new MethodSetItem()
                .setId(x.getId())
                .setDescription(x.getSafeBriefDescription())
                .setName(x.getName()))
                .collect(Collectors.toList());
    }

    private List<MethodResponse> getUsedMethodsFull(Method method, Long loginUserId){
        return method.getUsedMethods().stream().filter(x -> x != null)
                .map(x -> new MethodResponse(x, loginUserId, true))
                .collect(Collectors.toList());
    }

    public MethodResponse(Method method, Long loginUserId, boolean forList) {
        try {
            if (method != null) {
                if (!CollectionUtils.isNullOrNoItem(method.getUsedMethods())) {
                    if (forList) {
                        this.setSimpleUsedMethods(getUsedMethodsSimple(method));
                    } else {
                        this.setFullUsedMethods(getUsedMethodsFull(method, loginUserId));
                    }
                }

                this.setName(method.getName())
                        .setId(method.getId())
                        .setDescription(method.getSafeDescription())
                        .setDescriptionBrief(method.getSafeBriefDescription())
                        .setIsParent(method.getIsParent())
                        .setCost(method.getCost())
                        .setTime(method.getTime())
                        .setOwnerId(method.getCreatedByUser().getId())
                        .setOwner(method.getCreatedByUser().getFullName())
                        .setOwnerBadges(method.getCreatedByUser().getBadgeNames())
                        .setRate(method.getAverageRating())
                        .setIsMethodSet(false)
                        .setIsMethodSet(method.getIsSet())
                        .setIsPublished(method.getIsPublished())
                        .setNeedInvolvement(method.getNeedInvolvement())
                        .setEditable(method.getCreatedByUser().getId().equals(loginUserId))
                        .setAdvantages(StringUtils.fromDBArrayFormat(method.getAdvantages()))
                        .setDisadvantages(StringUtils.fromDBArrayFormat(method.getDisadvantages()))
                        .setReferences(StringUtils.fromDBArrayFormat(method.getReferencePhases()))
                        .setRelevantPhases(StringUtils.fromDBArrayFormat(method.getRelevantPhases()))
                        .setChangeHistory(method.getChangeHistory())
                        .setMiroLink(method.getMiroLink())
                        .setInput(StringUtils.fromDBArrayFormat(method.getInput()))
                        .setOutput(StringUtils.fromDBArrayFormat(method.getOutput()))
                        .setHowToConduct(StringUtils.fromDBArrayFormat(method.getHowToConduct()))
                        .setWhenToConduct(StringUtils.fromDBArrayFormat(method.getWhenToConduct()))
                        .setSameOutputMethods(StringUtils.fromDBArrayFormat(method.getSameOutputMethods()))
                ;
            }
        }catch (Exception e){
            var i =1;
        }
    }

}
