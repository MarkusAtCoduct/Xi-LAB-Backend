package com.codeleap.xilab.api.models.entities;

import java.time.LocalDateTime;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.codeleap.xilab.api.models.entities.auth.User;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user_badge",
		indexes = @Index(columnList = "user_id"))
public class UserBadge
{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false, columnDefinition = "UNIQUEIDENTIFIER default NEWID()")
    private UUID id;

    private String badgeType;

    private LocalDateTime achievedOn;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User belongToUser;
}
