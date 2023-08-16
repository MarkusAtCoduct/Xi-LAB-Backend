package com.codeleap.xilab.api.payload.request.auth;

import com.codeleap.xilab.api.models.entities.auth.User;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.Data;

@Data
public class UpdateProfileRequest {

    @NotBlank(message = "The first name must not be blank")
    @Size(min = 2, max = 45,
            message = "The first name '${validatedValue}' must be between {min} and {max} characters long")
    private String firstName;

    @NotBlank(message = "The last name must not be blank")
    @Size(min = 2, max = 45,
            message = "The last name '${validatedValue}' must be between {min} and {max} characters long")
    private String lastName;

    private String gender;

	private String industry;

	private String currentJob;

	private Short yearsOfExperience;

	private String linkedinLink;

	private String aboutMe;

	public void updateExistedEntity(User user){
	    if(user == null){
	        return;
        }

	    user.setAboutMe(this.aboutMe);
	    user.setFirstName(this.firstName);
	    user.setLastName(this.lastName);
	    user.setCurrentJob(this.currentJob);
	    user.setIndustry(this.industry);
	    user.setGender(this.gender);
	    user.setLinkedinLink(this.linkedinLink);
	    user.setYearsOfExperience(this.yearsOfExperience);
    }
}
