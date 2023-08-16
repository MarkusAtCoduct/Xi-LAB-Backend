package com.codeleap.xilab.api.models.entities.auth;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import com.codeleap.xilab.api.models.entities.Comment;
import com.codeleap.xilab.api.models.entities.Method;
import com.codeleap.xilab.api.models.entities.Rating;
import com.codeleap.xilab.api.models.entities.UserBadge;
import com.codeleap.xilab.api.utils.CollectionUtils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString(exclude = {"authInfo", "myMethods", "myComments", "myBadges", "myRatings"})
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users",
		uniqueConstraints = { @UniqueConstraint(columnNames = "email") },
		indexes = @Index(columnList = "email"))
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank
	@Size(max = 40)
	private String lastName;

	@NotBlank
	@Size(max = 40)
	private String firstName;

	@NotBlank
	@Size(max = 80)
	@Email
	private String email;

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"),
			inverseJoinColumns = @JoinColumn(name = "role_id"))
	private Set<Role> roles = new HashSet<>();

    @Column(name = "created_on")
    private LocalDateTime createdOn = LocalDateTime.now();

    @Column(name = "updated_on")
    private LocalDateTime updatedOn = LocalDateTime.now();

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "user")
    private AuthInfo authInfo;

    private String gender;

    private String industry;

    private String currentJob;

    private String aboutMe;

    private String linkedinLink;

    private short yearsOfExperience;

    private Boolean hasAvatar;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "createdByUser")
    private List<Method> myMethods;

    
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "commentedByUser")
    private List<Comment> myComments;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "belongToUser")
    private List<UserBadge> myBadges;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "ratedByUser")
    private List<Rating> myRatings;

    public String getFullName(){
        return String.format("%s %s", firstName, lastName);
    }

    public List<String> getBadgeNames(){
        if(CollectionUtils.isNullOrNoItem(myBadges))
            return null;
        return myBadges.stream().map(x -> x.getBadgeType()).collect(Collectors.toList());
    }
}
