package com.codeleap.xilab.api.models.entities.auth;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user_avatar")
public class UserAvatar {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false, columnDefinition = "UNIQUEIDENTIFIER default NEWID()")
    private UUID id;

    private Long userId;

    @Lob
    @Column(name="thumb_avatar")
    @Type(type="org.hibernate.type.BinaryType")
    private byte[] thumbAvatar;

    @Lob
    @Column(name="main_avatar")
    @Type(type="org.hibernate.type.BinaryType")
    private byte[] mainAvatar;

}
