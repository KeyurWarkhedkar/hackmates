package com.keyur.hackmates.Services;

import com.keyur.hackmates.DTOs.ApiResponseDto;
import com.keyur.hackmates.DTOs.UserSignupDto;
import com.keyur.hackmates.Entities.Interest;
import com.keyur.hackmates.Entities.User;
import com.keyur.hackmates.Repositories.InterestRepository;
import com.keyur.hackmates.Repositories.UserRepository;
import com.keyur.hackmates.Security.Users.SecurityUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {
    //fields
    UserRepository userRepository;
    private final SecurityUtils securityUtils;
    private final CloudinaryService cloudinaryService;
    private final InterestRepository interestRepository;

    //method to complete profile for user
    @Override
    @Transactional
    public User completeProfile(
            MultipartFile photo,
            List<String> interests,
            String bio,
            String username) {
        //extract the current user from the SecurityContext
        User currentUser = securityUtils.getCurrentUser();

        //check if the user has uploaded a photo or not and upload it to Cloudinary
        if(photo != null && !photo.isEmpty()) {
            String imageUrl = cloudinaryService.uploadImage(photo);
            currentUser.setProfilePhotoUrl(imageUrl);
        }

        //check if the user has give his username or not and update accordingly
        if(username != null && !username.isBlank()) {
            currentUser.setUsername(username);
        }

        //rest all are required fields and can set directly
        currentUser.setBio(bio);

        //build a Set of Interests and then update the profile
        Set<Interest> userInterests = interestsBuilder(interests);
        currentUser.setInterests(userInterests);

        return currentUser;
    }

    //helper method to build Interest set
    public Set<Interest> interestsBuilder(List<String> userInterests) {
        List<Interest> savedInterests = interestRepository.findAllByInterestIn(userInterests);

        //convert the list of interests to set of strings
        Set<String> savedNames = new HashSet<>();
        for(Interest interest : savedInterests) {
            savedNames.add(interest.getInterest());
        }


        //find the new interests and persist them first and also build the final user interest set
        Set<Interest> finalInterests = new HashSet<>(savedInterests);
        for(String interestName : userInterests) {
            if(!savedNames.contains(interestName)) {
                finalInterests.add(interestRepository.save(new Interest(interestName)));
            }
        }

        return finalInterests;
    }
}
