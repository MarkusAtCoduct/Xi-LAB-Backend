package com.codeleap.xilab.api.models.entities;

import com.codeleap.xilab.api.models.entities.auth.User;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "method_rating",
		indexes = @Index(columnList = "headline"))
public class Rating
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long methodId;

    private LocalDateTime ratedOn;

    private Short score;

    private String headline;

    private String message;

    @ManyToOne
    @JoinColumn(name = "rated_by")
    private User ratedByUser;
}
