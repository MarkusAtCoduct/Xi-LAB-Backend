package com.codeleap.xilab.api.models.entities;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OrderColumn;
import javax.persistence.Table;

import com.codeleap.xilab.api.models.entities.auth.User;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@ToString
@Table(name = "method",
		indexes = @Index(columnList = "name"))
public class Method
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;

    private String descriptionBrief;

    @Column(name = "relevant_phases")
    private String relevantPhases;

    @Column(name = "change_history", columnDefinition = "TEXT")
    private String changeHistory;

    @Column(name = "is_parent")
    private Boolean isParent;

    @Column(name = "miro_link")
    private String miroLink;

    @Column(name = "when_to_conduct")
    private String whenToConduct;

    @Column(name = "how_to_conduct")
    private String howToConduct;

    private String input;

    private String output;

    @Column(name = "reference_info", columnDefinition = "TEXT")
    private String referencePhases;

    private Short certainPhase = 0;

    @Column(name = "advantages")
    private String advantages;

    @Column(name = "disadvantages")
    private String disadvantages;

    @Column(name = "same_output_methods")
    private String sameOutputMethods;

    private Integer time;

    private BigDecimal cost;

    @Column(name = "average_rating")
    private Double averageRating = 0d;

    @Column(name = "is_published")
    private Boolean isPublished;

    private Boolean isSet;

    @Column(name = "need_involvement")
    private Boolean needInvolvement;

    @Column(name = "created_on")
    private LocalDateTime createdOn = LocalDateTime.now();

    @ToString.Exclude
    @ManyToOne
    @JoinColumn(name = "created_by")
    private User createdByUser;

    private Long importedBy;

    @ToString.Exclude
    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = "method_set",
            joinColumns = @JoinColumn( name = "set_id", referencedColumnName = "id") ,
            inverseJoinColumns = @JoinColumn(name = "item_id", referencedColumnName = "id")
    )
    @OrderColumn(name="item_order")
    private List<Method> usedMethods;

    public String getSafeDescription(){
        return description == null ? "" : description;
    }

    public String getSafeBriefDescription(){
        return descriptionBrief == null ? getSafeDescription() : descriptionBrief;
    }
}
